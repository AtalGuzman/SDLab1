package cl.usach.sd;

import java.util.ArrayList;

import cl.usach.sd.Message;	
import msg.*;
import peersim.config.Configuration;
import peersim.core.GeneralNode;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
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
	private ArrayList<Integer> registeredTopic; //Tópicos en los que publico
	private ArrayList<Integer> subscribedTopic; //Topicos en los que estoy subscritos
	private ArrayList<Integer> publisherRegistered; //Los publicadores que tengo registrados
	private ArrayList<Integer> subscriberSubscribed; //Los subscriptores que tengo subscritos
	
	public NodePS(String prefix) { 
		super(prefix);
		this.setIdNode(0);
		this.idTopic = -1; //ALgunos cambiarán en la incialización
		//this.setSubscriberSubscribed(new ArrayList<Integer>());
	//	this.setPublisherRegistered(new ArrayList<Integer>());

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

	@Override
	public void setTopicSub(ArrayList<Integer> subscribedTopic) {
		// TODO Auto-generated method stub
		this.subscribedTopic = subscribedTopic;
	}

	@Override
	public ArrayList<Integer> getTopicSub() {
		// TODO Auto-generated method stub
		return this.subscribedTopic;
	}

	public ArrayList<Integer> getSubscriberSubscribed() {
		return subscriberSubscribed;
	}

	public void setSubscriberSubscribed(ArrayList<Integer> subscriberSubscribed) {
		this.subscriberSubscribed = subscriberSubscribed;
	}

	public ArrayList<Integer> getPublisherRegistered() {
		return publisherRegistered;
	}

	public void setPublisherRegistered(ArrayList<Integer> publisherRegistered) {
		this.publisherRegistered = publisherRegistered;
	}

	@Override
	public Boolean registrado(int idNode){
		Boolean regist = false;
		for(int i = 0; i < this.publisherRegistered.size();i++){
			if(this.publisherRegistered.get(i) == idNode){
				regist = true;
			}
		}
		return regist;
	}

	@Override
	public ArrayList<TopicMsg> Publish(ArrayList<Integer> idSubscriber, int remitent, int idTopic) {
		ArrayList<TopicMsg> mensajesPorEnviar = new ArrayList<TopicMsg>();
		String content = "Se ha generado un post en el topico "+this.idTopic;
		for(int i = 0; i < this.subscriberSubscribed.size();i++){
			TopicMsg notificacion = new TopicMsg(remitent,subscriberSubscribed.get(i),content,2,idTopic);
			mensajesPorEnviar.add(notificacion);
		}
		return mensajesPorEnviar;
	}

	public void flooding(int layerId,int idTopic){
		int cantidadVecinos = ((Linkable) this.getProtocol(0)).degree();
		for(int i = 0; i<cantidadVecinos; i++){
			String content = "Soy el nodo "+this.getID()+" y haré un post en el tópico 0.";		
			int initMsg = ((NodePS) this).getIdNode();
			Node sendNode = Network.get((int) ((Linkable) this.getProtocol(0)).getNeighbor(i).getID());
			Message message = new PubMsg(initMsg, (int) sendNode.getID(),content, 3,idTopic); //El tipo 3 es especial, es el primer mensaje
			message.setIntermediario(false);
			EDSimulator.add(0, message, this, layerId);
		}
	}
/*	
	//Esta función es utilizada para la comunicación general, en caso de que yo no sea el destinatario	
	public void flooding(int layerId){
		int cantidadVecinos = ((Linkable) this.getProtocol(0)).degree();
		for(int i = 0; i<cantidadVecinos; i++){
			String content = "Soy el nodo "+this.getID()+" y haré un post en el tópico 0.";		
			int initMsg = ((NodePS) this).getIdNode();
			Node sendNode = Network.get((int) ((Linkable) this.getProtocol(0)).getNeighbor(i).getID());
			Message message = new TopicMsg(initMsg, (int) sendNode.getID(),content, 3); //Notar que se crear como un mensaje de topico 2
			EDSimulator.add(0, message, this, layerId);
		}
	}*/

	@Override
	public void register(int idPublisher) {
		this.getPublisherRegistered().add(idPublisher);
	}
}
