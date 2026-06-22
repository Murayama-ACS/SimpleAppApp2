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
		// 修改后的 SQL：按时间降序排列，只取最新的一条
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
	 * 申請者（empId）に対する通知一覧を取得する（外部結合・安全版）
	 */
	public List<NotificationBean> selectNotificationsByApplicant(String empId) {
		Connection con = dbConnect();
		PreparedStatement st = null;
		ResultSet rs = null;
		List<NotificationBean> list = new ArrayList<>();

		// INNER JOIN から LEFT JOIN に変更して、結合時のデータ脱落を完全に防ぐ
		String sql = "SELECT apv.approval_id, a.apct_id, a.content, apv.status_id, s.status_name, apv.comment, apv.time, apv.is_read, "
				+ "e.emp_name, p.pos_name "
				+ "FROM approvals apv "
				+ "JOIN applications a ON apv.apct_id = a.apct_id "
				+ "LEFT JOIN status s ON apv.status_id = s.status_id "     // LEFT JOIN に変更
				+ "LEFT JOIN employees e ON apv.emp_id = e.emp_id "       // LEFT JOIN に変更
				+ "LEFT JOIN positions p ON e.pos_id = p.pos_id "         // LEFT JOIN に変更
				+ "WHERE a.emp_id = ? AND a.is_deleted = 0 "
				+ "ORDER BY apv.time DESC LIMIT 10";

		try {
			if (con != null) {
				st = con.prepareStatement(sql);
				st.setString(1, empId);
				rs = st.executeQuery();

				while (rs.next()) {
					NotificationBean bean = new NotificationBean();
					bean.setApprovalId(rs.getString("approval_id"));
					bean.setApctId(rs.getString("apct_id"));
					
					// 申請内容がnullの場合のセーフティ
					String content = rs.getString("content");
					bean.setContent(content != null ? content : "（内容なし）");
					
					bean.setStatusId(rs.getInt("status_id"));
					
					// status_name が取得できなかった場合のセーフティ
					String sName = rs.getString("status_name");
					bean.setStatusName(sName != null ? sName : "更新");
					
					bean.setComment(rs.getString("comment"));
					
					// 日時の変換と文字列フォーマット処理（null安全）
					if (rs.getTimestamp("time") != null) {
						java.time.LocalDateTime ldt = rs.getTimestamp("time").toLocalDateTime();
						bean.setTime(ldt);
						java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
						bean.setTimeStr(ldt.format(formatter));
					} else {
						bean.setTimeStr("---");
					}
					
					// 処理者名・役職名がnullの場合のセーフティ
					String empName = rs.getString("emp_name");
					bean.setApproverName(empName != null ? empName : "システム担当");
					
					String posName = rs.getString("pos_name");
					bean.setApproverPosName(posName != null ? posName : "担当者");
					
					bean.setRead(rs.getInt("is_read") == 1);
					list.add(bean);
				}
			}
		} catch (SQLException e) {
			System.out.println("selectNotificationsByApplicantエラー: " + e.getMessage());
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (st != null) try { st.close(); } catch (SQLException e) {}
			dbClose(con);
		}
		return list;
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
}