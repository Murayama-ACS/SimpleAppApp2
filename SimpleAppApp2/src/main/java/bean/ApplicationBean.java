package bean;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 申請データを保持する Bean
 */
public class ApplicationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;            // 申請種別
    private int amount;             // 申請金額 (追加項目)
    private String paymentMethod;   // 精算方法
    private String employeeId;      // 社員ID
    private String content;         // 申請内容
    private String reason;          // 申請理由
    private String note;            // 備考
    private boolean urgent;         // 緊急フラグ
    private LocalDateTime createDate; // 登録日時

    // 無引数コンストラクタ（JavaBean の要件）
    public ApplicationBean() {
    }

    // 便利コンストラクタ
    public ApplicationBean(String type, int amount, String paymentMethod, String employeeId,
                           String content, String reason, String note,
                           boolean urgent, LocalDateTime createDate) {
        this.type = type;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.employeeId = employeeId;
        this.content = content;
        this.reason = reason;
        this.note = note;
        this.urgent = urgent;
        this.createDate = createDate;
    }

    // getter / setter
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgentFlag(boolean urgent) {
        this.urgent = urgent;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "ApplicationBean [type=" + type + ", amount=" + amount + ", paymentMethod=" + paymentMethod
                + ", employeeId=" + employeeId + ", content=" + content
                + ", reason=" + reason + ", note=" + note + ", urgent=" + urgent
                + ", createDate=" + createDate + "]";
    }
}