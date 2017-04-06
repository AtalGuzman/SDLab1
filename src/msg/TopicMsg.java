package msg;

import cl.usach.sd.Message;

public class TopicMsg extends Message {
	
	private int idTopic;
	
	public TopicMsg(int remitent, int destination, String content, int tipoDeMensaje, int idTopic) {
		super(remitent, destination, content, tipoDeMensaje);
		// TODO Auto-generated constructor stub
		this.setIdTopic(idTopic);
	}

	public int getIdTopic() {
		return idTopic;
	}

	public void setIdTopic(int idTopic) {
		this.idTopic = idTopic;
	}

}
