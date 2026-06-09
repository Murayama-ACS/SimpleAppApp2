package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.EmployeeBean;

public class EmployeeDAO extends DAO{
//	public int insertUser(EmployeeBean empBean, String pass) {//userBeanの内容をデータベースに登録する関数
//		Connection con = dbConnect();
//		int result = 0;
//		String sql = "insert into user (email, name, pass) values (?,?,?)";
//		
//		try {
//			if(con != null) {
//				
//				PreparedStatement st = con.prepareStatement(sql);
//				st.setString(1, userBean.getEmail());
//				st.setString(2, userBean.getName());
//				st.setString(3, pass);
//
//				int rs = st.executeUpdate();//これなんだっけ
//				result = rs;
//			}
//		}catch(SQLException e) {
//			System.out.println("SQLエラー");
//			System.out.println(e.getMessage());
//			return 0;
//		}
//		return result;
//	}
	public int updatePassword(EmployeeBean empBean, String newPass) {
		Connection con = dbConnect();
		int result = 0;
		String sql = "update employees set password=? where emp_id=?";
		String pattern =  "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
		System.out.println(!newPass.matches(pattern));
		if(newPass.equals("1234")){
			return -1;
		}else if(!newPass.matches(pattern)) {
			return -2;
		}
		try {
			if(con != null) {
				
				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, newPass);
				st.setString(2, empBean.getEmp_id());

				int rs = st.executeUpdate();//これなんだっけ
				result = rs;
			}
		}catch(SQLException e) {
			System.out.println("SQLエラー");
			System.out.println(e.getMessage());
			return 0;
		}
		return result;
	}
	
	//社員ID or メールアドレスとパスワードが一致する社員の情報をデータベースから取得するメソッド
	public EmployeeBean empInfo(String identifier, String pass, boolean isEmail) {
		Connection con = dbConnect();
		EmployeeBean employee = null;
		//identifierがメールか社員IDかでsql文を変更
		String sql = "SELECT * FROM employees WHERE emp_id = ? and password = ?";
		if(isEmail) {
			sql = "SELECT * FROM employees WHERE email = ? and password = ?";
		}
		try {
			if(con != null) {
				
				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, identifier);
				st.setString(2, pass);
				ResultSet rs = st.executeQuery();
				
				while(rs.next()) {
					String emp_id = rs.getString("emp_id");
					String emp_name = rs.getString("emp_name");
					String email = rs.getString("email");
					String dpt_id = rs.getString("dpt_id");
					String pos_id = rs.getString("pos_id");
					employee = new EmployeeBean(emp_id, emp_name, email, dpt_id, pos_id);
				}
			}
		}catch(SQLException e) {
			System.out.println("SQLエラー");
			System.out.println(e.getMessage());
			return null;
		}
		dbClose(con);
		
//		System.out.println("emp_id in EmployeeDAO:" + employee.getEmp_id());
//		System.out.println("emp_name in EmployeeDAO:" + employee.getEmp_name());
//		System.out.println("emp_email in EmployeeDAO:" + employee.getEmail());
//		System.out.println("dpt_id in EmployeeDAO:" + employee.getDpt_id());
//		System.out.println("pos_id in EmployeeDAO:" + employee.getPos_id());

		return employee;
	}
	
	//社員IDとメールアドレスが一致する社員の情報をデータベースから取得するメソッド
		public EmployeeBean empInfo(String emp_id, String email) {
			Connection con = dbConnect();
			EmployeeBean employee = null;
			//identifierがメールか社員IDかでsql文を変更
			String sql = "SELECT * FROM employees WHERE emp_id = ? and email = ?";
			
			try {
				if(con != null) {
					
					PreparedStatement st = con.prepareStatement(sql);
					st.setString(1, emp_id);
					st.setString(2, email);
					ResultSet rs = st.executeQuery();
					
					while(rs.next()) {
						String emp_name = rs.getString("emp_name");
						String dpt_id = rs.getString("dpt_id");
						String pos_id = rs.getString("pos_id");
						employee = new EmployeeBean(emp_id, emp_name, email, dpt_id, pos_id);
					}
				}
			}catch(SQLException e) {
				System.out.println("SQLエラー");
				System.out.println(e.getMessage());
				return null;
			}
			dbClose(con);

			return employee;
		}
}
