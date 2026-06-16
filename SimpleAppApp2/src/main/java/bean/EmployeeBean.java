package bean;

public class EmployeeBean {
	public EmployeeBean(){
	}
	public EmployeeBean(String emp_id, String emp_name, String email, String dpt_id, String pos_id){
		this.emp_id = emp_id;
		this.emp_name = emp_name;
		this.email = email;
		this.dpt_id = dpt_id;
		this.pos_id = pos_id;
	}
	public EmployeeBean(String emp_id, String emp_name, String emp_furigana, String email, String dpt_id, String pos_id){
		this.emp_id = emp_id;
		this.emp_name = emp_name;
		this.emp_furigana = emp_furigana;
		this.email = email;
		this.dpt_id = dpt_id;
		this.pos_id = pos_id;
	}
	String emp_id;
	String emp_name;
	String emp_furigana;
	String email;
	String dpt_id;
	String pos_id;
	String dpt_name;
	String pos_name;
	int pos_amount;
	boolean is_deleted = false;
	
	
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getEmp_name() {
		return emp_name;
	}
	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}
	public String getEmp_furigana() {
		return emp_furigana;
	}
	public void setEmp_furigana(String emp_furigana) {
		this.emp_furigana = emp_furigana;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getDpt_id() {
		return dpt_id;
	}
	public void setDpt_id(String dpt_id) {
		this.dpt_id = dpt_id;
	}
	public String getPos_id() {
		return pos_id;
	}
	public void setPos_id(String pos_id) {
		this.pos_id = pos_id;
	}
	public int getPos_amount() {
		return pos_amount;
	}
	public void setPos_amount(int pos_amount) {
		this.pos_amount = pos_amount;
	}
	public String getDpt_name() {
		return dpt_name;
	}
	public void setDpt_name(String dpt_name) {
		this.dpt_name = dpt_name;
	}
	public String getPos_name() {
		return pos_name;
	}
	public void setPos_name(String pos_name) {
		this.pos_name = pos_name;
	}
	public boolean isIs_deleted() {
		return is_deleted;
	}
	public void setIs_deleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
	
	
	
}
