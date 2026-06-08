package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DAO.EmployeeDAO;
import DAO.QuizDAO;
import bean.EmployeeBean;
import bean.QuizBean;

/**
 * Servlet implementation class PassResetConfirm
 */
@WebServlet("/PassResetConfirm1")
public class PassResetConfirm1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String url = "WEB-INF/jsp/pass_reset1.jsp";
		String eMsg = "";
		
		String pass = request.getParameter("pass");
		String retype = request.getParameter("retype");
		
		if(pass.equals("") || retype.equals("") || !pass.equals(retype)) {
			eMsg = "入力が間違っています。";
			request.setAttribute("eMsg", eMsg);
		}else {
			EmployeeDAO empDAO = new EmployeeDAO();
			HttpSession session = request.getSession();
			EmployeeBean empBean = (EmployeeBean)session.getAttribute("empBean");
			QuizDAO quizDAO = new QuizDAO();
			QuizBean quizBean = new QuizBean();
			if(empDAO.updatePassword(empBean, pass) == 0) {
				eMsg = "パスワードリセットが失敗しました。";
			}else {
				url = "WEB-INF/jsp/pass_reset_confirm.jsp";
			}
		}
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
