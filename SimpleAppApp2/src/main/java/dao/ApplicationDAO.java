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
	 * ログインユーザーの役職・部署・権限に応じた未承認申請一覧を取得する（部署名・社員名取得対応版）
	 */
	public List<ApplicationBean> getPendingApplications(EmployeeBean employee) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<ApplicationBean> list = new ArrayList<>();

		if (employee == null) {
			return list;
		}

		String userPos = employee.getPos_id();
		String userDpt = employee.getDpt_id();

		// 管理部部署ID（実際の環境に合わせて調整してください）
		boolean isManagementDept = "D400".equals(userDpt); 
		boolean isManager = userPos != null && userPos.matches("^E0[1-4]$");

		// 【修正】departmentsテーブルとemployeesテーブルを結合し、部署名と名前を取得するSQLに変更
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.apct_id, a.emp_id, a.content, a.type, a.method, a.amount, a.reason, a.remark, a.urgent, a.status_id, a.create_date, a.update_date, a.is_deleted, ");
		sql.append("       s.status_name, e.emp_name, d.dpt_name "); // 部署名と名前、ステータス名を選択
		sql.append("FROM applications a ");
		sql.append("JOIN employees e ON a.emp_id = e.emp_id ");
		sql.append("JOIN status s ON a.status_id = s.status_id ");
		sql.append("JOIN departments d ON e.dpt_id = d.dpt_id "); // 追加：部署テーブルを結合
		sql.append("WHERE a.is_deleted = 0 ");

		String targetPosId = null;
		if (isManager) {
			try {
				int posNum = Integer.parseInt(userPos.substring(2));
				targetPosId = "E0" + (posNum - 1);
			} catch (Exception e) {
				System.out.println("役職IDの解析エラー: " + userPos);
			}
		}

		if (isManagementDept && isManager) {
			sql.append("AND ((a.status_id = 2) OR (a.status_id = 1 AND e.dpt_id = ? AND e.pos_id = ?)) ");
		} else if (isManagementDept) {
			sql.append("AND a.status_id = 2 ");
		} else if (isManager) {
			sql.append("AND a.status_id = 1 AND e.dpt_id = ? AND e.pos_id = ? ");
		} else {
			sql.append("AND 1 = 0 ");
		}

		sql.append("ORDER BY a.create_date DESC");

		try {
			if (con != null) {
				st = con.prepareStatement(sql.toString());
				
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
					b.setStatusName(rs.getString("status_name"));

					// 【追加】新しく結合して取得した部署名と社員名をBeanに格納
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
	 * 申請履歴一覧を条件（対象範囲、ステータス、ログインユーザーの役職・部署）に応じて取得する
	 * @param employee ログインユーザーの情報
	 * @param scope 対象範囲（"self": 自身, "subordinate": 配下, "management": 管理）
	 * @param statusFilter ステータス絞り込み（"incomplete": 未完了, "all": 全て）
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
		
		// --- 1. 対象範囲（scope）による絞り込み条件の調整 ---
		if ("self".equals(scope) || scope == null || scope.isEmpty()) {
			// 自身：自分が申請者のデータ
			sql.append("AND a.emp_id = ? ");
			params.add(employee.getEmp_id());
			
		} else if ("subordinate".equals(scope)) {
			// =================================================================
			// 役職・部署の階層に応じた「配下」のデータ抽出ロジック
			// =================================================================
			String userPos = employee.getPos_id(); // 自身の役職 (E01〜E03)
			String userDpt = employee.getDpt_id(); // 自身の部署 (D000〜D740)

			// 1. 部署の絞り込み条件の組み立て
			if ("D712".equals(userDpt)) {
				// 情報システム部A課B課（D712）の部長は、A課(D710)、B課(D720)、および自身(D712)が対象
				sql.append("AND e.dpt_id IN ('D710', 'D720', 'D712') ");
			} else if ("D734".equals(userDpt)) {
				// 情報システム部C課D課（D734）の部長は、C課(D730)、D課(D740)、および自身(D734)が対象
				sql.append("AND e.dpt_id IN ('D730', 'D740', 'D734') ");
			} else if (userDpt != null && userDpt.length() >= 3) {
				// 通常の部署：
				// 本部長(E03)・部長(E02)の場合は、部傘下すべて（例: D500番台なら上2桁 'D5%' で前方一致）
				// 課長(E01)の場合は、自身の課のみ（完全一致）
				if ("E03".equals(userPos) || "E02".equals(userPos)) {
					String dptPrefix = userDpt.substring(0, 2) + "%";
					sql.append("AND e.dpt_id LIKE ? ");
					params.add(dptPrefix);
				} else if ("E01".equals(userPos)) {
					sql.append("AND e.dpt_id = ? ");
					params.add(userDpt);
				}
			}

			// 2. 役職の上下関係（職位）による絞り込み
			// 自分より下の役職（pos_idの数値が自分より小さいもの）を対象とする
			// 例：本部長(E03)ならE02以下、部長(E02)ならE01以下、課長(E01)ならE00のみ
			sql.append("AND e.pos_id < ? ");
			params.add(userPos);
			
			// 3. 安全ガード：自分自身の申請データは「配下」からは除外する
			sql.append("AND a.emp_id != ? ");
			params.add(employee.getEmp_id());
		} else if ("management".equals(scope)) {
			// 管理：管理部（D100）の場合は、全社員の申請を確認可能とする（emp_idの絞り込みを付与しない）
			if ("D100".equals(employee.getDpt_id())) {
				// 条件なし（全社員をロード）
			} else {
				// 管理部以外は自身のデータのみにフォールバック（安全対策）
				sql.append("AND a.emp_id = ? ");
				params.add(employee.getEmp_id());
			}
		}

		// --- 2. ステータスによる絞り込み（未完了 / 全て） ---
		if ("incomplete".equals(statusFilter)) {
			// 【要件変更】状態が未完了（status_idが1〜4）の表示に対応
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
	 * 指定された申請IDの削除フラグ(is_deleted)を1（削除済み）に更新する（論理削除）
	 */
	public int logicalDelete(String apctId) {
		Connection con = dbConnect();
		int result = 0;
		
		// is_deletedを1に更新するSQL
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
}