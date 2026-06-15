package bean;

public class DepartmentBean {
	public DepartmentBean() {}
	public DepartmentBean(String dpt_id, String dpt_name){
		this.dpt_id = dpt_id;
		this.dpt_name = dpt_name;
	}
	String dpt_id;
	String dpt_name;
	public String getDpt_id() {
		return dpt_id;
	}
	public void setDpt_id(String dpt_id) {
		this.dpt_id = dpt_id;
	}
	public String getDpt_name() {
		return dpt_name;
	}
	public void setDpt_name(String dpt_name) {
		this.dpt_name = dpt_name;
	}
	
	
}
