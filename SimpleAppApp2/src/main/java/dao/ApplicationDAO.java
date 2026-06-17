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

	/**
	 * 0. ページング用静的インナークラス
	 */
	public static class PageResult<T> {
		private final List<T> items;
		private final boolean hasNext;

		public PageResult(List<T> items, boolean hasNext) {
			this.items = items;
			this.hasNext = hasNext;
		}
		public List<T> getItems() { return items; }
		public boolean hasNext() { return hasNext; }
	}

	/**
	 * 1. 申請データをデータベースに登録する（新規登録専用）
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
	 * 2. 修正画面から送られた申請データを更新する（UPDATE専用）
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
	 * 3. 申請のステータスIDと更新日時を個別に上書き更新する（UPDATE専用）
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
	 * 4. 指定された申請IDの削除フラグ(is_deleted)を1（削除済み）に更新する（論理削除）
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

	/**
	 * 5. 社員IDを基に、指定されたEmployeeBeanを取得する（furigana対応済）
	 */
	public EmployeeBean selectEmployee(String empId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		EmployeeBean employee = null;
		String sql = "SELECT emp_id, emp_name, furigana, email, dpt_id, pos_id, is_deleted FROM employees WHERE emp_id = ? AND is_deleted = 0";
		try {
			if (con != null) {
				st = con.prepareStatement(sql);
				st.setString(1, empId);
				rs = st.executeQuery();
				if (rs.next()) {
					employee = new EmployeeBean(
						rs.getString("emp_id"), 
						rs.getString("emp_name"), 
						rs.getString("furigana"), 
						rs.getString("email"), 
						rs.getString("dpt_id"), 
						rs.getString("pos_id")
					);
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

	/**
	 * 6. 部署IDを基に、部署名を取得する
	 */
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

	/**
	 * 7. 役職IDを基に、その役職の申請上限金額を取得する
	 */
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

	/**
	 * 8. 申請IDをキーに単一の申請データを取得する
	 */
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

	/**
	 * 9. 管理部の上長のみに権限を絞り取得する未承認申請一覧ロジック（基本版）
	 */
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

	/**
	 * 10. 検索条件とソート順を動的に反映する未承認申請一覧取得ロジック（全項目ソート対応版）
	 */
	public List<ApplicationBean> getPendingApplications(
			EmployeeBean employee, 
			String searchDept, 
			String searchName, 
			String searchAmountMin, 
			String searchAmountMax, 
			String searchUrgent, 
			String sortColumn, 
			String sortOrder) {
		
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<ApplicationBean> list = new ArrayList<>();

		if (employee == null) {
			return list;
		}

		String userDpt = employee.getDpt_id();
		String userPos = employee.getPos_id();

		Map<String, String> colMap = Map.of(
			"date",   "a.create_date",                   // 申請日
			"id",     "a.apct_id",                       // 申請ID
			"dept",   "d.dpt_name",                      // 申請者の部署（漢字）
			"name",   "COALESCE(e.furigana, e.emp_name)",// 申請者の氏名（ふりがな/五十音）
			"type",   "a.type",                          // 申請種別
			"amount", "a.amount",                        // 申請金額
			"urgent", "a.urgent"                         // 緊急度
		);

		String orderBy = colMap.getOrDefault(sortColumn, "a.create_date");
		String dir = "ASC".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC";

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ");
		sql.append("       s.status_name, e.emp_name, d.dpt_name ");
		sql.append("FROM applications a ");
		sql.append("JOIN employees e ON a.emp_id = e.emp_id ");
		sql.append("JOIN status s ON a.status_id = s.status_id ");
		sql.append("JOIN departments d ON e.dpt_id = d.dpt_id "); 
		sql.append("WHERE a.is_deleted = 0 ");

		List<Object> params = new ArrayList<>();

		if ("E04".equals(userPos)) {
			sql.append("AND a.status_id = 1 AND ( (e.dpt_id NOT LIKE 'D7%' AND e.pos_id = 'E02') OR (e.dpt_id LIKE 'D7%' AND e.pos_id = 'E03') ) ");
		} else if ("E03".equals(userPos)) {
			if (userDpt.startsWith("D7")) {
				sql.append("AND a.status_id = 1 AND e.dpt_id LIKE 'D7%' AND e.pos_id = 'E02' ");
			} else {
				String deptPrefix = userDpt.substring(0, 3) + "%";
				sql.append("AND a.status_id = 1 AND e.dpt_id LIKE ? AND e.emp_id != ? ");
				params.add(deptPrefix);
				params.add(employee.getEmp_id());
			}
		} else if ("E02".equals(userPos)) {
			if ("D100".equals(userDpt)) {
				sql.append("AND ( (a.status_id = 1 AND e.dpt_id = 'D100' AND e.emp_id != ?) OR (a.status_id = 2) ) ");
				params.add(employee.getEmp_id());
			} else if ("D712".equals(userDpt)) {
				sql.append("AND a.status_id = 1 AND e.dpt_id IN ('D710', 'D720') AND e.pos_id = 'E01' ");
			} else if ("D734".equals(userDpt)) {
				sql.append("AND a.status_id = 1 AND e.dpt_id IN ('D730', 'D740') AND e.pos_id = 'E01' ");
			} else {
				String deptPrefix = userDpt.substring(0, 3) + "%";
				sql.append("AND a.status_id = 1 AND e.dpt_id LIKE ? AND e.emp_id != ? ");
				params.add(deptPrefix);
				params.add(employee.getEmp_id());
			}
		} else if ("E01".equals(userPos)) {
			if ("D100".equals(userDpt)) {
				sql.append("AND ( (a.status_id = 1 AND e.dpt_id = 'D100' AND e.pos_id = 'E00' AND e.emp_id != ?) OR (a.status_id = 2) ) ");
				params.add(employee.getEmp_id());
			} else {
				sql.append("AND a.status_id = 1 AND e.dpt_id = ? AND e.pos_id = 'E00' AND e.emp_id != ? ");
				params.add(userDpt);
				params.add(employee.getEmp_id());
			}
		} else {
			sql.append("AND 1 = 0 ");
		}

		if (searchDept != null && !searchDept.trim().isEmpty()) {
			sql.append("AND d.dpt_name LIKE ? ");
			params.add("%" + searchDept.trim() + "%");
		}
		
		if (searchName != null && !searchName.trim().isEmpty()) {
			sql.append("AND (e.emp_name LIKE ? OR e.furigana LIKE ?) ");
			String nameParam = "%" + searchName.trim() + "%";
			params.add(nameParam);
			params.add(nameParam);
		}
		
		if (searchAmountMin != null && !searchAmountMin.trim().isEmpty()) {
			try {
				sql.append("AND a.amount >= ? ");
				params.add(Integer.parseInt(searchAmountMin.trim()));
			} catch (NumberFormatException e) {}
		}
		
		if (searchAmountMax != null && !searchAmountMax.trim().isEmpty()) {
			try {
				sql.append("AND a.amount <= ? ");
				params.add(Integer.parseInt(searchAmountMax.trim()));
			} catch (NumberFormatException e) {}
		}
		
		if (searchUrgent != null && !searchUrgent.trim().isEmpty()) {
			sql.append("AND a.urgent = ? ");
			params.add(searchUrgent.trim());
		}

		sql.append("ORDER BY ").append(orderBy).append(" ").append(dir).append(", a.apct_id DESC");

		try {
			if (con != null) {
				st = con.prepareStatement(sql.toString());
				for (int i = 0; i < params.size(); i++) {
					st.setObject(i + 1, params.get(i));
				}
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
			System.out.println("getPendingApplications 検索版エラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException ex) {}
			if (st != null) try { st.close(); } catch (SQLException ex) {}
			dbClose(con);
		}
		return list;
	}

	/**
	 * 11. 申請履歴一覧を取得する（内部でsearchApplicationsを利用）
	 */
	public List<ApplicationBean> getHistoryApplications(EmployeeBean employee, String scope, String statusFilter) {
		try {
			PageResult<ApplicationBean> pr = searchApplications(
					employee, scope, statusFilter, 
					null, null, null, null, null, null, 
					"date", "desc", 9999, 0);
			return pr.getItems();
		} catch (SQLException e) {
			System.out.println("getHistoryApplicationsエラー: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * 12. 履歴一覧のページングおよび詳細検索を行う（申請内容ソート除外版）
	 */
	public PageResult<ApplicationBean> searchApplications(
			EmployeeBean loginUser, String scope, String statusFilter,
			String qStatus, String qName, String qDepartment, String qType, 
			Integer qAmountMin, Integer qAmountMax, 
			String sortKey, String sortDir, int limit, int offset) throws SQLException {

		Map<String, String> colMap = Map.of(
				"id",     "a.apct_id",                        // 申請ID
				"name",   "COALESCE(e.furigana, e.emp_name)", // 申請者名
				"dpt",    "d.dpt_name",                       // 部門
				"type",   "a.type",                           // 申請種別
				"method", "a.method",                         // 支払方法
				"amount", "a.amount",                         // 金額
				"status", "a.status_id",                      // 申請状況
				"date",   "a.create_date"                     // 申請日
				);

		if (sortKey == null || sortKey.isEmpty()) sortKey = "date";
		String orderBy = colMap.getOrDefault(sortKey, "a.create_date");
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

		if ("incomplete".equals(statusFilter)) {
			sql.append("AND a.status_id IN (1, 2, 3, 4) ");
		}

		if (qStatus != null && !qStatus.isEmpty()) {
			sql.append("AND a.status_id = ? ");
			params.add(Integer.parseInt(qStatus));
		}

		if (qName != null && !qName.isEmpty()) {
			if (!"E00".equals(posId)) { 
				sql.append("AND (e.emp_name LIKE ? OR e.furigana LIKE ?) ");
				params.add("%" + qName + "%");
				params.add("%" + qName + "%");
			}
		}

		if (qDepartment != null && !qDepartment.isEmpty()) {
			if ("E03".equals(posId) || "E04".equals(posId)) {
				sql.append("AND e.dpt_id = ? ");
				params.add(qDepartment);
			}
		}

		if (qType != null && !qType.isEmpty()) {
			sql.append("AND a.type = ? ");
			params.add(qType);
		}

		if (qAmountMin != null) {
			sql.append("AND a.amount >= ? ");
			params.add(qAmountMin);
		}

		if (qAmountMax != null) {
			sql.append("AND a.amount <= ? ");
			params.add(qAmountMax);
		}

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

		boolean hasNext = false;
		if (list.size() > limit) {
			hasNext = true;
			list.remove(list.size() - 1);
		}
		return new PageResult<>(list, hasNext);
	}

	/**
	 * 13. 経理部用に、管理部承認済み(3)または社長承認済み(4)の申請一覧を取得する（基本版）
	 */
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

	/**
	 * 14. 経理部専用：検索・ソート・ページング対応動的SQLメソッド
	 */
	public PageResult<ApplicationBean> searchAccountingApplications(
			String qStatus, String qName, String qType, 
			Integer qAmountMin, Integer qAmountMax, String qUrgent,
			String sortKey, String sortDir, int limit, int offset) throws SQLException {

		Map<String, String> colMap = Map.of(
				"id",     "a.apct_id",
				"status", "a.status_id",
				"name",   "COALESCE(e.furigana, e.emp_name)",
				"date",   "a.create_date",
				"dpt",    "d.dpt_name",
				"type",   "a.type",
				"amount", "a.amount",
				"urgent", "a.urgent"
				);

		if (sortKey == null || sortKey.isEmpty()) sortKey = "date";
		String orderBy = colMap.getOrDefault(sortKey, "a.create_date");
		String dir = "ASC".equalsIgnoreCase(sortDir) ? "ASC" : "DESC";

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, ")
		.append("a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ")
		.append("s.status_name, e.emp_name AS emp_name, d.dpt_name AS dpt_name ")
		.append("FROM applications a ")
		.append("JOIN employees e ON a.emp_id = e.emp_id ")
		.append("JOIN status s ON a.status_id = s.status_id ")
		.append("JOIN departments d ON e.dpt_id = d.dpt_id ")
		.append("WHERE a.is_deleted = 0 AND a.status_id IN (3, 4) ");

		ArrayList<Object> params = new ArrayList<>();

		if (qStatus != null && !qStatus.isEmpty()) {
			sql.append("AND a.status_id = ? ");
			params.add(Integer.parseInt(qStatus));
		}

		if (qName != null && !qName.isEmpty()) {
			sql.append("AND (e.emp_name LIKE ? OR e.furigana LIKE ?) ");
			params.add("%" + qName + "%"); 
			params.add("%" + qName + "%"); 
		}

		if (qType != null && !qType.isEmpty()) {
			sql.append("AND a.type = ? ");
			params.add(qType);
		}

		if (qAmountMin != null) {
			sql.append("AND a.amount >= ? ");
			params.add(qAmountMin);
		}

		if (qAmountMax != null) {
			sql.append("AND a.amount <= ? ");
			params.add(qAmountMax);
		}

		if (qUrgent != null && !qUrgent.isEmpty()) {
			sql.append("AND a.urgent = ? ");
			params.add(qUrgent);
		}

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

		boolean hasNext = false;
		if (list.size() > limit) {
			hasNext = true;
			list.remove(list.size() - 1);
		}
		return new PageResult<>(list, hasNext);
	}

	/**
	 * 15. ResultSet -> Bean マッピング（内部補助関数）
	 */
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