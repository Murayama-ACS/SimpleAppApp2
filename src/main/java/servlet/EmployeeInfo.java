package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.DepartmentBean;
import bean.EmployeeBean;
import bean.PositionBean;
import dao.DepartmentDAO;
import dao.EmployeeDAO;
import dao.EmployeeDAO.PageResult;
import dao.PositionDAO;

/**
 * Servlet implementation class EmployeeInfo
 */
@WebServlet("/EmployeeInfo")
public class EmployeeInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_LIMIT = 20;
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    request.setCharacterEncoding("UTF-8");

	    // 認証／権限チェック（既存のまま）
	    String loginUrl = "/index.jsp";
	    HttpSession session = request.getSession();
	    EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean");
	    if (employee == null) {
	    	request.setAttribute("eMsg", "session timeout");
	        response.sendRedirect(request.getContextPath() + loginUrl);
	        return;
	    }
	    String ldptId = employee.getDpt_id();
	    if (ldptId == null || !ldptId.matches("^D4.*$")) {
	        request.setAttribute("eMsg", "アクセス権限がありません。");
	        request.getRequestDispatcher(loginUrl).forward(request, response);
	        return;
	    }
	    
	 // EmployeeInfo.doGet の中、認可チェックの後に追加
	    DepartmentDAO deptDao = new DepartmentDAO();
	    List<DepartmentBean> dptList = deptDao.findAll(); // DB から全部署を取得するメソッド
	    request.setAttribute("dptList", dptList);
	    
	    PositionDAO posDao = new PositionDAO();
	    List<PositionBean> posList = posDao.findAll();
	    request.setAttribute("posList", posList);
	    
	    
	    // 検索パラメータを正規化
	    String empId   = trimToNull(request.getParameter("q_emp_id"));
	    String empName = trimToNull(request.getParameter("q_emp_name"));
	    String dptId   = trimToNull(request.getParameter("q_dpt_id"));
	    String posId   = trimToNull(request.getParameter("q_pos_id"));

	    // ソート・ページ
	    String sortKey = request.getParameter("sort");
	    String sortDir = request.getParameter("dir");
	    
	    // page の扱い（検索ボタンで page をリセット）
	    int page = 1;
	    String pageParam = request.getParameter("page");
	    if (pageParam != null) {
	        try { page = Integer.parseInt(pageParam); } catch (NumberFormatException ex) { page = 1; }
	    }
	    if (page < 1) page = 1;

	    final int LIMIT = 20;
	    int offset = (page - 1) * LIMIT;

	    EmployeeDAO dao = new EmployeeDAO();
	    List<EmployeeBean> empList = new ArrayList<>();
	    boolean hasNext = false;
	    try {
	        // DAO側で PageResult を返すように実装
	        PageResult<EmployeeBean> pageRes = dao.searchEmployees(empId, empName, dptId, posId, sortKey, sortDir, LIMIT, offset);
	        empList = pageRes.getItems();
	        hasNext = pageRes.hasNext();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        request.setAttribute("eMsg", "社員情報の取得中にエラーが発生しました");
	    }

	    // 検索条件・ページング情報を戻す
	    request.setAttribute("q_emp_id", empId);
	    request.setAttribute("q_emp_name", empName);
	    request.setAttribute("q_dpt_id", dptId);
	    request.setAttribute("q_pos_id", posId);
	    request.setAttribute("empList", empList);
	    request.setAttribute("page", page);
	    request.setAttribute("sort", sortKey);
	    request.setAttribute("dir", sortDir);
	    request.setAttribute("hasNext", hasNext);
	    String queryString = request.getQueryString(); // URLの ? 以降のパラメータを取得
		String currentUrl = request.getContextPath() + "/EmployeeInfo";
		if (queryString != null && !queryString.isEmpty()) {
			currentUrl += "?" + queryString;
		}
		session.setAttribute("lastListUrl", currentUrl);
	    request.getRequestDispatcher("/WEB-INF/jsp/user_info.jsp").forward(request, response);
	}

	private String trimToNull(String s) {
	    if (s == null) return null;
	    s = s.trim();
	    return s.isEmpty() ? null : s;
	}
	
}