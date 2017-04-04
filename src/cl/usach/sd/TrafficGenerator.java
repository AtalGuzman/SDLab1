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
		System.out.println("***************SOY TRAFFIC GENERATOR**************");
		//Consideraremos cualquier nodo de manera aleatoria de la red para comenzar y finalizar
		System.out.println("\tSe elige un nodo para iniciar el tráfico");
		Node initNode = Network.get(CommonState.r.nextInt(Network.size())); 
		System.out.println("\tEl nodo elegido corresponde a "+initNode.getID());
		//Obtener los vecinos y se les envía el mensaje de la vida! D:
		int cantidadVecinos = ((Linkable) initNode.getProtocol(0)).degree();
		
		for(int i = 0; i<cantidadVecinos; i++){
			
			System.out.println("\tSe crea el contenido");
			String content = "Soy el nodo "+initNode.getID()+" y haré un post en el tópico 0 ";
			
			System.out.println("\tSe obtiene la id del nodo que iniciará el tráfico");
			int initMsg = ((NodePS) initNode).getIdNode();
			
			
			Node sendNode = Network.get((int) ((Linkable) initNode.getProtocol(0)).getNeighbor(i).getID());
			Message message = new PubMsg(initMsg, (int) sendNode.getID(),content, 0,0); 
		
			System.out.println("\tSe agrega el mensaje a la cola de simulación discreta");

			EDSimulator.add(0, message, initNode, layerId);
			
		}		
		
		
		System.out.println("\tSe crea el mensaje");
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
