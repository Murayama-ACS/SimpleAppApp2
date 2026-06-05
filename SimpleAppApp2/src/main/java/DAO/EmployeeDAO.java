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
//	public int updateUser(EmployeeBean empBean, String newPass) {
//		Connection con = dbConnect();
//		int result = 0;
//		String sql = "update user set name=? ,pass=? where email=?";
//		
//		try {
//			if(con != null) {
//				
//				PreparedStatement st = con.prepareStatement(sql);
//				st.setString(1, userBean.getName());
//				st.setString(2, newPass);
//				st.setString(3, userBean.getEmail());
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
	
	//社員ID or メールアドレスとパスワードが一致する社員の情報をデータベースから取得するメソッド
	public EmployeeBean empInfo(String identifier, String pass, boolean isEmail) {
		Connection con = dbConnect();
		EmployeeBean employee = null;
		//identifierがメールか社員IDかでsql文を変更
		String sql = "SELECT name FROM USER WHERE emp_id = ? and pass = ?";
		if(isEmail) {
			sql = "SELECT name FROM USER WHERE email = ? and pass = ?";
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
					int dpt_id = rs.getInt("dpt_id");
					int pos_id = rs.getInt("pos_id");
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
