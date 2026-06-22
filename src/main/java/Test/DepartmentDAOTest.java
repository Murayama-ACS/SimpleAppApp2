package Test;

import java.util.List;

import bean.DepartmentBean;
import dao.DepartmentDAO;

public class DepartmentDAOTest {

	public static void main(String[] args) {
		DepartmentDAO deptDAO = new DepartmentDAO();
		List<DepartmentBean> dptList = deptDAO.findAll();
		int i = 1;
		for(DepartmentBean dptBean : dptList) {
			System.out.print(dptBean.getDpt_id() + ":");
			System.out.print(dptBean.getDpt_name() + ":");
			System.out.println(i++);
		}
		if(dptList.size() == 0) {
			System.out.println("リストで取得した情報がありません。");
		}
	}
}
