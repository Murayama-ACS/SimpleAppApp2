package servlet;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.EmployeeBean;
import dao.EmployeeDAO;

/**
 * Servlet implementation class EmployeeInfo
 */
@WebServlet("/EmployeeInfo")
public class EmployeeInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		EmployeeDAO empDAO = new EmployeeDAO();
		ArrayList<EmployeeBean> empList = empDAO.empInfo();
		if(empList == null) {
			empList = new ArrayList<EmployeeBean>();
			System.out.println("empListが空です");
		}
		
		ServletContext app = this.getServletContext();
		app.setAttribute("empList", empList);
		RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/user_info.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
