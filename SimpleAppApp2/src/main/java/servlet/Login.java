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

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String url = "login.jsp";
		String identifier = request.getParameter("identifier");
		String pass = request.getParameter("pass");
		String eMsg = "社員IDもしくはメールアドレスを入力してください。";
		
		if(identifier.equals("")) {
			//社員ID or メールアドレスが入力されていない場合はエラーメッセージをセットしてlogin.jspに戻る
			request.setAttribute("eMsg",eMsg);
		}else {
			//
			EmployeeDAO empDAO = new EmployeeDAO(); 
			String input = identifier.trim();
			boolean isEmail = input.contains("@");
			System.out.println(input);
			System.out.println(pass);

			System.out.println(isEmail);
			EmployeeBean empBean = empDAO.empInfo(identifier, pass, isEmail);
			//System.out.println("employee:" + empBean);
			if(empBean == null) {
				//従業員情報が取得できなかった場合はエラーメッセージをセットしてlogin.jspに戻る
				if(isEmail) {
					eMsg = "メールアドレスかパスワードの入力が間違っています。";
				}else {
					eMsg = "社員IDかパスワードの入力が間違っています。";
				}
				request.setAttribute("eMsg",eMsg);
			}else {
				//従業員情報を取得できたらセッションスコープにセットしてtoppage.jspにフォワードする
				HttpSession session = request.getSession();
				session.setAttribute("empBean", empBean);
				if(pass.equals("1234")) {
					url = "WEB-INF/jsp/pass_reset1.jsp";
				}else {
					url = "WEB-INF/jsp/toppage.jsp";
				}
			}
		}
		System.out.println(url + "+" + pass);
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

}
