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
 * Maximiliano P�rez y el profesor Daniwl Wladdimiro, en el cual explica el proceso de inicializaci�n de los nodos,
 * el manejo de mensajes y el tr�fico de los nodos.
 */

public class NodePS extends GeneralNode implements Publisher,Subscriber,Topic{
	/*Dado que estamos en un Red P2P, es posible
	 * que un nodo pueda ser cualquiera de los tres parcipantes
	 * el patr�n de dise�o publisher-subscriber, es decir,
	 * un nodo puede ser tanto un publisher, como un subscriber,
	 * como un t�pico.	 
	 * */
	//private int idNode; //IdNode es utilizado como un identificador del nodo
	private int idTopic;
	private ArrayList<Integer> registeredTopic; //T�picos en los que publico
	private ArrayList<Integer> subscribedTopic; //Topicos en los que estoy subscritos
	private ArrayList<Integer> publisherRegistered; //Los publicadores que tengo registrados
	private ArrayList<Integer> subscriberSubscribed; //Los subscriptores que tengo subscritos
	private int cantPublication = 0;
	
	/*Constructor de un nodo, notar que en un principio 
	 * uno nodo no posee ning�n t�pico y por lo tanto
	 * es inicializado con -1
	 *  */
	public NodePS(String prefix) { 
		super(prefix);
		this.idTopic = -1; 

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
	/* Funci�n utilizada para registrar un publicador
	 * sendNode, es el nodo que debe registrar
	 * publicationTopic es el t�pico en el cual ser� registrado
	 * */
	public Message registerPublisher(Node sendNode, int publicationTopic) {
		this.getRegisteredTopic().add(publicationTopic);
		System.out.println("\tMe registrar� al t�pico "+publicationTopic+" (como publicador)");
		Message msg = new PubMsg((int) this.getID(),(int)sendNode.getID(),"Me quiero registrar al t�pico"+publicationTopic,0, publicationTopic);
		msg.setAccion(0);
		return msg;
		}

	@Override
	/* Funci�n necesaria para realizar las publicaciones
	 * se toma la id del nodo que va a enviar la publicaci�n idEnviar
	 * la id del nodo de destino destinatario
	 * la id del t�pico en el cual se publica
	 * el contenido del mensaje content
	 * y type que es el tipo de mensaje
	 * */
	public PubMsg publish(int idEnviar, int idTopico, int destinatario, String content, int type) {
		PubMsg message = new PubMsg(idEnviar, destinatario,content, type, idTopic);
		return message;
	}

	/* Funci�n alternativa para la publicaci�n
	 * Se utiliza el nodo que recibir� el nodo sendNode
	 * y el t�pico donde se va a publicar publicationTopic
	 * */
	public Message publish(Node sendNode, int publicationTopic){
		System.out.println("\t Publicar� en un t�pico");
		System.out.println("\t Publicar� en el t�pico "+publicationTopic);
		String content = "He realizado una publicaci�n en el t�pico "+publicationTopic;
		this.cantPublication = this.cantPublication+1;
		Message msg = ((NodePS) this).publish((int)this.getID(),publicationTopic, (int)((NodePS)sendNode).getID(), content, 0);
		return msg;
	}
	
	@Override
	/* Funci�n para eliminar una publicaci�n
	 * es casi igual a la funci�n anterior,
	 * pero disminuye la cantidad de publicaciones
	 * */
	public Message deletePublication(Node sendNode, int publicationTopic){
		System.out.println("\t Publicar� en un t�pico");
		System.out.println("\t Publicar� en el t�pico "+publicationTopic);
		String content = "He realizado una publicaci�n en el t�pico "+publicationTopic;
		this.cantPublication = this.cantPublication-1;
		Message msg = ((NodePS) this).publish((int)this.getID(),publicationTopic, (int)((NodePS)sendNode).getID(), content, 0);
		return msg;
	}

	@Override
	/* Funci�n para desregistrar a un nodo
	 * sendNode, corresponde al nodo a enviar el mensaje
	 * publicationTopic, es el t�pico a registrarse
	 * */
	public Message deregisterPublisher(Node sendNode, int publicationTopic){
		this.getRegisteredTopic().remove((Object) publicationTopic);
		System.out.println("\tMe quiero desregistrar de t�pico "+publicationTopic+" (como publicador)");
		Message msg = new PubMsg((int) this.getID(),(int)sendNode.getID(),"Me quiero registrar al t�pico"+publicationTopic,0, publicationTopic);
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
	/* Funci�n para saber si est� registrado el nodo
	 * idNode, es la id del nodo a revisar si est� registrado
	 * */
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
	/* Funci�n publish para el t�pico
	 * genera los mensajes para hacer notificaciones
	 * o para solicitar actualizaciones
	 * idSubcriber, es la lista de nodos a enviar
	 * idTopic, el t�pico a enviar
	 * remitent, el que env�a el mensaje
	 * content, contenido del mensaje
	 * */
	public ArrayList<TopicMsg> Publish(ArrayList<Integer> idSubscriber, int remitent, int idTopic,String content) {
		ArrayList<TopicMsg> mensajesPorEnviar = new ArrayList<TopicMsg>();
		for(int i = 0; i < this.subscriberSubscribed.size();i++){
			TopicMsg notificacion = new TopicMsg(remitent,subscriberSubscribed.get(i),content,2,idTopic);
			mensajesPorEnviar.add(notificacion);
		}
		return mensajesPorEnviar;
	}

	/* Funci�n utilizada para iniciar el tr�fico de mensajes
	 * contiene el layerId, la capa de la transmisi�n
	 * idTopic, t�pico donde se enviar�
	 * type, tipo de mensaje a enviar
	 * */
	public void flooding(int layerId,int idTopic, int type){
		int cantidadVecinos = ((Linkable) this.getProtocol(0)).degree();
		for(int i = 0; i<cantidadVecinos; i++){
			String content = "Soy el nodo "+this.getID()+" y har� un post en el t�pico "+idTopic;		
			int initMsg = (int) ((NodePS) this).getID();
			Node sendNode = Network.get((int) ((Linkable) this.getProtocol(0)).getNeighbor(i).getID());		
			Message message = new PubMsg(initMsg, (int)sendNode.getID(),content, type, idTopic); //El tipo 3 es especial, es el primer mensaje
			message.setIntermediario(false);
			message.setAccion(2);
			EDSimulator.add(0, message, this, layerId);
		}
	}
	
	@Override
	/* registrar un publicador a un t�pico
	 * necesita la id del publicador a registrar idPublisher
	 * */
	public void register(int idPublisher) {
		this.getPublisherRegistered().add(idPublisher);
	}

	@Override
	/* Funci�n para subscribir a un t�pico, este genera un mensaje
	 * al t�pico para que lo subscriba
	 * utiliza la id del t�pico, idTopic
	 * el nodo al cual se enviar� el mensaje, sendNode
	 * y el contenido del mensaje, content
	 * */
	public SubMsg registerSub(int idTopic, int sendNode, String content) {
		this.getTopicSub().add(idTopic);
		SubMsg msg = new SubMsg((int) this.getID(), sendNode, idTopic, content,1);
		msg.setAccion(0);
		return msg;		
	}
	
	/* Funci�n que revisa si se est� subscrito o no a cieto t�pico
	 * y luego crea el mensaje
	 * recibe el t�pico a subscribir, subcriberTopic
	 * el nodo a enviar, sendNode
	 * cantTopic, la cantidad de t�picos de la red
	 * */
	public Message registerSub(int subcriberTopic,Node sendNode, int cantTopic){
		while(this.getTopicSub().contains(subcriberTopic)){
			subcriberTopic = CommonState.r.nextInt(cantTopic);
		}
		System.out.println("\tSolicito subscribirme al t�pico "+subcriberTopic); //Puede provocar print en el inicio, ya que si no 
		String content = "Solicito subscribirme al t�pico "+subcriberTopic;	//est� subscrito a nada por defecto se subscribe a alg�n t�pico aleatorio
		Message msg = this.registerSub(subcriberTopic, (int)((NodePS)sendNode).getID(), content);
		msg.setAccion(0);
		return msg;
	}
	
	@Override
	/* Genera un mensaje de eliminar subscripci�n
	 * recibe la id del t�pico, idTopic
	 * el nodo a enviar el mensaje sendNode
	 * y el contenido del mensaje
	 * */
	public SubMsg deregisterSub(int idTopic, int sendNode, String content) {
		this.getTopicSub().remove((Object) idTopic);
		SubMsg msg = new SubMsg((int) this.getID(), sendNode, idTopic, content,1);
		msg.setAccion(1);
		return msg;		
	}
	
	/* Funci�n para revisar si el t�pico a desubscribir me tiene subscrito
	 * y entonces crear el mensaje de eliminaci�n de subscripci�n
	 * subcriberTopic, es el t�pico a desubcribirme
	 * sendNOde, nodo a envair el mensaje
	 * cantTopic, la cantidad de t�picos en la red
	 * */
	public Message deregisterSub(int subcriberTopic,Node sendNode, int cantTopic){
		while(!this.getTopicSub().contains(subcriberTopic)){
			subcriberTopic = CommonState.r.nextInt(cantTopic);
		}
		System.out.println("\tSolicito desubscribirme del t�pico "+subcriberTopic);
		String content = "Solicito desubscribirme del t�pico "+subcriberTopic;
		Message msg = this.deregisterSub(subcriberTopic, (int)((NodePS)sendNode).getID(), content);
		msg.setAccion(1);
		return msg;
	}
	
	/* Funci�n utilizada por un subscriptor para solicitar una actualizaci�n a un t�pico
	 * idEnviar, nodo remitente del mensaje
	 * destinatario, nodo que va a recibir el mensaje
	 * idTopic, id del t�pico a que solicito mi mensaje
	 * content, contenido del mensaje
	 * */
	public SubMsg requestUpdate(int idEnviar, int idTopico, int destinatario, String content){
		SubMsg message = new SubMsg(idEnviar, destinatario,idTopico, content,1);
		message.setAccion(2);
		return message;
	}
	
	public int getCantPublication() {
		return cantPublication;
	}

	public void setCantPublication(int cantPublication) {
		this.cantPublication = cantPublication;
	}
}
