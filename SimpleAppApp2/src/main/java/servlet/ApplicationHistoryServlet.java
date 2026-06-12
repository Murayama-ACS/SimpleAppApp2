package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.ApplicationBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;

@WebServlet("/ApplicationHistoryServlet")
public class ApplicationHistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
		
		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		String posId = employee.getPos_id();
		String dptId = employee.getDpt_id();

		String scope = request.getParameter("scope");
		if (scope == null || scope.isEmpty()) {
			scope = "self";
		}

		if ("E00".equals(posId) && !"D100".equals(dptId)) {
			scope = "self";
		}
		
		if ("management".equals(scope) && !"D100".equals(dptId)) {
			scope = "self";
		}

		String filter = request.getParameter("filter");
		if (filter == null || filter.isEmpty()) {
			filter = "unapproved"; 
		}

		String statusFilter = "all";
		if ("unapproved".equals(filter)) {
			statusFilter = "incomplete";
		}

		try {
			ApplicationDAO dao = new ApplicationDAO();
			String dptName = dao.selectDepartmentName(dptId);
			
			List<ApplicationBean> list = dao.getHistoryApplications(employee, scope, statusFilter);

			request.setAttribute("empBean", employee);
			request.setAttribute("dpt_name", dptName);
			request.setAttribute("appList", list);
			request.setAttribute("currentScope", scope);
			request.setAttribute("currentStatusFilter", statusFilter);

			RequestDispatcher rd = request.getRequestDispatcher("/history.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			e.printStackTrace(); 
			
			// 【修正】自身のページ(history.jsp)でエラーを表示するための設定
			request.setAttribute("errorMessage", "履歴情報の取得中にシステムエラーが発生しました。詳細: " + e.getMessage());
			
			// history.jsp上部のスクリプトレットが初期表示でヌルポインタ(NullPointerException)を起こさないための最低限の補填
			request.setAttribute("empBean", employee);
			request.setAttribute("currentScope", scope);
			request.setAttribute("currentStatusFilter", statusFilter);
			
			// 遷移先を login_mock.jsp から history.jsp へ変更
			request.getRequestDispatcher("/history.jsp").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}
}