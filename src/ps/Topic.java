package ps;

import cl.usach.sd.NodePS;

import java.util.ArrayList;

import ps.Subscriber;

public interface Topic{
	
	public void publish(String publisher);

	public String toString();

	public int getTopic();

	public void setTopic(int topic);
}
