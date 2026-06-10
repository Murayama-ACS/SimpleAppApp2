package servlet;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.EmployeeBean;
import bean.QuizBean;
import dao.FailedLoginDAO;
import dao.QuizDAO;

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
		HttpSession session = request.getSession();
		EmployeeBean empBean = (EmployeeBean) session.getAttribute("empBean");
		if (empBean == null) {
		// セッションが切れている等の保険
		request.setAttribute("eMsg", "セッションが切れました。最初からやり直してください。");
		} else {
		String quiz = request.getParameter("quiz");
		String answer = request.getParameter("answer");

		    if (quiz == null || quiz.trim().isEmpty() || answer == null || answer.trim().isEmpty()) {
		        request.setAttribute("eMsg", "秘密の質問、秘密の回答が入力されていません。");
		    } else {
		        String emp_id = empBean.getEmp_id();
		        QuizDAO quizDAO = new QuizDAO();

		        // 要記録情報
		        String remoteAddr = request.getRemoteAddr();
		        String userAgent = request.getHeader("User-Agent");

		        try {
		            QuizBean quizBean = quizDAO.authenticateQuiz(quiz, answer, emp_id /*, remoteAddr, userAgent */);
		            if (quizBean == null) {
		            	FailedLoginDAO flDao = new FailedLoginDAO();
		            	try {
		            	Integer remaining = flDao.getRemainingQuizAttemptsByEmpId(emp_id);
		            	if (remaining != null) {
		            	if (remaining == -1) {
		            	request.setAttribute("eMsg", "アカウントは一時的にロックされています。");
		            	} else {
		            	request.setAttribute("eMsg", "秘密の質問または秘密の回答が正しくありません。あと " + remaining + " 回でアカウントがロックされます。");
		            	}
		            	} else {
		            	request.setAttribute("eMsg", "秘密の質問もしくは秘密の回答が正しくありません。");
		            	}
		            	} catch (SQLException ex) {
		            	ex.printStackTrace();
		            	request.setAttribute("eMsg", "秘密の質問もしくは秘密の回答が正しくありません。");
		            	}
		            	} else {
		            	// 成功
		            	url = "WEB-INF/jsp/pass_reset2.jsp";
		            	}
		        } catch (SQLException ex) {
		            ex.printStackTrace();
		            request.setAttribute("eMsg", "システムエラーが発生しました。時間を置いて再度お試しください。");
		        }
		    }
		}

		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
		}
	
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		request.setCharacterEncoding("UTF-8");
//		String url = "WEB-INF/jsp/verify2.jsp";
//
//		EmployeeDAO empDAO = new EmployeeDAO();
//		EmployeeBean empBean = new EmployeeBean();
//		QuizBean quizBean = new QuizBean();
//		HttpSession session = request.getSession();
//		String quiz = request.getParameter("quiz");
//		String answer = request.getParameter("answer");
//
//		if(quiz.equals("") || answer.equals("")){
//			request.setAttribute("eMsg", "秘密の質問、秘密の回答が入力されていません。");
//		}else {//verify2→pass_reset2への遷移
//			//秘密の質問に答えられるか判定
//			empBean = (EmployeeBean)session.getAttribute("empBean");
//			String emp_id = empBean.getEmp_id();
//			QuizDAO quizDAO = new QuizDAO();
//			quizBean = quizDAO.quizInfo(quiz,answer,emp_id);
//			if(quizBean == null) {
//				request.setAttribute("eMsg", "秘密の質問もしくは秘密の回答が正しくありません。");
//			}else {
//				url = "WEB-INF/jsp/pass_reset2.jsp";
//			}
//		}
//		
//		RequestDispatcher rd = request.getRequestDispatcher(url);
//		rd.forward(request, response);
//		
//
//	}

}
