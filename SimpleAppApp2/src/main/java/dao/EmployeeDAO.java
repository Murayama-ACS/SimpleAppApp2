package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.EmployeeBean;
import model.Hash;

public class EmployeeDAO extends DAO{
	Hash hash = new Hash();
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
		if(newPass.equals("Abcd1234")){
			return -1;
		}else if(!newPass.matches(pattern)) {
			return -2;
		}
		try {
			if(con != null) {
				
				PreparedStatement st = con.prepareStatement(sql);
				
				st.setString(1, hash.getSHA512(newPass));
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
	
	/*//社員ID or メールアドレスとパスワードが一致する社員の情報をデータベースから取得するメソッド
		public ArryayList<EmployeeBean> empInfo(String ide) {
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
					st.setString(2, hash.getSHA512(pass));
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
		}*/
		
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
				st.setString(2, hash.getSHA512(pass));
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

	private FailedLoginDAO failedLoginDao = new FailedLoginDAO();

	// identifier が emp_id または email。戻り値は認証成功時の EmployeeBean、失敗やロック時は null。
	public EmployeeBean authenticateAndGetEmployee(String identifier, String plainPass, boolean isEmail, String remoteAddr, String userAgent) throws SQLException {
		String sel = isEmail
				? "SELECT emp_id, emp_name, email, password, dpt_id, pos_id FROM employees WHERE email = ?"
						: "SELECT emp_id, emp_name, email, password, dpt_id, pos_id FROM employees WHERE emp_id = ?";

		try (Connection con = dbConnect();
				PreparedStatement ps = con.prepareStatement(sel)) {
			ps.setString(1, identifier);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					// ユーザ不存在 → セキュリティのため詳細は返さない
					return null;
				}
				String empId = rs.getString("emp_id");
				String storedPassword = rs.getString("password"); // 将来的にハッシュ値
				// まずロックチェック
				if (failedLoginDao.isLocked(empId)) {
					return null; // ロック中
				}

				// パスワード検証（平文照合かハッシュ照合に置換）
				boolean ok = verifyPassword(plainPass, storedPassword); // 実装参照コメント下
				if (ok) {
					// リセット
					failedLoginDao.resetOnSuccess(empId);
					// EmployeeBean を戻す
					String empName = rs.getString("emp_name");
					String email = rs.getString("email");
					String dptId = rs.getString("dpt_id");
					String posId = rs.getString("pos_id");
					return new EmployeeBean(empId, empName, email, dptId, posId);
				} else {
					// 失敗を記録
					FailedLoginDAO.FailureResult r = failedLoginDao.recordPasswordFailure(empId);
					// 呼び出し側（Servlet）は null を受け取りメッセージを出す
					return null;
				}
			}
		}
	}

	public String findEmpIdByEmail(String email) throws SQLException {//
		String sql = "SELECT emp_id FROM employees WHERE email = ?";
		try (Connection con = dbConnect();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getString("emp_id");
				return null;
			}
		}
	}
	// パスワード検証のプレースホルダ（既存 DB が平文かハッシュかに応じて実装を変更）
	private boolean verifyPassword(String plain, String stored) {
		// TODO: ここでハッシュ方式を使うなら BCrypt/Argon2 ライブラリを使って検証する
		// 現状 DB が平文なら単純比較（ただし本番ではハッシュを推奨）
		return plain != null && hash.getSHA512(plain).equals(stored);
	}
}
