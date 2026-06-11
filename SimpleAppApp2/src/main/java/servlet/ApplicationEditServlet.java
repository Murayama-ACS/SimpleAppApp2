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
		request.setCharacterEncoding("UTF-8");
		String apctId = request.getParameter("apct_id");

		ApplicationDAO dao = new ApplicationDAO();
		ApplicationBean app = dao.findById(apctId);

		request.setAttribute("application", app);
		
		// 【修正】WEBルート直下の app_edit.jsp へフォワード
		request.getRequestDispatcher("/app_edit.jsp").forward(request, response);
	}
}