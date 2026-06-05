package servlet;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import beans.ChatBean;
import beans.UserBean;
import dao.ChatDAO;
import dao.UserDAO;

/**
 * Servlet implementation class EditProfile
 */
@WebServlet("/EditProfile")
public class EditProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditProfile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		if (userBean == null) {
			response.sendRedirect("index.jsp");
			return;
		}
		RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/editProfile.jsp");
		rd.forward(request, response);	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		
		if (userBean == null) {
			response.sendRedirect("index.jsp");
			return;
		}
		String newName = request.getParameter("name");
		String newPass = request.getParameter("pass");
		
		if (newName == null || newName.isEmpty() || newPass == null || newPass.isEmpty()) {
			request.setAttribute("editMsg", "名前とパスワードを入力してください。");
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/editProfile.jsp");
			rd.forward(request, response);
			return;
		}
		
		UserBean updateUser = new UserBean();
		
		updateUser.setEmail(userBean.getEmail());
		updateUser.setName(newName);
		updateUser.setPass(newPass);
		
		UserDAO userDAO = new UserDAO();
		int result = userDAO.updateUser(updateUser);
		
		if (result > 0) {
		    userBean.setName(newName);
		    session.setAttribute("userBean", userBean);
		    ChatDAO chatDAO = new dao.ChatDAO();
		    ArrayList<ChatBean> chatList = chatDAO.selectChat();
			ServletContext application = this.getServletContext();
		    application.setAttribute("ChatList", chatList); 
			request.setAttribute("editMsg", "プロフィールを更新しました。");
		    RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/editProfile.jsp");
		    rd.forward(request, response);
		} else {
			request.setAttribute("editMsg", "プロフィールの更新に失敗しました。");
			RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/editProfile.jsp");
			rd.forward(request, response);
		}
	}

}
