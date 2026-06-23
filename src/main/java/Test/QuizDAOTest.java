package Test;

import java.sql.SQLException;

import bean.QuizBean;
import dao.QuizDAO;

public class QuizDAOTest {

	public static void main(String[] args) {
		QuizDAO quizDAO = new QuizDAO();
		QuizBean quizBean = null;
		try {
			quizBean = quizDAO.authenticateQuiz("初めて飼ったペットの名前は？", "いち", "A1");
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		if(quizBean == null) {
			System.out.println("秘密の質問が登録されていません。");
		}else {
			System.out.println(quizBean.getAnswer());
			System.out.println(quizBean.getQuiz());
			System.out.println(quizBean.getEmp_id());

		}
		
	}

}
