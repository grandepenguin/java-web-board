package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class JdbcUtil {

	public static Connection getConnection() {
		Connection con = null;

		try {
			Context initCtx = new InitialContext();
			DataSource ds = (DataSource) initCtx.lookup("java:comp/env/JDBC/BOARD");
			con = ds.getConnection();
			con.setAutoCommit(false);// Connection 객체에 트랜젝션을 완성하지 못하도록 false로 설정
		} catch (Exception e) {
			System.out.println("JdbcUtil 클래스의 getConnection()예외 = " + e);
		}

		return con;
	}

	/*
	 자원 해제 순서 : 일반적으로 ResultSet -> PreparedStatement -> Connection 순으로 자원을 해제하는 것이 좋습니다.
	 왜냐하면 PreparedStatement을 먼저 닫으면 PreparedStatement가 생성한 ResultSet을 닫게되어 예기치 않은 문제가 발생할 수 있습니다.
	 */
	
	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			System.out.println("JdbcUtil 클래스의 close(ResultSet rs)예외 = " + e);
		}
	}
	
	public static void close(PreparedStatement pstmt) {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (Exception e) {
			System.out.println("JdbcUtil 클래스의 close(PreparedStatement pstmt)예외 = " + e);
		}
	}
	
	public static void close(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			System.out.println("JdbcUtil 클래스의 close(Connection con)예외 = " + e);
		}
	}

	public static void commit(Connection con) {
		try {
			if (con != null) {
				con.commit();
			}
		} catch (Exception e) {
			System.out.println("JdbcUtil 클래스의 commit(Connection con)예외 = " + e);
		}
	}

	public static void rollback(Connection con) {
		try {
			if (con != null) {
				con.rollback();
			}
		} catch (Exception e) {
			System.out.println("JdbcUtil 클래스의 rollback(Connection con)예외 = " + e);
		}
	}

}
