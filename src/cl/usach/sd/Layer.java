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
	int cantTopic = Configuration.getInt("init.1statebuilder.cantTopic");
	/************************************************************************************************************************
	 * Método en el cual se va a procesar el mensaje que ha llegado al Nodo
	 * desde otro Nodo. Cabe destacar que el mensaje va a ser el evento descrito
	 * en la clase, a través de la simulación de eventos discretos.
	 * 
	 * En la consola de comandos se observará la traza del mensaje nodo a nodo,
	 * cada vez que un mensaje llegue a destino las acciones realizadas por el nodo
	 * están encuadradas por arriba y por abajo con asteriscos.
	 * 
	 * En esta situación se describe si el nodo actúa como subscriptor, publicador o nodo
	 * se describen las acciones que llevan a cabo y en caso de que se realiza un cambio en la red
	 * ya sea por la creación de un tópico por parte de un publicador o un tópico registra/deregistra a un publicador
	 * o un suscribe/desuscribe a un subscriptor
	*************************************************************************************************************************/
	@Override
	public void processEvent(Node myNode, int layerId, Object event) {
		//System.out.println("***** LAYER *****");
		//System.out.println("\tSe procesa un evento");
		Message message = (Message) event;
		System.out.println("\tMensajes pasando por el nodo "+myNode.getID()+" y el destinario es "+message.getDestination());
		//String ttlWarning = "\tEl mensaje tiene ttl "+message.getTtl();
		//if(message.getTtl() <= 0){
			//ttlWarning+=". El mensaje ya no será reenviado :c";
		//}
		//System.out.println(ttlWarning);

		int cantidadDeVecinos;
		int k = 2;
		int siguienteAccion = CommonState.r.nextInt(3);
		//int cantTopic = Configuration.getInt("init.1statebuilder.cantTopic");
		int rand;
		/*En caso de que sea el nodo de destino se debe revisar el comportamiento adecuado
		 * es decir
		 * si me lo mandó un publicador, me debo comportar como un tópico
		 * si me lo mandó un subscriptor, me debo comportar como un tópico
		 * si me lo mandó un tópico me debe comportar como publicador o como subscriptor dependiendo
		 * si se está solicitando una actualización o está llegando un nuevo post
		 * */
		if(message.getDestination() == myNode.getID()){
			//En esta parte se procesa en caso de que lo haya mandado un tópico
			if(message.getTipoDeMensaje() == 0 || message.getTipoDeMensaje() == 3){
				System.out.println("\tEl contenido el mensaje es \n\t\t\t"+message.getContent());
				int topicId = ((NodePS) myNode).getTopic();
				int idRemitent = message.getRemitent();
			//Si poseo un tópico
				if(topicId >=0){					
			//Si el tópico corresponde al tópico que poseo
					if(topicId == ((PubMsg) message).getIdTopico()){
						System.out.println("\tSoy el tópico que buscaban");
			//Si la acción es cero, entonces se trata de una solicitud de registro
						if(message.getAccion() == 0){
							System.out.println("\n**********************************\n");
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
							System.out.println("\tHe recibido una solicitud de registro del nodo "+message.getRemitent());
			//Si no tengo registrado el nodo, lo registro
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
			//En caso de que se elimine el registro del publciador
						else if(message.getAccion() == 1){
							//Eliminar al nodo
							System.out.println("\n**********************************\n");
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
							System.out.println("\tHe recibido una solicitud de eliminación de registro del nodo "+message.getRemitent());
			//Si lo tengo registrado lo puedo eliminar
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
			//Si la acción es cero, entonces el publicador realizó una publicación
						else if(message.getAccion() == 2){
							System.out.println("\n**********************************\n");
			//Si tengo registrado el nodo como publicador, entonces puedo enviar notificaciones
							if(((NodePS) myNode).registrado(idRemitent)){
								//Send message
								System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
								System.out.println("\tHe recibido una publicación del nodo "+message.getRemitent());
								System.out.println("\tEnviaré notificación a todos los  subscriptores");
								String content = "Se ha generado un post en el topico "+topicId;
								ArrayList<TopicMsg> notificaciones = ((NodePS) myNode).Publish( ((NodePS) myNode).getSubscriberSubscribed(), (int) myNode.getID(), topicId,content);
								for(Message msj: notificaciones){
									if(msj.getTtl() >0 ) sendmessage(myNode, layerId, msj);
								} 
							}
							else{
								System.out.println("\tEl nodo no está registrado en este tópico("+ ((NodePS)myNode).getTopic()+"). Por favor, enviar solicitud ");
								//((NodePS) myNode).register(message.getRemitent());
								if(message.getTtl()> 0) { 
									System.out.println("\tEl mensaje es difundido"); 
			//En caso de que se requiera una simulación más real, con más mensajes y mayor complejidad descomentar esta línea
									//difundir(myNode,message,0,k); 
								}
								else System.out.println("\tEl mensaje ya no tiene ttl");
							}
							System.out.println("\n**********************************\n");
						}
					} 
					else {
			//Si tengo tópico, pero no soy el tópico correspondiente difundo el mensaje un poquito
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
			//En esta caso me lo mandó un subscriber
			else if(message.getTipoDeMensaje()==1){
				System.out.println("\tLO RECIBO COMO TOPICO (desde un subcriber)");
				System.out.println("\tEl contenido el mensaje es \n\t\t"+message.getContent());
				int topicId = ((NodePS) myNode).getTopic();
			//Si tengo un tópico
				if(topicId >=0){
			//Si tengo el tópico solicitado
					if(topicId == ((SubMsg) message).getIdTopic()){
			//Si me está solicitando una subscripción
						if(message.getAccion() == 0){
							System.out.println("\n**********************************\n");
							System.out.println("\tSolicitud de subscripción al tópico "+((SubMsg)message).getIdTopic()+" y soy el tópico "+ ((NodePS) myNode).getTopic());						
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
							System.out.println("\tHe recibido una solicitud de subscripción del nodo "+message.getRemitent());
			//Si no lo tengo subscrito lo puedo subscribir
							if(!((NodePS) myNode).getSubscriberSubscribed().contains(message.getRemitent())){
								System.out.println("\tHe subscrito al nodo "+message.getRemitent());
								((NodePS) myNode).getSubscriberSubscribed().add(message.getRemitent());
								updateSystem();
							}	
							else{
								System.out.println("\tYa tengo subscrito al nodo "+message.getRemitent());
							}
							System.out.println("\n**********************************\n");
						}
			//Si me están solicitando un desubscripción
						else if(message.getAccion() == 1){
							System.out.println("\n**********************************\n");
							System.out.println("\tSolicitud de desubcripción del tópico "+((SubMsg)message).getIdTopic()+" y soy el tópico "+ ((NodePS) myNode).getTopic());
			//Si tengo el tópico solicitado	
							if(topicId == ((SubMsg) message).getIdTopic()){
								System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
								System.out.println("\tHe recibido una solicitud de desubscripción del nodo "+message.getRemitent());
			//Si lo tengo subscrito lo puedo eliminar
								if(((NodePS) myNode).getSubscriberSubscribed().contains(message.getRemitent())){
									System.out.println("\tHe desubscrito al nodo "+message.getRemitent());
									((NodePS) myNode).getSubscriberSubscribed().remove((Object)message.getRemitent());
									updateSystem();
								}
								else{
									System.out.println("\tNo tengo subscrito al nodo "+message.getRemitent());
								}
								System.out.println("\n**********************************\n");
							}
						}
			//Si me están solicitando una actualización
						else if(message.getAccion() == 2){
							//Se debe manejar el request update	
							System.out.println("\n**********************************\n");
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el tópico "+((NodePS) myNode).getTopic());
			//SI lo tengo subscrito puedo solicitar actualización a los publicistas
							if(((NodePS) myNode).getSubscriberSubscribed().contains(message.getRemitent())){
								//Send message
								System.out.println("\tHe recibido una solicitud de actualización del nodo "+message.getRemitent());
								System.out.println("\tEnviaré notificación a todos los  publicadores");
								String content = "Se ha solicitado una actualización en el topico "+topicId;
								ArrayList<TopicMsg> notificaciones = ((NodePS) myNode).Publish( ((NodePS) myNode).getSubscriberSubscribed(), (int) myNode.getID(), topicId, content);
								for(Message msj: notificaciones){
									if(msj.getTtl() >0 ) sendmessage(myNode, layerId, msj);
								} 
							}
							else{
								System.out.println("\tEl nodo"+ message.getRemitent()+" no está registrado en este tópico ("+ ((NodePS)myNode).getTopic()+"). Por favor, enviar solicitud ");
								//((NodePS) myNode).register(message.getRemitent());
							}
							System.out.println("\n**********************************\n");	
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
			//El mensaje me lo ha enviado un tópico, por tanto puedo comportarme como subscritor o como publicador
			else if(message.getTipoDeMensaje() == 2){ //me lo envía un tópico
				System.out.println("\tEl contenido del mensaje es \n\t\t"+message.getContent());
			//Si tengo tópicos, entonces puedo ver las acciones
				if( ((NodePS) myNode).getTopicSub().size() > 0 ){
			//Si la acción es cero entonces me lo manda, como una notificación
					if(message.getAccion() == 0){
						if(  ((NodePS) myNode).getTopicSub().contains( ((TopicMsg) message).getIdTopic()) ){
							System.out.println("\n****************\n");
							System.out.println("\tSoy el nodo "+myNode.getID());
							System.out.println("\tHe recibido la siguiente notificación desde el tópico: "+ ((TopicMsg) message).getIdTopic()+"\n\t"+((TopicMsg) message).getContent());
							queHacer(myNode,cantTopic,siguienteAccion);
							}
						else{
							difundir(myNode,message,2,k);
						}
					}
			//Si la acción es uno, entonces me lo manda como una solicitd de actualización
					else if(message.getAccion() == 1){
						if(  ((NodePS) myNode).getTopicSub().contains( ((TopicMsg) message).getIdTopic()) ){
							System.out.println("\n****************\n");
							System.out.println("\tSoy el nodo "+myNode.getID());
							System.out.println("\tHe recibido la siguiente notificación desde el tópico: "+ ((TopicMsg) message).getIdTopic()+"\n\t"+((TopicMsg) message).getContent());
							((NodePS) myNode).flooding(layerId, ((SubMsg) message).getIdTopic(), 3);
							System.out.println("\n****************\n");	
						}
						else{
							difundir(myNode,message,2,k);
						}
					}
				} 
				else{
			//Si no tengo ningún tópico, entonces debo difundir el mensaje
					cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();
					rand = CommonState.r.nextInt(cantidadDeVecinos);
					Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
					int subcriberTopic = CommonState.r.nextInt(cantTopic);
					Message msg = ((NodePS) myNode).registerSub(subcriberTopic, sendNode, cantTopic);
					if(msg.getTtl()>0) sendmessage(myNode, layerId,msg);
				}
			}
		}
		else{
			/*En caso de que no corresponda con el nodo de destino
			 * quiere decir que el mensaje lo envié yo, PERO este mensaje
			 * es reenviado SOLO si el ttl es mayor que 0*/
			if(message.getTtl() > 0){
				//El mensaje tipo 3, es especial ya que se maneja como el iniciador de todo el tráfico
				if(message.getTipoDeMensaje() == 3){ //Primer mensaje
					 //En este caso el destino es algún tópico
					PubMsg mensajePublicador = (PubMsg) message;
					//if(myNode.getID() == message.getRemitent()) 
					System.out.println("\t"+message.getContent());
					if(message.getTtl()>0)sendmessage(myNode,layerId,message);
				}
				else if(message.getTipoDeMensaje() == 0){
					//Es un mensaje iniciado por un publicador, entonces debo reenviarlo como intermediario
					envioIntermediario(message, myNode);
				}
				else if(message.getTipoDeMensaje()==1){ 
					//Es un mensaje iniciado por un subscriptor, entonces debo reenviarlo como intermediario
					envioIntermediario(message, myNode);

				}
				else if(message.getTipoDeMensaje() == 2){
					//Es un mensaje iniciado por un tópico, entonces debo reenviarlo como intermediario
					envioIntermediario(message, myNode);
				}
			} else{

				//System.out.println("********************\n");
				//System.out.println("\tSoy el nodo "+myNode.getID());
				//queHacer(myNode,message,cantTopic,siguienteAccion); 
			}
		} 
		getStats();
	}

	private void getStats() {
		Observer.message.add(1);
	}
	
	/*Este método es utilizado para enviar mensajes a través de la capa
	 * de transporte, como se observa recibe el nodo que lo envía, la id de la capa por 
	 * la cual se enviará y el objeto, el mensaje utilizado
	 * */
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
	
	/*Esta función es utilizada pa reenviar los mensajes
	 * en caso de que aún tenga ttl y que el nodo actual
	 * no sea el destino
	 * myNode: es el nodo que enviará el mensaje
	 * Message, el mensaje a ser reenviar
	 * tipoMensaje, es el tipo del mensaje para que pueda ser procesado en el nodo de destino
	 * k es la cantidad de nodos a los que se reenviará el mensaje	
	 * */
	
	public void difundir(Node myNode, Message message, int tipoMensaje,int k){
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
	
	/*Esta función es utilizada para mostrar cada vez se agrega un subscriptor, se registra un publicador
	 * o se eliminan de un tópico
	 * */
	public void updateSystem(){
		System.out.println("\t\nLa nueva configuración del sistema es \n\n");
		int topicAtNet = 0;
		for (int i = 0; i < Network.size(); i++) {
			NodePS temp = (NodePS) Network.get(i);
			if(temp.getTopic() != -1){
				topicAtNet++;
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
		System.out.println("\tLa red tiene "+topicAtNet+" tópicos");
	}
	
	/*Un nodo que no tiene nada que hacer, por ejemplo el mensaje no está dirigido a mí
	 * y además el mensaje no tiene ttl
	 * Recibe al nodo actual
	 * la cantidad de tópicos que están en la red
	 * y el número que determinará la siguiente acción
	 * */
	public void queHacer(Node myNode, int cantTopic, int siguienteAccion){
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
			//Si el nodo actual ya contiene un tópico procede a registrarse o deregistrarse a un tópico
				if( ((NodePS)myNode).getTopic() >0  ){
					Message msg = registerDeregister( myNode,  sendNode,  publicationTopic);
					if(msg.getTtl() > 0) sendmessage(myNode,layerId,msg);
				}
			//Si no tiene un tópico, entonces crea uno siempre y cuando lo soporte la red, ya que se trabaja
			//bajo el supuesto de que cada nodo tiene un tópico
				else{
					if(cantTopic < Network.size()){
						System.out.println("\t\tCrearé un tópico");
			//El tópico creado queda en mi mismo.
						int topics = 0;
						for (int i = 0; i < Network.size(); i++) {
							NodePS temp = (NodePS) Network.get(i);
							if(temp.getTopic() != -1){
								topics++;
							}
						}
						((NodePS) myNode).setTopic(topics);
						System.out.println("\t\tEl tópico creado corresponde a "+topics);
						System.out.println("\t\tEl tópico queda guardado en mi mismo");
						updateSystem();
					}
				}
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
			Message msg = subscribeDesubscribRequestUpdate(myNode, sendNode, subcriberTopic, cantTopic, rand);
			if(msg.getTtl()>0) sendmessage(myNode, layerId,msg);
			
		}
		else if(siguienteAccion >=50){
			//Nada, considero que es posible que un nodo no haga nada
		}
		System.out.println("\n****************\n");
	}
	
	/*Esta función es utilizada para saber si un publicador
	 * debe registrarse o desregistrarse
	 * Se utiliza el nodo actual Mynode
	 * El nodo al que se va a enviar sendNode
	 * y el topico al cual me registraré/desregistraré
	 * */
	public Message registerDeregister(Node myNode, Node sendNode, int publicationTopic){
		Message msg;
		int decision = CommonState.r.nextInt(2);
		decision = CommonState.r.nextInt(2);
		//Si es 1 me puedo deregistrar siempre y cuando esté registrado en algo
		if(decision == 1 && ((NodePS) myNode).getRegisteredTopic().size() > 0){
			msg = ((NodePS) myNode).deregisterPublisher(sendNode, publicationTopic);
		}
		else{
			publicationTopic = CommonState.r.nextInt(((NodePS) myNode).getRegisteredTopic().size());
			msg = ((NodePS) myNode).registerPublisher(sendNode, publicationTopic);			
		}
		return msg;
	}
	
	/*Esta función es utilizada para saber si un publicador
	 * debe eliminar o crear una publicación
	 * Se utiliza el nodo actual Mynode
	 * El nodo al que se va a enviar sendNode
	 * y el topico al cual publicaré/eliminaré la publicación
	 * */
	public Message publicationDeletePublication(Node myNode, Node sendNode, int publicationTopic){
		Message msg;
		int decision = CommonState.r.nextInt(2);
		decision = CommonState.r.nextInt(2);
		//Puedo eliminar publicaciones siempre y cuando tenga publicaciones
		if(((NodePS) myNode).getCantPublication() > 0 && decision == 0){
			publicationTopic = CommonState.r.nextInt(((NodePS) myNode).getRegisteredTopic().size());
			msg = ((NodePS) myNode).deletePublication(sendNode, publicationTopic);
		}
		else{
			msg = ((NodePS) myNode).publish(sendNode, publicationTopic);
		}
		return msg;
		
	}
	
	/*Esta función es utilizada para saber si un subscriptor
	 * debe subscriptor o desubscriptor
	 * Se utiliza el nodo actual Mynode
	 * El nodo al que se va a enviar sendNode
	 * y el topico al cual me subscribiré/desubscribiré
	 * */
	public Message subscribeDesubscribRequestUpdate(Node myNode, Node sendNode, int subcriberTopic, int cantTopic, int rand){
		Message msg;
		int decision = CommonState.r.nextInt(2);
		decision = CommonState.r.nextInt(2);
		if(((NodePS) myNode).getTopicSub().size() > 0 && decision == 0){
			subcriberTopic = CommonState.r.nextInt(((NodePS) myNode).getTopicSub().size());
			msg = ((NodePS) myNode).deregisterSub(subcriberTopic, sendNode, cantTopic);
		}
		else{ //Puedo subscribirme o pedir una actualización
			if(((NodePS) myNode).getTopicSub().contains((Object) subcriberTopic)){
				//mensaje de actualización
				System.out.println("Solicito una actualización del tópico "+subcriberTopic);
				String content = "Solicito una actualización del tópico "+subcriberTopic;
				msg = ((NodePS) myNode).requestUpdate((int) myNode.getID(), subcriberTopic, (int)sendNode.getID(), content);
			}
			else{
				System.out.println("\tMe subscribiré al topico "+subcriberTopic);
				msg = ((NodePS) myNode).registerSub(subcriberTopic, sendNode,  cantTopic);
			}
		}
		msg.setTipoDeMensaje(1);
		return msg;
	}
	
	
	/*
	 * Cuando yo no soy el destino puedo enviar mensajes
	 * el mensaje es el mensaje a reenviar
	 * el nodo corresponde al nodo que reenviará el mensaje*/
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
