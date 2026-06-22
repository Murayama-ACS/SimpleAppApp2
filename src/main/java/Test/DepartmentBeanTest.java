package Test;

import java.util.Objects;

import bean.DepartmentBean;

public class DepartmentBeanTest {

	public static void main(String[] args) {
		DepartmentBean deptBean = new DepartmentBean();
		System.out.println(deptBean);
		
		// 期待値（変数名そのままの文字列）
		String dpt_id = "D100";
		String dpt_name = "総務部";

		    // 1) コンストラクタで生成してテスト
		    DepartmentBean beanCtor = new DepartmentBean(dpt_id, dpt_name);

		    System.out.println("=== Constructor-based test ===");
		    // 以下の getter 名は実装に合わせて変更してください（例: getDpt_id() / getDpt_name()）
		    System.out.println("dpt_id expected: " + dpt_id + "  actual: " + beanCtor.getDpt_id());
		    System.out.println("dpt_name expected: " + dpt_name + "  actual: " + beanCtor.getDpt_name());

		    boolean ctorOk = dpt_id.equals(beanCtor.getDpt_id())
		            && dpt_name.equals(beanCtor.getDpt_name());
		    System.out.println("Constructor test: " + (ctorOk ? "OK" : "NG"));

		    // 2) セッターで設定してテスト（no-arg コンストラクタが存在する想定）
		    DepartmentBean beanSetter = new DepartmentBean(); // no-arg がなければ、ダミー引数で生成してから set してください

		    // 以下の setter 名も実装に合わせて変更してください（例: setDpt_id(...) / setDpt_name(...)）
		    beanSetter.setDpt_id(dpt_id);
		    beanSetter.setDpt_name(dpt_name);

		    System.out.println("=== Setter-based test ===");
		    System.out.println("dpt_id expected: " + dpt_id + "  actual: " + beanSetter.getDpt_id());
		    System.out.println("dpt_name expected: " + dpt_name + "  actual: " + beanSetter.getDpt_name());

		    boolean setterOk = Objects.equals(dpt_id, beanSetter.getDpt_id())
		            && Objects.equals(dpt_name, beanSetter.getDpt_name());
		    System.out.println("Setter test: " + (setterOk ? "OK" : "NG"));
	}

}
