package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import msg.PubMsg;

public class TrafficGenerator implements Control {
	private final static String PAR_PROT = "protocol";
	private final int layerId;

	public TrafficGenerator(String prefix) {
		layerId = Configuration.getPid(prefix + "." + PAR_PROT);

	}

	@Override
	public boolean execute() {
		System.out.println("\n***************TRAFFIC GENERATOR**************\n");
		//Consideraremos cualquier nodo de manera aleatoria de la red para comenzar y finalizar
		System.out.println("- Se elige un nodo para iniciar el tráfico.");
		Node initNode = Network.get(CommonState.r.nextInt(Network.size())); 
		System.out.println("- El nodo elegido corresponde a "+initNode.getID());
		//Obtener los vecinos y se les envía el mensaje de la vida! D:
		int cantidadVecinos = ((Linkable) initNode.getProtocol(0)).degree();
		System.out.println("- Tengo "+cantidadVecinos+" vecinos");
		System.out.println("- Se generan los mensajes iniciales, por defecto en le tópico 0.\n");
		for(int i = 0; i<cantidadVecinos; i++){
			
			//por defecto se publica en el tópico 0, notar que se asume que existe al menos un tópico
			String content = "Soy el nodo "+initNode.getID()+" y haré un post en el tópico 0.";		
			int initMsg = ((NodePS) initNode).getIdNode();
			Node sendNode = Network.get((int) ((Linkable) initNode.getProtocol(0)).getNeighbor(i).getID());
			Message message = new PubMsg(initMsg, (int) sendNode.getID(),content, 0,0); //Notar que se crear como un mensaje de publicador
			EDSimulator.add(0, message, initNode, layerId);
			
		}		
		
		System.out.println("- Mensajes iniciales enviados.");
		
		//Se debe verificar que exista algún tópico con cantValue (valor del archivo de configuración)
		
		// Y se envía, para realizar la simulación
		// Los parámetros corresponde a:
		// long arg0: Delay del evento
		// Object arg1: Evento enviado
		// Node arg2: Nodo por el cual inicia el envÃ­o del dato
		// int arg3: Número de la capa del protocolo que creamos (en este caso
		// de layerId)
	

		return false;
	}

}
