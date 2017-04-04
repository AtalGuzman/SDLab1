package ps;
import cl.usach.sd.NodePS;
import peersim.core.CommonState;
import cl.usach.sd.Message;
import java.util.ArrayList;
import msg.*;

public interface Publisher{
	
	public void registerPublisher(int idNode, int topic);
	
	public PubMsg publish(int idTopico,int destinatario);
	
	public void deletePublication(String topic);
	
	public void deregisterPublisher(String topic);

	public String toString();

	public ArrayList<Integer> getRegisteredTopic();

	public void setRegisteredTopic(ArrayList<Integer> registeredTopic);
	
}
