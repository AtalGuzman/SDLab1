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

	//Valores que sacaremos del archivo de configuraci�n	
	int argExample;
	int initValue;
	int cantTopic;
	
	public Initialization(String prefix) {
		System.out.println("\n*************** INITIALIZATION **************\n");
		this.prefix = prefix;
		/**
		 * Para obtener valores que deseamos como argumento del archivo de
		 * configuraci�n, podemos colocar el prefijo de la inicializaci�n en
		 * este caso "init.1statebuilder" y luego la variable de entrada
		 */
		// Configuration.getPid retornar al n�mero de la capa
		// que corresponden esa parte del protocolo
		this.idLayer = Configuration.getPid(prefix + ".protocol");
		System.out.println("La id del protocolo app a "+idLayer);
		this.idTransport = Configuration.getPid(prefix + ".transport");
		System.out.println("La id del protocolo de transporte corresponde a "+idTransport);

		// Configuration.getInt retorna el n�mero del argumento
		// que se encuentra en el archivo de configuraci�n.
		// Tambi�n hay Configuration.getBoolean, .getString, etc...
		
		// en el archivo de configuraci�n init.1statebuilder.argExample 100 y se puede usar ese valor.
		this.argExample = Configuration.getInt(prefix + ".argExample");
		this.initValue = Configuration.getInt(prefix + ".initValue");
		this.cantTopic = Configuration.getInt(prefix+".cantTopic");
		System.out.println("Arg: " + argExample);
		System.out.println("Valor inicial: "+ initValue);
		System.out.println("Cantidad de Topicos: "+cantTopic);
	}

	/**
	 * Ejecuci�n de la inicializaci�n en el momento de crear el overlay en el
	 * sistema
	 */
	@Override
	public boolean execute() {
		int Networksize = Network.size();
		//Preguntar como se hace esto
		if(cantTopic > Networksize){
			System.out.println("NO SE PUEDEN INICIALIZAR TANTOS TOPICOS");
			return false;
		}
		System.out.println("\n\tEJECUCI�N DEL SIMULADOR");
		/**
		 * Para comenzar tomaremos un nodo cualquiera de la red, a trav�s de un random
		 */
		/**Es conveniente inicializar los nodos, puesto que los nodos 
		 * son una clase clonable y si asignan valores desde el constructor
		 *  todas tomaran los mismos valores, puesto que tomaran la misma direcci�n
		 * de memoria*/
		System.out.println("\nINICIALIZACI�N DE NODOS\n");
		for (int i = 0; i < Networksize; i++) {
			((NodePS) Network.get(i)).setIdNode(i);
			((Publisher) ((NodePS) Network.get(i))).setRegisteredTopic(new ArrayList<Integer>(i%this.cantTopic));
			((Subscriber) ((NodePS) Network.get(i))).setTopicSub(new ArrayList<Integer>((i+1)%this.cantTopic));
		}
		System.out.println("*******************************************************\n");
		//Inicializaci�n de t�picos
		if(this.cantTopic>0){ 
			//Iniciar�n como publisher algunos nodos 
			int rand = CommonState.r.nextInt(Networksize);
			for(int j = 0; j < cantTopic; j++){
				rand = CommonState.r.nextInt(Networksize);
				System.out.println("- Se pondr� el topico " +j+" en el nodo "+rand);
				((Topic) ((NodePS) Network.get(rand))).setTopic(j);
			}
		}
		System.out.println("\n*******************************************************\n");

		
		for (int i = 0; i < Networksize; i++) {
			NodePS temp = (NodePS) Network.get(i);
			if(temp.getTopic() != -1){
				System.out.println("- Soy el nodo "+temp.getIdNode()+" y tengo el topico "+temp.getTopic()+".");
			} else{
				System.out.println("- Soy el nodo "+temp.getIdNode()+" y no tengo t�picos.");
			}
		}
		System.out.println("\n");
		return true;
	}

}
