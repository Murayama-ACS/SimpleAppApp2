package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import bean.ApprovalBean;
import bean.NotificationBean;

public class ApprovalDAO extends DAO {

	/**
	 * 承認データをデータベースに登録する（現在のテーブル設計に完全準拠）
	 */
	public int insert(ApprovalBean bean) {
		Connection con = dbConnect();
		int result = 0;
				String sql = "INSERT INTO approvals (approval_id, apct_id, emp_id, status_id, comment, time, is_read) "
				+ "VALUES (?, ?, ?, ?, ?, ?, 0)";
				
		try {
			if(con != null) {
				PreparedStatement st = con.prepareStatement(sql);
				
				// すべての値をBeanから取得してプレースホルダーに設定
				st.setString(1, bean.getApprovalId()); // 履歴ID
				st.setString(2, bean.getApctId());      // 申請ID
				st.setString(3, bean.getEmployeeId());  // 社員ID
				st.setInt(4, bean.getStatusId());       // 状態ID
				st.setString(5, bean.getComment());     // コメント
				
				if (bean.getCreateDate() != null) {
					st.setTimestamp(6, Timestamp.valueOf(bean.getCreateDate()));
				} else {
					st.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
				}

				result = st.executeUpdate();
			}
		} catch(SQLException e) {
			System.out.println("ApprovalDAO insertエラー");
			System.out.println(e.getMessage());
		} finally {
			dbClose(con);
		}
		
		return result;
	}

	/**
	 * 申請ID（apct_id）を基に、特定の承認データを取得する（現在のテーブル設計に完全準拠）
	 */
	public ApprovalBean selectByApctId(String apctId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		ApprovalBean approval = null;

		// テーブル名を approval から approvals に修正
		String sql = "SELECT approval_id, apct_id, emp_id, status_id, comment, time "
		           + "FROM approvals WHERE apct_id = ? "
		           + "ORDER BY time DESC LIMIT 1";
		try {
			if (con != null) {
				st = con.prepareStatement(sql);
				st.setString(1, apctId);
				rs = st.executeQuery();

				if (rs.next()) {
					approval = new ApprovalBean();
					approval.setApprovalId(rs.getString("approval_id"));
					approval.setApctId(rs.getString("apct_id"));
					approval.setEmployeeId(rs.getString("emp_id"));
					approval.setStatusId(rs.getInt("status_id")); // int型としてマッピング
					approval.setComment(rs.getString("comment"));
					
					// データベースの time から取得してセット
					if (rs.getTimestamp("time") != null) {
						approval.setCreateDate(rs.getTimestamp("time").toLocalDateTime());
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("ApprovalDAO selectByApctIdエラー");
			System.out.println(e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return approval;
	}
	

	/**
	 * 特定の社員の未読通知をすべて既読（is_read = 1）に更新する
	 */
	public int updateAllNotificationsAsRead(String empId) {
		Connection con = dbConnect();
		int result = 0;
		// 申請者が自分(empId)である申請に紐づく承認履歴のうち、未読のものを既読にする
		String sql = "UPDATE approvals apv "
				+ "JOIN applications a ON apv.apct_id = a.apct_id "
				+ "SET apv.is_read = 1 "
				+ "WHERE a.emp_id = ? AND apv.is_read = 0";
		try {
			if (con != null) {
				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, empId);
				result = st.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("updateAllNotificationsAsReadエラー: " + e.getMessage());
		} finally {
			dbClose(con);
		}
		return result;
	}
	
	/**
	 * 【新規追加】指定した社員の通知の「総件数」を取得する（ページネーション計算用）
	 */
	public int countNotificationsByApplicant(String empId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		int count = 0;

		String sql = "SELECT COUNT(*) AS total FROM approvals apv "
				+ "JOIN applications a ON apv.apct_id = a.apct_id "
				+ "WHERE a.emp_id = ? AND a.is_deleted = 0";
		try {
			if (con != null) {
				st = con.prepareStatement(sql);
				st.setString(1, empId);
				rs = st.executeQuery();
				if (rs.next()) {
					count = rs.getInt("total");
				}
			}
		} catch (SQLException e) {
			System.out.println("countNotificationsByApplicantエラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return count;
	}

	/**
	 * 【修正】ページネーション（LIMITとOFFSETを使って指定範囲の通知一覧を取得する）
	 */
	public List<NotificationBean> selectNotificationsByApplicant(String empId, int limit, int offset) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<NotificationBean> list = new ArrayList<>();

		String sql = "SELECT apv.approval_id, a.apct_id, a.content, apv.status_id, s.status_name, apv.comment, apv.time, apv.is_read, "
				+ "e.emp_name, p.pos_name "
				+ "FROM approvals apv "
				+ "JOIN applications a ON apv.apct_id = a.apct_id "
				+ "LEFT JOIN status s ON apv.status_id = s.status_id "
				+ "LEFT JOIN employees e ON apv.emp_id = e.emp_id "
				+ "LEFT JOIN positions p ON e.pos_id = p.pos_id "
				+ "WHERE a.emp_id = ? AND a.is_deleted = 0 "
				+ "ORDER BY apv.time DESC LIMIT ? OFFSET ?";

		try {
			if (con != null) {
				st = con.prepareStatement(sql);
				st.setString(1, empId);
				st.setInt(2, limit);
				st.setInt(3, offset);
				rs = st.executeQuery();

				while (rs.next()) {
					NotificationBean bean = new NotificationBean();
					bean.setApprovalId(rs.getString("approval_id"));
					bean.setApctId(rs.getString("apct_id"));
					
					String content = rs.getString("content");
					bean.setContent(content != null ? content : "（内容なし）");
					
					bean.setStatusId(rs.getInt("status_id"));
					
					String sName = rs.getString("status_name");
					bean.setStatusName(sName != null ? sName : "更新");
					
					bean.setComment(rs.getString("comment"));
					
					if (rs.getTimestamp("time") != null) {
						java.time.LocalDateTime ldt = rs.getTimestamp("time").toLocalDateTime();
						bean.setTime(ldt);
						java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
						bean.setTimeStr(ldt.format(formatter));
					} else {
						bean.setTimeStr("---");
					}
					
					String empName = rs.getString("emp_name");
					bean.setApproverName(empName != null ? empName : "システム担当");
					
					String posName = rs.getString("pos_name");
					bean.setApproverPosName(posName != null ? posName : "担当者");
					
					bean.setRead(rs.getInt("is_read") == 1);
					list.add(bean);
				}
			}
		} catch (SQLException e) {
			System.out.println("selectNotificationsByApplicant(Paging)エラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return list;
	}
	
	/**
	 * 【新規追加】指定した社員の「未読」通知の総件数のみを取得する
	 */
	public int countUnreadNotifications(String empId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		int count = 0;

		String sql = "SELECT COUNT(*) AS unread_count FROM approvals apv "
				+ "JOIN applications a ON apv.apct_id = a.apct_id "
				+ "WHERE a.emp_id = ? AND a.is_deleted = 0 AND apv.is_read = 0";
		try {
			if (con != null) {
				st = con.prepareStatement(sql);
				st.setString(1, empId);
				rs = st.executeQuery();
				if (rs.next()) {
					count = rs.getInt("unread_count");
				}
			}
		} catch (SQLException e) {
			System.out.println("countUnreadNotificationsエラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return count;
	}
}