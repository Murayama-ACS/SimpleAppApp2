package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.ApplicationBean;
import bean.EmployeeBean;

public class ApplicationDAO extends DAO {

	// =================================================================
	// 1. 新規追加：ページング用インナークラス（EmployeeDAOと同構造）
	// =================================================================
	public class PageResult<T> {
		private final List<T> items;
		private final boolean hasNext;
		public PageResult(List<T> items, boolean hasNext) {
			this.items = items;
			this.hasNext = hasNext;
		}
		public List<T> getItems() { return items; }
		public boolean hasNext() { return hasNext; }
	}

	// =================================================================
	// 2. 既存データ操作メソッド群（維持）
	// =================================================================

	/**
	 * 申請データをデータベースに登録する（新規登録専用）
	 */
	public int insert(ApplicationBean bean) {
		Connection con = dbConnect();
		int result = 0;
		String sql = "INSERT INTO applications (apct_id, emp_id, content, type, method, amount, reason, remark, urgent, status_id, create_date, update_date, is_deleted) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			if(con != null) {
				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, bean.getApctId());
				st.setString(2, bean.getEmployeeId());
				st.setString(3, bean.getContent());
				st.setString(4, bean.getType());
				st.setString(5, bean.getPaymentMethod());
				st.setInt(6, bean.getAmount()); 
				st.setString(7, bean.getReason());
				st.setString(8, bean.getNote());
				st.setString(9, bean.getUrgent());
				st.setInt(10, bean.getStatus_id());
				st.setTimestamp(11, bean.getCreateDate() != null ? Timestamp.valueOf(bean.getCreateDate()) : new Timestamp(System.currentTimeMillis()));
				st.setTimestamp(12, bean.getUpdateDate() != null ? Timestamp.valueOf(bean.getUpdateDate()) : new Timestamp(System.currentTimeMillis()));
				st.setInt(13, bean.isDeleted() ? 1 : 0);
				result = st.executeUpdate();
			}
		} catch(SQLException e) {
			System.out.println("insertエラー: " + e.getMessage());
		} finally {
			dbClose(con);
		}
		return result;
	}

	/**
	 * 修正画面から送られた申請データを更新する（UPDATE専用）
	 */
	public int update(ApplicationBean bean) {
		Connection con = dbConnect();
		int result = 0;
		String sql = "UPDATE applications SET type = ?, method = ?, amount = ?, content = ?, reason = ?, remark = ?, urgent = ?, update_date = ? "
				+ "WHERE apct_id = ? AND is_deleted = 0";
		try {
			if (con != null) {
				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, bean.getType());
				st.setString(2, bean.getPaymentMethod());
				st.setInt(3, bean.getAmount());
				st.setString(4, bean.getContent());
				st.setString(5, bean.getReason());
				st.setString(6, bean.getNote());
				st.setString(7, bean.getUrgent());
				st.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
				st.setString(9, bean.getApctId());
				result = st.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("ApplicationDAO updateエラー: " + e.getMessage());
		} finally {
			dbClose(con);
		}
		return result;
	}

	/**
	 * 申請のステータスIDと更新日時を個別に上書き更新する（UPDATE専用）
	 */
	public int updateStatus(String apctId, int nextStatusId, LocalDateTime updateDate) {
		Connection con = dbConnect();
		int result = 0;
		String sql = "UPDATE applications SET status_id = ?, update_date = ? WHERE apct_id = ? AND is_deleted = 0";
		try {
			if (con != null) {
				PreparedStatement st = con.prepareStatement(sql);
				st.setInt(1, nextStatusId);
				st.setTimestamp(2, Timestamp.valueOf(updateDate));
				st.setString(3, apctId);
				result = st.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("updateStatusエラー: " + e.getMessage());
		} finally {
			dbClose(con);
		}
		return result;
	}

	/**
	 * 指定された申請IDの削除フラグ(is_deleted)を1（削除済み）に更新する（論理削除）
	 */
	public int logicalDelete(String apctId) {
		Connection con = dbConnect();
		int result = 0;
		String sql = "UPDATE applications SET is_deleted = 1, update_date = ? WHERE apct_id = ? AND is_deleted = 0";
		try {
			if (con != null) {
				PreparedStatement st = con.prepareStatement(sql);
				st.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
				st.setString(2, apctId);
				result = st.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("ApplicationDAO logicalDeleteエラー: " + e.getMessage());
		} finally {
			dbClose(con);
		}
		return result;
	}

	// =================================================================
	// 3. マスタ参照系メソッド群（維持）
	// =================================================================

	public EmployeeBean selectEmployee(String empId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		EmployeeBean employee = null;
		String sql = "SELECT emp_id, emp_name, email, dpt_id, pos_id, is_deleted FROM employees WHERE emp_id = ? AND is_deleted = 0";
		try {
			if (con != null) {
				st = con.prepareStatement(sql);
				st.setString(1, empId);
				rs = st.executeQuery();
				if (rs.next()) {
					employee = new EmployeeBean(rs.getString("emp_id"), rs.getString("emp_name"), rs.getString("email"), rs.getString("dpt_id"), rs.getString("pos_id"));
					employee.setIs_deleted(rs.getBoolean("is_deleted"));
				}
			}
		} catch (SQLException e) {
			System.out.println("selectEmployeeエラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return employee;
	}

	public String selectDepartmentName(String dptId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		String dptName = "未所属";
		String sql = "SELECT dpt_name FROM departments WHERE dpt_id = ?";
		try {
			if (con != null) {
				st = con.prepareStatement(sql);
				st.setString(1, dptId);
				rs = st.executeQuery();
				if (rs.next()) dptName = rs.getString("dpt_name");
			}
		} catch (SQLException e) {
			System.out.println("selectDepartmentNameエラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return dptName;
	}

	public Integer selectPositionAmount(String posId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		Integer posAmount = null;
		String sql = "SELECT pos_amount FROM positions WHERE pos_id = ?";
		try {
			if (con != null) {
				st = con.prepareStatement(sql);
				st.setString(1, posId);
				rs = st.executeQuery();
				if (rs.next()) {
					int amount = rs.getInt("pos_amount");
					if (!rs.wasNull()) posAmount = amount;
				}
			}
		} catch (SQLException e) {
			System.out.println("selectPositionAmountエラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return posAmount;
	}

	public ApplicationBean findById(String apctId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		ApplicationBean b = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ")
		.append("s.status_name, e.emp_name, d.dpt_name ")
		.append("FROM applications a ")
		.append("JOIN status s ON a.status_id = s.status_id ")
		.append("JOIN employees e ON a.emp_id = e.emp_id ")
		.append("JOIN departments d ON e.dpt_id = d.dpt_id ")
		.append("WHERE a.apct_id = ? AND a.is_deleted = 0");
		try {
			if (con != null) {
				st = con.prepareStatement(sql.toString());
				st.setString(1, apctId);
				rs = st.executeQuery();
				if (rs.next()) {
					b = mapRowToBean(rs);
					b.setStatusName(rs.getString("status_name"));
					b.setEmployeeName(rs.getString("emp_name"));
					b.setDepartmentName(rs.getString("dpt_name"));
				}
			}
		} catch (SQLException e) {
			System.out.println("findByIdエラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return b;
	}

	// =================================================================
	// 4. 承認待ち・経理処理メソッド群（維持）
	// =================================================================

	public List<ApplicationBean> getPendingApplications(EmployeeBean employee) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<ApplicationBean> list = new ArrayList<>();
		if (employee == null) return list;

		String userDpt = employee.getDpt_id();
		String userPos = employee.getPos_id();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ")
		.append("s.status_name, e.emp_name, d.dpt_name ")
		.append("FROM applications a ")
		.append("JOIN employees e ON a.emp_id = e.emp_id ")
		.append("JOIN status s ON a.status_id = s.status_id ")
		.append("JOIN departments d ON e.dpt_id = d.dpt_id ")
		.append("WHERE a.is_deleted = 0 ");

		List<Object> params = new ArrayList<>();
		if ("E04".equals(userPos)) {
			sql.append("AND a.status_id = 1 AND ((e.dpt_id NOT LIKE 'D7%' AND e.pos_id = 'E02') OR (e.dpt_id LIKE 'D7%' AND e.pos_id = 'E03')) ");
		} else if ("E03".equals(userPos)) {
			if (userDpt.startsWith("D7")) {
				sql.append("AND a.status_id = 1 AND e.dpt_id LIKE 'D7%' AND e.pos_id = 'E02' ");
			} else {
				sql.append("AND a.status_id = 1 AND e.dpt_id LIKE ? AND e.emp_id != ? ");
				params.add(userDpt.substring(0, 3) + "%");
				params.add(employee.getEmp_id());
			}
		} else if ("E02".equals(userPos)) {
			if ("D100".equals(userDpt)) {
				sql.append("AND ((a.status_id = 1 AND e.dpt_id = 'D100' AND e.emp_id != ?) OR (a.status_id = 2)) ");
				params.add(employee.getEmp_id());
			} else if ("D712".equals(userDpt)) {
				sql.append("AND a.status_id = 1 AND e.dpt_id IN ('D710', 'D720') AND e.pos_id = 'E01' ");
			} else if ("D734".equals(userDpt)) {
				sql.append("AND a.status_id = 1 AND e.dpt_id IN ('D730', 'D740') AND e.pos_id = 'E01' ");
			} else {
				sql.append("AND a.status_id = 1 AND e.dpt_id LIKE ? AND e.emp_id != ? ");
				params.add(userDpt.substring(0, 3) + "%");
				params.add(employee.getEmp_id());
			}
		} else if ("E01".equals(userPos)) {
			if ("D100".equals(userDpt)) {
				sql.append("AND ((a.status_id = 1 AND e.dpt_id = 'D100' AND e.pos_id = 'E00' AND e.emp_id != ?) OR (a.status_id = 2)) ");
				params.add(employee.getEmp_id());
			} else {
				sql.append("AND a.status_id = 1 AND e.dpt_id = ? AND e.pos_id = 'E00' AND e.emp_id != ? ");
				params.add(userDpt);
				params.add(employee.getEmp_id());
			}
		} else {
			sql.append("AND 1 = 0 ");
		}
		sql.append("ORDER BY a.create_date DESC");

		try {
			if (con != null) {
				st = con.prepareStatement(sql.toString());
				for (int i = 0; i < params.size(); i++) st.setObject(i + 1, params.get(i));
				rs = st.executeQuery();
				while (rs.next()) {
					ApplicationBean b = mapRowToBean(rs);
					b.setStatusName(rs.getString("status_name"));
					b.setDepartmentName(rs.getString("dpt_name"));
					b.setEmployeeName(rs.getString("emp_name"));
					list.add(b);
				}
			}
		} catch (SQLException e) {
			System.out.println("getPendingApplicationsエラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException ex) {}
			if (st != null) try { st.close(); } catch (SQLException ex) {}
			dbClose(con);
		}
		return list;
	}

	public List<ApplicationBean> getHistoryApplications(EmployeeBean employee, String scope, String statusFilter) {
		// ※新設された下部の searchApplications メソッドにロジックを集約・一元化するため、
		// 既存の履歴一覧表示を呼び出す箇所との互換用、あるいは全件取得用のラッパーとして残しています。
		try {
			PageResult<ApplicationBean> pr = searchApplications(employee, scope, statusFilter, null, null, null, null, null, "date", "desc", 9999, 0);
			return pr.getItems();
		} catch (SQLException e) {
			return new ArrayList<>();
		}
	}

	public List<ApplicationBean> getAccountingApplications() {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<ApplicationBean> list = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ")
		.append("s.status_name, e.emp_name, d.dpt_name ")
		.append("FROM applications a ")
		.append("JOIN employees e ON a.emp_id = e.emp_id ")
		.append("JOIN status s ON a.status_id = s.status_id ")
		.append("JOIN departments d ON e.dpt_id = d.dpt_id ")
		.append("WHERE a.is_deleted = 0 AND a.status_id IN (3, 4) ")
		.append("ORDER BY a.create_date DESC");
		try {
			if (con != null) {
				st = con.prepareStatement(sql.toString());
				rs = st.executeQuery();
				while (rs.next()) {
					ApplicationBean b = mapRowToBean(rs);
					b.setStatusName(rs.getString("status_name"));
					b.setDepartmentName(rs.getString("dpt_name"));
					b.setEmployeeName(rs.getString("emp_name"));
					list.add(b);
				}
			}
		} catch (SQLException e) {
			System.out.println("getAccountingApplicationsエラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException ex) {}
			if (st != null) try { st.close(); } catch (SQLException ex) {}
			dbClose(con);
		}
		return list;
	}

	// =================================================================
	// 5. 新規統合：検索・ソート・ページング対応動的SQLメソッド（修正版）
	// =================================================================
	public PageResult<ApplicationBean> searchApplications(
			EmployeeBean loginUser, String scope, String statusFilter,
			String qStatus, String qName, String qDepartment, String qType, String qAmount,
			String sortKey, String sortDir, int limit, int offset) throws SQLException {

		// 【4-2. ソートキーのマッピング拡張】
		// 要件：申請状況、名前（ふりがな連動）、日付順、部門（部署名）、金額に対応
		Map<String, String> colMap = Map.of(
				"status", "a.status_id",
				"name",   "COALESCE(e.furigana, e.emp_name)", // 名前選択時はe.furiganaを最優先してソート
				"date",   "a.create_date",
				"dpt",    "d.dpt_name",                       // 部門（部署名順）ソートを追加
				"amount", "a.amount"                          // 金額順ソートを追加
				);

		if (sortKey == null || sortKey.isEmpty()) sortKey = "date";
		String orderBy = colMap.getOrDefault(sortKey, "a.create_date");

		// 昇順(ASC)・降順(DESC)の安全な判定
		String dir = "ASC".equalsIgnoreCase(sortDir) ? "ASC" : "DESC";

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, ")
		.append("a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ")
		.append("s.status_name, e.emp_name AS emp_name, d.dpt_name AS dpt_name ")
		.append("FROM applications a ")
		.append("JOIN employees e ON a.emp_id = e.emp_id ")
		.append("JOIN status s ON a.status_id = s.status_id ")
		.append("JOIN departments d ON e.dpt_id = d.dpt_id ")
		.append("WHERE a.is_deleted = 0 ");

		ArrayList<Object> params = new ArrayList<>();
		String posId = loginUser.getPos_id();
		String userDpt = loginUser.getDpt_id();

		// -----------------------------------------------------------------
		// 防壁：組織階層に応じた範囲制限の結合
		// -----------------------------------------------------------------
		if ("self".equals(scope) || scope == null || scope.isEmpty()) {
			sql.append("AND a.emp_id = ? ");
			params.add(loginUser.getEmp_id());

		} else if ("subordinate".equals(scope)) {
			if ("D712".equals(userDpt)) {
				sql.append("AND e.dpt_id IN ('D710', 'D720', 'D712') ");
			} else if ("D734".equals(userDpt)) {
				sql.append("AND e.dpt_id IN ('D730', 'D740', 'D734') ");
			} else if (userDpt != null && userDpt.length() >= 3) {
				if ("E03".equals(posId) || "E02".equals(posId)) {
					sql.append("AND e.dpt_id LIKE ? ");
					params.add(userDpt.substring(0, 2) + "%");
				} else if ("E01".equals(posId)) {
					sql.append("AND e.dpt_id = ? ");
					params.add(userDpt);
				}
			}
			sql.append("AND e.pos_id < ? AND a.emp_id != ? ");
			params.add(posId);
			params.add(loginUser.getEmp_id());

		} else if ("management".equals(scope)) {
			if (!"D100".equals(userDpt)) {
				sql.append("AND a.emp_id = ? ");
				params.add(loginUser.getEmp_id());
			}
		}

		// 簡易タブフィルター条件
		if ("incomplete".equals(statusFilter)) {
			sql.append("AND a.status_id IN (1, 2, 3, 4) ");
		}

		// -----------------------------------------------------------------
		// 【4-3. 検索機能の動的条件追加】
		// -----------------------------------------------------------------
		// ① 申請状況
		if (qStatus != null && !qStatus.isEmpty()) {
			sql.append("AND a.status_id = ? ");
			params.add(Integer.parseInt(qStatus));
		}

		// ② 名前（課長クラス以上のみ有効）
		if (qName != null && !qName.isEmpty()) {
			if (!"E00".equals(posId)) { 
				sql.append("AND e.emp_name LIKE ? ");
				params.add("%" + qName + "%");
			}
		}

		// ③ 部門（本部長クラス以上のみ有効）
		if (qDepartment != null && !qDepartment.isEmpty()) {
			if ("E03".equals(posId) || "E04".equals(posId)) {
				sql.append("AND e.dpt_id = ? ");
				params.add(qDepartment);
			}
		}

		// ④ 種別
		if (qType != null && !qType.isEmpty()) {
			sql.append("AND a.type = ? ");
			params.add(qType);
		}

		// ⑤ 金額（指定値以上の下限検索）
		if (qAmount != null && !qAmount.isEmpty()) {
			sql.append("AND a.amount >= ? ");
			params.add(Integer.parseInt(qAmount));
		}

		// ソート指定の構築と結合（第2ソートとして主キーを固定してページングのブレを防ぐ）
		sql.append(" ORDER BY ").append(orderBy).append(" ").append(dir).append(", a.apct_id DESC ");
		sql.append(" LIMIT ? OFFSET ?");

		ArrayList<ApplicationBean> list = new ArrayList<>();
		try (Connection con = dbConnect();
				PreparedStatement ps = con.prepareStatement(sql.toString())) {

			int idx = 1;
			for (Object p : params) {
				ps.setObject(idx++, p);
			}
			int effectiveLimit = limit + 1;
			ps.setInt(idx++, effectiveLimit);
			ps.setInt(idx++, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ApplicationBean b = mapRowToBean(rs);
					b.setStatusName(rs.getString("status_name"));
					b.setEmployeeName(rs.getString("emp_name"));
					b.setDepartmentName(rs.getString("dpt_name"));
					list.add(b);
				}
			}
		}

		// 次ページの有無判定
		boolean hasNext = false;
		if (list.size() > limit) {
			hasNext = true;
			list.remove(list.size() - 1);
		}
		return new PageResult<>(list, hasNext);
	}

	// =================================================================
	// 6. 共通補助マッピング関数（維持）
	// =================================================================
	private ApplicationBean mapRowToBean(ResultSet rs) throws SQLException {
		ApplicationBean b = new ApplicationBean();
		b.setApctId(rs.getString("apct_id"));
		b.setEmployeeId(rs.getString("emp_id"));
		b.setContent(rs.getString("content"));
		b.setType(rs.getString("type"));
		b.setPaymentMethod(rs.getString("method"));
		b.setAmount(rs.getInt("amount"));
		b.setReason(rs.getString("reason"));
		b.setNote(rs.getString("remark"));
		b.setUrgent(rs.getString("urgent")); 
		b.setStatus_id(rs.getInt("status_id"));
		b.setDeleted(rs.getInt("is_deleted") == 1); 

		Timestamp createTs = rs.getTimestamp("create_date");
		if (createTs != null) b.setCreateDate(createTs.toLocalDateTime());
		Timestamp updateTs = rs.getTimestamp("update_date");
		if (updateTs != null) b.setUpdateDate(updateTs.toLocalDateTime());

		return b;
	}
}