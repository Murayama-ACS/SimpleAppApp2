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
	 * 未承認申請一覧の表示処理
	 */
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

		// 2. 権限チェック
		String posId = employee.getPos_id();
		if ("E00".equals(posId)) {
			request.setAttribute("errorMessage", "アクセス権限がありません。この画面は各部上長、または管理部上長専用です。");
			request.getRequestDispatcher("/login_mock.jsp").forward(request, response);
			return;
		}


		// 画面のテキストボックス等から渡される表示用ステータスID（デフォルトは 1:未承認）
		String pendingStatus = request.getParameter("pendingStatus");
		if (pendingStatus == null || pendingStatus.trim().isEmpty()) {
			pendingStatus = "1";
		}

		try {
			ApplicationDAO appDao = new ApplicationDAO();
			// ログインユーザーの権限（部署・役職）に応じた申請データを取得
			List<ApplicationBean> applications = appDao.getPendingApplications(employee);

			request.setAttribute("applications", applications);
			request.setAttribute("currentStatus", pendingStatus);

			// パラメータのエラーメッセージがあれば引き継ぐ
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