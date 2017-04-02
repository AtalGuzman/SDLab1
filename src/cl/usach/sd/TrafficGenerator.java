package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

public class TrafficGenerator implements Control {
	private final static String PAR_PROT = "protocol";
	private final int layerId;

	public TrafficGenerator(String prefix) {
		layerId = Configuration.getPid(prefix + "." + PAR_PROT);

	}

	@Override
	public boolean execute() {
		System.out.println("***************SOY TRAFFIC GENERATOR**************");
		//Consideraremos cualquier nodo de manera aleatoria de la red para comenzar y finalizar
		System.out.println("\tSe elige un nodo para iniciar el tráfico");
		Node initNode = Network.get(CommonState.r.nextInt(Network.size())); 
		System.out.println("\tEl nodo elegido corresponde a "+initNode.getID());
		//Obtener los vecinos y se les envía el mensaje de la vida! D:
		int cantidadVecinos = ((Linkable) initNode.getProtocol(0)).degree();
		
		System.out.println("\tSe botiene el vecino del nodo "+initNode.getID());
		int sendNode = CommonState.r.nextInt(cantidadVecinos);
		Node neighBor = ((Linkable) initNode.getProtocol(0)).getNeighbor(sendNode);
		
		System.out.println("NeighborNode: "+ neighBor.getID());
		
		System.out.println("\tSe crea el contenido");
		String content = "Soy el nodo "+initNode.getID()+" y tú eres mi vecino de id "+neighBor.getID();
		
		System.out.println("\tSe obtiene la id del nodo que iniciará el tráfico");
		int initMsg = ((NodePS) initNode).getIdNode();
		
		System.out.println("\tSe crea el mensaje");
		Message message = new Message(initMsg, ((int) neighBor.getID()),content);
		
		System.out.println("\tSe agrega el mensaje a la cola de simulación discreta");
		EDSimulator.add(0, message, initNode, layerId);
	
		
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
