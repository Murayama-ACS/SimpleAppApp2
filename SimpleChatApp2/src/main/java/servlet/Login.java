package servlet;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import beans.ChatBean;
import beans.UserBean;
import dao.ChatDAO;
import dao.UserDAO;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//リクエストパラメータの取得
		request.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");
		if ("新規登録".equals(action)) {
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/register.jsp");
			rd.forward(request, response);
			return; 
		}
		if ("ログイン".equals(action)) {
			String email = request.getParameter("email");
			String pass = request.getParameter("pass");
			//DAOのインスタンス生成
			UserDAO userDAO = new UserDAO();
			//UserBeanインスタンス生成
			UserBean userBean = userDAO.userInfo(email,pass);
			//フォワード先のURL
			String url = "index.jsp";
			if(userBean != null) {//ユーザ情報を取得できた場合の処理
				HttpSession session = request.getSession();
				session.setAttribute("userBean", userBean);
				ChatDAO chatDAO = new ChatDAO();
				ArrayList<ChatBean> chatList = chatDAO.selectChat();
				ServletContext application = this.getServletContext();
				application.setAttribute("ChatList", chatList);
				url = "WEB-INF/jsp/main.jsp";
			} else { //ユーザ情報を取得できなかった場合の処理
				//リクエストスコープにエラーメッセージを保存
				request.setAttribute("eMsg", "Email又はパスワードが違います。");
			}
			//フォワード
			RequestDispatcher rd = request.getRequestDispatcher(url);
			rd.forward(request,response);
		}
	}



}
