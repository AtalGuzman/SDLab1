package cl.usach.sd;

import java.util.ArrayList;

import cl.usach.sd.Message;	
import msg.*;
import peersim.config.Configuration;
import peersim.core.CommonState;
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
	private int cantPublication = 0;
	
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
	public Message registerPublisher(Node sendNode, int publicationTopic) {
		this.getRegisteredTopic().add(publicationTopic);
		System.out.println("\tMe registraré al tópico "+publicationTopic+" (como publicador)");
		Message msg = new PubMsg((int) this.getID(),(int)sendNode.getID(),"Me quiero registrar al tópico"+publicationTopic,0, publicationTopic);
		msg.setAccion(0);
		return msg;
		}

	@Override
	public PubMsg publish(int idEnviar, int idTopico, int destinatario, String content, int type) {
		PubMsg message = new PubMsg(idEnviar, destinatario,content, type, idTopic);
		return message;
	}

	public Message publish(Node sendNode, int publicationTopic){
		System.out.println("\t Publicaré en un tópico");
		System.out.println("\t Publicaré en el tópico "+publicationTopic);
		String content = "He realizado una publicación en el tópico "+publicationTopic;
		this.cantPublication = this.cantPublication+1;
		Message msg = ((NodePS) this).publish((int)this.getID(),publicationTopic, (int)((NodePS)sendNode).getID(), content, 0);
		return msg;
	}
	
	@Override
	public Message deletePublication(Node sendNode, int publicationTopic){
		System.out.println("\t Publicaré en un tópico");
		System.out.println("\t Publicaré en el tópico "+publicationTopic);
		String content = "He realizado una publicación en el tópico "+publicationTopic;
		this.cantPublication = this.cantPublication-1;
		Message msg = ((NodePS) this).publish((int)this.getID(),publicationTopic, (int)((NodePS)sendNode).getID(), content, 0);
		return msg;
	}

	@Override
	public Message deregisterPublisher(Node sendNode, int publicationTopic){
		this.getRegisteredTopic().add(publicationTopic);
		System.out.println("\tMe quiero desregistrar de tópico "+publicationTopic+" (como publicador)");
		Message msg = new PubMsg((int) this.getID(),(int)sendNode.getID(),"Me quiero registrar al tópico"+publicationTopic,0, publicationTopic);
		msg.setAccion(1);
		return msg;	
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
		this.subscribedTopic = subscribedTopic;
	}

	@Override
	public ArrayList<Integer> getTopicSub() {
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
			String content = "Soy el nodo "+this.getID()+" y haré un post en el tópico "+idTopic;		
			int initMsg = (int) ((NodePS) this).getID();
			Node sendNode = Network.get((int) ((Linkable) this.getProtocol(0)).getNeighbor(i).getID());		
			Message message = new PubMsg(initMsg, (int)sendNode.getID(),content, 3, idTopic); //El tipo 3 es especial, es el primer mensaje
			message.setIntermediario(false);
			message.setAccion(2);
			EDSimulator.add(0, message, this, layerId);
		}
	}

	@Override
	public void register(int idPublisher) {
		this.getPublisherRegistered().add(idPublisher);
	}

	@Override
	public SubMsg registerSub(int idTopic, int sendNode, String content) {
		this.getTopicSub().add(idTopic);
		SubMsg msg = new SubMsg((int) this.getID(), sendNode, idTopic, content,1);
		msg.setAccion(0);
		return msg;		
	}
	
	public Message registerSub(int subcriberTopic,Node sendNode, int cantTopic, int rand){
		while(this.getTopicSub().contains(subcriberTopic)){
			subcriberTopic = CommonState.r.nextInt(cantTopic);
		}
		System.out.println("\tSolicito subscribirme al tópico "+subcriberTopic); //Puede provocar print en el inicio, ya que si no 
		String content = "Solicito subscribirme al tópico "+subcriberTopic;	//está subscrito a nada por defecto se subscribe a algún tópico aleatorio
		Message msg = this.registerSub(subcriberTopic, (int)((NodePS)sendNode).getID(), content);
		msg.setAccion(0);
		return msg;
	}
	
	@Override
	public SubMsg deregisterSub(int idTopic, int sendNode, String content) {
		this.getTopicSub().remove((Object) idTopic);
		SubMsg msg = new SubMsg((int) this.getID(), sendNode, idTopic, content,1);
		msg.setAccion(1);
		return msg;		
	}
	
	public Message deregisterSub(int subcriberTopic,Node sendNode, int cantTopic, int rand){
		while(!this.getTopicSub().contains(subcriberTopic)){
			subcriberTopic = CommonState.r.nextInt(cantTopic);
		}
		System.out.println("\tSolicito desubscribirme del tópico "+subcriberTopic);
		String content = "Solicito desubscribirme del tópico "+subcriberTopic;
		Message msg = this.deregisterSub(subcriberTopic, (int)((NodePS)sendNode).getID(), content);
		msg.setAccion(1);
		return msg;
	}
	
	public int getCantPublication() {
		return cantPublication;
	}

	public void setCantPublication(int cantPublication) {
		this.cantPublication = cantPublication;
	}
}
