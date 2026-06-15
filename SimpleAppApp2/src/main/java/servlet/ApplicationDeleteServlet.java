package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.ApplicationDAO;

@WebServlet("/ApplicationDelete")
public class ApplicationDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// 1. リクエスト解析および送信パラメータの取得ブロック
		request.setCharacterEncoding("UTF-8");
		
		// 削除対象となる申請IDを取得
		String apctId = request.getParameter("apct_id");

		// 2. 削除実行処理（DB更新および結果判定）ブロック
		try {
			ApplicationDAO dao = new ApplicationDAO();
			int result = 0;
			
			// 申請IDが空でない場合のみ、DAOを呼び出して論理削除（is_deleted = 1への更新）を実行
			if (apctId != null && !apctId.trim().isEmpty()) {
				result = dao.logicalDelete(apctId);
			}
			
			// 削除対象が存在しない、または何らかの理由で更新件数が0件だった場合の例外スロー
			if (result == 0) {
				throw new Exception("対象の申請データが存在しないか、既に削除されています。");
			}
			
			// 3. 正常完了時データフローブロック
			// 完了表示画面を挟むことなく、最新状態の履歴一覧サーブレットへダイレクトにリダイレクト
			response.sendRedirect(request.getContextPath() + "/ApplicationHistoryServlet");
			
		// 4. 削除処理エラーハンドリングブロック
		} catch (Exception e) {
			log("ApplicationDeleteServlet error", e);
			
			// 自画面（一覧画面）にエラーメッセージを引き渡すためのスコープ設定
			request.setAttribute("errorMessage", "申請の削除に失敗しました。理由: " + e.getMessage());
			
			// ログイン画面等に戻さず、履歴一覧サーブレットへ内部処理をフォワードしてアラート出力させる
			request.getRequestDispatcher("/ApplicationHistoryServlet").forward(request, response);
		}
	}
}