package bean;

public class ApprovalBean{
	
	// 承認履歴ID（あれば）
	private String approvalId;

	// 対象申請ID（application.apct_id に対応）
	private String applicationId;

	// 承認者情報
	private String approverId;
	private String approverName;

	// コメント／備考
	private String comment;

	// ステータス（例: "APPROVED", "REJECTED" など）
	private String status;

	// 承認日時（DB に合わせて型を調整。ここでは LocalDateTime を利用）
	private java.time.LocalDateTime decisionAt;

	// 論理削除フラグ等
	private boolean isDeleted;

	public ApprovalBean() {}

	public ApprovalBean(String approvalId, String applicationId, String approverId, String approverName,
			String comment, String status, java.time.LocalDateTime decisionAt, boolean isDeleted) {
		this.approvalId = approvalId;
		this.applicationId = applicationId;
		this.approverId = approverId;
		this.approverName = approverName;
		this.comment = comment;
		this.status = status;
		this.decisionAt = decisionAt;
		this.isDeleted = isDeleted;
	}

	// getters / setters
	public String getApprovalId() { return approvalId; }
	public void setApprovalId(String approvalId) { this.approvalId = approvalId; }

	public String getApplicationId() { return applicationId; }
	public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

	public String getApproverId() { return approverId; }
	public void setApproverId(String approverId) { this.approverId = approverId; }

	public String getApproverName() { return approverName; }
	public void setApproverName(String approverName) { this.approverName = approverName; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public java.time.LocalDateTime getDecisionAt() { return decisionAt; }
	public void setDecisionAt(java.time.LocalDateTime decisionAt) { this.decisionAt = decisionAt; }

	public boolean isDeleted() { return isDeleted; }
	public void setDeleted(boolean deleted) { isDeleted = deleted; }

	@Override
	public String toString() {
		return "ApprovalBean{" +
				"approvalId='" + approvalId + '\'' +
				", applicationId='" + applicationId + '\'' +
				", approverId='" + approverId + '\'' +
				", approverName='" + approverName + '\'' +
				", comment='" + comment + '\'' +
				", status='" + status + '\'' +
				", decisionAt=" + decisionAt +
				", isDeleted=" + isDeleted +
				'}';
	}
}