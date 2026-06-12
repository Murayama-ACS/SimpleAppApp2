package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.EmployeeDAO;


@WebServlet("/EmployeeRemove")
public class EmployeeRemove extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
