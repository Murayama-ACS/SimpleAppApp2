package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.ApplicationBean;
import bean.ApprovalBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;
import dao.ApprovalDAO;

@WebServlet("/ApplicationWaitList")
public class ApplicationWaitListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 未承認申請一覧の表示処理（検索項目順序修正）
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		// 1. ログインチェックブロック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee"); 

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		// 2. 画面アクセス権限チェックブロック
		String posId = employee.getPos_id();
		if ("E00".equals(posId)) {
			request.setAttribute("eMsg", "アクセス権限がありません。");
			request.getRequestDispatcher("/login_mock.jsp").forward(request, response);
			return;
		}

		// 3. リクエストパラメータ（検索・ソート・表示ステータス）の取得・検証ブロック
		String pendingStatus = request.getParameter("pendingStatus");
		if (pendingStatus == null || pendingStatus.trim().isEmpty()) {
			pendingStatus = "1";
		}

		String searchDept = request.getParameter("searchDept");
		String searchName = request.getParameter("searchName");
		String searchAmountMin = request.getParameter("searchAmountMin"); // 金額下限（以上）
		String searchAmountMax = request.getParameter("searchAmountMax"); // 金額上限（以下）
		String searchUrgent = request.getParameter("searchUrgent"); 

		String sortColumn = request.getParameter("sortColumn");
		String sortOrder = request.getParameter("sortOrder");

		if (sortColumn == null || sortColumn.trim().isEmpty()) {
			sortColumn = "date";
		}
		if (sortOrder == null || sortOrder.trim().isEmpty()) {
			sortOrder = "DESC";
		}

		// 4. データ取得および画面遷移制御ブロック
		try {
			ApplicationDAO appDao = new ApplicationDAO();
			
			List<ApplicationBean> applications = appDao.getPendingApplications(
					employee, searchDept, searchName, searchAmountMin, searchAmountMax, searchUrgent, sortColumn, sortOrder);

			// 画面の状態維持用アトリビュート設定
			request.setAttribute("applications", applications);
			request.setAttribute("currentStatus", pendingStatus);
			
			request.setAttribute("searchDept", searchDept);
			request.setAttribute("searchName", searchName);
			request.setAttribute("searchAmountMin", searchAmountMin);
			request.setAttribute("searchAmountMax", searchAmountMax);
			request.setAttribute("searchUrgent", searchUrgent);
			
			request.setAttribute("sortColumn", sortColumn);
			request.setAttribute("sortOrder", sortOrder);

			String errorMsg = request.getParameter("errorMessage");
			if (errorMsg != null) {
				request.setAttribute("errorMessage", errorMsg);
			}

			RequestDispatcher rd = request.getParameter("apct_id") != null 
					? request.getRequestDispatcher("WEB-INF/jsp/app_comment.jsp")
					: request.getRequestDispatcher("WEB-INF/jsp/app_wait.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			log("ApplicationWaitListServlet GET error", e);
			request.setAttribute("errorMessage", "データ取得中に例外が発生しました。");
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_wait.jsp");
			rd.forward(request, response);
		}
	}

	/**
	 * 一覧画面のポップアップから「確定」されたときの承認・却下登録処理
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		// 1. リクエスト解析および入力情報の取得ブロック
		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee");

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		String apctId = request.getParameter("apct_id");
		String comment = request.getParameter("comment");
		
		String pendingStatus = request.getParameter("pendingStatus");
		if (pendingStatus == null || pendingStatus.trim().isEmpty()) {
			pendingStatus = "1";
		}

		String nextStatusStr = request.getParameter("next_status_id");

		// 2. 必須パラメータ（申請ID）のバリデーションブロック
		if (apctId == null || apctId.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("申請IDが不正です。", "UTF-8"));
			return;
		}

		// 3. ビジネスロジック（申請データ検証・ステータス決定）ブロック
		try {
			ApplicationDAO appDao = new ApplicationDAO();
			ApprovalDAO approvalDao = new ApprovalDAO();

			ApplicationBean application = appDao.findById(apctId);
			if (application == null) {
				response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("対象データが見つかりません。", "UTF-8"));
				return;
			}
			int currentStatusId = application.getStatus_id();

			int nextStatusId = 0;

			if ("6".equals(nextStatusStr)) {
				nextStatusId = 6; 
			} else {
				String userDpt = employee.getDpt_id();
				String userPos = employee.getPos_id();

				if ("D100".equals(userDpt)) {
					nextStatusId = 3; 
				} else if (currentStatusId == 1 && "E04".equals(userPos)) {
					nextStatusId = 4; 
				} else {
					nextStatusId = currentStatusId + 1; 
				}
			}

			// 4. 登録用エンティティ（Bean）の生成およびデータセットブロック
			String timeStamp = new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
			String approvalId = "APV" + timeStamp;

			ApprovalBean approval = new ApprovalBean();
			approval.setApprovalId(approvalId);
			approval.setApctId(apctId);
			approval.setEmployeeId(employee.getEmp_id());
			approval.setStatusId(nextStatusId);
			approval.setComment(comment);
			approval.setCreateDate(LocalDateTime.now());

			// 5. データベースへのトランザクション（登録・更新）実行ブロック
			int insertResult = approvalDao.insert(approval);

			if (insertResult > 0) {
				int updateResult = appDao.updateStatus(apctId, nextStatusId, LocalDateTime.now());
				if (updateResult == 0) {
					response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("ステータス更新に失敗しました。", "UTF-8"));
					return;
				}
			} else {
				response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("承認履歴の登録に失敗しました。", "UTF-8"));
				return;
			}

			// 6. 処理成功時の画面リダイレクト制御ブロック
			response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&success=true");

		} catch (Exception e) {
			log("ApplicationWaitListServlet POST error", e);
			response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("処理中にシステムエラーが発生しました。", "UTF-8"));
		}
	}
}