package ps;
import cl.usach.sd.NodePS;

public class Publisher extends NodePS {
	
	public Publisher(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}

	public void registerPublisher(int idNode, int topic){
		System.out.println("Ahora el nodo "+idNode+"Puedo publicar en el topico "+topic);
		
	}
	
	public void publish(String topic){
		System.out.println("Publiqu� un post en el topico "+topic);
	}
	
	public void deletePublication(String topic){
		System.out.println("Elimino la publicaci�n del t�pico"+topic);
	}
	
	public void deregisterPublisher(String topic){
		System.out.println("No quiero publicar m�s del t�pico"+topic);
	}

	public String toString() {
		return "Publisher [id=" + this.getIdNode() + "] Node "+ this.getIdNode()+"\n";
	}
	
	
}
