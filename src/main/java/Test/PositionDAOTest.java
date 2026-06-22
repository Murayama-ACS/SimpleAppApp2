package Test;

import java.util.List;

import bean.PositionBean;
import dao.PositionDAO;

public class PositionDAOTest {

	public static void main(String[] args) {
		PositionDAO posDAO = new PositionDAO();
		List<PositionBean> posList = posDAO.findAll();
		int i = 1;
		for(PositionBean posBean : posList) {
			System.out.print(posBean.getPos_id() + ":");
			System.out.print(posBean.getPos_name() + ":");
			System.out.println(i++);
		}
		if(posList.size() == 0) {
			System.out.println("リストで取得した情報がありません。");
		}
	}

}
