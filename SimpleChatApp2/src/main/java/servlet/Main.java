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
import beans.DateTimeBean;
import beans.UserBean;
import dao.ChatDAO;
import model.TodaysDateTime;

/**
 * Servlet implementation class Main
 */
@WebServlet("/Main")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Main() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/main.jsp");
		rd.forward(request, response);	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		TodaysDateTime todaysDateTime = new TodaysDateTime();
		ChatDAO chatDAO = new ChatDAO();
		ServletContext application = this.getServletContext();
		//1)
		HttpSession session = request.getSession();
		UserBean userBean = (UserBean) session.getAttribute("userBean"); 
		if(userBean == null) {
			request.setAttribute("noMsg","ログインできていません");
			RequestDispatcher reDispatcher = request.getRequestDispatcher("index.jsp");
			reDispatcher.forward(request,response);
			return;
		}
		//2)
		String Msg = request.getParameter("Msg");
		if(Msg == null || Msg.isEmpty()) {
			request.setAttribute("noMsg","メッセージが入力されていません。");
			ArrayList<ChatBean> chatList = chatDAO.selectChat();
			application.setAttribute("ChatList", chatList);
			RequestDispatcher reDispatcher = request.getRequestDispatcher("WEB-INF/jsp/main.jsp");
			reDispatcher.forward(request,response);
			return;
		}
		//3)
		DateTimeBean datetime = todaysDateTime.getDayTime();
		ChatBean chatBean = new 
				ChatBean(userBean.getName(),datetime.getDate(),datetime.getTime(),Msg);
		//4)
		chatDAO.insertChat(chatBean);
		//5)
		ArrayList<ChatBean> chatList = chatDAO.selectChat();
		//6)

		application.setAttribute("ChatList", chatList);
		RequestDispatcher reDispatcher = request.getRequestDispatcher("WEB-INF/jsp/main.jsp");
		reDispatcher.forward(request, response);
	}

}
