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
import bean.DepartmentBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;
import dao.ApplicationDAO.PageResult;
import dao.DepartmentDAO;

@WebServlet("/ApplicationHistoryServlet")
public class ApplicationHistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int LIMIT = 20;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// 1. ログインチェック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
		
		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		String posId = employee.getPos_id();
		String dptId = employee.getDpt_id();

		// 2. 表示対象範囲（scope）の初期化とガード処理
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

		// 3. ステータスフィルターの変換
		String filter = request.getParameter("filter");
		if (filter == null || filter.isEmpty()) {
			filter = "unapproved"; 
		}
		String statusFilter = "all";
		if ("unapproved".equals(filter)) {
			statusFilter = "incomplete";
		}

		// 4. 新規追加：検索パラメータの取得とトリミング
		String qStatus = trimToNull(request.getParameter("q_status"));
		String qName = trimToNull(request.getParameter("q_name"));
		String qDepartment = trimToNull(request.getParameter("q_department"));
		String qType = trimToNull(request.getParameter("q_type"));
		String qAmount = trimToNull(request.getParameter("q_amount"));

		// 5. 新規追加：ソート・ページングパラメータの取得
		String sortKey = request.getParameter("sort");
		String sortDir = request.getParameter("dir");
		
		int page = 1;
		String pageParam = request.getParameter("page");
		if (pageParam != null) {
			try { page = Integer.parseInt(pageParam); } catch (NumberFormatException ex) { page = 1; }
		}
		if (request.getParameter("search") != null) {
			page = 1; // 検索ボタン押下時は1ページ目へリセット
		}
		if (page < 1) page = 1;
		int offset = (page - 1) * LIMIT;

		// 6. メイン処理（マスタデータおよび履歴一覧の取得）
		try {
			ApplicationDAO dao = new ApplicationDAO();
			DepartmentDAO deptDao = new DepartmentDAO();
			
			String dptName = dao.selectDepartmentName(dptId);
			List<DepartmentBean> dptList = deptDao.findAll(); // 部門検索用のマスタ取得

			// 検索、ソート条件を含めてDAO実行
			PageResult<ApplicationBean> pageRes = dao.searchApplications(
					employee, scope, statusFilter, 
					qStatus, qName, qDepartment, qType, qAmount, 
					sortKey, sortDir, LIMIT, offset);
			
			List<ApplicationBean> list = pageRes.getItems();
			boolean hasNext = pageRes.hasNext();

			// 画面へ返却する属性の設定
			request.setAttribute("empBean", employee);
			request.setAttribute("dpt_name", dptName);
			request.setAttribute("dptList", dptList); // 選択肢用
			request.setAttribute("appList", list);
			request.setAttribute("currentScope", scope);
			request.setAttribute("currentStatusFilter", statusFilter);
			
			// 検索状態、ソート、ページングの維持
			request.setAttribute("q_status", qStatus);
			request.setAttribute("q_name", qName);
			request.setAttribute("q_department", qDepartment);
			request.setAttribute("q_type", qType);
			request.setAttribute("q_amount", qAmount);
			request.setAttribute("sort", sortKey);
			request.setAttribute("dir", sortDir);
			request.setAttribute("page", page);
			request.setAttribute("hasNext", hasNext);

			RequestDispatcher rd = request.getRequestDispatcher("/history.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			e.printStackTrace(); 
			request.setAttribute("errorMessage", "履歴情報の取得中にシステムエラーが発生しました。詳細: " + e.getMessage());
			request.setAttribute("empBean", employee);
			request.setAttribute("currentScope", scope);
			request.setAttribute("currentStatusFilter", statusFilter);
			request.getRequestDispatcher("/history.jsp").forward(request, response);
		}
	}

	private String trimToNull(String s) {
		if (s == null) return null;
		s = s.trim();
		return s.isEmpty() ? null : s;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}
}