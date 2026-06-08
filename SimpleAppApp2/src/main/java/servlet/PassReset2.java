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
 * Servlet implementation class PassReset2
 */
@WebServlet("/PassReset2")
public class PassReset2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	//		
	//	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String url = "WEB-INF/jsp/verify2.jsp";
		String eMsg = "";

		EmployeeDAO empDAO = new EmployeeDAO();
		EmployeeBean empBean = new EmployeeBean();
		QuizBean quizBean = new QuizBean();
		HttpSession session = request.getSession();
		String quiz = request.getParameter("quiz");
		String answer = request.getParameter("answer");

		if(quiz.equals("") || answer.equals("")){
			request.setAttribute("eMsg", "秘密の質問、秘密の回答への入力が正しくありません。");
		}else {//verify2→pass_reset2への遷移
			//秘密の質問に答えられるか判定
			empBean = (EmployeeBean)session.getAttribute("empBean");
			String emp_id = empBean.getEmp_id();
			QuizDAO quizDAO = new QuizDAO();
			quizBean = quizDAO.quizInfo(quiz,answer,emp_id);
			url = "WEB-INF/jsp/pass_reset2.jsp";
		}
		
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
		

	}

}
