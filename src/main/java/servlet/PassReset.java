package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.EmployeeBean;
import dao.EmployeeDAO;

/**
 * Servlet implementation class PassReset
 */
@WebServlet("/PassReset")
public class PassReset extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String empId = request.getParameter("empId");
		String newPass = request.getParameter("newPassword");
		String confirmPass = request.getParameter("confirmPassword");

		request.setAttribute("empId", empId);

		if (newPass == null || !newPass.equals(confirmPass)) {
			request.setAttribute("isSuccess", false);
			request.setAttribute("errorMessage", "パスワードが一致しません。もう一度入力してください。");
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset2.jsp");
			rd.forward(request, response);
			return;
		}

		EmployeeDAO empDao = new EmployeeDAO();
		EmployeeBean emp = new EmployeeBean();
		emp.setEmp_id(empId); 
		
		int result = empDao.updatePassword(emp, newPass);

		if (result == -1) {
			request.setAttribute("isSuccess", false);
			request.setAttribute("errorMessage", "初期パスワード（Abcd1234）は使用できません。別のパスワードを設定してください。");
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset2.jsp");
			rd.forward(request, response);
			return;
		} else if (result == -2) {
			request.setAttribute("isSuccess", false);
			request.setAttribute("errorMessage", "パスワードは8文字以上で、大文字、小文字、数字をそれぞれ1つ以上含む必要があります。");
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/pass_reset2.jsp");
			rd.forward(request, response);
			return;
		} else if (result > 0) {
			request.setAttribute("isSuccess", true);
			request.setAttribute("message", "パスワードをリセットしました。新しいパスワードでログインしてください。");
		} else {
			request.setAttribute("isSuccess", false);
			request.setAttribute("message", "システムエラー：パスワードの設定に失敗しました。もう一度やり直してください。");
		}

		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/reset_confirm.jsp");
		rd.forward(request, response);
	}
}
