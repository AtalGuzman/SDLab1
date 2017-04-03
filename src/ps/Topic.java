package ps;

import cl.usach.sd.NodePS;

import java.util.ArrayList;

import ps.Subscriber;

public class Topic extends NodePS{
	
	private int topic;
	public Topic(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}

	public void publish(String publisher){
		System.out.println("El publicador "+publisher+" ha publicado en el topico "+this.getIdNode());
	}

	public String toString() {
		return "Topico [id=" + this.getIdNode() + "] Node "+ this.getIdNode()+"\n";
	}

	public int getTopic() {
		return topic;
	}

	public void setTopic(int topic) {
		this.topic = topic;
	}
}
