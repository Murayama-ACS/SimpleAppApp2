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


@WebServlet("/EmployeeRemove")
public class EmployeeRemove extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1. ログインチェック
		String loginUrl = "/login.jsp";
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean"); 

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + loginUrl);
			return;
		}

		// 2. 権限チェック
		String ldptId = employee.getDpt_id();
		if (!ldptId.matches("^D4.*$")) {
			request.setAttribute("eMsg", "アクセス権限がありません。");
			request.getRequestDispatcher(loginUrl).forward(request, response);
			return;
		}
		String url = "WEB-INF/jsp/user_info.jsp";
		String emp_id = request.getParameter("removeEmp_id");
		EmployeeDAO empDAO = new EmployeeDAO();
		int result = empDAO.deleteEmpInfo(emp_id);

		if(result == 0) {
			request.setAttribute("eMsg", "ユーザーの削除に失敗しました。");
		}else if(result == -1){
			request.setAttribute("eMsg", "既に存在しないユーザーです。");
		}else {
			url = "WEB-INF/jsp/remove_confirm.jsp";
		}
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
