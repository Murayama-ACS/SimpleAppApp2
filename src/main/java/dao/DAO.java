package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
	protected Connection dbConnect() {
		Connection con = null;
		try {
			//ドライバロード
			Class.forName("org.mariadb.jdbc.Driver");
			//データベースの確認
			con = DriverManager.getConnection("jdbc:mariadb://localhost/simpleappapp2?user=root&password=Maria1234");
		} catch (ClassNotFoundException e) {
			System.out.println("JDBCドライバが見つかりません。");
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			System.out.println("データベース接続エラー");
			System.out.println(e.getMessage());
		}
		return con;
	}

	protected void dbClose(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("DB切断時にエラーが発生しました。");
			System.out.println(e.getMessage());
		}
	}

	/*// 正常系で実際に dbConnect を呼ぶテスト
	protected static void testDbConnectNormal() {
	    System.out.println("=== testDbConnectNormal ===");
	    dao.DAO base = new dao.DAO();
	    Connection con = null;
	    try {
	        con = base.dbConnect();
	        if (con != null && !con.isClosed()) {
	            // isValid が使える場合は利用する（タイムアウト1秒）
	            boolean valid = false;
	            try {
	                valid = con.isValid(1);
	            } catch (AbstractMethodError | SQLException ignore) {}
	            System.out.println("dbConnect returned non-null Connection. isClosed=" + con.isClosed() + " isValid(if available)=" + valid);
	            System.out.println("testDbConnectNormal: PASS");
	        } else {
	            System.out.println("dbConnect returned null or closed Connection");
	            System.out.println("testDbConnectNormal: FAIL");
	        }
	    } catch (Exception e) {
	        System.out.println("testDbConnectNormal: EXCEPTION - " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        try { if (con != null && !con.isClosed()) con.close(); } catch (Exception e) {}
	    }
	}
	
	// ドライバが存在しないことを再現するサブクラス（Class.forName に存在しないクラス名を指定）
	static class MissingDriverDAO extends dao.DAO {
	    @Override
	    protected Connection dbConnect() {
	        Connection con = null;
	        try {
	            // 故意に存在しないドライバ名
	            Class.forName("non.existent.DriverForTest");
	            con = DriverManager.getConnection("jdbc:mariadb://localhost/simpleappapp2?user=root&password=Maria1234");
	        } catch (ClassNotFoundException e) {
	            System.out.println("JDBCドライバが見つかりません。 (simulated)");
	            System.out.println(e.getMessage());
	        } catch (SQLException e) {
	            System.out.println("データベース接続エラー");
	            System.out.println(e.getMessage());
	        }
	        return con;
	    }
	}
	
	protected static void testDbConnectMissingDriver() {
	    System.out.println("=== testDbConnectMissingDriver ===");
	    MissingDriverDAO dao = new MissingDriverDAO();
	    Connection con = null;
	    try {
	        con = dao.dbConnect();
	        if (con == null) {
	            System.out.println("dbConnect returned null as expected when driver is missing.");
	            System.out.println("testDbConnectMissingDriver: PASS");
	        } else {
	            System.out.println("dbConnect returned non-null Connection unexpectedly.");
	            System.out.println("testDbConnectMissingDriver: FAIL");
	        }
	    } catch (Exception e) {
	        System.out.println("testDbConnectMissingDriver: EXCEPTION - " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        try { if (con != null && !con.isClosed()) con.close(); } catch (Exception e) {}
	    }
	}
	
	// 接続に失敗する（SQLException を発生）サブクラス：不正な URL を与える
	static class BadUrlDAO extends dao.DAO {
	    @Override
	    protected Connection dbConnect() {
	        Connection con = null;
	        try {
	            Class.forName("org.mariadb.jdbc.Driver"); // 本来のドライバ名
	            // 故意に無効なURL（ポートやホストを存在しないものにする）
	            con = DriverManager.getConnection("jdbc:mariadb://invalid-host:3306/nonexistentdb?user=bad&password=bad");
	        } catch (ClassNotFoundException e) {
	            System.out.println("JDBCドライバが見つかりません。");
	            System.out.println(e.getMessage());
	        } catch (SQLException e) {
	            System.out.println("データベース接続エラー (simulated bad URL)");
	            System.out.println(e.getMessage());
	        }
	        return con;
	    }
	}
	
	protected static void testDbConnectSQLException() {
	    System.out.println("=== testDbConnectSQLException ===");
	    BadUrlDAO dao = new BadUrlDAO();
	    Connection con = null;
	    try {
	        con = dao.dbConnect();
	        if (con == null) {
	            System.out.println("dbConnect returned null as expected on connection failure.");
	            System.out.println("testDbConnectSQLException: PASS");
	        } else {
	            System.out.println("dbConnect returned non-null Connection unexpectedly.");
	            System.out.println("testDbConnectSQLException: FAIL");
	        }
	    } catch (Exception e) {
	        System.out.println("testDbConnectSQLException: EXCEPTION - " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        try { if (con != null && !con.isClosed()) con.close(); } catch (Exception e) {}
	    }
	}
	
	// dbClose: 有効な接続オブジェクトを渡す（正常クローズ）
	protected static void testDbCloseNormal() {
	    System.out.println("=== testDbCloseNormal ===");
	    dao.DAO d = new dao.DAO();
	    Connection con = null;
	    try {
	        con = d.dbConnect();
	        if (con == null) {
	            System.out.println("Skipping testDbCloseNormal because dbConnect returned null.");
	            System.out.println("testDbCloseNormal: SKIPPED");
	            return;
	        }
	        d.dbClose(con);
	        // dbClose が正常に動いていれば close() 後 isClosed が true
	        boolean closed = false;
	        try { closed = con.isClosed(); } catch (SQLException e) {  ignore  }
	        System.out.println("After dbClose, Connection.isClosed() = " + closed);
	        System.out.println("testDbCloseNormal: PASS (if isClosed==true or no exception)");
	    } catch (Exception e) {
	        System.out.println("testDbCloseNormal: EXCEPTION - " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	// dbClose: null を渡す
	private static void testDbCloseNull() {
	    System.out.println("=== testDbCloseNull ===");
	    dao.DAO d = new dao.DAO();
	    try {
	        d.dbClose(null);
	        System.out.println("dbClose(null) returned normally.");
	        System.out.println("testDbCloseNull: PASS");
	    } catch (Exception e) {
	        System.out.println("testDbCloseNull: EXCEPTION - " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	// dbClose: close() 呼び出しで SQLException を投げる Connection を渡す
	private static void testDbCloseThrowsSQLException() {
	    System.out.println("=== testDbCloseThrowsSQLException ===");
	    dao.DAO d = new dao.DAO();
	
	    // Proxy を作って close() のみ SQLException を投げる Connection を作る
	    Connection throwingConn = (Connection) Proxy.newProxyInstance(
	            Connection.class.getClassLoader(),
	            new Class[] { Connection.class },
	            new InvocationHandler() {
	                @Override
	                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	                    if ("close".equals(method.getName())) {
	                        throw new SQLException("Simulated close failure at " + Instant.now());
	                    }
	                    // 他のメソッドは何もしないかデフォルト
	                    return null;
	                }
	            }
	    );
	
	    try {
	        d.dbClose(throwingConn);
	        System.out.println("dbClose handled SQLException during close without throwing.");
	        System.out.println("testDbCloseThrowsSQLException: PASS");
	    } catch (Exception e) {
	        System.out.println("testDbCloseThrowsSQLException: EXCEPTION - " + e.getMessage());
	        e.printStackTrace();
	    }
	}*/
}
