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
	 * ログインユーザーの役職・部署・権限に応じた未承認申請一覧を取得する
	 */
	public List<ApplicationBean> getPendingApplications(EmployeeBean employee) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<ApplicationBean> list = new ArrayList<>();

		if (employee == null) {
			return list;
		}

		String userPos = employee.getPos_id(); // 役職ID（E00〜E04など）
		String userDpt = employee.getDpt_id(); // 部署ID（D000など）

		// 管理部かどうかの判定（プロジェクトの管理部部署IDに合わせて変更してください。ここでは "D400" と仮定）
		boolean isManagementDept = "D100".equals(userDpt); 
		
		// 上長（E01〜E04）かどうかの判定
		boolean isManager = userPos != null && userPos.matches("^E0[1-4]$");

		// 基盤となるSQL文（applicationsテーブルと、申請者の役職を確認するためにemployeesテーブルを結合）
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted ");
		sql.append("FROM applications a ");
		sql.append("JOIN employees e ON a.emp_id = e.emp_id ");
		sql.append("WHERE a.is_deleted = 0 ");

		// 条件分岐のパラメータ保持用
		String targetPosId = null;

		// 1つ下の役職IDを計算（例: "E02" -> "E01"）
		if (isManager) {
			try {
				int posNum = Integer.parseInt(userPos.substring(2));
				targetPosId = "E0" + (posNum - 1);
			} catch (Exception e) {
				System.out.println("役職IDの解析エラー: " + userPos);
			}
		}

		// --- 権限によるSQLの条件分岐 ---
		if (isManagementDept && isManager) {
			// パターン3：管理部の上長（管理部案件[status_id=2] と 自部署の1つ下からの案件[status_id=1] の両方）
			sql.append("AND ((a.status_id = 2) OR (a.status_id = 1 AND e.dpt_id = ? AND e.pos_id = ?)) ");
		} else if (isManagementDept) {
			// パターン2：管理部の一般社員（管理部案件[status_id=2] のみ）
			sql.append("AND a.status_id = 2 ");
		} else if (isManager) {
			// パターン1：一般部署の上長（自部署かつ1つ下の役職[status_id=1] のみ）
			sql.append("AND a.status_id = 1 AND e.dpt_id = ? AND e.pos_id = ? ");
		} else {
			// 上長でも管理部でもない一般社員の場合は、承認待ち一覧には何も表示しない
			sql.append("AND 1 = 0 ");
		}

		sql.append("ORDER BY a.create_date DESC");

		try {
			if (con != null) {
				st = con.prepareStatement(sql.toString());
				
				// プレースホルダーの動的設定
				if (isManagementDept && isManager) {
					st.setString(1, userDpt);
					st.setString(2, targetPosId);
				} else if (!isManagementDept && isManager) {
					st.setString(1, userDpt);
					st.setString(2, targetPosId);
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
			System.out.println("getPendingApplicationsエラー");
			System.out.println(e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return list;
	}
    /**
     * IDで詳細取得
     */
    public ApplicationBean findById(String id) {
        Connection con = dbConnect();
        PreparedStatement st = null;
        ResultSet rs = null;
        ApplicationBean bean = null;

        String sql = "SELECT apct_id, emp_id, content, type, method, amount, reason, remark, urgent, status_id, create_date, update_date, is_deleted "
                + "FROM applications "
                + "WHERE apct_id = ? AND is_deleted = 0";

        try {
            if (con != null) {
                st = con.prepareStatement(sql);
                st.setString(1, id);
                rs = st.executeQuery();

                if (rs.next()) {
                    bean = mapRowToBean(rs);
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
        return bean;
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
}