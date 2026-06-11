package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ApplicationDelete")
public class ApplicationDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String apctId = request.getParameter("apct_id");

		// TODO: 実際の削除ロジック（dao.delete等）が必要な場合はここに追記します

		request.setAttribute("deletedId", apctId);
		
		// 【修正】WEBルート直下の app_delete.jsp へフォワード
		request.getRequestDispatcher("/app_delete.jsp").forward(request, response);
	}
}