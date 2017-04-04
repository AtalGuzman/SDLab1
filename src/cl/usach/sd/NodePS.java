package cl.usach.sd;

import java.util.ArrayList;

import msg.PubMsg;
import peersim.core.GeneralNode;
import ps.*;
import java.util.ArrayList;
/*
 * Este simulador esta basado en el ejemplo entregado por el ayudante
 * Maximiliano Pérez y el profesor Daniwl Wladdimiro, en el cual explica el proceso de inicialización de los nodos,
 * el manejo de mensajes y el tráfico de los nodos.
 */

public class NodePS extends GeneralNode implements Publisher,Subscriber,Topic{
	/*Dado que estamos en un Red P2P, es posible
	 * que un nodo pueda ser cualquiera de los tres parcipantes
	 * el patrón de diseño publisher-subscriber, es decir,
	 * un nodo puede ser tanto un publisher, como un subscriber,
	 * como un tópico.	 */
	private int idNode; //IdNode es utilizado como un identificador del nodo
	private int idTopic;
	private ArrayList<Integer> registeredTopic;
	
	public NodePS(String prefix) { 
		super(prefix);
		this.setIdNode(0);
		this.idTopic = -1; //ALgunos cambiarán en la incialización
	}

	public int getIdNode() {
		return idNode;
	}

	public void setIdNode(int idNode) {
		this.idNode = idNode;
	}

	@Override
	public void publish(String publisher) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTopic() {
		return this.idTopic;
	}

	@Override
	public void setTopic(int topic) {
		this.idTopic = topic;
	}

	@Override
	public void registerPublisher(int idNode, int topic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PubMsg publish(int idTopico, int destinatario) {
		
		return null;
	}

	@Override
	public void deletePublication(String topic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deregisterPublisher(String topic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Integer> getRegisteredTopic() {
		// TODO Auto-generated method stub
		return this.registeredTopic;
	}

	@Override
	public void setRegisteredTopic(ArrayList<Integer> registeredTopic) {
		this.registeredTopic = registeredTopic;
	}
	
	
}
