package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.EmployeeBean;
import dao.ApplicationDAO;

@WebServlet("/ApplicationDelete")
public class ApplicationDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// 1. 文字化け防止のためのエンコーディング設定
		request.setCharacterEncoding("UTF-8");
		
		// 2. 削除対象の申請IDをフロントエンド（JSP）の隠しフォームから取得
		String apctId = request.getParameter("apct_id");

		//  3. 【追加】セッションから現在ログインしているユーザーの情報を取得
		// 誰がこの削除操作を行ったのか（operatorEmpId）を履歴に残すために必要です。
		HttpSession session = request.getSession();
		EmployeeBean loginUser = (EmployeeBean) session.getAttribute("empBean");

		// 万が一セッションが切れていた（ログインしていない）場合は、不正アクセス防止のためログイン画面へ弾く
		if (loginUser == null) {
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}

		// 操作を実行するユーザーの社員IDを取得
		String operatorEmpId = loginUser.getEmp_id();

		try {
			ApplicationDAO dao = new ApplicationDAO();
			int result = 0;
			
			// 申請IDが空でないことを確認してから削除処理を実行
			if (apctId != null && !apctId.trim().isEmpty()) {
				//  4. 【修正】DAOの新しいメソッドに合わせて、申請IDと操作者IDの2つの引数を渡す！
				result = dao.logicalDelete(apctId, operatorEmpId);
			}
			
			// result が 0 の場合、データベース側で更新が1件も行われなかった（既に削除済み等の理由）
			if (result == 0) {
				throw new Exception("対象の申請データが存在しないか、既に削除されています。");
			}
			
			// 5. 正常完了時は、申請履歴一覧画面（ApplicationHistoryServlet）へリダイレクトして画面を更新
			// セッションから直前の検索・ページング状態を含むURLを取得
			String lastListUrl = (String) request.getSession().getAttribute("lastListUrl");
			if (lastListUrl != null && !lastListUrl.isEmpty()) {
			    // 記憶があれば、検索状態を保ったまま元の場所へドンピシャで戻る
			    response.sendRedirect(lastListUrl);
			} else {
			    response.sendRedirect(request.getContextPath() + "/ApplicationHistoryServlet");
			}
			
		} catch (Exception e) {
			// 6. エラー発生時のログ出力とフォワード処理
			log("ApplicationDeleteServlet error", e);
			
			// エラーメッセージをリクエストスコープに持たせ、一覧画面のサーブレットへ処理を委譲（フォワード）
			request.setAttribute("errorMessage", "申請の削除に失敗しました。理由: " + e.getMessage());
			request.getRequestDispatcher("/ApplicationHistoryServlet").forward(request, response);
		}
	}
}