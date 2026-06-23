package Test;

import java.time.LocalDateTime;

import bean.ApprovalBean;

public class ApprovalBeanTest {
	public static void main(String[] args) {
		ApprovalBean appBean = new ApprovalBean();
		System.out.println(appBean);
		
		// 期待値（引数名と同じ文字列等を使う）
		String approvalId = "approvalId";
		String apctId = "apctId";
		String employeeId = "employeeId";
		int statusId = 2;
		String comment = "承認コメント";
		LocalDateTime createDate = LocalDateTime.of(2023, 1, 1, 10, 0);

		    // 1) コンストラクタによるテスト
		    ApprovalBean beanCtor = new ApprovalBean(approvalId, apctId, employeeId, statusId, comment, createDate);

		    System.out.println("=== Constructor-based test ===");
		    System.out.println("approvalId expected: " + approvalId + "  actual: " + beanCtor.getApprovalId());
		    System.out.println("apctId       expected: " + apctId       + "  actual: " + beanCtor.getApctId());
		    System.out.println("employeeId   expected: " + employeeId   + "  actual: " + beanCtor.getEmployeeId());
		    System.out.println("statusId     expected: " + statusId     + "  actual: " + beanCtor.getStatusId());
		    System.out.println("comment      expected: " + comment      + "  actual: " + beanCtor.getComment());
		    System.out.println("createDate   expected: " + createDate   + "  actual: " + beanCtor.getCreateDate());

		    boolean ctorOk = approvalId.equals(beanCtor.getApprovalId())
		            && apctId.equals(beanCtor.getApctId())
		            && employeeId.equals(beanCtor.getEmployeeId())
		            && statusId == beanCtor.getStatusId()
		            && comment.equals(beanCtor.getComment())
		            && createDate.equals(beanCtor.getCreateDate());

		    System.out.println("Constructor test: " + (ctorOk ? "OK" : "NG"));

		    // 2) setter によるテスト（ダミーコンストラクタで生成後、setter で上書き）
		    ApprovalBean beanSetter = new ApprovalBean("dummy", "dummy", "dummy", 0, "dummy", LocalDateTime.now());

		    beanSetter.setApprovalId(approvalId);
		    beanSetter.setApctId(apctId);
		    beanSetter.setEmployeeId(employeeId);
		    beanSetter.setStatusId(statusId);
		    beanSetter.setComment(comment);
		    beanSetter.setCreateDate(createDate);

		    System.out.println("=== Setter-based test ===");
		    System.out.println("approvalId expected: " + approvalId + "  actual: " + beanSetter.getApprovalId());
		    System.out.println("apctId       expected: " + apctId       + "  actual: " + beanSetter.getApctId());
		    System.out.println("employeeId   expected: " + employeeId   + "  actual: " + beanSetter.getEmployeeId());
		    System.out.println("statusId     expected: " + statusId     + "  actual: " + beanSetter.getStatusId());
		    System.out.println("comment      expected: " + comment      + "  actual: " + beanSetter.getComment());
		    System.out.println("createDate   expected: " + createDate   + "  actual: " + beanSetter.getCreateDate());

		    boolean setterOk = approvalId.equals(beanSetter.getApprovalId())
		            && apctId.equals(beanSetter.getApctId())
		            && employeeId.equals(beanSetter.getEmployeeId())
		            && statusId == beanSetter.getStatusId()
		            && comment.equals(beanSetter.getComment())
		            && createDate.equals(beanSetter.getCreateDate());

		    System.out.println("Setter test: " + (setterOk ? "OK" : "NG"));
	}
}
