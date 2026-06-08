package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import bean.ApplicationBean;

public class ApplicationDAO {

	private javax.sql.DataSource dataSource;

	public ApplicationDAO(javax.sql.DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// 未承認（pending）の申請一覧を取得する
	public List<ApplicationBean> getPendingApplications(String pendingStatus) throws SQLException {
		List<ApplicationBean> list = new ArrayList<>();
		// SQLのカラム名をBeanのコメント欄に記載されたテーブル定義（apct_id, emp_idなど）に適合
		String sql = "SELECT *"
				+ "FROM application_tbl "
				+ "WHERE status = ? AND is_deleted = FALSE "
				+ "ORDER BY create_date DESC";

		try (Connection conn = dataSource.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, pendingStatus);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ApplicationBean b = mapRowToBean(rs);
					list.add(b);
				}
			}
		}
		return list;
	}

	// IDで詳細取得
	public ApplicationBean findById(String id) throws SQLException {
		// SQLのカラム名をBeanのコメント欄に記載されたテーブル定義に適合
		String sql = "SELECT apct_id, emp_id, content, type, method, amount, reason, remark, urgent, status, create_date, update_date, is_deleted "
				+ "FROM application_tbl "
				+ "WHERE apct_id = ? AND is_deleted = FALSE";

		try (Connection conn = dataSource.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRowToBean(rs);
				} else {
					return null;
				}
			}
		}
	}

	// ResultSet -> Bean マッピング
	private ApplicationBean mapRowToBean(ResultSet rs) throws SQLException {
		ApplicationBean b = new ApplicationBean();
		
		// Beanのフィールド・型・メソッド名に完全に一致させる
		b.setApctId(rs.getString("apct_id"));
		b.setEmployeeId(rs.getString("emp_id"));
		b.setContent(rs.getString("content"));
		b.setType(rs.getString("type"));
		b.setPaymentMethod(rs.getString("method"));
		b.setAmount(rs.getInt("amount"));
		b.setReason(rs.getString("reason"));
		b.setNote(rs.getString("remark"));
		b.setUrgent(rs.getString("urgent")); // 型をStringとして取得
		b.setStatus(rs.getString("status"));
		b.setDeleted(rs.getBoolean("is_deleted")); // メソッド名 setDeleted に合わせる

		// 日時系フィールドのマッピング（LocalDateTimeへの変換）
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