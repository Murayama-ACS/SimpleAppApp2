package Test;

import java.sql.SQLException;

import dao.FailedLoginDAO;

public class failedloginDAOTest {

	public static void main(String[] args) {
		FailedLoginDAO flog = new FailedLoginDAO();
		try {
			System.out.println(flog.isLocked("A1"));
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			flog.resetOnSuccess("A1");
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			System.out.println(flog.recordQuizFailure("A1231"));
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			flog.adminUnlock("A1231", true, null, null);
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			System.out.println(	flog.getRemainingQuizAttemptsByEmpId("A20150909"));
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

}
