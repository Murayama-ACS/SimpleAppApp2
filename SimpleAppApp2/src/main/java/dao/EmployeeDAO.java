package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import bean.EmployeeBean;
import model.Hash;

public class EmployeeDAO extends DAO{
	Hash hash = new Hash();
	public int insertEmployee(EmployeeBean empBean) {//userBeanの内容をデータベースに登録する関数
		Connection con = dbConnect();
		int result = 0;
		String sql = "insert into employees (emp_id, emp_name, furigana, email, dpt_id, pos_id) values (?,?,?,?,?,?)";

		try {
			if(con != null) {

				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, empBean.getEmp_id());
				st.setString(2, empBean.getEmp_name());
				st.setString(3, empBean.getEmp_furigana());
				st.setString(4, empBean.getEmail());
				st.setString(5, empBean.getDpt_id());
				st.setString(6, empBean.getPos_id());

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
		String sql = "update employees set password=? where emp_id=? and is_deleted = 0";
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
		String sql = "UPDATE employees SET emp_name = ?, furigana = ?, email = ?, dpt_id = ?, pos_id = ? WHERE emp_id = ? and is_deleted = 0";
		int result = 0;
		try (
				Connection con = dbConnect();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, emp.getEmp_name());
			ps.setString(2, emp.getEmp_furigana());
			ps.setString(3, emp.getEmail());
			ps.setString(4, emp.getDpt_id());
			ps.setString(5, emp.getPos_id());
			ps.setString(6, emp.getEmp_id());

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

				int rs = st.executeUpdate();
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

	// PageResult クラス（DAO クラスファイルと同じパッケージに置く）
	public class PageResult<T> {
		private final java.util.List<T> items;
		private final boolean hasNext;
		public PageResult(java.util.List<T> items, boolean hasNext) {
			this.items = items;
			this.hasNext = hasNext;
		}
		public java.util.List<T> getItems() { return items; }
		public boolean hasNext() { return hasNext; }
	}
	//検索、ソートの結果をもとに社員情報を取得するメソッド
	public PageResult<EmployeeBean> searchEmployees(
			String empId, String empName, String dptId, String posId,
			String sortKey, String sortDir, int limit, int offset) throws SQLException {

		Map<String,String> colMap = Map.of(
		        "emp_id",   "e.emp_id",
		        "emp_name", "COALESCE(e.furigana, e.emp_name)",
		        "email",    "e.email",
		        "dpt_id",   "e.dpt_id",
		        "pos_id",   "e.pos_id"
		);

		if (sortKey == null || sortKey.isEmpty()) sortKey = "emp_id";
		String orderBy = colMap.getOrDefault(sortKey, "e.emp_id");
		String dir = "DESC".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT e.emp_id, e.emp_name, e.email, e.dpt_id, e.pos_id, ")
		   .append("d.dpt_name AS dpt_name, p.pos_name AS pos_name, e.furigana AS furigana ")
		   .append("FROM employees e ")
		   .append("LEFT JOIN departments d ON e.dpt_id = d.dpt_id ")
		   .append("LEFT JOIN positions p ON e.pos_id = p.pos_id ")
		   .append("WHERE e.is_deleted = 0 ");

		// パラメータを順番に保持
		ArrayList<Object> params = new ArrayList<>();

		// 動的な WHERE 句追加
		if (empId != null && !empId.isEmpty()) {
		    sql.append(" AND e.emp_id LIKE ? ");
		    params.add(empId + "%");
		}
		if (empName != null && !empName.isEmpty()) {
		    String input = empName.trim();
		    // ひらがな判定（U+3040〜U+309F を使用）。 prolonged sound mark (ー) を許可
		    boolean isHiragana = input.matches("^[\\u3040-\\u309F\\u30FC\\s]+$");
		    if (isHiragana) {
		        // 空白で分割してトークンごとに furigana LIKE 条件を追加（複数トークンは AND）
		        String[] tokens = input.split("\\s+");
		        for (String t : tokens) {
		            if (t.isEmpty()) continue;
		            sql.append(" AND e.furigana LIKE ? ");
		            params.add("%" + t + "%");
		        }
		    } else {
		        // デフォルトは漢字等で emp_name を検索（部分一致）
		        sql.append(" AND e.emp_name LIKE ? ");
		        params.add("%" + input + "%");
		    }
		}
		if (dptId != null && !dptId.isEmpty()) {
		    if (dptId.matches("^[A-Za-z0-9]{4}$") && dptId.endsWith("00")) {
		        String prefix = dptId.substring(0, 2);
		        sql.append(" AND e.dpt_id LIKE ? ");
		        params.add(prefix + "%");
		    } else {
		        sql.append(" AND e.dpt_id = ? ");
		        params.add(dptId);
		    }
		}
		if (posId != null && !posId.isEmpty()) {
		    sql.append(" AND e.pos_id = ? ");
		    params.add(posId);
		}

		// ORDER BY と LIMIT/OFFSET
		sql.append(" ORDER BY ").append(orderBy).append(" ").append(dir).append(", e.emp_id ASC ");
		sql.append(" LIMIT ? OFFSET ? ");

		ArrayList<EmployeeBean> list = new ArrayList<>();
		try (Connection con = dbConnect();
		     PreparedStatement ps = con.prepareStatement(sql.toString())) {

		    // パラメータをセット
		    int idx = 1;
		    for (Object p : params) {
		        ps.setObject(idx++, p);
		    }
		    int effectiveLimit = limit + 1;
		    ps.setInt(idx++, effectiveLimit);
		    ps.setInt(idx++, offset);

		    try (ResultSet rs = ps.executeQuery()) {
		        while (rs.next()) {
		            String empIdR = rs.getString("emp_id");
		            String empNameR = rs.getString("emp_name");
		            String email  = rs.getString("email");
		            String dptIdR = rs.getString("dpt_id");
		            String posIdR = rs.getString("pos_id");
		            EmployeeBean empBean = new EmployeeBean(empIdR, empNameR, email, dptIdR, posIdR);
		            empBean.setDpt_name(rs.getString("dpt_name"));
		            empBean.setPos_name(rs.getString("pos_name"));
		            // EmployeeBean に対応するセッタがあれば furigana もセット
		            try {
		                empBean.setEmp_furigana(rs.getString("furigana"));
		            } catch (NoSuchMethodError | AbstractMethodError | Exception e) {
		                // セッタがない場合は無視（必要なら EmployeeBean にフィールド追加してください）
		            }
		            list.add(empBean);
		        }
		    }
		}

		boolean hasNext = false;
		if (list.size() > limit) {
		    hasNext = true;
		    list.remove(list.size() - 1);
		}
		return new PageResult<>(list, hasNext);
	}

	/*//社員ID or メールアドレスとパスワードが一致する社員の情報をデータベースから取得するメソッド（ログイン認証）
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
	}*/

	//社員IDとメールアドレスが一致する社員の情報をデータベースから取得するメソッド
	public EmployeeBean empInfo(String emp_id, String email) {
		Connection con = dbConnect();
		EmployeeBean employee = null;
		//identifierがメールか社員IDかでsql文を変更
		String sql = "SELECT * FROM employees WHERE emp_id = ? and email = ? and is_deleted = 0";

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
				? "SELECT emp_id, emp_name, email, password, dpt_id, pos_id FROM employees WHERE email = ? and is_deleted = 0"
						: "SELECT emp_id, emp_name, email, password, dpt_id, pos_id FROM employees WHERE emp_id = ? and is_deleted = 0";

		try (Connection con = dbConnect();
				PreparedStatement ps = con.prepareStatement(sel)) {
			ps.setString(1, identifier);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					// ユーザ不存在 → セキュリティのため詳細は返さない
					return null;
				}
				String empId = rs.getString("emp_id");
				String storedPassword = rs.getString("password");
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
