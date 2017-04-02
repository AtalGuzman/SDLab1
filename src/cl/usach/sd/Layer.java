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
	 * Método en el cual se va a procesar el mensaje que ha llegado al Nodo
	 * desde otro Nodo. Cabe destacar que el mensaje va a ser el evento descrito
	 * en la clase, a través de la simulación de eventos discretos.
	 */
	@Override
	public void processEvent(Node myNode, int layerId, Object event) {
		
		System.out.println("*****CAPA LAYER*****");
		System.out.println("\tSe procesa un evento");
		Message message = (Message) event;
		System.out.println("\tSoy el nodo "+myNode.getID());
		if(message.getDestination() == myNode.getID()){
			System.out.println("\tHe recibido un mensaje desde el nodo "+message.getRemitent());
			System.out.println("\tEl mensaje dice: "+message.getContent());
			//sendmessage(myNode, layerId, message);
			//Preparar mensaje
			Node initNode = Network.get(message.getDestination());
			int rand = CommonState.r.nextInt(((Linkable) initNode.getProtocol(0)).degree());
			Node neighBor = ((Linkable) initNode.getProtocol(0)).getNeighbor(rand);
			if(message.getDestination()%2 == 0){ 
				System.out.println("\tPrepararé mi propio mensaje");
				String content = "Soy el nodo "+myNode.getID()+" y le envió un mensaje a "+neighBor.getID();
			/*
			 * La tarde es seda
			 * y los libros son mañana	
			 * me miras te miro
			 * no me importa nada
			 * */
				Message message2 = new Message((int) myNode.getID(),(int) neighBor.getID(),content);
			
				System.out.println("\tENVIARE UN MENSAJE!!!!!");
				sendmessage(myNode,layerId,message2);
			}
			else{
				System.out.println("No haré nada, porque me amurré >:c");
			}
		}
		else{
			System.out.println("\tEnviaré un mensaje a "+message.getDestination());
			sendmessage(myNode, layerId, message);
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
		Node sendNode = Network.get(sendNodeId);
		
		System.out.println("\tSe enviará al nodo "+sendNode.getID());
		
		String content = "Hola buenas soy el nodo "+currentNode.getID();
		System.out.println("\tCurrentNode: " + currentNode.getID() + " | Degree: " + ((Linkable) currentNode.getProtocol(0)).degree());
		
		for (int i = 0; i < ((Linkable) currentNode.getProtocol(0)).degree(); i++) {
			System.out.println("\tNeighborNode: "
					+ ((Linkable) currentNode.getProtocol(0)).getNeighbor(i).getID());
		}

		/**
		 * Envió del dato a través de la capa de transporte, la cual enviará
		 * según el ID del emisor y el receptor
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
