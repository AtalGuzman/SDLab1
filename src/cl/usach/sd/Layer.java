package cl.usach.sd;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireKOut;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import cl.usach.sd.Message;

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
		
		System.out.println("*****CAPA LAYER*****");
		System.out.println("\tSe procesa un evento");
		Message message = (Message) event;
		System.out.println("\tSoy el nodo "+myNode.getID());
		if(message.getDestination() == myNode.getID()){
			/*System.out.println("\tHe recibido un mensaje desde el nodo "+message.getRemitent());
			System.out.println("\tEl mensaje dice: "+message.getContent());
			//Preparar mensaje
			Node initNode = Network.get(message.getDestination());
			int rand = CommonState.r.nextInt(((Linkable) initNode.getProtocol(0)).degree());
			Node neighBor = ((Linkable) initNode.getProtocol(0)).getNeighbor(rand);
			if(message.getDestination()%2 == 0){ 
				System.out.println("\tPreparar� mi propio mensaje");
				String content = "Soy el nodo "+myNode.getID()+" y le envi� un mensaje a "+neighBor.getID();
				Message message2 = new Message((int) myNode.getID(),(int) neighBor.getID(),content);
				System.out.println("\tENVIARE UN MENSAJE!!!!!");
				sendmessage(myNode,layerId,message2);
			}
			else{
				System.out.println("No har� nada, porque me amurr� >:c");
			}*/
			/*RECIBI UN MENSAJE*/
			if(message.getTipoDeMensaje() == 0){ //Me lo envi� un publicador
				System.out.println("LO RECIBO COMO TOPICO");
			}
			else if(message.getTipoDeMensaje()==1){ //Me lo envi� un subscriber
				System.out.println("LO RECIBO COMO TOPICO");
			}
			else if(message.getTipoDeMensaje() == 2){ //Me lo envi{o un topico
				System.out.println("ME COMPORTO COMO SUB");
			}
			
		}
		else{
			/*ENVIAR� UN MENSAJE/
			/*System.out.println("\tEnviar� un mensaje a "+message.getDestination());
			sendmessage(myNode, layerId, message);*/
			
			if(message.getTipoDeMensaje() == 0){ //Me debo comportar como un publicador
				System.out.println("Voy a publicar en el t�pico "+message.getDestination()); //En este caso el destino es alg�n t�pico
				
			}
			else if(message.getTipoDeMensaje()==1){ //Me debo comportar como un subscriber
				System.out.println("Quiero realizar un update");
			}
			else if(message.getTipoDeMensaje() == 2){ //Me debo compotar como un topico
				System.out.println("Informar� a mis subscriptores");
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
		Node sendNode = Network.get(sendNodeId);
		
		System.out.println("\tSe enviar� al nodo "+sendNode.getID());
		
		String content = "Hola buenas soy el nodo "+currentNode.getID();
		System.out.println("\tCurrentNode: " + currentNode.getID() + " | Degree: " + ((Linkable) currentNode.getProtocol(0)).degree());
		
		for (int i = 0; i < ((Linkable) currentNode.getProtocol(0)).degree(); i++) {
			System.out.println("\tNeighborNode: "
					+ ((Linkable) currentNode.getProtocol(0)).getNeighbor(i).getID());
		}

		/**
		 * Envi� del dato a trav�s de la capa de transporte, la cual enviar�
		 * seg�n el ID del emisor y el receptor
		 */
		((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
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
