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

/**
 * Servlet implementation class InsertUser
 */
@WebServlet("/EmployeeAdd")
public class EmployeeAdd extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = "WEB-INF/jsp/user_signup.jsp";
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = "WEB-INF/jsp/user_signup.jsp";
		
		String emp_id = request.getParameter("emp_id");
		String emp_name = request.getParameter("emp_name");
		String email = request.getParameter("email");
		String dpt_id = request.getParameter("dpt_id");
		String pos_id = request.getParameter("pos_id");
		
		System.out.println("emp_id:" + emp_id);
		System.out.println("emp_name:" + emp_name);
		System.out.println("email:" + email);
		System.out.println("dpt_id:" + dpt_id);
		System.out.println("pos_id:" + pos_id);
		
		if(emp_id.isEmpty() || emp_name.isEmpty() || email.isEmpty() || dpt_id == null || pos_id == null) {
			request.setAttribute("eMsg", "社員ID、名前、Email、部署、役職のいずれかが入力されていません。");
		}else {
			EmployeeBean insertEmpBean = new EmployeeBean(emp_id, emp_name, email, dpt_id, pos_id);
			HttpSession session = request.getSession();
			session.setAttribute("insertEmpBean", insertEmpBean);
			url = "WEB-INF/jsp/user_confirm.jsp";
		}
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
