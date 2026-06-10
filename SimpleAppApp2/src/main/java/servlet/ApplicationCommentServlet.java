package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

@WebServlet("/ApplicationComment")
public class ApplicationCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 詳細画面の表示、および詳細画面からの承認・却下登録処理（POST）
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");

		// セッションからログイン情報をチェック
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("loginEmployee"); 
		
		if (employee == null) {
			response.sendRedirect(request.getContextPath() + "/login_mock.jsp");
			return;
		}

		String apctId = request.getParameter("apct_id"); 
		String nextStatusStr = request.getParameter("next_status_id");
		String comment = request.getParameter("comment");

		if (apctId == null || apctId.trim().isEmpty()) {
			forwardToWaitListWithError(request, response, "申請IDが指定されていません。");
			return;
		}

		try {
			ApplicationDAO appDao = new ApplicationDAO();
			ApprovalDAO approvalDao = new ApprovalDAO();

			// 【分岐処理】JSPの確認ポップアップで確定され、next_status_idが存在する場合のみ登録処理を実行
			if (nextStatusStr != null && !nextStatusStr.trim().isEmpty()) {
				int nextStatusId = Integer.parseInt(nextStatusStr.trim());

				// 1. 履歴ID (approval_id) の生成
				String timeStamp = new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
				String approvalId = "APV" + timeStamp;

				// 2. ApprovalBean の作成とデータ設定
				ApprovalBean approval = new ApprovalBean();
				approval.setApprovalId(approvalId);
				approval.setApctId(apctId);
				approval.setEmployeeId(employee.getEmp_id()); // 処理を行ったログインユーザーのID
				approval.setStatusId(nextStatusId);
				approval.setComment(comment);
				approval.setCreateDate(LocalDateTime.now());

				// 3. 承認履歴(approvals)テーブルへのデータ登録
				int insertResult = approvalDao.insert(approval);

				if (insertResult > 0) {
					// 4. 専用のUPDATEメソッドを呼び出してapplicationsテーブルのstatus_idを更新
					int updateResult = appDao.updateStatus(apctId, nextStatusId, LocalDateTime.now());
					if (updateResult == 0) {
						forwardToWaitListWithError(request, response, "対象の申請データが見つからないか、更新に失敗しました。");
						return;
					}
				} else {
					forwardToWaitListWithError(request, response, "承認データの登録に失敗しました。");
					return;
				}

				// --- 処理成功時：完了画面（app_done.jsp）へフォワード ---
				request.setAttribute("processType", nextStatusId == 5 ? "却下" : "承認");
				RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_done.jsp");
				rd.forward(request, response);
				return; 
			}

			// --- next_status_id が無い場合（一覧画面から「詳細」ボタンを押して遷移してきた時など） ---
			// データベースから該当の申請情報を取得
			ApplicationBean application = appDao.findById(apctId);
			if (application == null) {
				forwardToWaitListWithError(request, response, "指定された申請が見つかりません。");
				return;
			}

			// 画面表示用のリクエスト属性を設定（※過去履歴取得処理は要望に基づき削除）
			request.setAttribute("application", application);

			// 詳細画面（app_comment.jsp）へフォワード
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_comment.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			log("ApplicationCommentServlet error", e);
			forwardToWaitListWithError(request, response, "申請詳細の処理中にエラーが発生しました。");
		}
	}

	/**
	 * 共通エラー処理：不具合発生時は一覧画面へ安全に戻す
	 */
	private void forwardToWaitListWithError(HttpServletRequest request, HttpServletResponse response, String errorMessage) 
			throws ServletException, IOException {
		try {
			List<ApplicationBean> emptyList = new ArrayList<>();
			request.setAttribute("applications", emptyList);
			request.setAttribute("currentStatus", "1");
			request.setAttribute("errorMessage", errorMessage);
			
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/app_wait.jsp");
			rd.forward(request, response);
		} catch (Exception ex) {
			log("Fatal error in forwardToWaitListWithError", ex);
		}
	}

	/**
	 * GETリクエスト時もPOST処理（doPost）に委譲する
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}
}