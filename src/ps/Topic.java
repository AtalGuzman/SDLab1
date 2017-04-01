package ps;

import cl.usach.sd.NodePS;

import java.util.ArrayList;

import ps.Subscriber;

public class Topic extends NodePS{
	
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
}
