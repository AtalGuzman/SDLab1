package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

import java.util.ArrayList;

import msg.PubMsg;

public class TrafficGenerator implements Control {
	private final static String PAR_PROT = "protocol";
	private final int layerId;

	public TrafficGenerator(String prefix) {
		layerId = Configuration.getPid(prefix + "." + PAR_PROT);

	}

	@Override
	public boolean execute() {
		//Consideraremos cualquier nodo de manera aleatoria de la red para comenzar y finalizar
		System.out.println("\n***************TRAFFIC GENERATOR**************\n");
		System.out.println("- Se elige un nodo para iniciar el tr�fico.");
		
		Node initNode = Network.get(CommonState.r.nextInt(Network.size())); 
		
		while(((NodePS)initNode).getTopic() <0 ) initNode = Network.get(CommonState.r.nextInt(Network.size())); 
		
		System.out.println("- El nodo elegido corresponde a "+initNode.getID());
		
		//Obtener los vecinos y se les env�a el mensaje de la vida! D:
		int cantidadVecinos = ((Linkable) initNode.getProtocol(0)).degree();
		
		System.out.println("- Tengo "+cantidadVecinos+" vecinos");
		System.out.println("- Se generan los mensajes iniciales, por defecto en el t�pico: "+((NodePS) initNode).getRegisteredTopic().get(0));
		
		((NodePS)initNode).flooding(layerId, ((NodePS) initNode).getRegisteredTopic().get(0),3);
		
		System.out.println("- Mensajes iniciales enviados.");
				
		return false;
	}

}
