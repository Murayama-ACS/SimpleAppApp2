package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.QuizBean;
import model.Hash;

public class QuizDAO extends DAO {
	Hash hash = new Hash();

	//		public int insertQuiz(QuizBean quizBean) {//QuizBeanの内容をデータベースに登録する関数
	//			Connection con = dbConnect();
	//			int result = 0;
	//			String sql = "insert into security_quiz (emp_id, quiz, answer) values (?,?,?)";
	//			
	//			try {
	//				if(con != null) {
	//
	//					PreparedStatement st = con.prepareStatement(sql);
	//					st.setString(1, quizBean.getEmp_id());
	//					st.setString(2, quizBean.getQuiz());
	//					st.setString(3, quizBean.getAnswer());
	//
	//					int rs = st.executeUpdate();//これなんだっけ
	//					result = rs;
	//				}
	//			}catch(SQLException e) {
	//				System.out.println("SQLエラー");
	//				System.out.println(e.getMessage());
	//				return 0;
	//			}
	//			return result;
	//		}
	public int insertQuiz(QuizBean quizBean) {
		String sqlCheck = "SELECT 1 FROM security_quiz WHERE emp_id = ? AND quiz = ?";
		String sqlInsert = "INSERT INTO security_quiz (emp_id, quiz, answer) VALUES (?,?,?)";
		Connection con = dbConnect();
		try  {
			if (con == null) {
				System.out.println("DB接続に失敗");
				dbClose(con);
				return 0;
			}

			// 1) 重複チェック
			try (PreparedStatement stCheck = con.prepareStatement(sqlCheck)) {
				stCheck.setString(1, quizBean.getEmp_id());
				stCheck.setString(2, quizBean.getQuiz());
				try (ResultSet rs = stCheck.executeQuery()) {
					if (rs.next()) {
						System.out.println("既に同じ emp_id と quiz の組が存在します");
						return -1; // 重複を示す戻り値（呼び出し側で扱ってください）
					}
				}
			}

			// 2) 挿入
			try (PreparedStatement stIns = con.prepareStatement(sqlInsert)) {
				stIns.setString(1, quizBean.getEmp_id());
				stIns.setString(2, quizBean.getQuiz());
				stIns.setString(3, hash.getSHA512(quizBean.getAnswer())); // ※後述の注意点参照
				int rows = stIns.executeUpdate();
				return rows;
			}

		} catch (SQLException e) {
			System.out.println("SQLエラー: " + e.getMessage());
			return 0;
		}finally {
			dbClose(con);
		}
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

	

	// クイズの問題と回答、社員IDが一致する秘密の問題の情報を取得する。
	// 正解なら QuizBean を返す。間違いなら null を返す。
	// DB エラーは SQLException として呼び出し側へスローする。
	public QuizBean quizInfo(String quiz, String answer, String emp_id) throws SQLException {
	    String sql = "SELECT sq_id, quiz, answer, emp_id FROM security_quiz WHERE quiz = ? AND emp_id = ? LIMIT 1";
	    try (Connection con = dbConnect();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setString(1, quiz);
	        ps.setString(2, emp_id);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (!rs.next()) {
	                // レコードなし（質問が登録されていない／該当の組み合わせなし）
	                return null;
	            }
	            String storedHash = rs.getString("answer"); // 実スキーマに合わせる
	            String inputHash = hash.getSHA512(answer);
	            if (storedHash != null && storedHash.equals(inputHash)) {
	                QuizBean qb = new QuizBean(emp_id, quiz , answer);
	                return qb;
	            } else {
	                // レコードはあるが回答が不一致（不正解）
	                return null;
	            }
	        }
	    }
	}

	private FailedLoginDAO failedLoginDao = new FailedLoginDAO();

	// 秘密質問認証（成功時 QuizBean、失敗時 null）
	public QuizBean authenticateQuiz(String quiz, String answer, String empId) throws SQLException {
		// 1) まずロックチェック
		if (failedLoginDao.isLocked(empId)) {
			return null;
		}

		String sql = "SELECT sq_id, answer FROM security_quiz WHERE quiz = ? AND emp_id = ?";
		Connection con = dbConnect();
		try (
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, quiz);
			ps.setString(2, empId);
			try (ResultSet rs = ps.executeQuery()) {
				boolean matched = false;
				String sqId = null;
				answer = hash.getSHA512(answer);

				while (rs.next()) {
					String storedAnswer = rs.getString("answer");
					// answer は現状平文比較。可能ならハッシュ化して照合する
					if (storedAnswer != null && storedAnswer.equals(answer)) {
						matched = true;
						sqId = rs.getString("sq_id");
						break;
					}
				}
				if (matched) {
					// 成功時は quiz_attempts をリセットする実装（failedLoginDao.resetOnSuccess も可）
					failedLoginDao.resetOnSuccess(empId); // 全リセットでも良い
					return new QuizBean(empId, quiz, answer);
				} else {
					failedLoginDao.recordQuizFailure(empId);
					return null;
				}
			}
		}finally {
			dbClose(con);
		}
	}
}
