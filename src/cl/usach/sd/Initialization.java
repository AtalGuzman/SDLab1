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
		this.cantTopic = Configuration.getInt(prefix+".cantTopic"); //Cantidad de tópicos es la cantidad inicial de la red. 
																	//Está creado bajo el supuesto de que existe al menos 1 tópico cuando inicia la red
		System.out.println("Arg: " + argExample);
		System.out.println("Valor inicial: "+ initValue);
		System.out.println("Cantidad de Topicos: "+cantTopic);
	}

	/**
	 * Ejecución de la inicialización en el momento de crear la 
	 * capa del sistema
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
		/*Se inicializa cada una de las
		 * listas necesarias para identificar 
		 * a los publicadores, subscriptores y tópicos
		 * */
		for(int j=0; j < Networksize; j++){
			
			NodePS init = (NodePS) Network.get(j);
			init.setRegisteredTopic(new ArrayList<Integer>()); 		//Tópicos a los que se registra
			init.setSubscriberSubscribed(new ArrayList<Integer>()); // Se inicializan los subscriptores subscritos al nodo actuando como topico
			init.setTopicSub(new ArrayList<Integer>());				//Tópicos a los que estoy subscrito
			init.setPublisherRegistered(new ArrayList<Integer>()); 	//Los publicadores que tengo registrados
			
		}
		
		for (int i = 0; i < Networksize; i++) {
			
			NodePS init = (NodePS) Network.get(i);

			//Si el topico no ha sido creado se crea, y se asigna y se registra el nodo publicador
			if(topicosInicializados<cantTopic){
				//System.out.println("Se escoje un nodo de tópico");
				NodePS node = (NodePS) Network.get(top);
				while(node.getTopic() != -1){
					top = CommonState.r.nextInt(Networksize);
					node = (NodePS) Network.get(top);
				}
			//Se inicializa el tópico
				node.setTopic(topicosInicializados);
			//Se inicializan los publicadores registrados
				node.getPublisherRegistered().add((int) init.getID()); 
			//Se registra al publicador
				topicosInicializados++; 
				top = CommonState.r.nextInt(Networksize);
			//Se procede a registrar topico en el publicador
				init.getRegisteredTopic().add(node.getTopic());
			}
			else{
			//Si el nodo ya está creado, entonces puedo registrarme como publicador
				for(int j=0; j < Networksize; j++){
					if(((NodePS) Network.get(j)).getTopic() >= 0 && ((NodePS) Network.get(j)).getTopic() == i%this.cantTopic){
						((NodePS) Network.get(j)).getPublisherRegistered().add( (int) init.getID() );
						init.getRegisteredTopic().add(((NodePS) Network.get(j)).getTopic());
					}
				}
			}
			rand = CommonState.r.nextInt(Networksize);
		}
		
		//A continuación se procede a inicializar y crear los subscriptores
		for(int i = 0; i < Networksize; i++){
			NodePS temp = (NodePS) Network.get(i);
			if(temp.getTopic()>=0){
				ArrayList<Integer> Sub = temp.getSubscriberSubscribed();
				Sub.add(rand); 

				for(int sub: Sub){
					NodePS subcriptor = (NodePS)Network.get(sub);
					subcriptor.getTopicSub().add(temp.getTopic());
				}
				rand = CommonState.r.nextInt(Networksize);
			}
		}
	
		System.out.println("\n*******************************************************\n");

		
		//Se muestra el contenido de la red según la inicialización de subscriptores, publicadores y tópicos
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
				for(int topic: temp.getTopicSub()){
					System.out.println("\t- Estoy subscrito en el topico "+ topic);
				}
				ArrayList<Integer> publicoEn = temp.getRegisteredTopic();
				for(int topic: publicoEn){
					System.out.println("\t- Publico en "+ topic);
				}
			} else{
				System.out.println("- Soy el nodo "+temp.getID()+" y no tengo tópicos.");
				for(int topic: temp.getTopicSub()){
					System.out.println("\t- Estoy subscrito en el topico "+ topic);
				}
				ArrayList<Integer> publicoEn = temp.getRegisteredTopic();
				for(int topic: publicoEn){
					System.out.println("\t- Publico en "+ topic);
				}
			}
		}
		System.out.println("\n");
		return true;
	}

}
