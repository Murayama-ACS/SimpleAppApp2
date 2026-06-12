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
		request.setCharacterEncoding("UTF-8");
		String apctId = request.getParameter("apct_id");

		try {
			ApplicationDAO dao = new ApplicationDAO();
			int result = 0;
			
			if (apctId != null && !apctId.trim().isEmpty()) {
				result = dao.logicalDelete(apctId);
			}
			
			if (result == 0) {
				throw new Exception("対象の申請データが存在しないか、既に削除されています。");
			}
			
			// 正常完了時はリダイレクト
			response.sendRedirect(request.getContextPath() + "/ApplicationHistoryServlet");
			
		} catch (Exception e) {
			log("ApplicationDeleteServlet error", e);
			// 【修正】エラー時はメッセージを持たせて一覧サーブレットへ処理をフォワード
			request.setAttribute("errorMessage", "申請の削除に失敗しました。理由: " + e.getMessage());
			request.getRequestDispatcher("/ApplicationHistoryServlet").forward(request, response);
		}
	}
}