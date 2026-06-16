package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import bean.ApplicationBean;
import bean.EmployeeBean;

public class ApplicationDAO extends DAO {
	
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
				
				if (bean.getCreateDate() != null) {
					st.setTimestamp(11, Timestamp.valueOf(bean.getCreateDate()));
				} else {
					st.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
				}
				
				if (bean.getUpdateDate() != null) {
					st.setTimestamp(12, Timestamp.valueOf(bean.getUpdateDate()));
				} else {
					st.setTimestamp(12, new Timestamp(System.currentTimeMillis()));
				}
				
				st.setInt(13, bean.isDeleted() ? 1 : 0);

				result = st.executeUpdate();
			}
		} catch(SQLException e) {
			System.out.println("insertエラー");
			System.out.println(e.getMessage());
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
				st.setTimestamp(8, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
				st.setString(9, bean.getApctId());

				result = st.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("ApplicationDAO updateエラー");
			System.out.println(e.getMessage());
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
			System.out.println("updateStatusエラー");
			System.out.println(e.getMessage());
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
				st.setTimestamp(1, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
				st.setString(2, apctId);
				
				result = st.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("ApplicationDAO logicalDeleteエラー");
			System.out.println(e.getMessage());
		} finally {
			dbClose(con);
		}
		
		return result;
	}

	/**
	 * 社員IDを基に、指定されたEmployeeBeanを取得する
	 */
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
					employee = new EmployeeBean();
					employee.setEmp_id(rs.getString("emp_id"));
					employee.setEmp_name(rs.getString("emp_name"));
					employee.setEmail(rs.getString("email"));
					employee.setDpt_id(rs.getString("dpt_id"));
					employee.setPos_id(rs.getString("pos_id"));
					employee.setIs_deleted(rs.getBoolean("is_deleted"));
				}
			}
		} catch (SQLException e) {
			System.out.println("selectEmployeeエラー");
			System.out.println(e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return employee;
	}

	/**
	 * 部署IDを基に、部署名を取得する
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

				if (rs.next()) {
					dptName = rs.getString("dpt_name");
				}
			}
		} catch (SQLException e) {
			System.out.println("selectDepartmentNameエラー");
			System.out.println(e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return dptName;
	}

	/**
	 * 役職IDを基に、その役職の申請上限金額を取得する
	 * @param posId 役職ID
	 * @return 上限金額（上限がない場合は null を返す）
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
					if (!rs.wasNull()) {
						posAmount = amount;
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("selectPositionAmountエラー");
			System.out.println(e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return posAmount;
	}

	/**
	 * 申請IDをキーに、部署名・氏名・ステータス名を含めた単一の申請データを取得する
	 */
	public ApplicationBean findById(String apctId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		ApplicationBean b = null;

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ");
		sql.append("       s.status_name, e.emp_name, d.dpt_name ");
		sql.append("FROM applications a ");
		sql.append("JOIN status s ON a.status_id = s.status_id ");
		sql.append("JOIN employees e ON a.emp_id = e.emp_id ");
		sql.append("JOIN departments d ON e.dpt_id = d.dpt_id ");
		sql.append("WHERE a.apct_id = ? AND a.is_deleted = 0");

		try {
			if (con != null) {
				st = con.prepareStatement(sql.toString());
				st.setString(1, apctId);
				rs = st.executeQuery();

				if (rs.next()) {
					b = new ApplicationBean();
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

					b.setStatusName(rs.getString("status_name"));
					b.setEmployeeName(rs.getString("emp_name"));
					b.setDepartmentName(rs.getString("dpt_name"));

					Timestamp createTs = rs.getTimestamp("create_date");
					if (createTs != null) {
						b.setCreateDate(createTs.toLocalDateTime());
					}
					Timestamp updateTs = rs.getTimestamp("update_date");
					if (updateTs != null) {
						b.setUpdateDate(updateTs.toLocalDateTime());
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("findByIdエラー");
			System.out.println(e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return b;
	}

	/**
	 * 検索条件（範囲指定順序修正）とソート順を動的に反映する未承認申請一覧取得ロジック
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

		// ベースとなるSQL文の組み立て
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ");
		sql.append("       s.status_name, e.emp_name, d.dpt_name ");
		sql.append("FROM applications a ");
		sql.append("JOIN employees e ON a.emp_id = e.emp_id ");
		sql.append("JOIN status s ON a.status_id = s.status_id ");
		sql.append("JOIN departments d ON e.dpt_id = d.dpt_id "); 
		sql.append("WHERE a.is_deleted = 0 ");

		List<Object> params = new ArrayList<>();

		// 権限ベースによる絞り込みブロック
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

		// 動的検索条件の追加ブロック
		if (searchDept != null && !searchDept.trim().isEmpty()) {
			sql.append("AND d.dpt_name LIKE ? ");
			params.add("%" + searchDept.trim() + "%");
		}
		
		if (searchName != null && !searchName.trim().isEmpty()) {
			sql.append("AND e.emp_name LIKE ? ");
			params.add("%" + searchName.trim() + "%");
		}
		
		if (searchAmountMin != null && !searchAmountMin.trim().isEmpty()) {
			try {
				sql.append("AND a.amount >= ? ");
				params.add(Integer.parseInt(searchAmountMin.trim()));
			} catch (NumberFormatException e) {
				// スキップ
			}
		}
		
		if (searchAmountMax != null && !searchAmountMax.trim().isEmpty()) {
			try {
				sql.append("AND a.amount <= ? ");
				params.add(Integer.parseInt(searchAmountMax.trim()));
			} catch (NumberFormatException e) {
				// スキップ
			}
		}
		
		if (searchUrgent != null && !searchUrgent.trim().isEmpty()) {
			sql.append("AND a.urgent = ? ");
			params.add(searchUrgent.trim());
		}

		// ソート条件の追加ブロック
		String orderByColumn = "a.create_date"; 
		if ("dept".equals(sortColumn)) orderByColumn = "d.dpt_name";
		else if ("name".equals(sortColumn)) orderByColumn = "e.emp_name";
		else if ("amount".equals(sortColumn)) orderByColumn = "a.amount";
		else if ("urgent".equals(sortColumn)) orderByColumn = "a.urgent";

		String orderByOrder = "DESC"; 
		if ("ASC".equalsIgnoreCase(sortOrder)) {
			orderByOrder = "ASC";
		}

		sql.append("ORDER BY ").append(orderByColumn).append(" ").append(orderByOrder);

		try {
			if (con != null) {
				st = con.prepareStatement(sql.toString());
				for (int i = 0; i < params.size(); i++) {
					st.setObject(i + 1, params.get(i));
				}
				rs = st.executeQuery();

				while (rs.next()) {
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
					b.setStatusName(rs.getString("status_name"));
					b.setDepartmentName(rs.getString("dpt_name"));
					b.setEmployeeName(rs.getString("emp_name"));

					Timestamp createTs = rs.getTimestamp("create_date");
					if (createTs != null) {
						b.setCreateDate(createTs.toLocalDateTime());
					}
					Timestamp updateTs = rs.getTimestamp("update_date");
					if (updateTs != null) {
						b.setUpdateDate(updateTs.toLocalDateTime());
					}
					list.add(b);
				}
			}
		} catch (SQLException e) {
			System.out.println("getPendingApplications 確定版エラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException ex) {}
			if (st != null) try { st.close(); } catch (SQLException ex) {}
			dbClose(con);
		}
		return list;
	}
	/**
	 * 申請履歴一覧を条件（対象範囲、ステータス、ログインユーザーの役職・部署）に応じて取得する
	 */
	public List<ApplicationBean> getHistoryApplications(EmployeeBean employee, String scope, String statusFilter) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<ApplicationBean> list = new ArrayList<>();

		if (employee == null) {
			return list;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ");
		sql.append("       s.status_name, e.emp_name, d.dpt_name ");
		sql.append("FROM applications a ");
		sql.append("JOIN employees e ON a.emp_id = e.emp_id ");
		sql.append("JOIN status s ON a.status_id = s.status_id ");
		sql.append("JOIN departments d ON e.dpt_id = d.dpt_id ");
		sql.append("WHERE a.is_deleted = 0 ");

		List<Object> params = new ArrayList<>();
		
		if ("self".equals(scope) || scope == null || scope.isEmpty()) {
			sql.append("AND a.emp_id = ? ");
			params.add(employee.getEmp_id());
			
		} else if ("subordinate".equals(scope)) {
			String userPos = employee.getPos_id();
			String userDpt = employee.getDpt_id();

			if ("D712".equals(userDpt)) {
				sql.append("AND e.dpt_id IN ('D710', 'D720', 'D712') ");
			} else if ("D734".equals(userDpt)) {
				sql.append("AND e.dpt_id IN ('D730', 'D740', 'D734') ");
			} else if (userDpt != null && userDpt.length() >= 3) {
				if ("E03".equals(userPos) || "E02".equals(userPos)) {
					String dptPrefix = userDpt.substring(0, 2) + "%";
					sql.append("AND e.dpt_id LIKE ? ");
					params.add(dptPrefix);
				} else if ("E01".equals(userPos)) {
					sql.append("AND e.dpt_id = ? ");
					params.add(userDpt);
				}
			}

			sql.append("AND e.pos_id < ? ");
			params.add(userPos);
			
			sql.append("AND a.emp_id != ? ");
			params.add(employee.getEmp_id());
		} else if ("management".equals(scope)) {
			if ("D100".equals(employee.getDpt_id())) {
				// 全社員をロードするため追加の条件なし
			} else {
				sql.append("AND a.emp_id = ? ");
				params.add(employee.getEmp_id());
			}
		}

		if ("incomplete".equals(statusFilter)) {
			sql.append("AND a.status_id IN (1, 2, 3, 4) ");
		}

		sql.append("ORDER BY a.create_date DESC");

		try {
			if (con != null) {
				st = con.prepareStatement(sql.toString());
				for (int i = 0; i < params.size(); i++) {
					st.setObject(i + 1, params.get(i));
				}
				rs = st.executeQuery();

				while (rs.next()) {
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
					b.setStatusName(rs.getString("status_name"));
					b.setDepartmentName(rs.getString("dpt_name"));
					b.setEmployeeName(rs.getString("emp_name"));

					Timestamp createTs = rs.getTimestamp("create_date");
					if (createTs != null) {
						b.setCreateDate(createTs.toLocalDateTime());
					}
					Timestamp updateTs = rs.getTimestamp("update_date");
					if (updateTs != null) {
						b.setUpdateDate(updateTs.toLocalDateTime());
					}
					list.add(b);
				}
			}
		} catch (SQLException e) {
			System.out.println("getHistoryApplicationsエラー");
			System.out.println(e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return list;
	}

	/**
	 * 経理部用に、管理部承認済み(3)または社長承認済み(4)の申請一覧を取得する
	 */
	public List<ApplicationBean> getAccountingApplications() {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<ApplicationBean> list = new ArrayList<>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ");
		sql.append("       s.status_name, e.emp_name, d.dpt_name ");
		sql.append("FROM applications a ");
		sql.append("JOIN employees e ON a.emp_id = e.emp_id ");
		sql.append("JOIN status s ON a.status_id = s.status_id ");
		sql.append("JOIN departments d ON e.dpt_id = d.dpt_id ");
		sql.append("WHERE a.is_deleted = 0 AND a.status_id IN (3, 4) ");
		sql.append("ORDER BY a.create_date DESC");

		try {
			if (con != null) {
				st = con.prepareStatement(sql.toString());
				rs = st.executeQuery();

				while (rs.next()) {
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
					b.setStatusName(rs.getString("status_name"));
					b.setDepartmentName(rs.getString("dpt_name"));
					b.setEmployeeName(rs.getString("emp_name"));

					Timestamp createTs = rs.getTimestamp("create_date");
					if (createTs != null) b.setCreateDate(createTs.toLocalDateTime());
					Timestamp updateTs = rs.getTimestamp("update_date");
					if (updateTs != null) b.setUpdateDate(updateTs.toLocalDateTime());
					
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
	 * ResultSet -> Bean マッピング（内部補助関数）
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
		if (createTs != null) {
			b.setCreateDate(createTs.toLocalDateTime());
		}
		
		Timestamp updateTs = rs.getTimestamp("update_date");
		if (updateTs != null) {
			b.setUpdateDate(updateTs.toLocalDateTime());
		}
		
		return b;
	}
}