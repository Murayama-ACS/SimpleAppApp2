package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import bean.ApplicationBean;

public class ApplicationDAO extends DAO {
	public int insert(ApplicationBean bean) {
		Connection con = dbConnect();
		int result = 0;
		
		// SQL文のテンプレート（プレースホルダーの数をセットする値の数「8個」に修正）
		String sql = "INSERT INTO applications (type, method, content, amount, reason, remark, urgent, create_date) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				
		try {
			if(con != null) {
				// ステートメントインスタンス（バッファ）の生成
				PreparedStatement st = con.prepareStatement(sql);
				
				// SQLテンプレートへの挿入
				st.setString(1, bean.getType());
				st.setString(2, bean.getPaymentMethod());
				st.setString(3, bean.getEmployeeId());
				st.setString(4, bean.getContent());
				st.setString(5, bean.getReason());
				st.setString(6, bean.getNote());
				st.setBoolean(7, bean.isUrgent());

				// createDate の型に応じたセット処理
				Object createDate = bean.getCreateDate();
				if (createDate instanceof java.time.LocalDateTime) {
					st.setTimestamp(8, Timestamp.valueOf((java.time.LocalDateTime) createDate));
				} else if (createDate instanceof java.sql.Timestamp) {
					st.setTimestamp(8, (Timestamp) createDate);
				} else if (createDate instanceof java.util.Date) {
					st.setTimestamp(8, new Timestamp(((java.util.Date) createDate).getTime()));
				} else if (createDate instanceof String) {
					st.setString(8, (String) createDate);
				} else {
					st.setTimestamp(8, null);
				}

				result = st.executeUpdate();
			}
		} catch(SQLException e) {
			System.out.println("insertエラー");
			System.out.println(e.getMessage());
		}
		
		dbClose(con);
		return result;
	}
}