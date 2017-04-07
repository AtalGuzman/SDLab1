package ps;
import java.util.ArrayList;
import msg.*;

public interface Publisher{
	
	public void registerPublisher(int idNode, int topic);
	
	public PubMsg publish(int idEnviar, int idTopico, int destinatario, String content, int type);
	
	public void deletePublication(int idEnviar, int idTopico, int destinatario, int type);
	
	public void deregisterPublisher(int topic);

	public String toString();

	public ArrayList<Integer> getRegisteredTopic();

	public void setRegisteredTopic(ArrayList<Integer> registeredTopic);
	
}
