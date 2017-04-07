package ps;

import java.util.ArrayList;
import ps.Topic;
import cl.usach.sd.NodePS;

public interface Subscriber{

	public String toString();
	
	public void setTopicSub(ArrayList<Integer> subscribedTopic);
	
	public ArrayList<Integer> getTopicSub();
	
	public void registerSub(int idTopic);
}
