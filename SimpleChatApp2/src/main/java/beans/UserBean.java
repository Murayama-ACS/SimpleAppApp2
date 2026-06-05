package beans;

public class UserBean {
	//constructor
	public UserBean() {}
	public UserBean(String email,String name) {
		this.email = email;
		this.name = name;
	}
	public UserBean(String email, String name, String pass) {
		this.email = email;
		this.name = name;
		this.pass = pass;
	}
	//data
	private String email,name,pass;
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	//method
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
