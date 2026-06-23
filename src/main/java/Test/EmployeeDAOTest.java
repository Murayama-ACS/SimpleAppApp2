package Test;

import java.sql.SQLException;

import bean.EmployeeBean;
import dao.EmployeeDAO;

public class EmployeeDAOTest {

	public static void main(String[] args) {
		EmployeeDAO empDAO = new EmployeeDAO();
		EmployeeBean empBean = new EmployeeBean("A3", "松原", "まつばら","a@a", "D700", "E03");
		int r = empDAO.insertEmployee(empBean);
		System.out.println(r);
		int upr = empDAO.updatePassword(empBean, "Test0123");
		System.out.println(upr);
		int delr = empDAO.deleteEmpInfo("A2", "A1");
		System.out.println(delr);
		/*try {
			PageResult<EmployeeBean> pr = empDAO.searchEmployees(null, null, null, null, null, null, 20, 0);
			List<EmployeeBean> empList = pr.getItems();
			
			System.out.println(empList.size());
			for(EmployeeBean emp : empList) {
				System.out.println(emp.getEmp_id() + ":" + emp.getEmp_name());
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}*/
		
		EmployeeBean boku = null;
		try {
			boku = empDAO.authenticateAndGetEmployee("A1", "Test01234" , false, null, null);
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
//		System.out.println(boku.getEmp_name());
		try {
			System.out.println(empDAO.findEmpIdByEmail("ryochan@example.com"));
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
