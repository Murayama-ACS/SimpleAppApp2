package bean;

import java.io.Serializable;
import java.time.LocalDateTime;

public class NotificationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String apctId;
    private String content;             // 申請内容
    private int statusId;               // 変更後のステータスID
    private String statusName;          // ステータス名（承認、却下、完了など）
    private String approverName;        // アクションを行った社員名
    private String approverPosName;     // アクションを行った社員の役職名
    private String comment;             // コメント
    private LocalDateTime time;         // アクション日時
    private String approvalId; // 通知を特定・既読更新するために必要
    private boolean isRead;    // 既読フラグ
 // フィールドに追加
    private String timeStr;

    public NotificationBean() {}

    // getter / setter
    public String getApctId() { return apctId; }
    public void setApctId(String apctId) { this.apctId = apctId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }

    public String getApproverPosName() { return approverPosName; }
    public void setApproverPosName(String approverPosName) { this.approverPosName = approverPosName; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
    
    public String getApprovalId() { return approvalId; }
    public void setApprovalId(String approvalId) { this.approvalId = approvalId; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
    
    public String getTimeStr() { return timeStr; }
    public void setTimeStr(String timeStr) { this.timeStr = timeStr; }
}