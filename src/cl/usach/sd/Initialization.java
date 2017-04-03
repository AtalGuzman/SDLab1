package cl.usach.sd;

import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import ps.*;
import java.util.ArrayList;

public class Initialization implements Control {
	String prefix;

	int idLayer;
	int idTransport;

	//Valores que sacaremos del archivo de configuración	
	int argExample;
	int initValue;
	int cantTopic;
	
	public Initialization(String prefix) {
		System.out.println("***************SOY TRAFFIC GENERATOR**************");
		this.prefix = prefix;
		/**
		 * Para obtener valores que deseamos como argumento del archivo de
		 * configuración, podemos colocar el prefijo de la inicialización en
		 * este caso "init.1statebuilder" y luego la variable de entrada
		 */
		// Configuration.getPid retornar al número de la capa
		// que corresponden esa parte del protocolo
		this.idLayer = Configuration.getPid(prefix + ".protocol");
		System.out.println("La id del protocolo app a "+idLayer);
		this.idTransport = Configuration.getPid(prefix + ".transport");
		System.out.println("La id del protocolo de transporte corresponde a "+idTransport);

		// Configuration.getInt retorna el número del argumento
		// que se encuentra en el archivo de configuración.
		// También hay Configuration.getBoolean, .getString, etc...
		
		// en el archivo de configuración init.1statebuilder.argExample 100 y se puede usar ese valor.
		this.argExample = Configuration.getInt(prefix + ".argExample");
		this.initValue = Configuration.getInt(prefix + ".initValue");
		System.out.println("Arg: " + argExample);
		System.out.println("Valor inicial: "+ initValue);
	}

	/**
	 * Ejecución de la inicialización en el momento de crear el overlay en el
	 * sistema
	 */
	@Override
	public boolean execute() {
		System.out.println("\n EJECUTAMOS EL SIMULADOR");
		/**
		 * Para comenzar tomaremos un nodo cualquiera de la red, a través de un random
		 */
		//int nodoInicial = CommonState.r.nextInt(Network.size());
		
		
	
		/**Es conveniente inicializar los nodos, puesto que los nodos 
		 * son una clase clonable y si asignan valores desde el constructor
		 *  todas tomaran los mismos valores, puesto que tomaran la misma dirección
		 * de memoria*/
		System.out.println("Inicialización de nodos:");
		int Networksize = Network.size();
		for (int i = 0; i < Networksize; i++) {
			((NodePS) Network.get(i)).setIdNode(i);
			if(i%3 == 1 && this.cantTopic>0){ 
				//Iniciarán como publisher algunos nodos 
				((Publisher) ((NodePS) Network.get(i))).setRegisteredTopic(new ArrayList<Integer>(i%this.cantTopic));
			}
			if(i%3 == 2 && this.cantTopic>0){ //Si es igual a cero se debe partir desde abajo
				//Iniciarán como tópicos algunos nodos 
				((Topic) ((NodePS) Network.get(i))).setTopic(i%this.cantTopic);
			}
		}
		return true;
	}

}
