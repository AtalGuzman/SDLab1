package cl.usach.sd;

import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;

public class Observer implements Control {

	private int layerId;
	private String prefix;

	public static IncrementalStats message = new IncrementalStats();

	public Observer(String prefix) {
		this.prefix = prefix;
		this.layerId = Configuration.getPid(prefix + ".protocol");
	}

	@Override
	public boolean execute() {
		int size = Network.size();
		for (int i = 0; i < Network.size(); i++) {
			if (!Network.get(i).isUp()) {
				size--;
			}
		}

		String s = String.format("[time=%d]:[with N=%d nodes] [%d Total send message]", CommonState.getTime(), size,
				(int) message.getSum());
		int topicAtNet = 0;
		for (int i = 0; i < Network.size(); i++) {
			NodePS temp = (NodePS) Network.get(i);
			if(temp.getTopic() != -1){
				topicAtNet++;
				System.err.println("NodeID:"+temp.getID()+" Topic "+temp.getTopic());
			}
		}
		System.err.println("Topics in net "+topicAtNet);		
		System.err.println(s);
		return false;
	}
}
