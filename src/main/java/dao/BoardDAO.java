package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static db.JdbcUtil.*;

import vo.Board;

public class BoardDAO {

	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private static BoardDAO boardDAO = null;

	public static BoardDAO getInstance() {
		if (boardDAO == null) {
			boardDAO = new BoardDAO();
		}
		return boardDAO;
	}

	public void setConnection(Connection con) {
		this.con = con;
	}

	/** ----------------------------게시판 기능--------------------------------- */

	// 게시물 등록
	public int Insertboard(Board board) {
		int result = 0;
		String sql = "INSERT INTO board (num, writer, pw, title, content, searchPos)"
				+ " SELECT board_Num.NEXTVAL, ?, ?, ?, ?, TRUNC(board_Num.NEXTVAL / 10000) FROM dual";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, board.getWriter());
			pstmt.setString(2, board.getPw());
			pstmt.setString(3, board.getTitle());
			pstmt.setString(4, board.getContent());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 boardInsert()에서 예외 발생 : " + e);
		} finally {
			close(pstmt);
		}

		return result;
	}

	// 비밀번호 확인
	public String selectBoardPw(Board board) {
		String pwCheck = null;
		String sql = "SELECT pw FROM board WHERE num=? AND pw=? AND searchPos=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, board.getNum());
			pstmt.setString(2, board.getPw());
			pstmt.setInt(3, board.getSearchPos());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				pwCheck = rs.getString("Pw");
			}

		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 selectBoardPw()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return pwCheck;
	}

	// board 테이블 게시물(num) 존재유무
	public int selectBoardNum(Board board) {
		int numCheck = 0;
		String sql = "SELECT num FROM board WHERE num=? AND searchPos=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, board.getNum());
			pstmt.setInt(2, board.getSearchPos());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				numCheck = rs.getInt("num");
			}

		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 selectBoardNum()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return numCheck;
	}

	// 게시물 삭제
	public int deleteBoard(Board board) {
		int result = 0;
		String sql = "DELETE FROM board WHERE num=? AND pw=? AND searchPos=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, board.getNum());
			pstmt.setString(2, board.getPw());
			pstmt.setInt(3, board.getSearchPos());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 deleteBoard()에서 예외 발생 : " + e);
		} finally {
			close(pstmt);
		}

		return result;
	}

	// index.jsp에 보여줄 게시물들(번호, 제목, 작성자, 작성일, 조회수) 가져오기.
	public ArrayList<Board> selectBoardMainPageView(int searchPos, int startIndex, int endIndex) {
		ArrayList<Board> boardList = null;

		String sql = "SELECT * FROM(SELECT num, title, writer, createdDate, views,"
				+ " ROW_NUMBER() OVER (ORDER BY num DESC) AS subRowNum FROM board WHERE searchPos=?)"
				// 서브쿼리에 WHERE searchPos=?이 없다면 Board 테이블의 모든 데이터를 검색하기 때문에 검색이 느려집니다.
				+ " WHERE subRowNum BETWEEN ? AND ? ORDER BY subRowNum ASC";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, searchPos);
			pstmt.setInt(2, startIndex);
			pstmt.setInt(3, endIndex);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				if (boardList == null) {
					boardList = new ArrayList<>();
				}
				Board board = new Board();

				board.setNum(rs.getInt("num"));
				board.setTitle(rs.getString("title"));
				board.setWriter(rs.getString("writer"));
				board.setCreatedDate(rs.getTimestamp("createdDate"));
				board.setViews(rs.getInt("views"));

				boardList.add(board);
			}

		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 selectBoardMainPageView()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return boardList;
	}

	// Num 최대값 가져오기.
	public int selectBoardMaxNum() {
		int maxNum = 0;
		String sql = "SELECT MAX(num) AS maxNum FROM board";

		try {
			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				maxNum = rs.getInt("maxNum");
			}
		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 selectBoardMaxNum()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return maxNum;
	}

	// 게시물 제목, 작성자, 작성일, 조회수, 내용 가져오기
	public Board selectBoardContent1(int num, int searchPos) {
		Board board = null;
		String sql = "SELECT title, writer, createdDate, views, content FROM board WHERE num=? AND searchPos=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, num);
			pstmt.setInt(2, searchPos);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				if (board == null) {
					board = new Board();
				}
				board.setTitle(rs.getString("title"));
				board.setWriter(rs.getString("writer"));
				board.setCreatedDate(rs.getTimestamp("createdDate"));
				board.setViews(rs.getInt("views"));
				board.setContent(rs.getString("content"));
			}

		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 selectBoardContent1()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return board;
	}

	// 게시물 작성자, 비밀번호, 제목, 내용 가져오기
	public Board selectboardContent2(Board boardParam) {
		Board board = null;
		String sql = "SELECT writer, pw, title, content FROM board WHERE num=? AND searchPos=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardParam.getNum());
			pstmt.setInt(2, boardParam.getSearchPos());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				if (board == null) {
					board = new Board();
				}
				board.setWriter(rs.getString("writer"));
				board.setPw(rs.getString("pw"));
				board.setTitle(rs.getString("title"));
				board.setContent(rs.getString("content"));
			}

		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 selectboardContent2()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return board;
	}

	// 게시물 수정
	public int modifyBoard(Board board) {
		int result = 0;
		String sql = "UPDATE board SET title=?, content=? WHERE num=? AND writer=? AND pw=? AND searchPos=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, board.getTitle());
			pstmt.setString(2, board.getContent());
			pstmt.setInt(3, board.getNum());
			pstmt.setString(4, board.getWriter());
			pstmt.setString(5, board.getPw());
			pstmt.setInt(6, board.getSearchPos());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 modifyBoard()에서 예외 발생 : " + e);
		} finally {
			close(pstmt);
		}

		return result;
	}

	// 조회수 증가
	public int updateBoardViews(int num, int searchPos) {
		int result = 0;
		String sql = "UPDATE board SET views=views+1 WHERE num=? AND searchPos=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, num);
			pstmt.setInt(2, searchPos);

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 updateBoardViews()에서 예외 발생 : " + e);
		} finally {
			close(pstmt);
		}

		return result;
	}

	// searchPos 최대값 가져오기.
	public int selectBoardMaxSearchPos() {
		int maxSearchPos = -1;
		String sql = "SELECT MAX(searchPos) AS searchPos  FROM board";

		try {
			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				maxSearchPos = rs.getInt("searchPos");
			}

		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 selectBoardMaxSearchPos()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return maxSearchPos;
	}

	// index.jsp에 보여줄 클라이언트가 '검색한' 게시물들(번호, 제목, 작성자, 작성일, 조회수) 가져오기.
	public ArrayList<Board> SelectBoardSearchMainPageView(String type, String keyword, int searchPos, int startIndex,
			int endIndex) {
		ArrayList<Board> boardList = null;
		String searchKeyword = "%" + keyword + "%";

		String sql = "SELECT * FROM (SELECT num, title, writer, createdDate, views, content,"
				+ " ROW_NUMBER() OVER (ORDER BY num DESC) AS subRowNum FROM board WHERE";

		switch (type) {
		case "titleOrContent":
			sql += " (title LIKE ? OR content LIKE ?) AND searchPos=?";
			break;
		case "title":
			sql += " title LIKE ? AND searchPos=?";
			break;
		case "content":
			sql += " content LIKE ? AND searchPos=?";
			break;
		case "writer":
			sql += " writer LIKE ? AND searchPos=?";
			break;
		}

		sql += ") WHERE subRowNum BETWEEN ? AND ? ORDER BY subRowNum ASC";

		try {
			pstmt = con.prepareStatement(sql);

			int pstmtIndex = 1;

			if (type.equals("titleOrContent")) {
				pstmt.setString(pstmtIndex++, searchKeyword);
			}

			pstmt.setString(pstmtIndex++, searchKeyword);
			pstmt.setInt(pstmtIndex++, searchPos);
			pstmt.setInt(pstmtIndex++, startIndex);
			pstmt.setInt(pstmtIndex++, endIndex);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				if (boardList == null) {
					boardList = new ArrayList<>();
				}

				Board board = new Board();

				board.setNum(rs.getInt("num"));
				board.setTitle(rs.getString("title"));
				board.setWriter(rs.getString("writer"));
				board.setCreatedDate(rs.getTimestamp("createdDate"));
				board.setViews(rs.getInt("views"));
				board.setContent(rs.getString("content"));

				boardList.add(board);
			}

		} catch (Exception e) {
			System.out.println("BoardDAO클래스의 SelectBoardSearchMainPageView()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return boardList;
	}

	// 검색한 게시물들 총 갯수(만개 단위에서 총 갯수)
	public int selectBoardSearchMaxNum(String type, String keyword, int searchPos) {
		int maxNum = 0;
		String searchKeyword = "%" + keyword + "%";

		String sql = "SELECT COUNT(num) AS maxNum FROM board WHERE";

		switch (type) {
		case "titleOrContent":
			sql += " (title LIKE ? OR content LIKE ?) AND searchPos=?";
			break;
		case "title":
			sql += " title LIKE ? AND searchPos=?";
			break;
		case "content":
			sql += " content LIKE ? AND searchPos=?";
			break;
		case "writer":
			sql += " writer LIKE ? AND searchPos=?";
			break;
		}

		try {
			pstmt = con.prepareStatement(sql);

			int pstmtIndex = 1;

			if (type.equals("titleOrContent")) {
				pstmt.setString(pstmtIndex++, searchKeyword);
			}

			pstmt.setString(pstmtIndex++, searchKeyword);
			pstmt.setInt(pstmtIndex++, searchPos);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				maxNum = rs.getInt("maxNum");
			}

		} catch (Exception e) {
			System.out.println("BoardDAO 클래스의 selectBoardSearchMaxNum()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return maxNum;
	}

}
