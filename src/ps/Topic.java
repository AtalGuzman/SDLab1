package ps;

import cl.usach.sd.Message;
import cl.usach.sd.NodePS;
import msg.TopicMsg;

import java.util.ArrayList;

import ps.Subscriber;

public interface Topic{
	
	public void publish(String publisher);

	public String toString();

	public int getTopic();

	public void setTopic(int topic);
	
	public ArrayList<Integer> getPublisherRegistered();
	
	public void setPublisherRegistered(ArrayList<Integer> publisherRegistered);

	public Boolean registrado(int idNode);
	
	public ArrayList<TopicMsg> Publish(ArrayList<Integer> idSubscriber, int remitent, int idTopic,String content);
	
	public void register(int idPublisher);
}
