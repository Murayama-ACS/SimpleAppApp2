package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import beans.UserBean;
import dao.UserDAO;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/register.jsp");
		rd.forward(request, response);	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String pass = request.getParameter("pass");

		if (name == null || name.isEmpty() || email == null || email.isEmpty() || pass == null || pass.isEmpty()) {
			request.setAttribute("rMsg", "すべての項目を入力してください。");
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/register.jsp");
			rd.forward(request, response);
			return;
		}
		UserBean newUser = new UserBean();
		newUser.setName(name);
		newUser.setEmail(email);
		newUser.setPass(pass);

		UserDAO userDAO = new UserDAO();
		int result = userDAO.insertUser(newUser);

		if (result > 0) {
			request.setAttribute("eMsg", "アカウントの登録が完了しました。ログインしてください。");
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
		} else {
			request.setAttribute("rMsg", "このEmailは既に登録されているか、登録に失敗しました。");
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/register.jsp");
			rd.forward(request, response);
		}
	}
}

