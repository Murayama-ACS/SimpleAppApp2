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
 * Servlet implementation class InitPassReset
 */
@WebServlet("/InitPassReset")
public class InitPassReset extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String url = "WEB-INF/jsp/pass_reset1.jsp";//login→verify1への遷移
		String eMsg = "";

		String pass = request.getParameter("pass");
		String retype = request.getParameter("retype");
		String quiz = request.getParameter("quiz");
		String answer = request.getParameter("answer");

		if(pass.equals("") || retype.equals("") || !pass.equals(retype)) {
			eMsg = "パスワードの入力が正しくありません";
			request.setAttribute("eMsg", eMsg);
		}else if(quiz.equals("") || answer.equals("")){
			eMsg = "秘密の質問の入力が正しくありません";
			request.setAttribute("eMsg", eMsg);
		}else {
			EmployeeDAO empDAO = new EmployeeDAO();
			HttpSession session = request.getSession();
			EmployeeBean empBean = (EmployeeBean)session.getAttribute("empBean");
			QuizDAO quizDAO = new QuizDAO();
			QuizBean quizBean = new QuizBean(empBean.getEmp_id(), quiz, answer);
			int resultPass = empDAO.updatePassword(empBean, pass);
			if(resultPass == 0) {//パスワードのリセット時にエラー発生の場合
				System.out.println("パスワードリセット失敗 in InitPassReset");
				eMsg = "パスワードリセットが失敗しました。";
				request.setAttribute("eMsg", eMsg);
			}else if(resultPass == -1) {//入力したパスワードが初期パスワード（1234）の場合
				eMsg = "初期パスワードから変更されていません。新しいパスワードを入力してください。";
				request.setAttribute("eMsg", eMsg);
			}else if(resultPass == -2){//パスワードの制約に則していない場合
				eMsg = "パスワードは8文字以上で、英大文字・英小文字・数字・記号をそれぞれ1文字以上含めてください。";
				request.setAttribute("eMsg", eMsg);
			}else { 
				int resultQuiz = quizDAO.insertQuiz(quizBean);
				if(resultQuiz == 0) {
					System.out.println("問題の登録失敗 in InitPassReset");
					eMsg = "問題の登録が失敗しました。";
					request.setAttribute("eMsg", eMsg);
				}else if(resultQuiz == -1){
					eMsg = "同じ問題への回答が登録されています。\nセキュリティ保護のため、異なる問題への回答を登録してください";
					request.setAttribute("eMsg", eMsg);
				}else {
					url = "WEB-INF/jsp/initpassresetconfirm.jsp";
				}
			}
		}

		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
