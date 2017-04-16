package ps;

import java.util.ArrayList;
import ps.Topic;
import cl.usach.sd.Message;
import cl.usach.sd.NodePS;
import msg.SubMsg;
import peersim.core.Node;

public interface Subscriber{

	/*Funciones descritas en NodePS*/
	public String toString();
	
	public void setTopicSub(ArrayList<Integer> subscribedTopic);
	
	public ArrayList<Integer> getTopicSub();
	
	public SubMsg registerSub(int idTopic, int sendNode, String content);
	
	public Message registerSub(int subcriberTopic,Node sendNode, int cantTopic);
	
	public SubMsg deregisterSub(int idTopic, int sendNode, String content); 
	
	public SubMsg requestUpdate(int idEnviar, int idTopico, int destinatario, String content);
	
	}
