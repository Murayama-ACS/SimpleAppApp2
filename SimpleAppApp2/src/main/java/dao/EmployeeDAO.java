package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import bean.EmployeeBean;
import model.Hash;

public class EmployeeDAO extends DAO{
	Hash hash = new Hash();
	public int insertEmployee(EmployeeBean empBean) {//userBeanの内容をデータベースに登録する関数
		Connection con = dbConnect();
		int result = 0;
		String sql = "insert into employees (emp_id, emp_name, email, dpt_id, pos_id) values (?,?,?,?,?)";

		try {
			if(con != null) {

				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, empBean.getEmp_id());
				st.setString(2, empBean.getEmp_name());
				st.setString(3, empBean.getEmail());
				st.setString(4, empBean.getDpt_id());
				st.setString(5, empBean.getPos_id());

				int rs = st.executeUpdate();//これなんだっけ
				result = rs;
			}
		}catch(SQLException e) {
			System.out.println("SQLエラー");
			String eMsg = e.getMessage();
			System.out.println(eMsg);
			//すでに登録されている社員ID（Primary）、もしくはメールアドレス（Unique）が挿入された場合
			if(eMsg.contains("Duplicate entry")) {
				result = -1;
			}else {
				result = 0;
			}
			return result;
		}
		return result;
	}
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

				result = st.executeUpdate();//これなんだっけ
			}
		}catch(SQLException e) {
			System.out.println("SQLエラー");
			System.out.println(e.getMessage());
			return 0;
		}
		return result;
	}
	public int updateEmpInfo(EmployeeBean emp){
		String sql = "UPDATE employees SET emp_name = ?, email = ?, dpt_id = ?, pos_id = ? WHERE emp_id = ?";
		int result = 0;
		try (
			Connection con = dbConnect();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, emp.getEmp_name());
			ps.setString(2, emp.getEmail());
			ps.setString(3, emp.getDpt_id());
			ps.setString(4, emp.getPos_id());
			ps.setString(5, emp.getEmp_id());
			
			result = ps.executeUpdate();
		}catch(SQLException e) {
			System.out.println("SQLエラー");
			String eMsg = e.getMessage();
			System.out.println(eMsg);
			//すでに登録されているメールアドレス（Unique）に更新しようとした場合
			if(eMsg.contains("Duplicate entry")) {
				result = -1;
			}else {
				result = 0;
			}
			return result;
		}
		return result;
	}
	
	//引数の社員IDを持つ社員を削除するメソッド
	public int deleteEmpInfo(String emp_id) {
		Connection con = dbConnect();
		int result = 0;
		//削除対象の削除フラグを更新、データ自体はDBに残存
		String sql = "update employees SET is_deleted = 1 WHERE emp_id=? AND is_deleted = 0";

		try {
			if(con != null) {
				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, emp_id);

				int rs = st.executeUpdate();//これなんだっけ
				result = rs;
			}
		}catch(SQLException e) {
			System.out.println("SQLエラー");
			String eMsg = e.getMessage();
			System.out.println(eMsg);
			//すでに削除フラグが更新されている社員を削除しようとした場合
			if(e.getMessage().contains("Unknown column")) {
				result = -1;
			}else {
				result = 0;
			}

			return result;
		}
		return result;
	}
	//社員ID or メールアドレスとパスワードが一致する社員の情報をデータベースから取得するメソッド
	public ArrayList<EmployeeBean> empInfo() {
		Connection con = dbConnect();
		EmployeeBean employee = null;
		ArrayList<EmployeeBean> empList = new ArrayList<EmployeeBean>();
		//identifierがメールか社員IDかでsql文を変更
		String sql = "SELECT e.emp_id as '社員ID', e.emp_name as '名前', e.email, d.dpt_name AS '部署', p.pos_name AS '役職' "
				+ "from employees e left outer join departments d on e.dpt_id = d.dpt_id "
				+ "left outer JOIN positions p on e.pos_id = p.pos_id WHERE e.is_deleted = 0";

		try {
			if(con != null) {

				//					PreparedStatement st = con.prepareStatement(sql);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql);
				//					ResultSet rs = st.executeQuery();

				while(rs.next()) {
					String emp_id = rs.getString("社員ID");
					String emp_name = rs.getString("名前");
					String email = rs.getString("e.email");
					String dpt_name = rs.getString("部署");
					String pos_name = rs.getString("役職");
					employee = new EmployeeBean(emp_id, emp_name, email, dpt_name, pos_name);
					empList.add(employee);
				}
			}
		}catch(SQLException e) {
			System.out.println("SQLエラーこれですか？");
			System.out.println(e.getMessage());
			return null;
		}
		dbClose(con);

		return empList;
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
