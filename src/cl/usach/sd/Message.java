package cl.usach.sd;

/**
 * Clase la cual vamos a utilizar para enviar datos de un Peer a otro
 */
public class Message {
	private int remitent; /*Corresponde a la forma en que está actuando un nodo:
							0, para un publicar
							1 para un tópico
							2 para un subscriptor, */
	private int destination; //Corresponde al nodo al que se le entregará el mensaje
	
	private String content; //Corresponde al contenido del mensaje

	public Message(int value,  int destination, String content) {
		this.setValue(value);
		this.setDestination(destination);
		this.setContent(content);
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public int getValue() {
		return remitent;
	}

	public void setValue(int value) {
		this.remitent = value;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
