package dao;

import bean.ApplicationBean;

public class ApplicationDAO {

	private javax.sql.DataSource dataSource;

	// DataSourceはコンテナやDIから注入する想定
	public ApplicationDAO(javax.sql.DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// 未承認（pending）の申請一覧を取得する例
	public java.util.List<ApplicationBean> getPendingApplications(String pendingStatus) throws java.sql.SQLException {
		java.util.List<ApplicationBean> list = new java.util.ArrayList<>();
		String sql = "SELECT id, applicant_id, content, type, method, amount, reason, remark, urgent, status, create_date, update_date, is_deleted "
				+ "FROM application_tbl "
				+ "WHERE status = ? AND is_deleted = FALSE "
				+ "ORDER BY create_date DESC";

		try (java.sql.Connection conn = dataSource.getConnection();
				java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, pendingStatus);

			try (java.sql.ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ApplicationBean b = mapRowToBean(rs);
					list.add(b);
				}
			}
		}
		return list;
	}

	// IDで詳細取得
	public ApplicationBean findById(String id) throws java.sql.SQLException {
		String sql = "SELECT id, applicant_id, content, type, method, amount, reason, remark, urgent, status, create_date, update_date, is_deleted "
				+ "FROM application_tbl "
				+ "WHERE id = ? AND is_deleted = FALSE";

		try (java.sql.Connection conn = dataSource.getConnection();
				java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, id);

			try (java.sql.ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRowToBean(rs);
				} else {
					return null;
				}
			}
		}
	}

	// ResultSet -> Bean マッピング（再利用）
	private ApplicationBean mapRowToBean(java.sql.ResultSet rs) throws java.sql.SQLException {
		ApplicationBean b = new ApplicationBean();
		b.setId(rs.getString("id"));
		b.setApplicantId(rs.getString("applicant_id"));
		b.setContent(rs.getString("content"));
		b.setType(rs.getString("type"));
		b.setMethod(rs.getString("method"));
		b.setAmount(rs.getInt("amount"));
		b.setReason(rs.getString("reason"));
		b.setRemark(rs.getString("remark"));
		b.setUrgent(rs.getString("urgent"));
		b.setStatus(rs.getString("status"));
		b.setCreateDate(rs.getDate("create_date"));
		b.setUpdateDate(rs.getDate("update_date"));
		b.setIsDeleted(rs.getBoolean("is_deleted"));
		return b;
	}

	// 検索やソート、ページングが必要なら別メソッドで実装する（PreparedStatementで動的SQLを組み立てる等）
}
