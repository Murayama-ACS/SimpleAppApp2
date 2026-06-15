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

		// 3. リクエストパラメータ（表示ステータス）の取得・検証ブロック
		String pendingStatus = request.getParameter("pendingStatus");
		if (pendingStatus == null || pendingStatus.trim().isEmpty()) {
			pendingStatus = "1";
		}

		// 4. データ取得および画面遷移制御ブロック
		try {
			ApplicationDAO appDao = new ApplicationDAO();
			
			// ログインユーザーの権限（部署・役職）に応じた申請一覧データを取得
			List<ApplicationBean> applications = appDao.getPendingApplications(employee);

			// 画面表示用のアトリビュート設定
			request.setAttribute("applications", applications);
			request.setAttribute("currentStatus", pendingStatus);

			// 遷移元からのエラーメッセージが存在すれば引き継ぐ
			String errorMsg = request.getParameter("errorMessage");
			if (errorMsg != null) {
				request.setAttribute("errorMessage", errorMsg);
			}

			// 申請IDパラメータの有無で、詳細画面か一覧画面かを切り替えてフォワード
			RequestDispatcher rd = request.getParameter("apct_id") != null 
					? request.getRequestDispatcher("WEB-INF/jsp/app_comment.jsp")
					: request.getRequestDispatcher("WEB-INF/jsp/app_wait.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			// 例外発生時のログ出力およびエラー画面（一覧の型）へのフォワード処理
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

			// 対象申請データの存在確認と現在の状態を取得
			ApplicationBean application = appDao.findById(apctId);
			if (application == null) {
				response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("対象データが見つかりません。", "UTF-8"));
				return;
			}
			int currentStatusId = application.getStatus_id();

			int nextStatusId = 0;

			// 却下アクション、または承認者属性（管理部・社長・その他上長）に応じた遷移先ステータスIDの算出
			if ("6".equals(nextStatusStr)) {
				nextStatusId = 6; 
			} else {
				String userDpt = employee.getDpt_id();
				String userPos = employee.getPos_id();

				if ("D100".equals(userDpt)) {
					nextStatusId = 3; // 管理部上長の承認時は一律「3: 管理部承認」
				} else if (currentStatusId == 1 && "E04".equals(userPos)) {
					nextStatusId = 4; // 社長直行ルート
				} else {
					nextStatusId = currentStatusId + 1; // 通常の段階的承認ルート
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
				// 承認履歴の保存に成功した場合のみ、本申請情報のステータスを更新
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
			// システム例外発生時の共通エラーハンドリングとリダイレクト遷移
			log("ApplicationWaitListServlet POST error", e);
			response.sendRedirect(request.getContextPath() + "/ApplicationWaitList?pendingStatus=" + pendingStatus + "&errorMessage=" + java.net.URLEncoder.encode("処理中にシステムエラーが発生しました。", "UTF-8"));
		}
	}
}