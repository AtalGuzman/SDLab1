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
		
		System.out.println("***** LAYER *****");
		System.out.println("\tSe procesa un evento");
		Message message = (Message) event;
		System.out.println("\tSoy el nodo "+myNode.getID());
		if(message.getDestination() == myNode.getID()){
			/*if(message.getTipoDeMensaje() == 3){					
					System.out.println("\tLO RECIBO COMO TOPICO DESDE EL NODO "+message.getRemitent());
					int topicId = ((NodePS) myNode).getTopic();
					
					if(topicId >=0){
						System.out.println("\tSí soy un tópico");
						if(topicId == ((PubMsg) message).getIdTopico()){
							System.out.println("\tSoy el tópico que buscaban");
							//Message answer = new TopicMsg((int) myNode.getID(), , String content, int tipoDeMensaje);
							//Revisar si el nodo que me lo mandó está registrado
							if(((NodePS) myNode).registrado(message.getRemitent())){
								//Send message
								System.out.println("Enviaré un mensaje a todos los  subscriptores");
								((NodePS) myNode).Publish( ((NodePS) myNode).getSubscriberSubscribed(), (int) myNode.getID());
							}
							else{
								//registrar
								((NodePS) myNode).register(message.getRemitent());
								System.out.println("El nodo no está registrado en esta tópico, debe ser registrado");
							}
						} 
						else {
							System.out.println("\tNo soy el tópico que buscaban u-u");
								//reenviar el mismo mensaje, pero a uno de mis vecinos de modo random
							NodePS original = (NodePS) Network.get(message.getRemitent());
							int cantidadVecinos = ((Linkable) myNode.getProtocol(0)).degree();
							int rand = CommonState.r.nextInt(cantidadVecinos);
							System.out.println("Reenviaré el mensaje a "+rand);
							Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
									((Transport) myNode.getProtocol(transportId)).send(original,  
											sendNode, message, layerId);
							}
					} 
					else {
						System.out.println("\tNo se posee un tópico.");
						NodePS original = (NodePS) Network.get(message.getRemitent());
						int cantidadVecinos = ((Linkable) myNode.getProtocol(0)).degree();
						int rand = CommonState.r.nextInt(cantidadVecinos);
						System.out.println("Reenviaré el mensaje a "+rand);
						Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
								((Transport) myNode.getProtocol(transportId)).send(original,  
										sendNode, message, layerId);
					}
				}*/
			if(message.getTipoDeMensaje() == 0 || message.getTipoDeMensaje() == 3){ //Me lo envió un publicador
				
				System.out.println("\tLO RECIBO COMO TOPICO DESDE EL NODO "+message.getRemitent());
				int topicId = ((NodePS) myNode).getTopic();
				int idRemitent = message.getRemitent();
				if(topicId >=0){
					System.out.println("\tSí tengo un tópico");
					if(topicId == ((PubMsg) message).getIdTopico()){
						System.out.println("\tSoy el tópico que buscaban");
						//Message answer = new TopicMsg((int) myNode.getID(), , String content, int tipoDeMensaje);
						//Revisar si el nodo que me lo mandó está registrado
						((NodePS)Network.get(idRemitent)).getRegisteredTopic();
						if(((NodePS) myNode).registrado(idRemitent)){
							//Send message
							System.out.println("\tEnviaré un mensaje a todos los  subscriptores");
							ArrayList<Message> notificaciones = ((NodePS) myNode).Publish( ((NodePS) myNode).getSubscriberSubscribed(), (int) myNode.getID());
							for(Message msj: notificaciones){
								sendmessage(myNode, layerId, msj);
							} 
							System.out.println("\tLos mensajes han sido despachados");
							//ITerar y enviar
						}
						else{
							//registrar
							System.out.println("\tEl nodo no está registrado en esta tópico, Está siendo registrado");
							((NodePS) myNode).register(message.getRemitent());
							
						}
					} 
					else {
						System.out.println("\tNo soy el tópico que buscaban u-u");
							//reenviar el mismo mensaje, pero a uno de mis vecinos de modo random
						NodePS original = (NodePS) Network.get(message.getRemitent());
						int cantidadVecinos = ((Linkable) myNode.getProtocol(0)).degree();
						int rand = CommonState.r.nextInt(cantidadVecinos);
						System.out.println("Reenviaré el mensaje a "+rand);
						message.setTipoDeMensaje(0);
						Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
						message.setDestination( (int) sendNode.getID());
						((Transport) myNode.getProtocol(transportId)).send(original,sendNode, message, layerId);
						}
				} 
				else {
					System.out.println("\tNo se posee un tópico.");
					NodePS original = (NodePS) Network.get(message.getRemitent());
					int cantidadVecinos = ((Linkable) myNode.getProtocol(0)).degree();
					int rand = CommonState.r.nextInt(cantidadVecinos);
					System.out.println("Reenviaré el mensaje a "+rand);
					message.setTipoDeMensaje(0);
					message.setIntermediario(true);
					Node sendNode = Network.get((int) ((Linkable) myNode.getProtocol(0)).getNeighbor(rand).getID());
					message.setDestination( (int) sendNode.getID());
					((Transport) myNode.getProtocol(transportId)).send(original, sendNode, message, layerId);
				}
			}
			else if(message.getTipoDeMensaje()==1){ //Me lo envió un subscriber
				System.out.println("\tLO RECIBO COMO TOPICO");
			}
			else if(message.getTipoDeMensaje() == 2){ //Me lo envi{o un topico
				System.out.println("\tME COMPORTO COMO SUB");
				System.out.println("\tHe recibido el siguiente mensaje: \n\t\t"+message.getContent());
			}
			
		}
		else{
			/*ENVIARÉ UN MENSAJE*/
			if(message.getTipoDeMensaje() == 3){ //MePrimer mensaje
				 //En este caso el destino es algún tópico
				PubMsg mensajePublicador = (PubMsg) message;
				System.out.println("\tVoy a publicar en el tópico "+mensajePublicador.getIdTopico()+". Le enviaré el mensaje a "+ mensajePublicador.getDestination()); //Obtengo el nodo
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
					System.out.println("\tVoy a publicar en el tópico "+mensajePublicador.getIdTopico()+". Le enviaré el mensaje a "+ mensajePublicador.getDestination()); //Obtengo el nodo
					sendmessage(myNode,layerId,message);
				}
			}
			else if(message.getTipoDeMensaje()==1){ //Me debo comportar como un subscriber
				System.out.println("\tQuiero realizar un update");
			}
			else if(message.getTipoDeMensaje() == 2){ //Me debo compotar como un topico
				System.out.println("\tInformaré a mis subscriptores");
			}
			
		} //El nodo inicial empieza a reenviar los datos, entonces hay que buscar la forma de que envíe el dato una pura vez
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
			//System.out.println("\tSe envía un mensaje al nodo "+sendNode);
			//System.out.println("\t\tTiene el tópico "+ ((NodePS)((Linkable) currentNode.getProtocol(0)).getNeighbor(i)).getTopic());


		/**
		 * Envió del dato a través de la capa de transporte, la cual enviará
		 * según el ID del emisor y el receptor
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
}
