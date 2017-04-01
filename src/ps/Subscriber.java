package ps;

import java.util.ArrayList;
import ps.Topic;
import cl.usach.sd.NodePS;

public class Subscriber extends NodePS	{

	public Subscriber(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}

	public String toString() {
		return "Subscriber [id=" + this.getIdNode() + "] Node "+ this.getIdNode()+"\n";
	}
	
	
}
