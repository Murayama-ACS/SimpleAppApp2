package Test;

import bean.EmployeeBean;

public class EmployeeBeanTest {

	public static void main(String[] args) {
		EmployeeBean empBean = new EmployeeBean();
		System.out.println(empBean);
		
		// 期待値（変数）
        String emp_id1 = "emp_id";
        String emp_name1 = "emp_name";
        String email1 = "email@example.com";
        String dpt_id1 = "D100";
        String pos_id1 = "P01";

        String emp_id2 = "emp_id2";
        String emp_name2 = "emp_name2";
        String furigana2 = "ふりがな";
        String email2 = "email2@example.com";
        String dpt_id2 = "D200";
        String pos_id2 = "P02";

        // 1) no-arg コンストラクタ + setter を使う場合
        EmployeeBean bean0 = new EmployeeBean();
        bean0.setEmp_id(emp_id1);
        bean0.setEmp_name(emp_name1);
        // furigana は今回はセットしない（null の想定）
        bean0.setEmail(email1);
        bean0.setDpt_id(dpt_id1);
        bean0.setPos_id(pos_id1);

        // 2) 5引数コンストラクタ (emp_id, emp_name, email, dpt_id, pos_id)
        EmployeeBean bean1 = new EmployeeBean(emp_id1, emp_name1, email1, dpt_id1, pos_id1);

        // 3) 6引数コンストラクタ (emp_id, emp_name, furigana, email, dpt_id, pos_id)
        EmployeeBean bean2 = new EmployeeBean(emp_id2, emp_name2, furigana2, email2, dpt_id2, pos_id2);

        // 出力（直接 getter を呼ぶ）
        System.out.println("=== bean0 (no-arg + setters) ===");
        System.out.println("emp_id expected: " + emp_id1 + "  actual: " + bean0.getEmp_id());
        System.out.println("emp_name expected: " + emp_name1 + "  actual: " + bean0.getEmp_name());
        System.out.println("furigana expected: " + null + "  actual: " + bean0.getFurigana());
        System.out.println("email expected: " + email1 + "  actual: " + bean0.getEmail());
        System.out.println("dpt_id expected: " + dpt_id1 + "  actual: " + bean0.getDpt_id());
        System.out.println("pos_id expected: " + pos_id1 + "  actual: " + bean0.getPos_id());

        System.out.println("\n=== bean1 (5-arg constructor) ===");
        System.out.println("emp_id expected: " + emp_id1 + "  actual: " + bean1.getEmp_id());
        System.out.println("emp_name expected: " + emp_name1 + "  actual: " + bean1.getEmp_name());
        System.out.println("furigana expected: " + null + "  actual: " + bean1.getFurigana());
        System.out.println("email expected: " + email1 + "  actual: " + bean1.getEmail());
        System.out.println("dpt_id expected: " + dpt_id1 + "  actual: " + bean1.getDpt_id());
        System.out.println("pos_id expected: " + pos_id1 + "  actual: " + bean1.getPos_id());

        System.out.println("\n=== bean2 (6-arg constructor) ===");
        System.out.println("emp_id expected: " + emp_id2 + "  actual: " + bean2.getEmp_id());
        System.out.println("emp_name expected: " + emp_name2 + "  actual: " + bean2.getEmp_name());
        System.out.println("furigana expected: " + furigana2 + "  actual: " + bean2.getFurigana());
        System.out.println("email expected: " + email2 + "  actual: " + bean2.getEmail());
        System.out.println("dpt_id expected: " + dpt_id2 + "  actual: " + bean2.getDpt_id());
        System.out.println("pos_id expected: " + pos_id2 + "  actual: " + bean2.getPos_id());
	}

}
