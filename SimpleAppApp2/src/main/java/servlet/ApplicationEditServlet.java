package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.ApplicationBean;
import dao.ApplicationDAO;

@WebServlet("/ApplicationEdit")
public class ApplicationEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// 1. リクエスト解析および共通オブジェクト初期化ブロック
		request.setCharacterEncoding("UTF-8");
		
		// 必須パラメータ（申請ID、提出フラグ）の取得
		String apctId = request.getParameter("apct_id");
		String isSubmit = request.getParameter("isSubmit");

		// データベース操作用DAOのインスタンス化
		ApplicationDAO dao = new ApplicationDAO();

		// 2. 修正実行処理（DB更新および結果判定）ブロック
		// 編集画面から「提出（更新）」が要求された場合の判定
		if ("true".equals(isSubmit)) {
			try {
				// 入力フォームから各送信パラメータの取得
				String type = request.getParameter("applicationType");
				String method = request.getParameter("paymentMethod");
				String amountParam = request.getParameter("amount");
				String content = request.getParameter("content");
				String reason = request.getParameter("reason");
				String note = request.getParameter("note");
				String urgentFlagParam = request.getParameter("urgentFlag");

				// 金額データの数値変換処理（空文字・ヌル対策含む）
				int amount = 0;
				if (amountParam != null && !amountParam.trim().isEmpty()) {
					amount = Integer.parseInt(amountParam.trim());
				}

				// 緊急フラグの文言判定処理
				String urgentStr = "通常";
				if (urgentFlagParam != null) {
					String u = urgentFlagParam.trim().toLowerCase();
					if ("on".equals(u) || "true".equals(u) || "1".equals(u)) {
						urgentStr = "緊急";
					}
				}

				// 更新データを格納するBeanオブジェクトの組み立て
				ApplicationBean bean = new ApplicationBean();
				bean.setApctId(apctId);
				bean.setType(type);
				bean.setPaymentMethod(method);
				bean.setAmount(amount);
				bean.setContent(content);
				bean.setReason(reason);
				bean.setNote(note);
				bean.setUrgent(urgentStr);

				// DAOを介してデータベースのレコードを更新
				int result = dao.update(bean);
				
				// 更新対象が存在しない、または競合による失敗時の例外スロー
				if (result == 0) {
					throw new Exception("対象の申請データが見つからないか、他のユーザーによって更新されました。");
				}

				// 3. 正常完了時データフローブロック
				// 中間画面を挟むことなく、最新状態の履歴一覧サーブレットへダイレクトにリダイレクト
				response.sendRedirect(request.getContextPath() + "/ApplicationHistoryServlet");
				return;

			// 4. 更新処理エラーハンドリングブロック
			} catch (Exception e) {
				log("ApplicationEditServlet update error", e);
				
				// 自画面（一覧画面）にエラーメッセージを引き渡すためのスコープ設定
				request.setAttribute("errorMessage", "申請の修正に失敗しました。理由: " + e.getMessage());
				
				// ログイン画面等に戻さず、履歴一覧サーブレットへ内部処理をフォワードしてアラート出力させる
				request.getRequestDispatcher("/ApplicationHistoryServlet").forward(request, response);
				return;
			}
		}

		// 5. 編集画面初期表示処理（データ遷移）ブロック
		// 提出フラグが立っていない場合（一覧から遷移してきた直後）は、現在の申請内容をDBから取得
		ApplicationBean app = dao.findById(apctId);
		
		// 取得したBeanを格納し、修正入力画面（app_edit.jsp）へフォワード
		request.setAttribute("application", app);
		request.getRequestDispatcher("/app_edit.jsp").forward(request, response);
	}
}