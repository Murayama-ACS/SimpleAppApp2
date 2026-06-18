package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.EmployeeBean;
import dao.EmployeeDAO;

/**
 * Servlet implementation class Verify1
 */
@WebServlet("/Verify1")
public class Verify1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/verify1.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String empId = request.getParameter("empId");
        String email = request.getParameter("email");

        EmployeeDAO empDao = new EmployeeDAO();
        EmployeeBean emp = empDao.empInfo(empId, email);

        if (emp == null) {
            request.setAttribute("errorMessage", "社員IDまたはEmailが間違っています。");
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/verify1.jsp");
    		rd.forward(request, response);
            return;
        }
        
        request.setAttribute("empId", empId);
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/verify2.jsp");
		rd.forward(request, response);
    }

}
