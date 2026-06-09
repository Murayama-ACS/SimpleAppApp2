package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import bean.ApprovalBean;

public class ApprovalDAO extends DAO {

	/**
	 * 承認データをデータベースに登録する（現在のテーブル設計に完全準拠）
	 */
	public int insert(ApprovalBean bean) {
		Connection con = dbConnect();
		int result = 0;
		
		// 設計書の全6カラム（approval_id, apct_id, emp_id, apct_type, comment, time）に対応するINSERT文
		String sql = "INSERT INTO approval (approval_id, apct_id, emp_id, apct_type, comment, time) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
				
		try {
			if(con != null) {
				PreparedStatement st = con.prepareStatement(sql);
				
				// すべての値をBeanから取得してプレースホルダーに設定
				st.setString(1, bean.getApprovalId()); // 履歴ID
				st.setString(2, bean.getApctId());      // 申請ID
				st.setString(3, bean.getEmployeeId());  // 社員ID
				st.setString(4, bean.getApctType());    // 申請種別
				st.setString(5, bean.getComment());     // コメント
				
				// テーブルのカラム名「time」に合わせてLocalDateTimeをTimestampに変換して設定
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

		// 設計書に存在するカラムのみを取得するSQL文に変更
		String sql = "SELECT approval_id, apct_id, emp_id, apct_type, comment, time FROM approval WHERE apct_id = ?";

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
					approval.setApctType(rs.getString("apct_type"));
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
}