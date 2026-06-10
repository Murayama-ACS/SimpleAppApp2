package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.EmployeeBean;
import dao.EmployeeDAO;


@WebServlet("/PassReset1")
public class PassReset1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String url = "WEB-INF/jsp/verify1.jsp";//login→verify1への遷移
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String url = "WEB-INF/jsp/verify1.jsp";
		String eMsg = "";

		EmployeeDAO empDAO = new EmployeeDAO();
		EmployeeBean empBean = new EmployeeBean();
		HttpSession session = request.getSession();
		//社員IDとメールアドレスをリクエストスコープから取得
		String emp_id = request.getParameter("emp_id");
		String email = request.getParameter("email");
		System.out.println("emp_id in PassReset1:" + emp_id);
		System.out.println("email in PassReset1:" + email);

		if(emp_id.equals("") || email.equals("")){
			eMsg = "社員IDもしくはメールアドレスの入力が正しくありません。";
			request.setAttribute("eMsg", eMsg);
		}else{
			//登録されている社員なのか判定
			empBean = empDAO.empInfo(emp_id, email);
			if(empBean == null) {
				eMsg = "該当する社員が存在しません。";
				request.setAttribute("eMsg", eMsg);
			}else {
				session.setAttribute("empBean", empBean);
				url = "WEB-INF/jsp/verify2.jsp";
			}
		}

		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
