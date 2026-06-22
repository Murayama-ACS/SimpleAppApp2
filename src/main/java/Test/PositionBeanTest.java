package Test;

import bean.PositionBean;

public class PositionBeanTest {

	public static void main(String[] args) {
		PositionBean posBean = new PositionBean();
		System.out.println(posBean);
		
		// 期待値
		String posId = "P01";
		String posName = "Manager";

		    // 1) no-arg コンストラクタ + setter
		    PositionBean pos0 = new PositionBean();
		    pos0.setPos_id(posId);
		    pos0.setPos_name(posName);

		    System.out.println("=== PositionBean: pos0 (no-arg + setters) ===");
		    System.out.println("pos_id expected: " + posId + "  actual: " + pos0.getPos_id());
		    System.out.println("pos_name expected: " + posName + "  actual: " + pos0.getPos_name());

		    // 2) 2-arg コンストラクタ
		    PositionBean pos1 = new PositionBean(posId, posName);

		    System.out.println("\n=== PositionBean: pos1 (2-arg constructor) ===");
		    System.out.println("pos_id expected: " + posId + "  actual: " + pos1.getPos_id());
		    System.out.println("pos_name expected: " + posName + "  actual: " + pos1.getPos_name());
	}

}
