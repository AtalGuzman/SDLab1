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
		//Se inicializan los nodos 
		int topicosInicializados = 0;
		int top = CommonState.r.nextInt(Networksize);
		int rand = CommonState.r.nextInt(Networksize);

		System.out.println("*******************************************************\n");
		for(int j=0; j < Networksize; j++){
			((NodePS) Network.get(j)).setSubscriberSubscribed(new ArrayList<Integer>()); // Se inicializan los subscriptores subscritos al nodo actuando como topico
		}
		for (int i = 0; i < Networksize; i++) {
			NodePS init = (NodePS) Network.get(i);
			init.setRegisteredTopic(new ArrayList<Integer>(i%this.cantTopic)); //Se registra en ciertos t�picos
			init.setSubscriberSubscribed(new ArrayList<Integer>()); // Se inicializan los subscriptores subscritos al nodo actuando como topico
			init.setTopicSub(new ArrayList<Integer>());
			
			
			//Si el topico no ha sido creado se crea, y se asigna y se registra el nodo publicador
			if(topicosInicializados<cantTopic){
				NodePS node = (NodePS) Network.get(top);
				while(node.getTopic() != -1){
					top = CommonState.r.nextInt(Networksize);
					node = (NodePS) Network.get(top);
				}
				node.setTopic(topicosInicializados);
				node.setPublisherRegistered(new ArrayList<Integer>(i)); //Se inicializan los publicadores registrados
				topicosInicializados++;
				node.register(i);
				top = CommonState.r.nextInt(Networksize);
			}
			else{
				for(int j=0; j < Networksize; j++){
					if(((NodePS) Network.get(j)).getTopic() >= 0 && ((NodePS) Network.get(j)).getTopic() == i%this.cantTopic){
						((NodePS) Network.get(j)).register( (int) init.getID() );
					}
				}
			}
			rand = CommonState.r.nextInt(100);
			if(rand >=0){
				init.getTopicSub().add((i+1)%this.cantTopic); //Se sbuscribe a ciertos t�picos
				for(int j=0; j < Networksize; j++){
					if(((NodePS) Network.get(j)).getTopic() >= 0 && ((NodePS) Network.get(j)).getTopic() == (i+1)%this.cantTopic){
						((NodePS) Network.get(j)).getSubscriberSubscribed().add( (int) init.getID() );
					}
				}
			}
		}
		//Inicializaci�n de t�picos
		/*if(this.cantTopic>0){ 
			//Iniciar�n como publisher algunos nodos 
			rand = CommonState.r.nextInt(Networksize);
			for(int j = 0; j < cantTopic; j++){
				rand = CommonState.r.nextInt(Networksize);
				System.out.println("- Se pondr� el topico " +j+" en el nodo "+j);
				((Topic) ((NodePS) Network.get(j))).setTopic(j); //Se ingresa cierto t�pico
				(((NodePS) Network.get(j))).setSubscriberSubscribed(new ArrayList<Integer>()); //Se muestra qu� nodos est�n subscritos a �l
				(((NodePS) Network.get(j))).getSubscriberSubscribed().add(rand); //Se agrega un nodo subscrito
				rand = CommonState.r.nextInt(Networksize);	
				(((NodePS) Network.get(j))).setPublisherRegistered(new ArrayList<Integer>(rand)); //Se muestra qu� nodos est�n registrado en �l
			}
		}*/
		System.out.println("\n*******************************************************\n");

		
		for (int i = 0; i < Networksize; i++) {
			NodePS temp = (NodePS) Network.get(i);
			if(temp.getTopic() != -1){
				System.out.println("- Soy el nodo "+temp.getID()+" y tengo el topico "+temp.getTopic()+".");
				ArrayList<Integer> publicadores = ((NodePS) temp).getPublisherRegistered();
				for(int pub: publicadores){
					System.out.println("\t- Tengo registrado como publicador a "+pub);
				}
				ArrayList<Integer> subscriptores = ((NodePS) temp).getSubscriberSubscribed();
				for(int sub: subscriptores){
					System.out.println("\t- Tengo subscrito a "+sub);
				}
			} else{
				System.out.println("- Soy el nodo "+temp.getID()+" y no tengo t�picos.");
				
			}
		}
		System.out.println("\n");
		return true;
	}

}
