package cl.usach.sd;

/**
 * Clase la cual vamos a utilizar para enviar datos de un Peer a otro
 */
public class Message {
	private int remitent; 
	private int destination; //Corresponde al nodo al que se le entregará el mensaje
	private Boolean intermediario;
	private int tipoDeMensaje; /*Corresponde a la forma en que está actuando un nodo:
	0, para un publicador
	1 para un subscriptor
	2 para un topico, 
	3 inicialización*/
	
	private String content; //Corresponde al contenido del mensaje

	public Message(int remitent,  int destination, String content,int tipoDeMensaje) {
		this.setRemitent(remitent);
		this.setDestination(destination);
		this.setContent(content);
		this.setTipoDeMensaje(tipoDeMensaje);
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public int getRemitent() {
		return remitent;
	}

	public void setRemitent(int value) {
		this.remitent = value;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getTipoDeMensaje() {
		return tipoDeMensaje;
	}

	public void setTipoDeMensaje(int tipoDeMensaje) {
		this.tipoDeMensaje = tipoDeMensaje;
	}

	public Boolean getIntermediario() {
		return intermediario;
	}

	public void setIntermediario(Boolean intermediario) {
		this.intermediario = intermediario;
	}
}
