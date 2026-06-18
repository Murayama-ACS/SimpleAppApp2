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
	 * 未承認申請一覧の表示処理（検索・ソート対応）
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		// 1. ログインチェック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean"); 

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/WEB-INF/jsp/approval_list.jsp");
			return;
		}

		// 2. 権限チェック
		String posId = employee.getPos_id();
		if ("E00".equals(posId)) {
			request.setAttribute("eMsg", "アクセス権限がありません。");
			request.getRequestDispatcher("/WEB-INF/jsp/approval_list.jsp").forward(request, response);
			return;
		}

		// 3. 検索パラメータの取得
		String qDept = request.getParameter("q_dept");
		String qName = request.getParameter("q_name");
		String qAmountMin = request.getParameter("q_amount_min");
		String qAmountMax = request.getParameter("q_amount_max");
		String qUrgent = request.getParameter("q_urgent");

		// 4. ソートパラメータの取得
		String sortKey = request.getParameter("sort");
		if (sortKey == null || sortKey.isEmpty()) sortKey = "date"; // デフォルトは申請日順
		
		String sortDir = request.getParameter("dir");
		if (sortDir == null || sortDir.isEmpty()) sortDir = "ASC"; // デフォルトは古い順

		try {
			
			ApplicationDAO appDao = new ApplicationDAO();
			String dptName = appDao.selectDepartmentName(employee.getDpt_id());
			request.setAttribute("dpt_name", dptName);
			// DAOの第10メソッド（検索・ソート対応版）を呼び出す
			List<ApplicationBean> applications = appDao.getPendingApplications(
					employee, qDept, qName, qAmountMin, qAmountMax, qUrgent, sortKey, sortDir);

			// 画面にデータを渡す
			request.setAttribute("applications", applications);
			
			// 検索状態を画面に維持するためのセット
			request.setAttribute("q_dept", qDept);
			request.setAttribute("q_name", qName);
			request.setAttribute("q_amount_min", qAmountMin);
			request.setAttribute("q_amount_max", qAmountMax);
			request.setAttribute("q_urgent", qUrgent);
			request.setAttribute("sort", sortKey);
			request.setAttribute("dir", sortDir);

			// パラメータのエラーメッセージがあれば引き継ぐ
			String errorMsg = request.getParameter("errorMessage");
			if (errorMsg != null) {
				request.setAttribute("errorMessage", errorMsg);
			}
			String queryString = request.getQueryString(); // URLの ? 以降のパラメータを取得
			String currentUrl = request.getContextPath() + "/ApplicationWaitList";
			if (queryString != null && !queryString.isEmpty()) {
				currentUrl += "?" + queryString;
			}
			session.setAttribute("lastListUrl", currentUrl);
			RequestDispatcher rd = request.getParameter("apct_id") != null 
					? request.getRequestDispatcher("WEB-INF/jsp/app_comment.jsp")
							: request.getRequestDispatcher("/WEB-INF/jsp/approval_list.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			log("ApplicationWaitListServlet GET error", e);
			request.setAttribute("errorMessage", "データ取得中に例外が発生しました。");
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/approval_list.jsp");
			rd.forward(request, response);
		}
	}

	/**
	 * 一覧画面のポップアップから「確定」されたときの承認・却下登録処理
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean");

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "//WEB-INF/jsp/approval_list.jsp");
			return;
		}

		String apctId = request.getParameter("apct_id");
		String comment = request.getParameter("comment");
		String pendingStatus = request.getParameter("pendingStatus");
		if (pendingStatus == null || pendingStatus.trim().isEmpty()) {
			pendingStatus = "1";
		}

		// 一覧画面のモーダルから「next_status_id」の代わりに送信されるアクションを受け取る場合はここで取得
		// もし現在JSPのhiddenが「next_status_id」のままであれば、安全のためにサーバー側で上書き判定します
		String nextStatusStr = request.getParameter("next_status_id");

		if (apctId == null || apctId.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("申請IDが不正です。", "UTF-8"));
			return;
		}

		try {
			ApplicationDAO appDao = new ApplicationDAO();
			ApprovalDAO approvalDao = new ApprovalDAO();

			// 1. 最新の申請状況をDBから再確認
			ApplicationBean application = appDao.findById(apctId);
			if (application == null) {
				response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("対象データが見つかりません。", "UTF-8"));
				return;
			}
			int currentStatusId = application.getStatus_id();

			// 2. サーバー側での安全なステータス分岐ロジックの適用
			int nextStatusId = 0;

			// 却下判定（JSP側のopenRejectModalでセットした数値、または操作で判定）
			if ("6".equals(nextStatusStr)) {
				nextStatusId = 6; // 却下一律
			} else {
				// 承認処理時のルート判定
				String userDpt = employee.getDpt_id();
				String userPos = employee.getPos_id();

				if ("D100".equals(userDpt)) {
					// 管理部上長の承認であれば一律「3: 管理部承認」に設定
					nextStatusId = 3;
				} else if (currentStatusId == 1 && "E04".equals(userPos)) {
					// 社長直行ルート
					nextStatusId = 4;
				} else {
					// 通常ルート
					nextStatusId = currentStatusId + 1;
				}
			}

			// 3. 履歴IDの自動生成
			String timeStamp = new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
			String approvalId = "APV" + timeStamp;

			// 4. Beanの設定と保存
			ApprovalBean approval = new ApprovalBean();
			approval.setApprovalId(approvalId);
			approval.setApctId(apctId);
			approval.setEmployeeId(employee.getEmp_id());
			approval.setStatusId(nextStatusId);
			approval.setComment(comment);
			approval.setCreateDate(LocalDateTime.now());

			int insertResult = approvalDao.insert(approval);

			if (insertResult > 0) {
				// 5. applicationsテーブルの更新
				int updateResult = appDao.updateStatus(apctId, nextStatusId, LocalDateTime.now());
				if (updateResult == 0) {
					response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("ステータス更新に失敗しました。", "UTF-8"));
					return;
				}
			} else {
				response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("承認履歴の登録に失敗しました。", "UTF-8"));
				return;
			}

			// 6. 旧仕様（app_done.jspへのフォワード）を廃止し、一覧へリダイレクト（パラメータ付き）
			response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&success=true");

		} catch (Exception e) {
			log("ApplicationWaitListServlet POST error", e);
			response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("処理中にシステムエラーが発生しました。", "UTF-8"));
		}
	}
}