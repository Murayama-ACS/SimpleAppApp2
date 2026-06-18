package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.EmployeeBean;
import dao.ApplicationDAO;

@WebServlet("/ApplicationDelete")
public class ApplicationDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		String apctId = request.getParameter("apct_id");

		// セッションから操作者（ログインユーザー）の情報を取得
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
		
		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}
		
		String operatorEmpId = employee.getEmp_id();

		try {
			ApplicationDAO dao = new ApplicationDAO();
			int result = 0;
			
			if (apctId != null && !apctId.trim().isEmpty()) {
				// 取得した文字列パラメータを直接DAOの引数として渡す
				result = dao.logicalDelete(apctId, operatorEmpId);
			}
			
			if (result == 0) {
				throw new Exception("対象の申請データが存在しないか、既に削除されています。");
			}
			
			response.sendRedirect(request.getContextPath() + "/ApplicationHistoryServlet");
			
		} catch (Exception e) {
			log("ApplicationDeleteServlet error", e);
			request.setAttribute("errorMessage", "申請の削除に失敗しました。理由: " + e.getMessage());
			request.getRequestDispatcher("/ApplicationHistoryServlet").forward(request, response);
		}
	}
}