package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.ApplicationBean;
import bean.DepartmentBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;
import dao.DepartmentDAO;

@WebServlet("/ApplicationHistoryServlet")
public class ApplicationHistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		// 1. ログインチェック
		HttpSession session = request.getSession();
		EmployeeBean loginUser = (EmployeeBean) session.getAttribute("empBean");
		if (loginUser == null) {
        	session.setAttribute("eMsg", "error : session timeout");
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}

		// 2. タブ・フィルタパラメータの取得
		String scope = request.getParameter("scope");
		if (scope == null || scope.isEmpty()) {
		    // E04(社長)の場合は「自身の申請」がないため、デフォルトを「配下の申請」にする
		    if ("E04".equals(loginUser.getPos_id())) {
		        scope = "subordinate";
		    } else {
		        scope = "self"; // 社長以外は「自身の申請」がデフォルト
		    }
		}

		String filter = request.getParameter("filter");
		String statusFilter = "unapproved".equals(filter) ? "incomplete" : "";

		// 3. 検索フォームからのパラメータ取得
		String qStatus = request.getParameter("q_status");
		String qName = request.getParameter("q_name");
		String qDepartment = request.getParameter("q_department");
		String qType = request.getParameter("q_type");
		String qAmountMinStr = request.getParameter("q_amount_min");
		String qAmountMaxStr = request.getParameter("q_amount_max");
		
		Integer qAmountMin = null;
		Integer qAmountMax = null;
		
		if (qAmountMinStr != null && !qAmountMinStr.trim().isEmpty()) {
			try { qAmountMin = Integer.parseInt(qAmountMinStr.trim()); } catch (NumberFormatException e) {}
		}
		if (qAmountMaxStr != null && !qAmountMaxStr.trim().isEmpty()) {
			try { qAmountMax = Integer.parseInt(qAmountMaxStr.trim()); } catch (NumberFormatException e) {}
		}

		// 4. ソート・ページングパラメータの取得
		String sortKey = request.getParameter("sort");
		if (sortKey == null || sortKey.isEmpty()) sortKey = "urgent";// 【変更】デフォルトは緊急度

		String sortDir = request.getParameter("dir");
		if (sortDir == null || sortDir.isEmpty()) sortDir = "DESC";// 【変更】デフォルトは致急

		int page = 1;
		String pageStr = request.getParameter("page");
		if (pageStr != null && !pageStr.isEmpty()) {
			try { page = Integer.parseInt(pageStr); } catch (NumberFormatException e) {}
		}
		
		int limit = 20; // 1ページに表示する件数
		int offset = (page - 1) * limit;

		ApplicationDAO dao = new ApplicationDAO();
		try {
			String dptName = dao.selectDepartmentName(loginUser.getDpt_id());
			request.setAttribute("dpt_name", dptName);
			// E03(本部長), E04(社長)の場合は部署絞り込み用のリストを取得
			if ("E03".equals(loginUser.getPos_id()) || "E04".equals(loginUser.getPos_id())) {
				DepartmentDAO dptDao = new DepartmentDAO();
				List<DepartmentBean> dptList = dptDao.findAll();
				request.setAttribute("dptList", dptList);
			}

			// 5. 新しい検索・ページングメソッドを呼び出す（第12メソッド）
			ApplicationDAO.PageResult<ApplicationBean> pageResult = dao.searchApplications(
					loginUser, scope, statusFilter, qStatus, qName, qDepartment, qType,
					qAmountMin, qAmountMax, sortKey, sortDir, limit, offset
			);

			// 6. JSPへ渡すデータをセット
			request.setAttribute("appList", pageResult.getItems());
			request.setAttribute("hasNext", pageResult.hasNext());
			request.setAttribute("page", page);
			request.setAttribute("sort", sortKey);
			request.setAttribute("dir", sortDir);

			// 画面の入力状態を維持するためのセット
			request.setAttribute("currentScope", scope);
			request.setAttribute("currentStatusFilter", "incomplete".equals(statusFilter) ? "incomplete" : "all");
			request.setAttribute("q_status", qStatus);
			request.setAttribute("q_name", qName);
			request.setAttribute("q_department", qDepartment);
			request.setAttribute("q_type", qType);
			request.setAttribute("q_amount_min", qAmountMinStr);
			request.setAttribute("q_amount_max", qAmountMaxStr);

		} catch (SQLException e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", "データ取得中にエラーが発生しました。");
		}
		String queryString = request.getQueryString(); // URLの ? 以降のパラメータを取得
		String currentUrl = request.getContextPath() + "/ApplicationHistoryServlet";
		if (queryString != null && !queryString.isEmpty()) {
			currentUrl += "?" + queryString;
		}
		session.setAttribute("lastListUrl", currentUrl);
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/application_history.jsp");
		rd.forward(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}