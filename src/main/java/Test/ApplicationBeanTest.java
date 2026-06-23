package Test;

import java.time.LocalDateTime;

import bean.ApplicationBean;

public class ApplicationBeanTest {

	public static void main(String[] args) {
		ApplicationBean appBean = new ApplicationBean();
		System.out.println(appBean);
		String apctId = "apctId";
		String employeeId = "employeeId";
		String content = "content";
		String type = "type";
		String paymentMethod = "paymentMethod";
		int amount = 123;
		String reason = "reason";
		String note = "note";
		String urgent = "urgent";
		int status_id = 1;
		LocalDateTime createDate = LocalDateTime.now();
		LocalDateTime updateDate = LocalDateTime.now();
		boolean isDeleted = false;
		String StatusName = "StatusName";
		String employeeName = "employeeName";
		String departmentName = "departmentName";

		/*// ApplicationBean のインスタンス化
		ApplicationBean app = new ApplicationBean(apctId, employeeId, content, type, 
				paymentMethod,  amount, reason, note, 
				urgent,  status_id,  createDate,  updateDate, 
				isDeleted,  StatusName, employeeName, departmentName);

		// println で期待値と getter の戻り値を比較表示
		System.out.println("apctId expected: " + apctId + "  actual: " + app.getApctId());
		System.out.println("employeeId expected: " + employeeId + "  actual: " + app.getEmployeeId());
		System.out.println("content expected: " + content + "  actual: " + app.getContent());
		System.out.println("type expected: " + type + "  actual: " + app.getType());
		System.out.println("paymentMethod expected: " + paymentMethod + "  actual: " + app.getPaymentMethod());
		System.out.println("amount expected: " + amount + "  actual: " + app.getAmount());
		System.out.println("reason expected: " + reason + "  actual: " + app.getReason());
		System.out.println("note expected: " + note + "  actual: " + app.getNote());
		System.out.println("urgent expected: " + urgent + "  actual: " + app.getUrgent());
		System.out.println("status_id expected: " + status_id + "  actual: " + app.getStatus_id()); // getter 名を getStatusId() にしている想定
		System.out.println("createDate expected: " + createDate + "  actual: " + app.getCreateDate());
		System.out.println("updateDate expected: " + updateDate + "  actual: " + app.getUpdateDate());
		System.out.println("isDeleted expected: " + isDeleted + "  actual: " + app.isDeleted()); // boolean の場合 isXxx() を想定
		System.out.println("StatusName expected: " + StatusName + "  actual: " + app.getStatusName());
		System.out.println("employeeName expected: " + employeeName + "  actual: " + app.getEmployeeName());
		System.out.println("departmentName expected: " + departmentName + "  actual: " + app.getDepartmentName());*/

		ApplicationBean app_setter = new ApplicationBean();

		// セッターで値をセット（メソッド名は実装に合わせて調整）
		app_setter.setApctId(apctId);
		app_setter.setEmployeeId(employeeId);
		app_setter.setContent(content);
		app_setter.setType(type);
		app_setter.setPaymentMethod(paymentMethod);
		app_setter.setAmount(amount);
		app_setter.setReason(reason);
		app_setter.setNote(note);
		app_setter.setUrgent(urgent);
		app_setter.setStatus_id(status_id);          // get/set 名は実装に合わせて調整
		app_setter.setCreateDate(createDate);
		app_setter.setUpdateDate(updateDate);
		app_setter.setDeleted(isDeleted);         // または setIsDeleted(...)
		app_setter.setStatusName(StatusName);
		app_setter.setEmployeeName(employeeName);
		app_setter.setDepartmentName(departmentName);

		// ゲッターで取得して出力（メソッド名は実装に合わせて調整）
		System.out.println("apctId: " + app_setter.getApctId());
		System.out.println("employeeId: " + app_setter.getEmployeeId());
		System.out.println("content: " + app_setter.getContent());
		System.out.println("type: " + app_setter.getType());
		System.out.println("paymentMethod: " + app_setter.getPaymentMethod());
		System.out.println("amount: " + app_setter.getAmount());
		System.out.println("reason: " + app_setter.getReason());
		System.out.println("note: " + app_setter.getNote());
		System.out.println("urgent: " + app_setter.getUrgent());
		System.out.println("statusId: " + app_setter.getStatus_id()); // getStatusId() or getStatus_id()
		System.out.println("createDate: " + app_setter.getCreateDate());
		System.out.println("updateDate: " + app_setter.getUpdateDate());
		System.out.println("isDeleted: " + app_setter.isDeleted()); // isDeleted() or getIsDeleted()
		System.out.println("statusName: " + app_setter.getStatusName());
		System.out.println("employeeName: " + app_setter.getEmployeeName());
		System.out.println("departmentName: " + app_setter.getDepartmentName());
		
		 // 実行：toString を出力
        String s = app_setter.toString();
        System.out.println(s);

        // 簡易チェック：期待文字列が含まれるか（必要なら assert や JUnit に置き換え）
        if (s.contains("apctId") && s.contains("employeeId") && s.contains("StatusName")) {
            System.out.println("toString 動作確認: OK");
        } else {
            System.err.println("toString に期待値が含まれていません。出力を確認してください。");
        }
	}
}



