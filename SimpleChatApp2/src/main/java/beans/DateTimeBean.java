package beans;

public class DateTimeBean {
	//data
	public DateTimeBean(){}
	public DateTimeBean(String date,String time){
		this.date = date;
		this.time = time;
	}
	private String date,time;
	//method
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
	

}
