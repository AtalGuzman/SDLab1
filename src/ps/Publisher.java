package ps;
import cl.usach.sd.NodePS;
import peersim.core.CommonState;
import cl.usach.sd.Message;
import java.util.ArrayList;

public class Publisher extends NodePS {
	
	private ArrayList<Integer> registeredTopic;
	
	public Publisher(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}

	public void registerPublisher(int idNode, int topic){
		System.out.println("Ahora el nodo "+idNode+"Puedo publicar en el topico "+topic);
		
	}
	
	public Message publish(int idTopico){
			Message msj = new Message((int) this.getID(), idTopico, "He publicado a un tópico",0);
			return msj;
	}
	
	public void deletePublication(String topic){
		System.out.println("Elimino la publicación del tópico"+topic);
	}
	
	public void deregisterPublisher(String topic){
		System.out.println("No quiero publicar más del tópico"+topic);
	}

	public String toString() {
		return "Publisher [id=" + this.getIdNode() + "] Node "+ this.getIdNode()+"\n";
	}

	public ArrayList<Integer> getRegisteredTopic() {
		return registeredTopic;
	}

	public void setRegisteredTopic(ArrayList<Integer> registeredTopic) {
		this.registeredTopic = registeredTopic;
	}
	
	
}
