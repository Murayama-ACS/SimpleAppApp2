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
 * Servlet implementation class InitPassReset
 */
@WebServlet("/InitPassReset")
public class InitPassReset extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset1.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		try {
	        HttpSession session = request.getSession();
	        EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean");

	        if (employee == null || employee.getEmp_id() == null) {
	            response.sendRedirect(request.getContextPath() + "/index.jsp");
	            return;
	        }

	        String newPassword = request.getParameter("newPassword");
	        String confirmPassword = request.getParameter("confirmPassword");

	        String q1 = request.getParameter("question1");
	        String a1 = request.getParameter("answer1");
	        String q2 = request.getParameter("question2");
	        String a2 = request.getParameter("answer2");
	        String q3 = request.getParameter("question3");
	        String a3 = request.getParameter("answer3");

	        if (newPassword == null || !newPassword.equals(confirmPassword)) {
	            request.setAttribute("errorMessage", "パスワードが一致しません。もう一度入力してください。");
	            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset1.jsp");
	            rd.forward(request, response);
	            return;
	        }

	        if (q1.equals(q2) || q2.equals(q3) || q1.equals(q3)) {
	            request.setAttribute("errorMessage", "異なる秘密の質問を3つ選択してください。");
	            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset1.jsp");
	            rd.forward(request, response);
	            return;
	        }

	        String employeeId = employee.getEmp_id();

	        dao.EmployeeDAO empDao = new dao.EmployeeDAO();
	        int updateResult = empDao.updatePassword(employee, newPassword);

	        if (updateResult == -1) {
	            request.setAttribute("errorMessage", "初期パスワード（Abcd1234）は使用できません。別のパスワードを設定してください。");
	            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset1.jsp");
	            rd.forward(request, response);
	            return;
	        } else if (updateResult == -2) {
	            request.setAttribute("errorMessage", "パスワードは8文字以上で、大文字、小文字、数字をそれぞれ1つ以上含む必要があります。");
	            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset1.jsp");
	            rd.forward(request, response);
	            return;
	        } else if (updateResult == 0) {
	            request.setAttribute("errorMessage", "パスワードの更新に失敗しました。");
	            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset1.jsp");
	            rd.forward(request, response);
	            return;
	        }

	        dao.QuizDAO quizDao = new dao.QuizDAO();
	        quizDao.insertQuiz(new bean.QuizBean(employeeId, q1, a1));
	        quizDao.insertQuiz(new bean.QuizBean(employeeId, q2, a2));
	        quizDao.insertQuiz(new bean.QuizBean(employeeId, q3, a3));

	        request.setAttribute("isSuccess", true);
	        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset1.jsp");
            rd.forward(request, response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        log("InitPassReset error", e);
	        request.setAttribute("errorMessage", "システムエラーが発生しました。詳細: " + e.getMessage());
	        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset1.jsp");
	        rd.forward(request, response);
	    }
	}
}
