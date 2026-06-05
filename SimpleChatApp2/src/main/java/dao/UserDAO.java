package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.UserBean;

public class UserDAO extends DAO{
	public UserBean userInfo(String email,String pass) {
		Connection con = dbConnect();
		UserBean user = null;
		String sql = "SELECT name FROM user "
				+ "WHERE email = ? and pass = ?";
		try {
			if(con != null) {
				//ステートメントインスタンス（バッファ）の生成
				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, email);
				st.setString(2, pass);
				ResultSet rs = st.executeQuery();
				//実行結果
				while(rs.next()) {
					String name = rs.getString("name");
					user = new UserBean(email,name);
				}
			}
		}catch(SQLException e) {
			System.out.println("SQLエラー");
			System.out.println(e.getMessage());
			return null;
		}
		return user;
	}

	public int insertUser(UserBean userBean) {
		Connection con = dbConnect();
		String sql = "INSERT INTO user(email, pass, name) VALUES(?, ?, ?)";
		try {
			if (con != null) {
				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, userBean.getEmail());
				st.setString(2, userBean.getPass());
				st.setString(3, userBean.getName());
				st.executeUpdate();
				return 1; 
			}
		} catch (SQLException e) {
			System.out.println("SQLエラー");
			System.out.println(e.getMessage());
			return 0;
		}
		dbClose(con);
		return 0;
	}
	
	public int updateUser(UserBean userBean) {
	    Connection con = dbConnect();
	    String sql = "UPDATE user SET name = ?, pass = ? WHERE email = ?";
	    try {
	        if (con != null) {
	            PreparedStatement st = con.prepareStatement(sql);
	            st.setString(1, userBean.getName());
	            st.setString(2, userBean.getPass());
	            st.setString(3, userBean.getEmail());
	            
	            st.executeUpdate();
	            return 1;
	        }
	    } catch (SQLException e) {
	        System.out.println("SQLエラー");
	        System.out.println(e.getMessage());
	        return 0;
	    } finally {
	        dbClose(con);
	    }
	    return 0;
	}
}
