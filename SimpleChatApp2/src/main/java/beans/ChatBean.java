package beans;

public class ChatBean {
	//data
	public ChatBean(){}
	public ChatBean(String name,String date,String time,String text){
		this.name = name;
		this.date = date;
		this.time = time;
		this.text = text;
	}
	private String name,date,time,text;
	//method
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
