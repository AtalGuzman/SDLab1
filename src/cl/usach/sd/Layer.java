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
	 * M�todo en el cual se va a procesar el mensaje que ha llegado al Nodo
	 * desde otro Nodo. Cabe destacar que el mensaje va a ser el evento descrito
	 * en la clase, a trav�s de la simulaci�n de eventos discretos.
	 * 
	 * En la consola de comandos se observar� la traza del mensaje nodo a nodo,
	 * cada vez que un mensaje llegue a destino las acciones realizadas por el nodo
	 * est�n encuadradas por arriba y por abajo con asteriscos.
	 * 
	 * En esta situaci�n se describe si el nodo act�a como subscriptor, publicador o nodo
	 * se describen las acciones que llevan a cabo y en caso de que se realiza un cambio en la red
	 * ya sea por la creaci�n de un t�pico por parte de un publicador o un t�pico registra/deregistra a un publicador
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
			//ttlWarning+=". El mensaje ya no ser� reenviado :c";
		//}
		//System.out.println(ttlWarning);

		int cantidadDeVecinos;
		int k = 2;
		int siguienteAccion = CommonState.r.nextInt(3);
		//int cantTopic = Configuration.getInt("init.1statebuilder.cantTopic");
		int rand;
		/*En caso de que sea el nodo de destino se debe revisar el comportamiento adecuado
		 * es decir
		 * si me lo mand� un publicador, me debo comportar como un t�pico
		 * si me lo mand� un subscriptor, me debo comportar como un t�pico
		 * si me lo mand� un t�pico me debe comportar como publicador o como subscriptor dependiendo
		 * si se est� solicitando una actualizaci�n o est� llegando un nuevo post
		 * */
		if(message.getDestination() == myNode.getID()){
			//En esta parte se procesa en caso de que lo haya mandado un t�pico
			if(message.getTipoDeMensaje() == 0 || message.getTipoDeMensaje() == 3){
				System.out.println("\tEl contenido el mensaje es \n\t\t\t"+message.getContent());
				int topicId = ((NodePS) myNode).getTopic();
				int idRemitent = message.getRemitent();
			//Si poseo un t�pico
				if(topicId >=0){					
			//Si el t�pico corresponde al t�pico que poseo
					if(topicId == ((PubMsg) message).getIdTopico()){
						System.out.println("\tSoy el t�pico que buscaban");
			//Si la acci�n es cero, entonces se trata de una solicitud de registro
						if(message.getAccion() == 0){
							System.out.println("\n**********************************\n");
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el t�pico "+((NodePS) myNode).getTopic());
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
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el t�pico "+((NodePS) myNode).getTopic());
							System.out.println("\tHe recibido una solicitud de eliminaci�n de registro del nodo "+message.getRemitent());
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
			//Si la acci�n es cero, entonces el publicador realiz� una publicaci�n
						else if(message.getAccion() == 2){
							System.out.println("\n**********************************\n");
			//Si tengo registrado el nodo como publicador, entonces puedo enviar notificaciones
							if(((NodePS) myNode).registrado(idRemitent)){
								//Send message
								System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el t�pico "+((NodePS) myNode).getTopic());
								System.out.println("\tHe recibido una publicaci�n del nodo "+message.getRemitent());
								System.out.println("\tEnviar� notificaci�n a todos los  subscriptores");
								String content = "Se ha generado un post en el topico "+topicId;
								ArrayList<TopicMsg> notificaciones = ((NodePS) myNode).Publish( ((NodePS) myNode).getSubscriberSubscribed(), (int) myNode.getID(), topicId,content);
								for(Message msj: notificaciones){
									if(msj.getTtl() >0 ) sendmessage(myNode, layerId, msj);
								} 
							}
							else{
								System.out.println("\tEl nodo no est� registrado en este t�pico("+ ((NodePS)myNode).getTopic()+"). Por favor, enviar solicitud ");
								//((NodePS) myNode).register(message.getRemitent());
								if(message.getTtl()> 0) { 
									System.out.println("\tEl mensaje es difundido"); 
			//En caso de que se requiera una simulaci�n m�s real, con m�s mensajes y mayor complejidad descomentar esta l�nea
									//difundir(myNode,message,0,k); 
								}
								else System.out.println("\tEl mensaje ya no tiene ttl");
							}
							System.out.println("\n**********************************\n");
						}
					} 
					else {
			//Si tengo t�pico, pero no soy el t�pico correspondiente difundo el mensaje un poquito
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
			//En esta caso me lo mand� un subscriber
			else if(message.getTipoDeMensaje()==1){
				System.out.println("\tLO RECIBO COMO TOPICO (desde un subcriber)");
				System.out.println("\tEl contenido el mensaje es \n\t\t"+message.getContent());
				int topicId = ((NodePS) myNode).getTopic();
			//Si tengo un t�pico
				if(topicId >=0){
			//Si tengo el t�pico solicitado
					if(topicId == ((SubMsg) message).getIdTopic()){
			//Si me est� solicitando una subscripci�n
						if(message.getAccion() == 0){
							System.out.println("\n**********************************\n");
							System.out.println("\tSolicitud de subscripci�n al t�pico "+((SubMsg)message).getIdTopic()+" y soy el t�pico "+ ((NodePS) myNode).getTopic());						
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el t�pico "+((NodePS) myNode).getTopic());
							System.out.println("\tHe recibido una solicitud de subscripci�n del nodo "+message.getRemitent());
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
			//Si me est�n solicitando un desubscripci�n
						else if(message.getAccion() == 1){
							System.out.println("\n**********************************\n");
							System.out.println("\tSolicitud de desubcripci�n del t�pico "+((SubMsg)message).getIdTopic()+" y soy el t�pico "+ ((NodePS) myNode).getTopic());
			//Si tengo el t�pico solicitado	
							if(topicId == ((SubMsg) message).getIdTopic()){
								System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el t�pico "+((NodePS) myNode).getTopic());
								System.out.println("\tHe recibido una solicitud de desubscripci�n del nodo "+message.getRemitent());
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
			//Si me est�n solicitando una actualizaci�n
						else if(message.getAccion() == 2){
							//Se debe manejar el request update	
							System.out.println("\n**********************************\n");
							System.out.println("\tSoy el nodo "+myNode.getID()+" y tengo el t�pico "+((NodePS) myNode).getTopic());
			//SI lo tengo subscrito puedo solicitar actualizaci�n a los publicistas
							if(((NodePS) myNode).getSubscriberSubscribed().contains(message.getRemitent())){
								//Send message
								System.out.println("\tHe recibido una solicitud de actualizaci�n del nodo "+message.getRemitent());
								System.out.println("\tEnviar� notificaci�n a todos los  publicadores");
								String content = "Se ha solicitado una actualizaci�n en el topico "+topicId;
								ArrayList<TopicMsg> notificaciones = ((NodePS) myNode).Publish( ((NodePS) myNode).getSubscriberSubscribed(), (int) myNode.getID(), topicId, content);
								for(Message msj: notificaciones){
									if(msj.getTtl() >0 ) sendmessage(myNode, layerId, msj);
								} 
							}
							else{
								System.out.println("\tEl nodo"+ message.getRemitent()+" no est� registrado en este t�pico ("+ ((NodePS)myNode).getTopic()+"). Por favor, enviar solicitud ");
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
			//El mensaje me lo ha enviado un t�pico, por tanto puedo comportarme como subscritor o como publicador
			else if(message.getTipoDeMensaje() == 2){ //me lo env�a un t�pico
				System.out.println("\tEl contenido del mensaje es \n\t\t"+message.getContent());
			//Si tengo t�picos, entonces puedo ver las acciones
				if( ((NodePS) myNode).getTopicSub().size() > 0 ){
			//Si la acci�n es cero entonces me lo manda, como una notificaci�n
					if(message.getAccion() == 0){
						if(  ((NodePS) myNode).getTopicSub().contains( ((TopicMsg) message).getIdTopic()) ){
							System.out.println("\n****************\n");
							System.out.println("\tSoy el nodo "+myNode.getID());
							System.out.println("\tHe recibido la siguiente notificaci�n desde el t�pico: "+ ((TopicMsg) message).getIdTopic()+"\n\t"+((TopicMsg) message).getContent());
							queHacer(myNode,cantTopic,siguienteAccion);
							}
						else{
							difundir(myNode,message,2,k);
						}
					}
			//Si la acci�n es uno, entonces me lo manda como una solicitd de actualizaci�n
					else if(message.getAccion() == 1){
						if(  ((NodePS) myNode).getTopicSub().contains( ((TopicMsg) message).getIdTopic()) ){
							System.out.println("\n****************\n");
							System.out.println("\tSoy el nodo "+myNode.getID());
							System.out.println("\tHe recibido la siguiente notificaci�n desde el t�pico: "+ ((TopicMsg) message).getIdTopic()+"\n\t"+((TopicMsg) message).getContent());
							((NodePS) myNode).flooding(layerId, ((SubMsg) message).getIdTopic(), 3);
							System.out.println("\n****************\n");	
						}
						else{
							difundir(myNode,message,2,k);
						}
					}
				} 
				else{
			//Si no tengo ning�n t�pico, entonces debo difundir el mensaje
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
			 * quiere decir que el mensaje lo envi� yo, PERO este mensaje
			 * es reenviado SOLO si el ttl es mayor que 0*/
			if(message.getTtl() > 0){
				//El mensaje tipo 3, es especial ya que se maneja como el iniciador de todo el tr�fico
				if(message.getTipoDeMensaje() == 3){ //Primer mensaje
					 //En este caso el destino es alg�n t�pico
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
					//Es un mensaje iniciado por un t�pico, entonces debo reenviarlo como intermediario
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
	
	/*Este m�todo es utilizado para enviar mensajes a trav�s de la capa
	 * de transporte, como se observa recibe el nodo que lo env�a, la id de la capa por 
	 * la cual se enviar� y el objeto, el mensaje utilizado
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
	
	/*Esta funci�n es utilizada pa reenviar los mensajes
	 * en caso de que a�n tenga ttl y que el nodo actual
	 * no sea el destino
	 * myNode: es el nodo que enviar� el mensaje
	 * Message, el mensaje a ser reenviar
	 * tipoMensaje, es el tipo del mensaje para que pueda ser procesado en el nodo de destino
	 * k es la cantidad de nodos a los que se reenviar� el mensaje	
	 * */
	
	public void difundir(Node myNode, Message message, int tipoMensaje,int k){
		NodePS original = (NodePS) Network.get(message.getRemitent());
		int cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();
		int rand = CommonState.r.nextInt(cantidadDeVecinos);
		Node sendNode;
		message.setTtl(message.getTtl()-1);
		int i = 0;
		while(i < k){
		//System.out.println("\tReenviar� el mensaje a "+rand);
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
	
	/*Esta funci�n es utilizada para mostrar cada vez se agrega un subscriptor, se registra un publicador
	 * o se eliminan de un t�pico
	 * */
	public void updateSystem(){
		System.out.println("\t\nLa nueva configuraci�n del sistema es \n\n");
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
					System.out.println("\t- Puedo publicar en el t�pico "+top);
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
		System.out.println("\tLa red tiene "+topicAtNet+" t�picos");
	}
	
	/*Un nodo que no tiene nada que hacer, por ejemplo el mensaje no est� dirigido a m�
	 * y adem�s el mensaje no tiene ttl
	 * Recibe al nodo actual
	 * la cantidad de t�picos que est�n en la red
	 * y el n�mero que determinar� la siguiente acci�n
	 * */
	public void queHacer(Node myNode, int cantTopic, int siguienteAccion){
		int cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();;
		int rand = CommonState.r.nextInt(cantidadDeVecinos);
		
		siguienteAccion = CommonState.r.nextInt(100);
		//System.out.println(siguienteAccion);
		if(	 siguienteAccion < 25){
			System.out.println("\tActuar� como publicador");
			//Act�o como un publicador 
			cantidadDeVecinos = ((Linkable) myNode.getProtocol(0)).degree();
			rand =  CommonState.r.nextInt(cantidadDeVecinos);
			Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
			int publicationTopic = CommonState.r.nextInt(cantTopic);
			if(((NodePS)myNode).getRegisteredTopic().contains(publicationTopic)){
				Message msg = publicationDeletePublication(myNode, sendNode, publicationTopic);
				if(msg.getTtl() > 0) sendmessage(myNode, layerId,msg);	
			}
			else{
			//Si el nodo actual ya contiene un t�pico procede a registrarse o deregistrarse a un t�pico
				if( ((NodePS)myNode).getTopic() >0  ){
					Message msg = registerDeregister( myNode,  sendNode,  publicationTopic);
					if(msg.getTtl() > 0) sendmessage(myNode,layerId,msg);
				}
			//Si no tiene un t�pico, entonces crea uno siempre y cuando lo soporte la red, ya que se trabaja
			//bajo el supuesto de que cada nodo tiene un t�pico
				else{
					if(cantTopic < Network.size()){
						System.out.println("\t\tCrear� un t�pico");
			//El t�pico creado queda en mi mismo.
						int topics = 0;
						for (int i = 0; i < Network.size(); i++) {
							NodePS temp = (NodePS) Network.get(i);
							if(temp.getTopic() != -1){
								topics++;
							}
						}
						((NodePS) myNode).setTopic(topics);
						System.out.println("\t\tEl t�pico creado corresponde a "+topics);
						System.out.println("\t\tEl t�pico queda guardado en mi mismo");
						updateSystem();
					}
				}
			}
		}
		else if(siguienteAccion >= 25 || siguienteAccion < 50){
			//Act�o como un subscriptor
			System.out.println("\tActuar� como un subscriptor");
			//Puedo registrarme a una t�pico
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
	
	/*Esta funci�n es utilizada para saber si un publicador
	 * debe registrarse o desregistrarse
	 * Se utiliza el nodo actual Mynode
	 * El nodo al que se va a enviar sendNode
	 * y el topico al cual me registrar�/desregistrar�
	 * */
	public Message registerDeregister(Node myNode, Node sendNode, int publicationTopic){
		Message msg;
		int decision = CommonState.r.nextInt(2);
		decision = CommonState.r.nextInt(2);
		//Si es 1 me puedo deregistrar siempre y cuando est� registrado en algo
		if(decision == 1 && ((NodePS) myNode).getRegisteredTopic().size() > 0){
			msg = ((NodePS) myNode).deregisterPublisher(sendNode, publicationTopic);
		}
		else{
			publicationTopic = CommonState.r.nextInt(((NodePS) myNode).getRegisteredTopic().size());
			msg = ((NodePS) myNode).registerPublisher(sendNode, publicationTopic);			
		}
		return msg;
	}
	
	/*Esta funci�n es utilizada para saber si un publicador
	 * debe eliminar o crear una publicaci�n
	 * Se utiliza el nodo actual Mynode
	 * El nodo al que se va a enviar sendNode
	 * y el topico al cual publicar�/eliminar� la publicaci�n
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
	
	/*Esta funci�n es utilizada para saber si un subscriptor
	 * debe subscriptor o desubscriptor
	 * Se utiliza el nodo actual Mynode
	 * El nodo al que se va a enviar sendNode
	 * y el topico al cual me subscribir�/desubscribir�
	 * */
	public Message subscribeDesubscribRequestUpdate(Node myNode, Node sendNode, int subcriberTopic, int cantTopic, int rand){
		Message msg;
		int decision = CommonState.r.nextInt(2);
		decision = CommonState.r.nextInt(2);
		if(((NodePS) myNode).getTopicSub().size() > 0 && decision == 0){
			subcriberTopic = CommonState.r.nextInt(((NodePS) myNode).getTopicSub().size());
			msg = ((NodePS) myNode).deregisterSub(subcriberTopic, sendNode, cantTopic);
		}
		else{ //Puedo subscribirme o pedir una actualizaci�n
			if(((NodePS) myNode).getTopicSub().contains((Object) subcriberTopic)){
				//mensaje de actualizaci�n
				System.out.println("Solicito una actualizaci�n del t�pico "+subcriberTopic);
				String content = "Solicito una actualizaci�n del t�pico "+subcriberTopic;
				msg = ((NodePS) myNode).requestUpdate((int) myNode.getID(), subcriberTopic, (int)sendNode.getID(), content);
			}
			else{
				System.out.println("\tMe subscribir� al topico "+subcriberTopic);
				msg = ((NodePS) myNode).registerSub(subcriberTopic, sendNode,  cantTopic);
			}
		}
		msg.setTipoDeMensaje(1);
		return msg;
	}
	
	
	/*
	 * Cuando yo no soy el destino puedo enviar mensajes
	 * el mensaje es el mensaje a reenviar
	 * el nodo corresponde al nodo que reenviar� el mensaje*/
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
			//System.out.println("\tVoy a publicar en el t�pico "+mensajePublicador.getIdTopico()+". Le enviar� el mensaje a "+ mensajePublicador.getDestination()); //Obtengo el nodo
			if(message.getTtl() > 0) sendmessage(myNode,layerId,message);
		}
	}
}
