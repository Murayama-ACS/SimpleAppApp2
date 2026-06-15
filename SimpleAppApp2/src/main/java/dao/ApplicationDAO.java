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
	 * 管理部の上長のみに権限を絞り、status_id=2 も合算して取得する未承認申請一覧取得ロジック
	 */
	public List<ApplicationBean> getPendingApplications(EmployeeBean employee) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<ApplicationBean> list = new ArrayList<>();

		if (employee == null) {
			return list;
		}

		String userDpt = employee.getDpt_id(); // ログインユーザーの部署ID
		String userPos = employee.getPos_id(); // ログインユーザーの役職コード ("E04", "E02", "E03", "E01", "E00")

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
			// 【社長ルール】（変更なし）
			sql.append("AND a.status_id = 1 ");
			sql.append("AND ( ");
			sql.append("  (e.dpt_id NOT LIKE 'D7%' AND e.pos_id = 'E02') ");
			sql.append("  OR (e.dpt_id LIKE 'D7%' AND e.pos_id = 'E03') ");
			sql.append(") ");

		} else if ("E03".equals(userPos)) {
			// 【本部長ルール】（変更なし）
			if (userDpt.startsWith("D7")) {
				sql.append("AND a.status_id = 1 AND e.dpt_id LIKE 'D7%' AND e.pos_id = 'E02' ");
			} else {
				String deptPrefix = userDpt.substring(0, 3) + "%";
				sql.append("AND a.status_id = 1 AND e.dpt_id LIKE ? AND e.emp_id != ? ");
				params.add(deptPrefix);
				params.add(employee.getEmp_id());
			}

		} else if ("E02".equals(userPos)) {
			// 【部長ルール】
			if ("D100".equals(userDpt)) {
				// 管理部部長の場合：
				// ①管理部内の部下の未承認申請(status_id=1)
				// ②他部署から上がってきた管理部承認待ち申請(status_id=2) の両方を表示
				sql.append("AND ( ");
				sql.append("  (a.status_id = 1 AND e.dpt_id = 'D100' AND e.emp_id != ?) ");
				sql.append("  OR (a.status_id = 2) ");
				sql.append(") ");
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
			// 【課長ルール】
			if ("D100".equals(userDpt)) {
				// 管理部課長の場合：
				// ①管理部内の一般社員の未承認申請(status_id=1)
				// ②他部署から上がってきた管理部承認待ち申請(status_id=2) の両方を表示
				sql.append("AND ( ");
				sql.append("  (a.status_id = 1 AND e.dpt_id = 'D100' AND e.pos_id = 'E00' AND e.emp_id != ?) ");
				sql.append("  OR (a.status_id = 2) ");
				sql.append(") ");
				params.add(employee.getEmp_id());
			} else {
				sql.append("AND a.status_id = 1 AND e.dpt_id = ? AND e.pos_id = 'E00' AND e.emp_id != ? ");
				params.add(userDpt);
				params.add(employee.getEmp_id());
			}

		} else {
			// 管理部の一般社員(E00)もここに含まれ、他人の申請は見えなくなります
			sql.append("AND 1 = 0 ");
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
			System.out.println("getPendingApplications管理部上長限定版エラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException ex) {}
			if (st != null) try { st.close(); } catch (SQLException ex) {}
			dbClose(con);
		}
		return list;
	}
	
	/**
	 * 申請IDをキーに、部署名・氏名・ステータス名を含めた単一の申請データを取得する
	 */
	public ApplicationBean findById(String apctId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		ApplicationBean b = null;

		// SQL文を修正：必要な情報を網羅するため4つのテーブルを結合
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ");
		sql.append("       s.status_name, e.emp_name, d.dpt_name ");
		sql.append("FROM applications a ");
		sql.append("JOIN status s ON a.status_id = s.status_id ");
		sql.append("JOIN employees e ON a.emp_id = e.emp_id ");
		sql.append("JOIN departments d ON e.dpt_id = d.dpt_id "); // 部署テーブルを結合
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

					// 外部参照で取得した名称をBeanにマッピング
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
        b.setStatus_id(rs.getInt("status_id")); // int型としてマッピング
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
    /**
     * 経理部用に、管理部承認済み(3)または社長承認済み(4)の申請一覧を取得する
     */
    public List<ApplicationBean> getAccountingApplications() {
        Connection con = dbConnect();
        PreparedStatement st = null;
        ResultSet rs = null;
        List<ApplicationBean> list = new ArrayList<>();

        // status_id が 3 または 4 の未処理データを取得するSQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ");
        sql.append("       s.status_name, e.emp_name, d.dpt_name ");
        sql.append("FROM applications a ");
        sql.append("JOIN employees e ON a.emp_id = e.emp_id ");
        sql.append("JOIN status s ON a.status_id = s.status_id ");
        sql.append("JOIN departments d ON e.dpt_id = d.dpt_id ");
        sql.append("WHERE a.is_deleted = 0 AND a.status_id IN (3, 4) "); // 条件を指定
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

                    java.sql.Timestamp createTs = rs.getTimestamp("create_date");
                    if (createTs != null) b.setCreateDate(createTs.toLocalDateTime());
                    java.sql.Timestamp updateTs = rs.getTimestamp("update_date");
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
}