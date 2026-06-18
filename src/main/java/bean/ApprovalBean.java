package bean;

import java.time.LocalDateTime;

public class ApprovalBean {
	
	// 履歴ID (DB: approval_id)
	private String approvalId;

	// 申請ID (DB: apct_id)
	private String apctId;

	// 社員ID (DB: emp_id)
	private String employeeId;

	// 状態ID (DB: status_id)
	private int statusId;

	// コメント (DB: comment)
	private String comment;

	// 承認時間 (DB: time)
	private LocalDateTime createDate;

	// コンストラクタ（引数なし）
	public ApprovalBean() {}

	// コンストラクタ（全フィールド指定）
	public ApprovalBean(String approvalId, String apctId, String employeeId, int statusId, String comment, LocalDateTime createDate) {
		this.approvalId = approvalId;
		this.apctId = apctId;
		this.employeeId = employeeId;
		this.statusId = statusId;
		this.comment = comment;
		this.createDate = createDate;
	}

	// getter / setter
	public String getApprovalId() {
		return approvalId;
	}

	public void setApprovalId(String approvalId) {
		this.approvalId = approvalId;
	}

	public String getApctId() {
		return apctId;
	}

	public void setApctId(String apctId) {
		this.apctId = apctId;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "ApprovalBean{" +
				"approvalId='" + approvalId + '\'' +
				", apctId='" + apctId + '\'' +
				", employeeId='" + employeeId + '\'' +
				", statusId=" + statusId +
				", comment='" + comment + '\'' +
				", createDate=" + createDate +
				'}';
	}

}
