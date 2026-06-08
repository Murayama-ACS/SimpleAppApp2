package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.QuizBean;

public class QuizDAO extends DAO{
	public int insertQuiz(QuizBean quizBean) {//userBeanの内容をデータベースに登録する関数
		Connection con = dbConnect();
		int result = 0;
		String sql = "insert into security_quiz (emp_id, quiz, answer) values (?,?,?)";
		String[] questions = {
				"初めて飼ったペットの名前は？",
				"子どものころに一番よく遊んだ路地や通りの名前は？",
				"初めて一人で泊まった旅館やホテルの名前は？",
				"学生時代に仲間から呼ばれていたあだ名は？",
				"自分で最初に作った料理の名前は？",
				"初めて自分で買った雑誌やコミックのタイトルは？",
				"最初に参加したライブやイベントの会場名は？",
				"幼少期によく遊んだ公園の呼び名や特徴（例：「大きな松のある公園」など）は？",
				"人生で最初に成し遂げた印象的な出来事を一言で表すと？",
				"家族やごく親しい友人だけが使う自分の呼び名（愛称）は？"
				};
		try {
			if(con != null) {

				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, quizBean.getEmp_id());
				st.setString(2, quizBean.getQuiz());
				st.setString(3, quizBean.getAnswer());

				int rs = st.executeUpdate();//これなんだっけ
				result = rs;
			}
		}catch(SQLException e) {
			System.out.println("SQLエラー");
			System.out.println(e.getMessage());
			return 0;
		}
		return result;
	}
	//public int updateUser(EmployeeBean empBean, String newPass) {
	//	Connection con = dbConnect();
	//	int result = 0;
	//	String sql = "update user set name=? ,pass=? where email=?";
	//	
	//	try {
	//		if(con != null) {
	//			
	//			PreparedStatement st = con.prepareStatement(sql);
	//			st.setString(1, userBean.getName());
	//			st.setString(2, newPass);
	//			st.setString(3, userBean.getEmail());
	//
	//			int rs = st.executeUpdate();//これなんだっけ
	//			result = rs;
	//		}
	//	}catch(SQLException e) {
	//		System.out.println("SQLエラー");
	//		System.out.println(e.getMessage());
	//		return 0;
	//	}
	//	return result;
	//}

	//クイズの問題と回答、社員IDが一致する秘密の問題の情報をデータベースから取得するメソッド
	public QuizBean quizInfo(String quiz, String answer, String emp_id) {
		Connection con = dbConnect();
		QuizBean quizBean = null;
		//identifierがメールか社員IDかでsql文を変更
		String sql = "SELECT * FROM security_quiz WHERE quiz = ? and answer = ? and emp_id = ?";

		try {
			if(con != null) {

				PreparedStatement st = con.prepareStatement(sql);
				st.setString(1, quiz);
				st.setString(2, answer);
				st.setString(3, emp_id);
				ResultSet rs = st.executeQuery();

				while(rs.next()) {
					String sq_id = rs.getString("sq_id");
					quizBean = new QuizBean(emp_id, quiz, answer);
				}
			}
		}catch(SQLException e) {
			System.out.println("SQLエラー");
			System.out.println(e.getMessage());
			return null;
		}
		dbClose(con);
		
		return quizBean;
	}

}
