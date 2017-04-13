package cl.usach.sd;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireKOut;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

import java.util.ArrayList;

import cl.usach.sd.Message;
import msg.*;
import ps.*;

public class Layer implements Cloneable, EDProtocol {
	private static final String PAR_TRANSPORT = "transport";
	private static String prefix = null;
	private int transportId;
	private int layerId;

	/**
	 * Método en el cual se va a procesar el mensaje que ha llegado al Nodo
	 * desde otro Nodo. Cabe destacar que el mensaje va a ser el evento descrito
	 * en la clase, a través de la simulación de eventos discretos.
	 */
	@Override
	public void processEvent(Node myNode, int layerId, Object event) {
		//System.out.println("***** LAYER *****");
		//System.out.println("\tSe procesa un evento");
		Message message = (Message) event;
		//System.out.println("\tMensajes pasando por el nodo "+myNode.getID()+" y el destinario es "+message.getDestination());
		//String ttlWarning = "\tEl mensaje tiene ttl "+message.getTtl();
		//if(message.getTtl() <= 0){
			//ttlWarning+=". El mensaje ya no será reenviado :c";
		//}
		//System.out.println(ttlWarning);

		int cantidadDeVecinos;
		int k = 4;
		int siguienteAccion = CommonState.r.nextInt(3);
		int cantTopic = Configuration.getInt("init.1statebuilder.cantTopic");
		int rand;
		if(message.getDestination() == myNode.getID()){
			if(message.getTipoDeMensaje() == 0 || message.getTipoDeMensaje() == 3){ //Me lo envió un publicador
				System.out.println("\tEl contenido el mensaje es \n\t\t"+message.getContent());
				int topicId = ((NodePS) myNode).getTopic();
				int idRemitent = message.getRemitent();
				if(topicId >=0){					
					if(topicId == ((PubMsg) message).getIdTopico()){
						System.out.println("\tSoy el tópico que buscaban");
						//Message answer = new TopicMsg((int) myNode.getID(), , String content, int tipoDeMensaje);
						//Revisar si el nodo que me lo mandó está registrado
						if(message.getAccion() == 0){
							System.out.println("\n**********************************\n");
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
							System.out.println("\tHe recibido una solicitud de registro del nodo "+message.getRemitent());
							if( !((NodePS) myNode).getPublisherRegistered().contains(message.getRemitent()) ){
								System.out.println("\tHe registrado como publicador al nodo"+message.getRemitent());
								((NodePS) myNode).getPublisherRegistered().add(message.getRemitent());
								updateSystem();
							}
							else{
								System.out.println("\tYa tengo registrado al nodo "+message.getRemitent());
							}
							System.out.println("\n**********************************\n");							
						}
						else if(message.getAccion() == 1){
							//Eliminar al nodo
							System.out.println("\n**********************************\n");
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
							System.out.println("\tHe recibido una solicitud de eliminación de registro del nodo "+message.getRemitent());
							if(((NodePS) myNode).getPublisherRegistered().contains(message.getRemitent())){
								System.out.println("\tHe eliminado como publicador al nodo"+message.getRemitent());
								((NodePS) myNode).getPublisherRegistered().remove((Object)message.getRemitent());
								updateSystem();
							}
							else{
								System.out.println("\tNo tengo registrado al nodo "+message.getRemitent());
							}
							System.out.println("\n**********************************\n");	
						}
						else if(message.getAccion() == 2){
							((NodePS)Network.get(idRemitent)).getRegisteredTopic();
							System.out.println("\n**********************************\n");
							if(((NodePS) myNode).registrado(idRemitent)){
								//Send message
								System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
								System.out.println("\tHe recibido una publicación del nodo "+message.getRemitent());
								System.out.println("\tEnviaré notificación a todos los  subscriptores");
								ArrayList<TopicMsg> notificaciones = ((NodePS) myNode).Publish( ((NodePS) myNode).getSubscriberSubscribed(), (int) myNode.getID(), topicId);
								for(Message msj: notificaciones){
									if(msj.getTtl() >0 ) sendmessage(myNode, layerId, msj);
								} 
							}
							else{
								System.out.println("\tEl nodo no está registrado en este tópico("+ ((NodePS)myNode).getTopic()+"). Por favor, enviar solicitud ");
								//((NodePS) myNode).register(message.getRemitent());
								if(message.getTtl()> 0) { System.out.println("\tEl mensaje es difundido"); difundir(myNode,message,0,k); }
								else System.out.println("\tEl mensaje ya no tiene ttl");
							}
							System.out.println("\n**********************************\n");
						}
					} 
					else {
						//System.out.println("\tNo soy el tópico solicitado");
							//reenviar el mismo mensaje, pero a uno de mis vecinos de modo random
						if(message.getTtl()> 0) { System.out.println("\tEl mensaje es difundido"); difundir(myNode,message,0,k); }
						else System.out.println("\tEl mensaje ya no tiene ttl");
					}
				} 
				else {
					if(message.getTtl()> 0) { System.out.println("\tEl mensaje es difundido"); difundir(myNode,message,0,k); }
					else System.out.println("\tEl mensaje ya no tiene ttl");
				}
			}
			else if(message.getTipoDeMensaje()==1){ //Me lo envió un subscriber
				System.out.println("\tLO RECIBO COMO TOPICO (desde un subcriber)");
				System.out.println("\tEl contenido el mensaje es \n\t"+message.getContent());
				int topicId = ((NodePS) myNode).getTopic();
				if(topicId >=0){
					System.out.println("Debo revisar si tengo que subscribir");
					if(topicId == ((SubMsg) message).getIdTopic()){
						System.out.println("La acción del mensaje es "+message.getAccion());
						if(message.getAccion() == 0){
							System.out.println("\n**********************************\n");
							System.out.println("\tSolicitud de subscripción al tópico "+((SubMsg)message).getIdTopic()+" y soy el tópico "+ ((NodePS) myNode).getTopic());						
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
							System.out.println("\tHe recibido una solicitud de subscripción del nodo "+message.getRemitent());
							if(!((NodePS) myNode).getSubscriberSubscribed().contains(message.getRemitent())){
								System.out.println("\tHe subscrito al nodo"+message.getRemitent());
								((NodePS) myNode).getSubscriberSubscribed().add(message.getRemitent());
								updateSystem();
							}	
							else{
								System.out.println("\tYa tengo subscrito al nodo "+message.getRemitent());
							}
							System.out.println("\n**********************************\n");
						}
						else if(message.getAccion() == 1){
							System.out.println("\n**********************************\n");
							System.out.println("\tSolicitud de desubcripción del tópico "+((SubMsg)message).getIdTopic()+" y soy el tópico "+ ((NodePS) myNode).getTopic());
							if(topicId == ((SubMsg) message).getIdTopic()){
								System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
								System.out.println("\tHe recibido una solicitud de subscripción del nodo "+message.getRemitent());
								if(((NodePS) myNode).getSubscriberSubscribed().contains(message.getRemitent())){
									System.out.println("He subscrito al nodo"+message.getRemitent());
									((NodePS) myNode).getSubscriberSubscribed().remove((Object)message.getRemitent());
									updateSystem();
								}
								else{
									System.out.println("\tNo tengo subscrito al nodo "+message.getRemitent());
								}
								System.out.println("\n**********************************\n");
								}
								
						}
						else if(message.getAccion() == 2){
							//Se debe manejar el request update

						}
						if(message.getTtl()> 0) difundir(myNode,message,1,k);
						else System.out.println("\tEl mensaje ya no tiene ttl");	
					}
					if(message.getTtl()> 0) difundir(myNode,message,1,k);
					else System.out.println("\tEl mensaje ya no tiene ttl");	
				}
				else{
					if(message.getTtl()> 0) difundir(myNode,message,1,k);
						
					else System.out.println("\tEl mensaje ya no tiene ttl");
				}
			}
			else if(message.getTipoDeMensaje() == 2){ //me lo envía un tópico
				System.out.println("El contenido del mensaje es "+message.getContent());//Me lo envi{o un topico
				if( ((NodePS) myNode).getTopicSub().size() > 0 ){
					if(  ((NodePS) myNode).getTopicSub().contains( ((TopicMsg) message).getIdTopic() )){
						System.out.println("\n****************\n");
						System.out.println("\tSoy el nodo "+myNode.getID());
						System.out.println("\tHe recibido la siguiente notificación desde el tópico: "+ ((TopicMsg) message).getIdTopic()+"\n\t"+((TopicMsg) message).getContent());
						queHacer(myNode,message,cantTopic,siguienteAccion);
						}
					else{
						difundir(myNode,message,2,k);
					}
				} 
				else{
					cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();
					rand = CommonState.r.nextInt(cantidadDeVecinos);
					Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
					int subcriberTopic = CommonState.r.nextInt(cantTopic);
					Message msg = ((NodePS) myNode).registerSub(subcriberTopic, sendNode, cantTopic, rand);
					if(msg.getTtl()>0) sendmessage(myNode, layerId,msg);
				}
			}
		}
		else{
			/*ENVIARÉ UN MENSAJE*/
			if(message.getTtl() > 0){
				if(message.getTipoDeMensaje() == 3){ //Primer mensaje
					 //En este caso el destino es algún tópico
					PubMsg mensajePublicador = (PubMsg) message;
					//if(myNode.getID() == message.getRemitent()) 
					System.out.println("\t"+message.getContent());
					if(message.getTtl()>0)sendmessage(myNode,layerId,message);
				}
				else if(message.getTipoDeMensaje() == 0){
					//me debo comportar como un publicador supongo ...
					envioIntermediario(message, myNode);
				}
				else if(message.getTipoDeMensaje()==1){ //Me debo comportar como un subscriber
					envioIntermediario(message, myNode);

				}
				else if(message.getTipoDeMensaje() == 2){ //Me debo compotar como un topico
					envioIntermediario(message, myNode);
				}
			} else{
				System.out.println("Se acabó el ttl de este mensaje");
				System.out.println("********************\n");
				System.out.println("\tSoy el nodo "+myNode.getID());
				queHacer(myNode,message,cantTopic,siguienteAccion); 
			}
		} //El nodo inicial empieza a reenviar los datos, entonces hay que buscar la forma de que envíe el dato una pura vez
		getStats();
	}

	private void getStats() {
		Observer.message.add(1);
	}

	public void sendmessage(Node currentNode, int layerId, Object message) {
		//System.out.println("\tPreparar mensaje");
		int sendNodeId = ((Message) message).getDestination();
		//System.out.println("\tCurrentNode: " + currentNode.getID() + " | Tengo: " + ((Linkable) currentNode.getProtocol(0)).degree()+" vecinos");
		((Transport) currentNode.getProtocol(transportId)).send(currentNode,  Network.get(sendNodeId), message, layerId);
	}
	/**
	 * Constructor por defecto de la capa Layer del protocolo construido
	 * 
	 * @param prefix
	 */
	public Layer(String prefix) {
		/**
		 * InicializaciÃ³n del Nodo
		 */
		Layer.prefix = prefix;
		transportId = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
		/**
		 * Siguiente capa del protocolo
		 */
		layerId = transportId + 1;
	}

	private Node searchNode(int id) {
		return Network.get(id);
	}

	/**
	 * Definir Clone() para la replicacion de protocolo en nodos
	 */
	public Object clone() {
		Layer dolly = new Layer(Layer.prefix);
		return dolly;
	}
	
	public void difundir(Node myNode, Message message, int tipoMensaje,int k){
		System.out.print("*");
		NodePS original = (NodePS) Network.get(message.getRemitent());
		int cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();
		int rand = CommonState.r.nextInt(cantidadDeVecinos);
		Node sendNode;
		message.setTtl(message.getTtl()-1);
		int i = 0;
		while(i < k){
		//System.out.println("\tReenviaré el mensaje a "+rand);
			message.setTipoDeMensaje(tipoMensaje);
			message.setIntermediario(true);
			sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
			message.setDestination( (int) sendNode.getID());
		//message.setTtl(message.getTtl()-1);
			((Transport) myNode.getProtocol(transportId)).send(original, sendNode, message, layerId);
			rand = CommonState.r.nextInt(cantidadDeVecinos);
			i++;
		}
	}
	
	public void updateSystem(){
		System.out.println("\t\nLa nueva configuración del sistema es \n\n");
		for (int i = 0; i < Network.size(); i++) {
			NodePS temp = (NodePS) Network.get(i);
			if(temp.getTopic() != -1){
				System.out.println("- Soy el nodo "+temp.getID()+" y tengo el topico "+temp.getTopic()+".");
				ArrayList<Integer> publicadores = ((NodePS) temp).getPublisherRegistered();
				ArrayList<Integer> publicoEn = ((NodePS) temp).getRegisteredTopic();
				for(int pub: publicadores){
					System.out.println("\t- Tengo registrado como publicador a "+pub);
				}
				ArrayList<Integer> subscriptores = ((NodePS) temp).getSubscriberSubscribed();
				for(int sub: subscriptores){
					System.out.println("\t- Tengo subscrito a "+sub);
				}
				for(int topic: temp.getTopicSub()){
					System.out.println("\t- Estoy subscrito en el topico "+ topic);
				}
				for(int top: publicoEn){
					System.out.println("\t- Puedo publicar en el tópico "+top);
				}
			} else{	
				System.out.println("- Soy el nodo "+temp.getID()+" y no tengo tópicos.");
				ArrayList<Integer> publicoEn = ((NodePS) temp).getRegisteredTopic();
				for(int topic: temp.getTopicSub()){
					System.out.println("\t- Estoy subscrito en el topico "+ topic);
				}
				for(int pub: publicoEn){
					System.out.println("\t- Puedo publicar en "+pub);
				}
			}
		}
	}
	
	public void queHacer(Node myNode, Message message, int cantTopic, int siguienteAccion){
		int cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();;
		int rand = CommonState.r.nextInt(cantidadDeVecinos);
		
		siguienteAccion = CommonState.r.nextInt(100);
		//System.out.println(siguienteAccion);
		if(	 siguienteAccion < 25){
			System.out.println("\tActuaré como publicador");
			//Actúo como un publicador 
			cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();
			rand =  CommonState.r.nextInt(cantidadDeVecinos);
			Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
			int publicationTopic = CommonState.r.nextInt(cantTopic);
			
			if(((NodePS)myNode).getRegisteredTopic().contains(publicationTopic)){
				Message msg = publicationDeletePublication(myNode, sendNode, publicationTopic);
				if(msg.getTtl() > 0) sendmessage(myNode, layerId,msg);	
			}
			else{
				Message msg = registerDeregister( myNode,  sendNode,  publicationTopic);
				if(msg.getTtl() > 0) sendmessage(myNode,layerId,msg);
			}
		}
		else if(siguienteAccion >= 25 || siguienteAccion < 50){
			//Actúo como un subscriptor
			System.out.println("\tActuaré como un subscriptor");
			//Puedo registrarme a una tópico
			cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();
			rand =  CommonState.r.nextInt(cantidadDeVecinos);
			Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
			int subcriberTopic = CommonState.r.nextInt(cantTopic);
			Message msg = subscribeDesubscribe(myNode, sendNode, subcriberTopic, cantTopic, rand);
			if(msg.getTtl()>0) sendmessage(myNode, layerId,msg);
			
		}
		else if(siguienteAccion >=50){
			//Nada, considero que es posible que un nodo no haga nada
		}
		System.out.println("\n****************\n");
	}
	
	public Message registerDeregister(Node myNode, Node sendNode, int publicationTopic){
		Message msg;
		int decision = CommonState.r.nextInt(2);
		decision = CommonState.r.nextInt(2);
		if(decision == 0){
			System.out.println("Me subscribo");
			msg = ((NodePS) myNode).registerPublisher(sendNode, publicationTopic);
		}
		else{
			System.out.println("El topico a eliminar es "+publicationTopic);
			publicationTopic = CommonState.r.nextInt(((NodePS) myNode).getRegisteredTopic().size());
			msg = ((NodePS) myNode).deregisterPublisher(sendNode, publicationTopic);
		}
		return msg;
	}
	
	public Message publicationDeletePublication(Node myNode, Node sendNode, int publicationTopic){
		Message msg;
		int decision = CommonState.r.nextInt(2);
		decision = CommonState.r.nextInt(2);
		if(((NodePS) myNode).getCantPublication() > 0 && decision == 0){
			publicationTopic = CommonState.r.nextInt(((NodePS) myNode).getRegisteredTopic().size());
			msg = ((NodePS) myNode).deletePublication(sendNode, publicationTopic);
		}
		else{
			msg = ((NodePS) myNode).publish(sendNode, publicationTopic);
		}
		return msg;
		
	}
	
	public Message subscribeDesubscribe(Node myNode, Node sendNode, int subcriberTopic, int cantTopic, int rand){
		Message msg;
		int decision = CommonState.r.nextInt(2);
		decision = CommonState.r.nextInt(2);
		decision = 0;
		if(((NodePS) myNode).getTopicSub().size() > 0 && decision == 0){
			subcriberTopic = CommonState.r.nextInt(((NodePS) myNode).getTopicSub().size());
			msg = ((NodePS) myNode).deregisterSub( subcriberTopic, sendNode, cantTopic, rand);
		}
		else{
			System.out.println("\tMe subscribiré al topico "+subcriberTopic);
			msg = ((NodePS) myNode).registerSub( subcriberTopic, sendNode,  cantTopic, rand);
		}
		return msg;
		
	}
	
	public void envioIntermediario(Message message, Node myNode){
		if(message.getIntermediario()){
			//System.out.println("\tEstoy enviando como intermediario este mensaje al nodo "+message.getDestination());
			if(message.getTtl() > 0){
				message.setTtl(message.getTtl()-1);
				sendmessage(myNode,layerId,message);
			}
		} 
		else{
			PubMsg mensajePublicador = (PubMsg) message;
			//System.out.println("\tVoy a publicar en el tópico "+mensajePublicador.getIdTopico()+". Le enviaré el mensaje a "+ mensajePublicador.getDestination()); //Obtengo el nodo
			if(message.getTtl() > 0) sendmessage(myNode,layerId,message);
		}
	}
}
