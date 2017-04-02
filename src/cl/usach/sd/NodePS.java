package cl.usach.sd;

import peersim.core.GeneralNode;

/*
 * Este simulador esta basado en el ejemplo entregado por el ayudante
 * Maximiliano Pérez y el profesor Daniwl Wladdimiro, en el cual explica el proceso de inicialización de los nodos,
 * el manejo de mensajes y el tráfico de los nodos.
 */

public class NodePS extends GeneralNode {
	/*Dado que estamos en un Red P2P, es posible
	 * que un nodo pueda ser cualquiera de los tres parcipantes
	 * el patrón de diseño publisher-subscriber, es decir,
	 * un nodo puede ser tanto un publisher, como un subscriber,
	 * como un tópico.	 */
	private int idNode; //IdNode es utilizado como un identificador del nodo
	
	public NodePS(String prefix) { 
		super(prefix);
		this.setIdNode(0);
	}

	public int getIdNode() {
		return idNode;
	}

	public void setIdNode(int idNode) {
		this.idNode = idNode;
	}
	
	
}
