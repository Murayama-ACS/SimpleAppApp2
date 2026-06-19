package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

public class FailedLoginDAO extends DAO {
	
	private static final int PASSWORD_THRESHOLD = 5;//パスワード認証の試行可能回数
	private static final int QUIZ_THRESHOLD = 3;//秘密の質問認証の試行可能回数
	private static final int WINDOW_MINUTES = 15;//カウントウィンドウ（この時間を超えるとカウントはリセット）
	private static final int BASE_LOCK_MINUTES = 15;//初回ロック時間、lock_countが2以上の場合、lock_countの値に応じて倍化（上限 MAX_LOCK_MINUTES = 24 時間）
	private static final int MAX_LOCK_MINUTES = 24 * 60;//

	// ロック中かチェック（DB時刻を使う）
	public boolean isLocked(String empId) throws SQLException {
		String sql = "SELECT locked_until FROM failed_logins WHERE emp_id = ?";
		Connection con = dbConnect();
		try (
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Timestamp lockedUntil = rs.getTimestamp("locked_until");
					if (lockedUntil != null) {
						dbClose(con);
						// DB の UTC_TIMESTAMP() と比較する簡易チェック（アプリ時刻と同期していればOK）
						return lockedUntil.toInstant().isAfter(Instant.now());
					}
				}
			}
		}
		dbClose(con);
		return false;
	}

	// 成功時リセット（パスワード成功・全体リセット）
	public void resetOnSuccess(String empId) throws SQLException {
		String upd = "UPDATE failed_logins SET password_attempts = 0, first_failed_password_at = NULL, last_failed_password_at = NULL, quiz_attempts = 0, first_failed_quiz_at = NULL, last_failed_quiz_at = NULL, locked_until = NULL, lock_count = 0 WHERE emp_id = ?";
		Connection con = dbConnect();
		try (
				PreparedStatement ps = con.prepareStatement(upd)) {
			ps.setString(1, empId);
			ps.executeUpdate();
		}
		dbClose(con);
	}

	// パスワード失敗を記録 → 戻り値で状態を返す
	public enum FailureResult { AUTH_FAILED, NOW_LOCKED, ALREADY_LOCKED }

	public FailureResult recordPasswordFailure(String empId) throws SQLException {
		try (Connection con = dbConnect()) {
			con.setAutoCommit(false);
			try {
				// FOR UPDATE で行ロック
				String sel = "SELECT password_attempts, first_failed_password_at, lock_count, locked_until FROM failed_logins WHERE emp_id = ? FOR UPDATE";
				Integer attempts = null;
				Timestamp firstFailed = null;
				int lockCount = 0;
				Timestamp lockedUntil = null;

				try (PreparedStatement ps = con.prepareStatement(sel)) {
					ps.setString(1, empId);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							attempts = rs.getInt("password_attempts");
							if (rs.wasNull()) attempts = 0;
							firstFailed = rs.getTimestamp("first_failed_password_at");
							lockCount = rs.getInt("lock_count");
							lockedUntil = rs.getTimestamp("locked_until");
						}
					}
				}

				if (attempts == null) {
					// 行がなければ挿入して初期化
					String ins = "INSERT INTO failed_logins (emp_id, password_attempts, quiz_attempts, lock_count, created_at, updated_at) VALUES (?, 0, 0, 0, UTC_TIMESTAMP(), UTC_TIMESTAMP())";
					try (PreparedStatement pis = con.prepareStatement(ins)) {
						pis.setString(1, empId);
						pis.executeUpdate();
					}
					attempts = 0; firstFailed = null; lockCount = 0; lockedUntil = null;
				}

				Instant now = Instant.now();
				if (lockedUntil != null && lockedUntil.toInstant().isAfter(now)) {
					con.rollback();
					return FailureResult.ALREADY_LOCKED;
				}

				// ウィンドウ判定
				boolean resetWindow = false;
				if (firstFailed == null || firstFailed.toInstant().plus(Duration.ofMinutes(WINDOW_MINUTES)).isBefore(now)) {
					attempts = 0;
					resetWindow = true;
				}

				attempts = attempts + 1;
				if (attempts >= PASSWORD_THRESHOLD) {
					lockCount = lockCount + 1;
					long multiplier = 1L << (Math.max(0, lockCount - 1));
					int lockMinutes = (int)Math.min((long)BASE_LOCK_MINUTES * multiplier, MAX_LOCK_MINUTES);
					Instant lockedUntilInst = now.plus(Duration.ofMinutes(lockMinutes));
					Timestamp lockedUntilTs = Timestamp.from(lockedUntilInst);

					String updLock = "UPDATE failed_logins SET password_attempts = 0, first_failed_password_at = NULL, last_failed_password_at = UTC_TIMESTAMP(), lock_count = ?, locked_until = ?, updated_at = UTC_TIMESTAMP() WHERE emp_id = ?";
					try (PreparedStatement pup = con.prepareStatement(updLock)) {
						pup.setInt(1, lockCount);
						pup.setTimestamp(2, lockedUntilTs);
						pup.setString(3, empId);
						pup.executeUpdate();
					}
					con.commit();
					return FailureResult.NOW_LOCKED;
				} else {
					Timestamp firstToStore = (firstFailed == null || resetWindow) ? Timestamp.from(now) : firstFailed;
					String upd = "UPDATE failed_logins SET password_attempts = ?, first_failed_password_at = ?, last_failed_password_at = UTC_TIMESTAMP(), updated_at = UTC_TIMESTAMP() WHERE emp_id = ?";
					try (PreparedStatement pup = con.prepareStatement(upd)) {
						pup.setInt(1, attempts);
						pup.setTimestamp(2, firstToStore);
						pup.setString(3, empId);
						pup.executeUpdate();
					}
					con.commit();
					return FailureResult.AUTH_FAILED;
				}
			} catch (SQLException ex) {
				con.rollback();
				throw ex;
			} finally {
				con.setAutoCommit(true);
				dbClose(con);
			}
		}
	}

	// 秘密質問の失敗記録（パスワード版と同様のロジックを quiz_attempts 用に実装）
	public FailureResult recordQuizFailure(String empId) throws SQLException {
		// 実装は recordPasswordFailure と同様（QUIZ_THRESHOLD を使用し quiz_attempts と first_failed_quiz_at を操作）
		// 簡略例: 同じロジックをコピーして quiz_* カラム名に置き換えてください
		try (Connection con = dbConnect()) {
			con.setAutoCommit(false);
			try {
				// FOR UPDATE で行ロック
				String sel = "SELECT quiz_attempts, first_failed_quiz_at, lock_count, locked_until FROM failed_logins WHERE emp_id = ? FOR UPDATE";
				Integer attempts = null;
				Timestamp firstFailed = null;
				int lockCount = 0;
				Timestamp lockedUntil = null;

				try (PreparedStatement ps = con.prepareStatement(sel)) {
					ps.setString(1, empId);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							attempts = rs.getInt("quiz_attempts");
							if (rs.wasNull()) attempts = 0;
							firstFailed = rs.getTimestamp("first_failed_quiz_at");
							lockCount = rs.getInt("lock_count");
							lockedUntil = rs.getTimestamp("locked_until");
						}
					}
				}

				if (attempts == null) {
					// 行がなければ挿入して初期化
					String ins = "INSERT INTO failed_logins (emp_id, password_attempts, quiz_attempts, lock_count, created_at, updated_at) VALUES (?, 0, 0, 0, UTC_TIMESTAMP(), UTC_TIMESTAMP())";
					try (PreparedStatement pis = con.prepareStatement(ins)) {
						pis.setString(1, empId);
						pis.executeUpdate();
					}
					attempts = 0; firstFailed = null; lockCount = 0; lockedUntil = null;
				}

				Instant now = Instant.now();
				if (lockedUntil != null && lockedUntil.toInstant().isAfter(now)) {
					con.rollback();
					return FailureResult.ALREADY_LOCKED;
				}

				// ウィンドウ判定
				boolean resetWindow = false;
				if (firstFailed == null || firstFailed.toInstant().plus(Duration.ofMinutes(WINDOW_MINUTES)).isBefore(now)) {
					attempts = 0;
					resetWindow = true;
				}

				attempts = attempts + 1;
				if (attempts >= QUIZ_THRESHOLD) {
					lockCount = lockCount + 1;
					long multiplier = 1L << (Math.max(0, lockCount - 1));
					int lockMinutes = (int)Math.min((long)BASE_LOCK_MINUTES * multiplier, MAX_LOCK_MINUTES);
					Instant lockedUntilInst = now.plus(Duration.ofMinutes(lockMinutes));
					Timestamp lockedUntilTs = Timestamp.from(lockedUntilInst);

					String updLock = "UPDATE failed_logins SET quiz_attempts = 0, first_failed_quiz_at = NULL, last_failed_quiz_at = UTC_TIMESTAMP(), lock_count = ?, locked_until = ?, updated_at = UTC_TIMESTAMP() WHERE emp_id = ?";
					try (PreparedStatement pup = con.prepareStatement(updLock)) {
						pup.setInt(1, lockCount);
						pup.setTimestamp(2, lockedUntilTs);
						pup.setString(3, empId);
						pup.executeUpdate();
					}
					con.commit();
					return FailureResult.NOW_LOCKED;
				} else {
					Timestamp firstToStore = (firstFailed == null || resetWindow) ? Timestamp.from(now) : firstFailed;
					String upd = "UPDATE failed_logins SET quiz_attempts = ?, first_failed_quiz_at = ?, last_failed_quiz_at = UTC_TIMESTAMP(), updated_at = UTC_TIMESTAMP() WHERE emp_id = ?";
					try (PreparedStatement pup = con.prepareStatement(upd)) {
						pup.setInt(1, attempts);
						pup.setTimestamp(2, firstToStore);
						pup.setString(3, empId);
						pup.executeUpdate();
					}
					con.commit();
					return FailureResult.AUTH_FAILED;
				}
			} catch (SQLException ex) {
				con.rollback();
				throw ex;
			} finally {
				con.setAutoCommit(true);
				dbClose(con);
			}
		}
	}

	// 管理者解除（fullReset が true なら lock_count も 0 にする）
	public void adminUnlock(String empId, boolean fullReset, String adminId, String note) throws SQLException {
		String upd;
		if (fullReset) {
			upd = "UPDATE failed_logins SET password_attempts = 0, first_failed_password_at = NULL, last_failed_password_at = NULL, quiz_attempts = 0, first_failed_quiz_at = NULL, last_failed_quiz_at = NULL, lock_count = 0, locked_until = NULL, updated_at = UTC_TIMESTAMP() WHERE emp_id = ?";
		} else {
			upd = "UPDATE failed_logins SET password_attempts = 0, first_failed_password_at = NULL, last_failed_password_at = NULL, quiz_attempts = 0, first_failed_quiz_at = NULL, last_failed_quiz_at = NULL, locked_until = NULL, updated_at = UTC_TIMESTAMP() WHERE emp_id = ?";
		}
		try (Connection con = dbConnect();
				PreparedStatement ps = con.prepareStatement(upd)) {
			ps.setString(1, empId);
			ps.executeUpdate();
		}
		// 監査テーブルを使っていない前提なのでここでは省略。必要なら audit INSERT を追加
	}

	public Integer getRemainingPasswordAttemptsByEmpId(String empId) throws SQLException {
		String sql = "SELECT password_attempts, first_failed_password_at, locked_until FROM failed_logins WHERE emp_id = ?";
		Connection con = dbConnect();
		try (
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empId);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return null; // 行がない = ユーザデータなし（まだ失敗記録が作られていない）
				}
				int attempts = rs.getInt("password_attempts");
				Timestamp lockedUntil = rs.getTimestamp("locked_until");
				if (lockedUntil != null && lockedUntil.toInstant().isAfter(Instant.now())) {
					return -1; // ロック中
				}
				// ウィンドウ処理：first_failed が古ければ attempts を 0 とみなす（WINDOW_MINUTES と合わせる必要あり）
				Timestamp firstFailed = rs.getTimestamp("first_failed_password_at");
				if (firstFailed == null || firstFailed.toInstant().plus(Duration.ofMinutes(WINDOW_MINUTES)).isBefore(Instant.now())) {
					attempts = 0;
				}
				int remaining = PASSWORD_THRESHOLD - attempts;
				if (remaining < 0) remaining = 0;
				return remaining;
			}
		}finally {
			dbClose(con);
		}
	}

	public Integer getRemainingQuizAttemptsByEmpId(String empId) throws SQLException {
		String sql = "SELECT quiz_attempts, first_failed_quiz_at, locked_until FROM failed_logins WHERE emp_id = ?";
		try (Connection con = dbConnect();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, empId);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) return null;
				int attempts = rs.getInt("quiz_attempts");
				Timestamp lockedUntil = rs.getTimestamp("locked_until");
				if (lockedUntil != null && lockedUntil.toInstant().isAfter(Instant.now())) return -1;
				Timestamp firstFailed = rs.getTimestamp("first_failed_quiz_at");
				if (firstFailed == null || firstFailed.toInstant().plus(Duration.ofMinutes(WINDOW_MINUTES)).isBefore(Instant.now())) {
					attempts = 0;
				}
				int remaining = QUIZ_THRESHOLD - attempts;
				if (remaining < 0) remaining = 0;
				return remaining;
			}
		}finally {
			Connection con = dbConnect();
		}
	}
}


