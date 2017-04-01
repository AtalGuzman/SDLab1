package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
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
		
		// Consideraremos cualquier nodo de manera aleatoria de la red para comenzar y finalizar
		Node initNode = Network.get(CommonState.r.nextInt(Network.size())); 
		int sendNode = CommonState.r.nextInt(Network.size());

		// Se crea un nuevo mensaje utilizando el valor que tiene el nodo inicial
		int initMsg = ((NodePS) initNode).getIdNode();
		Message message = new Message(initMsg, sendNode,"Contenido de prueba");

		// Y se env�a, para realizar la simulaci�n
		// Los par�metros corresponde a:
		// long arg0: Delay del evento
		// Object arg1: Evento enviado
		// Node arg2: Nodo por el cual inicia el envío del dato
		// int arg3: N�mero de la capa del protocolo que creamos (en este caso
		// de layerId)
		EDSimulator.add(0, message, initNode, layerId);

		return false;
	}

}
