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
import dao.EmployeeDAO;

/**
 * Servlet implementation class PassResetConfirm
 */
@WebServlet("/PassResetConfirm2")
public class PassResetConfirm2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String url = "WEB-INF/jsp/pass_reset2.jsp";//login→verify1への遷移
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
				url = "WEB-INF/jsp/pass_reset_confirm.jsp";
			}
		}
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
