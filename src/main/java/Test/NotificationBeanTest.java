package Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bean.NotificationBean;

public class NotificationBeanTest {

	public static void main(String[] args) {
		NotificationBean notBean = new NotificationBean();
		System.out.println(notBean);
		
		// 共通の日時フォーマッタ
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		    // ----- テスト1: 全 setter を使って設定し getter で出力 -----
		    NotificationBean nb = new NotificationBean();

		    String apctId = "apct-001";
		    String content = "申請内容のテスト";
		    int statusId = 2;
		    String statusName = "承認";
		    String approverName = "山田太郎";
		    String approverPosName = "部長";
		    String comment = "承認しました";
		    LocalDateTime time = LocalDateTime.of(2024, 6, 22, 15, 30, 45);
		    String approvalId = "approval-123";
		    boolean isRead = false;
		    String timeStr = time.format(fmt);

		    // setter でセット
		    nb.setApctId(apctId);
		    nb.setContent(content);
		    nb.setStatusId(statusId);
		    nb.setStatusName(statusName);
		    nb.setApproverName(approverName);
		    nb.setApproverPosName(approverPosName);
		    nb.setComment(comment);
		    nb.setTime(time);
		    nb.setApprovalId(approvalId);
		    nb.setRead(isRead);
		    nb.setTimeStr(timeStr);

		    // getter で出力
		    System.out.println("=== Test 1: All setters then getters ===");
		    System.out.println("apctId:          " + nb.getApctId());
		    System.out.println("content:         " + nb.getContent());
		    System.out.println("statusId:        " + nb.getStatusId());
		    System.out.println("statusName:      " + nb.getStatusName());
		    System.out.println("approverName:    " + nb.getApproverName());
		    System.out.println("approverPosName: " + nb.getApproverPosName());
		    System.out.println("comment:         " + nb.getComment());
		    System.out.println("time (LocalDateTime): " + nb.getTime());
		    System.out.println("approvalId:      " + nb.getApprovalId());
		    System.out.println("isRead:          " + nb.isRead());
		    System.out.println("timeStr:         " + nb.getTimeStr());

		    // 簡易チェック
		    boolean allMatch = apctId.equals(nb.getApctId())
		            && content.equals(nb.getContent())
		            && statusId == nb.getStatusId()
		            && statusName.equals(nb.getStatusName())
		            && approverName.equals(nb.getApproverName())
		            && approverPosName.equals(nb.getApproverPosName())
		            && comment.equals(nb.getComment())
		            && time.equals(nb.getTime())
		            && approvalId.equals(nb.getApprovalId())
		            && isRead == nb.isRead()
		            && timeStr.equals(nb.getTimeStr());

		    System.out.println("Test1 result: " + (allMatch ? "OK" : "NG"));

		    // ----- テスト2: time と timeStr を別の値に変更して出力 -----
		    LocalDateTime newTime = LocalDateTime.of(2025, 1, 1, 9, 0, 0);
		    String newTimeStr = newTime.format(fmt);

		    nb.setTime(newTime);
		    nb.setTimeStr(newTimeStr);

		    System.out.println("\n=== Test 2: Update time and timeStr, then getters ===");
		    System.out.println("time (LocalDateTime): " + nb.getTime());
		    System.out.println("timeStr:              " + nb.getTimeStr());

		    boolean timeMatch = newTime.equals(nb.getTime()) && newTimeStr.equals(nb.getTimeStr());
		    System.out.println("Test2 result: " + (timeMatch ? "OK" : "NG"));
	}

}
