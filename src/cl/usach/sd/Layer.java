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
		
		System.out.println("***** LAYER *****");
		System.out.println("\tSe procesa un evento");
		Message message = (Message) event;
		System.out.println("\tSoy el nodo "+myNode.getID());
		if(message.getDestination() == myNode.getID()){
			/*if(message.getTipoDeMensaje() == 3){					
					System.out.println("\tLO RECIBO COMO TOPICO DESDE EL NODO "+message.getRemitent());
					int topicId = ((NodePS) myNode).getTopic();
					
					if(topicId >=0){
						System.out.println("\tS� soy un t�pico");
						if(topicId == ((PubMsg) message).getIdTopico()){
							System.out.println("\tSoy el t�pico que buscaban");
							//Message answer = new TopicMsg((int) myNode.getID(), , String content, int tipoDeMensaje);
							//Revisar si el nodo que me lo mand� est� registrado
							if(((NodePS) myNode).registrado(message.getRemitent())){
								//Send message
								System.out.println("Enviar� un mensaje a todos los  subscriptores");
								((NodePS) myNode).Publish( ((NodePS) myNode).getSubscriberSubscribed(), (int) myNode.getID());
							}
							else{
								//registrar
								((NodePS) myNode).register(message.getRemitent());
								System.out.println("El nodo no est� registrado en esta t�pico, debe ser registrado");
							}
						} 
						else {
							System.out.println("\tNo soy el t�pico que buscaban u-u");
								//reenviar el mismo mensaje, pero a uno de mis vecinos de modo random
							NodePS original = (NodePS) Network.get(message.getRemitent());
							int cantidadVecinos = ((Linkable) myNode.getProtocol(0)).degree();
							int rand = CommonState.r.nextInt(cantidadVecinos);
							System.out.println("Reenviar� el mensaje a "+rand);
							Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
									((Transport) myNode.getProtocol(transportId)).send(original,  
											sendNode, message, layerId);
							}
					} 
					else {
						System.out.println("\tNo se posee un t�pico.");
						NodePS original = (NodePS) Network.get(message.getRemitent());
						int cantidadVecinos = ((Linkable) myNode.getProtocol(0)).degree();
						int rand = CommonState.r.nextInt(cantidadVecinos);
						System.out.println("Reenviar� el mensaje a "+rand);
						Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
								((Transport) myNode.getProtocol(transportId)).send(original,  
										sendNode, message, layerId);
					}
				}*/
			if(message.getTipoDeMensaje() == 0 || message.getTipoDeMensaje() == 3){ //Me lo envi� un publicador
				
				System.out.println("\tLO RECIBO COMO TOPICO DESDE EL NODO "+message.getRemitent());
				int topicId = ((NodePS) myNode).getTopic();
				int idRemitent = message.getRemitent();
				if(topicId >=0){
					System.out.println("\tS� tengo un t�pico");
					if(topicId == ((PubMsg) message).getIdTopico()){
						System.out.println("\tSoy el t�pico que buscaban");
						//Message answer = new TopicMsg((int) myNode.getID(), , String content, int tipoDeMensaje);
						//Revisar si el nodo que me lo mand� est� registrado
						((NodePS)Network.get(idRemitent)).getRegisteredTopic();
						if(((NodePS) myNode).registrado(idRemitent)){
							//Send message
							System.out.println("\tEnviar� un mensaje a todos los  subscriptores");
							ArrayList<Message> notificaciones = ((NodePS) myNode).Publish( ((NodePS) myNode).getSubscriberSubscribed(), (int) myNode.getID());
							for(Message msj: notificaciones){
								sendmessage(myNode, layerId, msj);
							} 
							System.out.println("\tLos mensajes han sido despachados");
							//ITerar y enviar
						}
						else{
							//registrar
							System.out.println("\tEl nodo no est� registrado en esta t�pico, Est� siendo registrado");
							((NodePS) myNode).register(message.getRemitent());
							
						}
					} 
					else {
						System.out.println("\tNo soy el t�pico que buscaban u-u");
							//reenviar el mismo mensaje, pero a uno de mis vecinos de modo random
						NodePS original = (NodePS) Network.get(message.getRemitent());
						int cantidadVecinos = ((Linkable) myNode.getProtocol(0)).degree();
						int rand = CommonState.r.nextInt(cantidadVecinos);
						System.out.println("Reenviar� el mensaje a "+rand);
						message.setTipoDeMensaje(0);
						Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
						message.setDestination( (int) sendNode.getID());
						((Transport) myNode.getProtocol(transportId)).send(original,sendNode, message, layerId);
						}
				} 
				else {
					System.out.println("\tNo se posee un t�pico.");
					NodePS original = (NodePS) Network.get(message.getRemitent());
					int cantidadVecinos = ((Linkable) myNode.getProtocol(0)).degree();
					int rand = CommonState.r.nextInt(cantidadVecinos);
					System.out.println("Reenviar� el mensaje a "+rand);
					message.setTipoDeMensaje(0);
					message.setIntermediario(true);
					Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
					message.setDestination( (int) sendNode.getID());
					((Transport) myNode.getProtocol(transportId)).send(original, sendNode, message, layerId);
				}
			}
			else if(message.getTipoDeMensaje()==1){ //Me lo envi� un subscriber
				System.out.println("\tLO RECIBO COMO TOPICO");
			}
			else if(message.getTipoDeMensaje() == 2){ //Me lo envi{o un topico
				System.out.println("\tME COMPORTO COMO SUB");
				System.out.println("\tHe recibido el siguiente mensaje: \n\t\t"+message.getContent());
			}
			
		}
		else{
			/*ENVIAR� UN MENSAJE*/
			if(message.getTipoDeMensaje() == 3){ //MePrimer mensaje
				 //En este caso el destino es alg�n t�pico
				PubMsg mensajePublicador = (PubMsg) message;
				System.out.println("\tVoy a publicar en el t�pico "+mensajePublicador.getIdTopico()+". Le enviar� el mensaje a "+ mensajePublicador.getDestination()); //Obtengo el nodo
				sendmessage(myNode,layerId,message);
			}
			else if(message.getTipoDeMensaje() == 0){
				//me debo comportar como un publicador supongo ...
				if(message.getIntermediario()){
					System.out.println("\tEstoy enviando como intermediario este mensaje al nodo "+message.getDestination());
					sendmessage(myNode,layerId,message);
				} 
				else{
					PubMsg mensajePublicador = (PubMsg) message;
					System.out.println("\tVoy a publicar en el t�pico "+mensajePublicador.getIdTopico()+". Le enviar� el mensaje a "+ mensajePublicador.getDestination()); //Obtengo el nodo
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
		System.out.println("\tPreparar mensaje");
		/**
		 * Random degree
		 */
		int sendNodeId = ((Message) message).getDestination();
		
		/**
		 * sendNode ID del Nodo que se debe enviar
		 */
		
		System.out.println("\tCurrentNode: " + currentNode.getID() + " | Tengo: " + ((Linkable) currentNode.getProtocol(0)).degree()+" vecinos");
		
		((Transport) currentNode.getProtocol(transportId)).send(currentNode,  Network.get(sendNodeId), message, layerId);
			//System.out.println("\tSe env�a un mensaje al nodo "+sendNode);
			//System.out.println("\t\tTiene el t�pico "+ ((NodePS)((Linkable) currentNode.getProtocol(0)).getNeighbor(i)).getTopic());


		/**
		 * Envi� del dato a trav�s de la capa de transporte, la cual enviar�
		 * seg�n el ID del emisor y el receptor
		 */
		// Otra forma de hacerlo
		// ((Transport)
		// currentNode.getProtocol(FastConfig.getTransport(layerId))).send(currentNode,
		// searchNode(sendNode), message, layerId);
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
