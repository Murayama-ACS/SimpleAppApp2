package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

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
        String url = "/WEB-INF/jsp/verify2.jsp";
        
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
        
        System.out.println("q1:" + q1 + ":a1:" + a1);
        System.out.println("q2:" + q2 + ":a2:" + a2);
        System.out.println("q3:" + q3 + ":a3:" + a3);

        FailedLoginDAO flDao = new FailedLoginDAO();

        try {
            if (flDao.isLocked(empId)) {
                request.setAttribute("errorMessage", "アカウントは現在ロックされています。時間を置いて再試行するか、管理者にお問い合わせください。");
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }
            
            // answers も同様に検証（空チェック等）
            if (q1 == null || q2 == null || q3 == null) {
                request.setAttribute("errorMessage", "質問を3つ選択してください。");
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }
            Set<String> set = new HashSet<>();
            set.add(q1);
            set.add(q2);
            set.add(q3);
            if (set.size() < 3) {
                request.setAttribute("errorMessage", "同じ質問を複数選択しないでください。");
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }
            QuizDAO quizDao = new QuizDAO();

            // 次に回答の検証（DBエラーはSQLExceptionでcatch）
            QuizBean check1 = quizDao.quizInfo(q1, a1, empId);
            QuizBean check2 = quizDao.quizInfo(q2, a2, empId);
            QuizBean check3 = quizDao.quizInfo(q3, a3, empId);

            if (check1 != null && check2 != null && check3 != null) {
                
                flDao.resetOnSuccess(empId);
                url = "WEB-INF/jsp/pass_reset2.jsp";
                request.getRequestDispatcher(url).forward(request, response);
                
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
                request.getRequestDispatcher(url).forward(request, response);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "システムエラーが発生しました。時間を置いて再度お試しください。");
            request.getRequestDispatcher(url).forward(request, response);
        }
	}
}
