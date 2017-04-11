package msg;

import cl.usach.sd.Message;

public class SubMsg extends Message{
	private int idTopic;
	
	public SubMsg(int remitent, int destination,int idTopic, String content, int tipoDeMensaje) {
		super(remitent, destination, content, tipoDeMensaje);
		this.setIdTopic(idTopic);
	}
	//Aún no sé que debe hacer en este caso

	public int getIdTopic() {
		return idTopic;
	}

	public void setIdTopic(int idTopic) {
		this.idTopic = idTopic;
	}
}
