package ps;
import java.util.ArrayList;

import cl.usach.sd.Message;
import msg.*;
import peersim.core.Node;

public interface Publisher{
	
	public Message registerPublisher(Node sendNode, int publicationTopic);
	
	public PubMsg publish(int idEnviar, int idTopico, int destinatario, String content, int type);
	
	public Message deletePublication(Node sendNode, int publicationTopic);
	
	public Message deregisterPublisher(Node sendNode, int publicationTopic);

	public String toString();

	public ArrayList<Integer> getRegisteredTopic();

	public void setRegisteredTopic(ArrayList<Integer> registeredTopic);
	
}
