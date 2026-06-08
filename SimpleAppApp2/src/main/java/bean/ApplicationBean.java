package bean;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 申請データを保持する Bean（テーブル定義の全項目を網羅）
 */
public class ApplicationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // テーブル定義に対応するフィールド
    private String apctId;          // apct_id (申請ID)
    private String employeeId;      // emp_id (社員ID)
    private String content;         // content (申請内容)
    private String type;            // type (申請種別)
    private String paymentMethod;   // method (精算方式)
    private int amount;             // amount (申請金額)
    private String reason;          // reason (申請理由)
    private String note;            // remark (備考)
    private String urgent;          // urgent (緊急度) ※DBのVARCHAR(20)に適合
    private String status;          // status (申請状態)
    private LocalDateTime createDate; // create_date (作成時間)
    private LocalDateTime updateDate; // update_date (変更時間)
    private boolean isDeleted;      // is_deleted (削除)

    // 無引数コンストラクタ（JavaBean の要件）
    public ApplicationBean() {
    }

    // 全フィールドを網羅した便利コンストラクタ
    public ApplicationBean(String apctId, String employeeId, String content, String type, 
                           String paymentMethod, int amount, String reason, String note, 
                           String urgent, String status, LocalDateTime createDate, 
                           LocalDateTime updateDate, boolean isDeleted) {
        this.apctId = apctId;
        this.employeeId = employeeId;
        this.content = content;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.reason = reason;
        this.note = note;
        this.urgent = urgent;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.isDeleted = isDeleted;
    }

    // getter / setter
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUrgent() {
        return urgent;
    }

    public void setUrgent(String urgent) {
        this.urgent = urgent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "ApplicationBean [apctId=" + apctId + ", employeeId=" + employeeId + ", content=" + content
                + ", type=" + type + ", paymentMethod=" + paymentMethod + ", amount=" + amount + ", reason=" + reason
                + ", note=" + note + ", urgent=" + urgent + ", status=" + status + ", createDate=" + createDate
                + ", updateDate=" + updateDate + ", isDeleted=" + isDeleted + "]";
    }
}