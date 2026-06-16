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
		
		// 1. ログインチェック（セッション検証）ブロック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");
		
		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		String posId = employee.getPos_id();
		String dptId = employee.getDpt_id();

		// 2. 表示対象範囲（scope）の初期化とガード処理ブロック
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

		// 3. ステータスフィルターの変換ブロック
		String filter = request.getParameter("filter");
		if (filter == null || filter.isEmpty()) {
			filter = "unapproved"; 
		}
		String statusFilter = "all";
		if ("unapproved".equals(filter)) {
			statusFilter = "incomplete";
		}

		// 4. 検索パラメータの取得ブロック
		String qStatus = trimToNull(request.getParameter("q_status"));
		String qName = trimToNull(request.getParameter("q_name"));
		String qDepartment = trimToNull(request.getParameter("q_department"));
		String qType = trimToNull(request.getParameter("q_type"));
		
		// 金額の下限（円以上）・上限（円以下）パラメータを文字列として取得
		String qAmountMinStr = trimToNull(request.getParameter("q_amount_min"));
		String qAmountMaxStr = trimToNull(request.getParameter("q_amount_max"));

		// 【型不一致の解消】DAOの引数（int型の金額フィールド連動）に対応するため、StringからIntegerオブジェクトへ安全に変換
		Integer qAmountMin = null;
		Integer qAmountMax = null;
		try {
			if (qAmountMinStr != null) {
				qAmountMin = Integer.valueOf(qAmountMinStr);
			}
		} catch (NumberFormatException e) {
			// 画面から数値以外の値、または不当な文字が入った場合は未指定（null）として処理を継続
		}
		try {
			if (qAmountMaxStr != null) {
				qAmountMax = Integer.valueOf(qAmountMaxStr);
			}
		} catch (NumberFormatException e) {
			// 画面から数値以外の値、または不当な文字が入った場合は未指定（null）として処理を継続
		}

		// 5. ソート・ページングパラメータの取得ブロック
		String sortKey = request.getParameter("sort");
		String sortDir = request.getParameter("dir");
		
		int page = 1;
		String pageParam = request.getParameter("page");
		if (pageParam != null) {
			try { page = Integer.parseInt(pageParam); } catch (NumberFormatException ex) { page = 1; }
		}
		if (request.getParameter("search") != null) { 
			page = 1; // 新規検索ボタン押下時は先頭ページへ強制初期化
		}
		if (page < 1) page = 1;
		int offset = (page - 1) * LIMIT;

		// 6. データ取得および画面遷移（メイン処理）ブロック
		try {
			ApplicationDAO dao = new ApplicationDAO();
			DepartmentDAO deptDao = new DepartmentDAO();
			
			String dptName = dao.selectDepartmentName(dptId);
			List<DepartmentBean> dptList = deptDao.findAll(); // 部門検索プルダウン用のマスタデータ

			// 型変換を終えた「qAmountMin」「qAmountMax」をDAOの統合メソッドへバインド
			PageResult<ApplicationBean> pageRes = dao.searchApplications(
					employee, scope, statusFilter, 
					qStatus, qName, qDepartment, qType, qAmountMin, qAmountMax, 
					sortKey, sortDir, LIMIT, offset);
			
			List<ApplicationBean> list = pageRes.getItems();
			boolean hasNext = pageRes.hasNext();

			// JSPへ引き渡すリクエストスコープのセット
			request.setAttribute("empBean", employee);
			request.setAttribute("dpt_name", dptName);
			request.setAttribute("dptList", dptList); 
			request.setAttribute("appList", list);
			request.setAttribute("currentScope", scope);
			request.setAttribute("currentStatusFilter", statusFilter);
			
			// 検索窓の状態維持（画面表示用に元の文字列データを返却）
			request.setAttribute("q_status", qStatus);
			request.setAttribute("q_name", qName);
			request.setAttribute("q_department", qDepartment);
			request.setAttribute("q_type", qType);
			request.setAttribute("q_amount_min", qAmountMinStr);
			request.setAttribute("q_amount_max", qAmountMaxStr);
			
			// ソート・ページング情報の引き継ぎ
			request.setAttribute("sort", sortKey);
			request.setAttribute("dir", sortDir);
			request.setAttribute("page", page);
			request.setAttribute("hasNext", hasNext);

			RequestDispatcher rd = request.getRequestDispatcher("/history.jsp");
			rd.forward(request, response);

		// 7. 例外発生時の自画面エラーハンドリングブロック
		} catch (Exception e) {
			e.printStackTrace(); 
			request.setAttribute("errorMessage", "履歴情報の取得中にシステムエラーが発生しました。");
			
			// history.jsp側のヌルポインタによる二次クラッシュを防ぐ最低限の補填
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