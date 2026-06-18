package servlet;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.QuizBean;
import dao.FailedLoginDAO;
import dao.QuizDAO;

/**
 * Servlet implementation class Verify2
 */
@WebServlet("/Verify2")
public class Verify2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
        String empId = request.getParameter("empId");
        
        String q1 = request.getParameter("q1");
        String a1 = request.getParameter("answer1");
        String q2 = request.getParameter("q2");
        String a2 = request.getParameter("answer2");
        String q3 = request.getParameter("q3");
        String a3 = request.getParameter("answer3");

        request.setAttribute("q1", q1);
        request.setAttribute("q2", q2);
        request.setAttribute("q3", q3);
        request.setAttribute("empId", empId);

        FailedLoginDAO flDao = new FailedLoginDAO();

        try {
            if (flDao.isLocked(empId)) {
                request.setAttribute("errorMessage", "アカウントは現在ロックされています。時間を置いて再試行するか、管理者にお問い合わせください。");
                request.getRequestDispatcher("/WEB-INF/jsp/verify2.jsp").forward(request, response);
                return;
            }

            QuizDAO quizDao = new QuizDAO();
            QuizBean check1 = quizDao.quizInfo(q1, a1, empId);
            QuizBean check2 = quizDao.quizInfo(q2, a2, empId);
            QuizBean check3 = quizDao.quizInfo(q3, a3, empId);

            if (check1 != null && check2 != null && check3 != null) {
                
                flDao.resetOnSuccess(empId);
                
                request.getRequestDispatcher("/WEB-INF/jsp/pass_reset2.jsp").forward(request, response);
                
            } else {
                FailedLoginDAO.FailureResult result = flDao.recordQuizFailure(empId);
                
                String eMsg = "回答が間違っています。";
                
                if (result == FailedLoginDAO.FailureResult.NOW_LOCKED || result == FailedLoginDAO.FailureResult.ALREADY_LOCKED) {
                    eMsg = "試行回数の上限に達したため、アカウントがロックされました。";
                } else {
                    Integer remaining = flDao.getRemainingQuizAttemptsByEmpId(empId);
                    if (remaining != null) {
                        eMsg += "（あと " + remaining + " 回でアカウントがロックされます）";
                    }
                }

                request.setAttribute("errorMessage", eMsg);
                request.getRequestDispatcher("/WEB-INF/jsp/verify2.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "システムエラーが発生しました。時間を置いて再度お試しください。");
            request.getRequestDispatcher("/WEB-INF/jsp/verify2.jsp").forward(request, response);
        }
	}
}
