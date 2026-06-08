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
import bean.EmployeeBean;

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
			int result = empDAO.updatePassword(empBean, pass);
			if(result == 0) {
				eMsg = "パスワードリセットが失敗しました。";
			}else if(result == -1) {
				eMsg = "初期パスワードから変更されていません。新しいパスワードを入力してください。";
				request.setAttribute("eMsg", eMsg);
			}else {
				url = "WEB-INF/jsp/pass_reset_confirm.jsp";
			}
		}
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
