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
		/**Este metodo trabajar� sobre el mensaje*/
		/* En este caso es posible que existan varios mensajes dependiendo del remitente, es decir, si es un mensaje proveniente de un nodo actuando como
		 * t�pico, de un nodo actuando como un publisher o de un nodo actuando como un subscriber, en cuyo caso es necesario
		 * realizar un tratamiento diferente para cada tipo de nodo */	
		Message message = (Message) event;
		int messageValue = ((NodePS) myNode).getIdNode();
				
		message.setValue(messageValue);
		sendmessage(myNode, layerId, message);
		getStats();
	}

	private void getStats() {
		Observer.message.add(1);
	}

	public void sendmessage(Node currentNode, int layerId, Object message) {
		/**Con este m�todo se enviar� el mensaje de un nodo a otro
		 * CurrentNode, es el nodo actual
		 * message, es el mensaje que viene como objeto, por lo cual se debe trabajar sobre �l
		 */
		
		/**----EJEMPLO 1:----*/
		/**
		 * Para esto se debe descomentar el archivo de configuraci�n init.0rndlink.undir false
		 * Con la funci�n degree, se puede conseguir la cantidad de vecinos que posee un nodo*/
		//int randDegree = CommonState.r.nextInt(((Linkable) currentNode.getProtocol(0)).degree());
		/**Se selecciona un nodo destino a trav�s de este random*/
		//Node sendNode = ((Linkable) currentNode.getProtocol(0)).getNeighbor(randDegree);
		/**Adem�s se pueden mostrar los vecinos que posee el nodo actual*/
		//System.out.println("CurrentNode: " + currentNode.getID() + " | Degree: " + ((Linkable) currentNode.getProtocol(0)).degree());
		//for (int i = 0; i < ((Linkable) currentNode.getProtocol(0)).degree(); i++) {
		//	System.out.println("NeighborNode: "
		//			+ ((Linkable) currentNode.getProtocol(0)).getNeighbor(i).getID());
		//}
		
		/**---EJEMPLO 2: ----*/
		
		int randSize = CommonState.r.nextInt(Network.size());
		Node sendNode = Network.get(randSize);
		
		/*Antes de enviar al siguiente nodo asignamos el valor del mensaje al nodo (por hacer algo)*/
		int value = ((Message) message).getValue();
		NodePS actual = (NodePS) currentNode;
		actual.setIdNode(value);
		//System.out.println("\tNodeID: "+(int) actual.getID()+"\tNode Type: "+ actual.getType()+"\tValue: "+ actual.getValue());
		
		
		/**
		 * Env�o del dato a trav�s de la capa de transporte, la cual enviar�
		 * seg�n el ID del emisor y el receptor
		 */	
		((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
		System.out.println("\tNodo "+(int) actual.getID()+" envia mensaje a nodo "+ (int) ((NodePS) sendNode).getID());
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
