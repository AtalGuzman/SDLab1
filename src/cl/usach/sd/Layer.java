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
	 * M�todo en el cual se va a procesar el mensaje que ha llegado al Nodo
	 * desde otro Nodo. Cabe destacar que el mensaje va a ser el evento descrito
	 * en la clase, a trav�s de la simulaci�n de eventos discretos.
	 */
	@Override
	public void processEvent(Node myNode, int layerId, Object event) {
		//System.out.println("***** LAYER *****");
		//System.out.println("\tSe procesa un evento");
		Message message = (Message) event;
		System.out.println("\tMensajes pasando por el nodo "+myNode.getID());
		int cantidadDeVecinos;
		int siguienteAccion = CommonState.r.nextInt(3);
		int cantTopic = 5;
		int rand;
		if(message.getDestination() == myNode.getID()){
			if(message.getTipoDeMensaje() == 0 || message.getTipoDeMensaje() == 3){ //Me lo envi� un publicador
				//System.out.println("\tLO RECIBO COMO TOPICO DESDE EL NODO "+message.getRemitent());
				int topicId = ((NodePS) myNode).getTopic();
				int idRemitent = message.getRemitent();
				if(topicId >=0){
					//System.out.println("\tS� tengo un t�pico");
					if(topicId == ((PubMsg) message).getIdTopico()){
						//System.out.println("\tSoy el t�pico que buscaban");
						//Message answer = new TopicMsg((int) myNode.getID(), , String content, int tipoDeMensaje);
						//Revisar si el nodo que me lo mand� est� registrado
						if(message.getAccion() == 0){
							System.out.println("\tRegistrar� al nodo "+message.getRemitent());
							((NodePS) myNode).getPublisherRegistered().add(message.getRemitent());
							
							
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
									for(int pub: publicoEn){
										System.out.println("\t- Puedo publicar en "+pub);
									}
								} else{	
									System.out.println("- Soy el nodo "+temp.getID()+" y no tengo t�picos.");
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
						else if(message.getAccion() == 1){
							
						}
						else if(message.getAccion() == 2){
							((NodePS)Network.get(idRemitent)).getRegisteredTopic();
							if(((NodePS) myNode).registrado(idRemitent)){
								//Send message
								//System.out.println("\tEnviar� un mensaje a todos los  subscriptores");
								ArrayList<TopicMsg> notificaciones = ((NodePS) myNode).Publish( ((NodePS) myNode).getSubscriberSubscribed(), (int) myNode.getID(), topicId);
								for(Message msj: notificaciones){
									sendmessage(myNode, layerId, msj);
								} 
								//System.out.println("\tLos mensajes han sido despachados");
							}
							else{
								//System.out.println("\tEl nodo no est� registrado en esta t�pico. Por favor, enviar solicitud ");
								((NodePS) myNode).register(message.getRemitent());
							}
						}
					} 
					else {
						//System.out.println("\tNo soy el t�pico solicitado");
							//reenviar el mismo mensaje, pero a uno de mis vecinos de modo random
						NodePS original = (NodePS) Network.get(message.getRemitent());
						int cantidadVecinos = ((Linkable) myNode.getProtocol(0)).degree();
						rand = CommonState.r.nextInt(cantidadVecinos);
						//System.out.println("Reenviar� el mensaje a "+rand);
						message.setTipoDeMensaje(0);
						Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
						message.setDestination( (int) sendNode.getID());
						((Transport) myNode.getProtocol(transportId)).send(original,sendNode, message, layerId);
					}
				} 
				else {
					//System.out.println("\tNo se posee un t�pico.");
					NodePS original = (NodePS) Network.get(message.getRemitent());
					cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();
					rand = CommonState.r.nextInt(cantidadDeVecinos);
					//System.out.println("\tReenviar� el mensaje a "+rand);
					message.setTipoDeMensaje(0);
					message.setIntermediario(true);
					Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
					message.setDestination( (int) sendNode.getID());
					//message.setTtl(message.getTtl()-1);
					((Transport) myNode.getProtocol(transportId)).send(original, sendNode, message, layerId);
				}
			}
			else if(message.getTipoDeMensaje()==1){ //Me lo envi� un subscriber
				System.out.println("\tLO RECIBO COMO TOPICO");
			}
			else if(message.getTipoDeMensaje() == 2){ //Me lo envi{o un topico
				if( ((NodePS) myNode).getTopicSub().size() != 0 ){
				
					if(  ((NodePS) myNode).getTopicSub().contains( ((TopicMsg) message).getIdTopic() )) {
						System.out.println("\n****************\n");
						System.out.println("\tSoy el nodo "+myNode.getID());
						System.out.println("\tHe recibido la siguiente notificaci�n desde el t�pico: "+ ((TopicMsg) message).getIdTopic()+"\n\t\t"+((TopicMsg) message).getContent());
						}
						//Se debe ver qu� realizar, comportarse como alg
						siguienteAccion = CommonState.r.nextInt(100);
						//System.out.println(siguienteAccion);
						if(siguienteAccion < 60){
							System.out.println("\tActuar� como publicador");
							//Puedo registrarme a una t�pico
							cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();
							rand =  CommonState.r.nextInt(cantidadDeVecinos);
							Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
							int myCantTopic = ((NodePS)myNode).getRegisteredTopic().size();
							
							if(myCantTopic > 0){
								System.out.println("\tMe registrar� a un t�pico");
								int indexTopic = CommonState.r.nextInt(myCantTopic);
								System.out.println(myCantTopic);
								int chooseTopic = ((NodePS)myNode).getRegisteredTopic().get(indexTopic);
								String content = "He realizado una publicaci�n en el t�pico "+chooseTopic;
								Message msg = ((NodePS) myNode).publish((int)myNode.getID(),chooseTopic, (int)((NodePS)sendNode).getID(), content, 0);
								sendmessage(myNode, layerId,msg);
								//Desregistrarme a un t�pico
							}
							else{
								System.out.println("\tMe registrar� a un t�pico");
								int topic = 2;
								((NodePS) myNode).getRegisteredTopic().add(topic);
								Message msg = new PubMsg((int) myNode.getID(),(int)sendNode.getID(),"Me quiero registrar al t�pico"+topic,0, topic);
								msg.setAccion(0);
								System.out.println("Mensaje : \n la acci�n es "+msg.getAccion());
								System.out.println("Mensaje : \n el remitente es "+msg.getRemitent());
								System.out.println("Mensaje : \n el destinatario es "+msg.getDestination());
								sendmessage(myNode,layerId,msg);
								///Registrad�simo
							}
						}
						else if(siguienteAccion >=60){
							System.out.println("\tAhora actuar� como un subscriptor");
						}
					System.out.println("\n****************\n");
				} else{
					//System.out.println("\t*No estoy subscrito a ning�n t�pico*");+
					//No se ha subscrito a ning�n t�pico
				}
			}
			
		}
		else{
			/*ENVIAR� UN MENSAJE*/
			if(message.getTipoDeMensaje() == 3){ //Primer mensaje
				 //En este caso el destino es alg�n t�pico
				PubMsg mensajePublicador = (PubMsg) message;
				//if(myNode.getID() == message.getRemitent()) 
				System.out.println("\tVoy a publicar en el t�pico "+mensajePublicador.getIdTopico()+". Le enviar� el mensaje a "+ mensajePublicador.getDestination()); //Obtengo el nodo
				sendmessage(myNode,layerId,message);
			}
			else if(message.getTipoDeMensaje() == 0){
				//me debo comportar como un publicador supongo ...
				if(message.getIntermediario()){
					//System.out.println("\tEstoy enviando como intermediario este mensaje al nodo "+message.getDestination());
					sendmessage(myNode,layerId,message);
				} 
				else{
					PubMsg mensajePublicador = (PubMsg) message;
					//System.out.println("\tVoy a publicar en el t�pico "+mensajePublicador.getIdTopico()+". Le enviar� el mensaje a "+ mensajePublicador.getDestination()); //Obtengo el nodo
					sendmessage(myNode,layerId,message);
				}
			}
			else if(message.getTipoDeMensaje()==1){ //Me debo comportar como un subscriber
				System.out.println("\tQuiero realizar un update");
			}
			else if(message.getTipoDeMensaje() == 2){ //Me debo compotar como un topico
				System.out.println("\tInformar� a mis subscriptores");
			}
			
		} //El nodo inicial empieza a reenviar los datos, entonces hay que buscar la forma de que env�e el dato una pura vez
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
		 * Inicialización del Nodo
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
}
