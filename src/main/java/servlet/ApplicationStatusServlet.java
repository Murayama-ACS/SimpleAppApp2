package servlet;

import java.io.IOException;
import java.sql.SQLException;

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

@WebServlet("/ApplicationStatus")
public class ApplicationStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		EmployeeBean loginUser = (EmployeeBean) session.getAttribute("empBean");

		// D100(管理部) または D200(経理部) 以外は弾く
		if (loginUser == null || (!"D200".equals(loginUser.getDpt_id()) && !"D100".equals(loginUser.getDpt_id()))) {
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}

		// 1. 検索パラメータの取得
		String qDept = request.getParameter("q_dept");
		String qName = request.getParameter("q_name");
		String qType = request.getParameter("q_type");
		String qUrgent = request.getParameter("q_urgent");

		// 金額は Integer に変換
		Integer qAmountMin = null;
		String minStr = request.getParameter("q_amount_min");
		if (minStr != null && !minStr.trim().isEmpty()) {
			try { qAmountMin = Integer.parseInt(minStr.trim()); } catch (Exception e) {}
		}

		Integer qAmountMax = null;
		String maxStr = request.getParameter("q_amount_max");
		if (maxStr != null && !maxStr.trim().isEmpty()) {
			try { qAmountMax = Integer.parseInt(maxStr.trim()); } catch (Exception e) {}
		}

		// ソート・ページングパラメータの取得
		String sortKey = request.getParameter("sort");
		if (sortKey == null || sortKey.isEmpty()) sortKey = "date";

		String sortDir = request.getParameter("dir");
		if (sortDir == null || sortDir.isEmpty()) sortDir = "ASC";

		int page = 1;
		String pageStr = request.getParameter("page");
		if (pageStr != null && !pageStr.isEmpty()) {
			try { page = Integer.parseInt(pageStr); } catch (NumberFormatException e) {}
		}

		int limit = 20; // 1ページ20件
		int offset = (page - 1) * limit;

		ApplicationDAO dao = new ApplicationDAO();
		try {
			// 2. 6つの検索条件をDAOに渡す
			ApplicationDAO.PageResult<ApplicationBean> pageResult = dao.searchAccountingApplications(
					qDept, qName, qType, qAmountMin, qAmountMax, qUrgent, sortKey, sortDir, limit, offset
			);

			request.setAttribute("applications", pageResult.getItems());
			request.setAttribute("hasNext", pageResult.hasNext());
			request.setAttribute("page", page);
			request.setAttribute("sort", sortKey);
			request.setAttribute("dir", sortDir);

			// 3. 検索状態をJSPの検索フォームに維持するためにセット
			request.setAttribute("q_dept", qDept);
			request.setAttribute("q_name", qName);
			request.setAttribute("q_type", qType);
			request.setAttribute("q_amount_min", qAmountMin);
			request.setAttribute("q_amount_max", qAmountMax);
			request.setAttribute("q_urgent", qUrgent);

			// 4.現在の検索・ソート状態を保持したURLを生成してSessionに記憶させる
			String queryString = request.getQueryString(); 
			String currentUrl = request.getContextPath() + "/ApplicationStatus";
			if (queryString != null && !queryString.isEmpty()) {
				currentUrl += "?" + queryString;
			}
			session.setAttribute("lastAccountingListUrl", currentUrl); // 経理用の一覧URLを保存！

		} catch (SQLException e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", "データ取得中にエラーが発生しました。");
		}

		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/app_list.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String apctId = request.getParameter("apct_id");
		
		if (apctId != null && !apctId.trim().isEmpty()) {
			ApplicationDAO dao = new ApplicationDAO();
			ApplicationBean app = dao.findById(apctId); 
			
			if (app != null) {
				request.setAttribute("application", app);
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/app_status.jsp");
				rd.forward(request, response);
				return; 
			}
		}
		
		doGet(request, response);
	}
}