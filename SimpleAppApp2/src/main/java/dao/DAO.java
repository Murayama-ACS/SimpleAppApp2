package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
	protected Connection dbConnect() {
		Connection con = null;
		try {
			//driverload
			Class.forName("org.mariadb.jdbc.Driver");
			//connect to database
			con = DriverManager.getConnection
					("jdbc:mariadb://localhost/simpleappapp2?"
							+"user=root&password=Maria1234");
		}catch(ClassNotFoundException e){
			System.out.println("JDBCドライバが見つかりません。");
			System.out.println(e.getMessage());
		}catch(SQLException e) {
			System.out.println("データベース接続エラー");
			System.out.println(e.getMessage());
		}
		return con;
	}
	protected void dbClose(Connection con) {
		try {
			if(con != null) {
				con.close();
			}
		}catch(SQLException e) {
			System.out.println("DB接続時にエラーが発生しました");
			System.out.println(e.getMessage());
		}
	}
}
