package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.ApplicationBean;
import bean.EmployeeBean;
import dao.ApplicationDAO;

@WebServlet("/ApplicationEdit")
public class ApplicationEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean");
		
		if (employee == null) {
        	session.setAttribute("eMsg", "error : session timeout");
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			return;
		}
		
		String apctId = request.getParameter("apct_id");
		String isSubmit = request.getParameter("isSubmit");

		ApplicationDAO dao = new ApplicationDAO();

		// 修正時のDB更新処理
		if ("true".equals(isSubmit)) {
			try {
				String type = request.getParameter("applicationType");
				String method = request.getParameter("paymentMethod");
				String amountParam = request.getParameter("amount");
				String content = request.getParameter("content");
				String reason = request.getParameter("reason");
				String note = request.getParameter("note");
				String urgentFlagParam = request.getParameter("urgentFlag");

				int amount = 0;
				if (amountParam != null && !amountParam.trim().isEmpty()) {
					amount = Integer.parseInt(amountParam.trim());
				}
				
				ApplicationDAO aDao = new ApplicationDAO();
				Integer posAmount = aDao.selectPositionAmount(employee.getPos_id());
				//【修正】上限金額チェック追加	
				if (posAmount != null && amount > posAmount) {
					response.setContentType("text/plain; charset=UTF-8");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 エラー
					response.getWriter().write("申請金額が役職の上限（" + posAmount + "円）を超えています。");
					return;
				}

				String urgentStr = "通常";
				if (urgentFlagParam != null) {
					String u = urgentFlagParam.trim().toLowerCase();
					if ("on".equals(u) || "true".equals(u) || "1".equals(u)) {
						urgentStr = "緊急";
					}
				}

				ApplicationBean bean = new ApplicationBean();
				bean.setApctId(apctId);
				bean.setType(type);
				bean.setPaymentMethod(method);
				bean.setAmount(amount);
				bean.setContent(content);
				bean.setReason(reason);
				bean.setNote(note);
				bean.setUrgent(urgentStr);

				int result = dao.update(bean);
				
				if (result == 0) {
					throw new Exception("対象の申請データが見つからないか、他のユーザーによって更新されました。");
				}

				// 正常完了時はリダイレクトで一覧へ
				response.sendRedirect(request.getContextPath() + "/ApplicationHistoryServlet");
				return;

			} catch (Exception e) {
				log("ApplicationEditServlet update error", e);
				// 【修正】エラー時はメッセージを持たせて一覧サーブレットへ処理をフォワード
				request.setAttribute("errorMessage", "申請の修正に失敗しました。理由: " + e.getMessage());
				request.getRequestDispatcher("/ApplicationHistoryServlet").forward(request, response);
				return;
			}
		}

		// 初期表示
		ApplicationBean app = dao.findById(apctId);
		request.setAttribute("application", app);
		request.getRequestDispatcher("/WEB-INF/jsp/app_edit.jsp").forward(request, response);
	}
}
