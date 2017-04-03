package msg;

import cl.usach.sd.Message;

public class PubMsg extends Message {
	
	private int IdTopico;
	
	public PubMsg(int remitent, int destination, String content, int tipoDeMensaje,int idTopico) {
		super(remitent,destination,content,tipoDeMensaje);
		this.setContent(content);
		this.setRemitent(remitent);
		this.setDestination(destination);
		this.setTipoDeMensaje(tipoDeMensaje);
		this.setIdTopico(idTopico);
	}

	public int getIdTopico() {
		return IdTopico;
	}

	public void setIdTopico(int idTopico) {
		IdTopico = idTopico;
	}

}
