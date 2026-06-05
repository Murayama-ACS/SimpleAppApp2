package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import beans.ChatBean;

public class ChatDAO extends DAO{
	//method
	public int insertChat(ChatBean chatBean) {
		Connection con = dbConnect();
		String sql = "INSERT INTO chathistory(date,time,name,msg)"
				+ "values(?,?,?,?)";
		try {
			if(con != null) {
				//ステートメントインスタンス（バッファ）の生成
				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, chatBean.getDate());
				st.setString(2, chatBean.getTime());
				st.setString(3, chatBean.getName());
				st.setString(4, chatBean.getText());
				st.executeUpdate();
			}
		}catch(SQLException e) {
			System.out.println("SQLエラー");
			System.out.println(e.getMessage());
			return 0;
		}
		return 1;
	}
	public ArrayList<ChatBean> selectChat(){
		//method
			Connection con = dbConnect();
			String sql = "SELECT * FROM chathistory ORDER BY date DESC, time DESC, no ASC;";
			ArrayList<ChatBean> chatList = new ArrayList<ChatBean>();
			try {
				if(con != null) {
					//ステートメントインスタンス（バッファ）の生成
					PreparedStatement st = con.prepareStatement(sql);
					ResultSet rs = st.executeQuery();
					while(rs.next()) {
					    String date = rs.getString("date");
					    String time = rs.getString("time");
					    String name = rs.getString("name");
					    String msg  = rs.getString("msg");
					    ChatBean chat = new ChatBean(name, date, time, msg);
					    chatList.add(chat);
					}
				}
			}catch(SQLException e) {
				System.out.println("SQLエラー");
				System.out.println(e.getMessage());
				return null;
			}
			return chatList;
	}
}
