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
		System.out.println("\n*************** INITIALIZATION **************\n");
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
		this.cantTopic = Configuration.getInt(prefix+".cantTopic");
		System.out.println("Arg: " + argExample);
		System.out.println("Valor inicial: "+ initValue);
		System.out.println("Cantidad de Topicos: "+cantTopic);
	}

	/**
	 * Ejecución de la inicialización en el momento de crear el overlay en el
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
		System.out.println("\n\tEJECUCIÓN DEL SIMULADOR");
		/**
		 * Para comenzar tomaremos un nodo cualquiera de la red, a través de un random
		 */
		/**Es conveniente inicializar los nodos, puesto que los nodos 
		 * son una clase clonable y si asignan valores desde el constructor
		 *  todas tomaran los mismos valores, puesto que tomaran la misma dirección
		 * de memoria*/
		System.out.println("\nINICIALIZACIÓN DE NODOS\n");
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
			init.setRegisteredTopic(new ArrayList<Integer>(i%this.cantTopic)); //Se registra en ciertos tópicos
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
				node.getSubscriberSubscribed().add(rand);

			}
			else{
				for(int j=0; j < Networksize; j++){
					if(((NodePS) Network.get(j)).getTopic() >= 0 && ((NodePS) Network.get(j)).getTopic() == i%this.cantTopic){
						((NodePS) Network.get(j)).register( (int) init.getID() );
					}
					if(((NodePS) Network.get(j)).getTopic() >= 0){
						while(((NodePS) Network.get(j)).getSubscriberSubscribed().contains(rand)){
							rand = CommonState.r.nextInt(Networksize);
						}
						((NodePS) Network.get(j)).getSubscriberSubscribed().add(rand);
					}
				}
			}
			rand = CommonState.r.nextInt(Networksize);
		}
		
		for(int i = 0; i < Networksize; i++){
			NodePS temp = (NodePS) Network.get(i);
			ArrayList<Integer> Sub = temp.getSubscriberSubscribed();
			if(Sub.size() > 0){
				for(int sub: Sub){
						NodePS subcriptor = (NodePS)Network.get(sub);
						subcriptor.getTopicSub().add(i);
				}
				
				//Este pequeño algoritmo fue obtenido desde http://stackoverflow.com/questions/17967114/how-to-efficiently-remove-duplicates-from-an-array-without-using-set
				int current = Sub.get(0);
				boolean found = false;
			
			}
		}
	    
		//Todos los topicos están inicializados
		//Falta agregar las subscripciones D:
		/*for(int j=0; j < Networksize; j++){
			if(((NodePS) Network.get(j)).getTopic() == (i+1)%this.cantTopic){
				((NodePS) Network.get(j)).getSubscriberSubscribed().add( (int) Network.get(rand).getID() );
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
				System.out.println("- Soy el nodo "+temp.getID()+" y no tengo tópicos.");
				
			}
		}
		System.out.println("\n");
		return true;
	}

}
