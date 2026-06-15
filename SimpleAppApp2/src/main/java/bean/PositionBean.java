package bean;

public class PositionBean {
	public PositionBean() {}
	public PositionBean(String pos_id, String pos_name) {
		this.pos_id = pos_id;
		this.pos_name = pos_name;
	}
	String pos_id;
	String pos_name;
	public String getPos_id() {
		return pos_id;
	}
	public void setPos_id(String pos_id) {
		this.pos_id = pos_id;
	}
	public String getPos_name() {
		return pos_name;
	}
	public void setPos_name(String pos_name) {
		this.pos_name = pos_name;
	}
	
	
	
	
}
