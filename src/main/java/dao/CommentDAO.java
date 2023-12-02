package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static db.JdbcUtil.*;

import vo.BoardComment;

public class CommentDAO {

	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private static CommentDAO commentDAO = null;

	public static CommentDAO getInstance() {
		if (commentDAO == null) {
			commentDAO = new CommentDAO();
		}
		return commentDAO;
	}

	public void setConnection(Connection con) {
		this.con = con;
	}

	/** ----------------------------댓글 기능--------------------------------- */

	// 댓글 등록 (통합 메서드)
	public int insertComment(BoardComment boardComment) {
		int result = 0;

		if (boardComment.getCommentId() == 0) {
			// 댓글 등록
			result = addComment(boardComment);
		} else {
			// 대댓글 등록
			result = addReply(boardComment);
			if (result == 0) {
				return result;
			}

			// 대댓글 등록 후, boardNum, commentId가 같은 모든 댓글, 대댓글에 childNum 1 증가(정렬에 사용)
			result = increaseChildNum(boardComment);
		}

		return result;
	}

	// 댓글 등록(모듈)
	public int addComment(BoardComment boardComment) {
		int result = 0;
		String sql = "INSERT INTO boardComment(boardNum, commentId, refId, refOrder, writer, pw, content)"
				+ " VALUES(?, NVL((SELECT MAX(commentId) FROM boardComment WHERE boardNum=?),0)+1, ?, ?, ?, ?, ?)";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardComment.getBoardNum());
			pstmt.setInt(2, boardComment.getBoardNum());
			pstmt.setInt(3, boardComment.getRefId());
			pstmt.setInt(4, boardComment.getRefOrder());
			pstmt.setString(5, boardComment.getWriter());
			pstmt.setString(6, boardComment.getPw());
			pstmt.setString(7, boardComment.getContent());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("CommentDAO 클래스의 addComment()에서 예외 발생 : " + e);
		} finally {
			close(pstmt);
		}

		return result;
	}

	// 대댓글 등록(모듈)
	public int addReply(BoardComment boardComment) {
		int result = 0;
		String sql = "INSERT INTO boardComment(boardNum, commentId, refId, refOrder, childNum, writer, pw, content)"
				+ " VALUES(?, ?, ?, NVL((SELECT MAX(refOrder) FROM boardComment WHERE boardNum=? AND refId=?), 0)+1,"
				+ " (SELECT MAX(childNum) FROM boardComment WHERE boardNum=? AND commentId=? AND refId=0), ?, ?, ?)";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardComment.getBoardNum());
			pstmt.setInt(2, boardComment.getCommentId());
			pstmt.setInt(3, boardComment.getRefId());
			pstmt.setInt(4, boardComment.getBoardNum());
			pstmt.setInt(5, boardComment.getRefId());
			pstmt.setInt(6, boardComment.getBoardNum());
			pstmt.setInt(7, boardComment.getCommentId());
			pstmt.setString(8, boardComment.getWriter());
			pstmt.setString(9, boardComment.getPw());
			pstmt.setString(10, boardComment.getContent());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("CommentDAO 클래스의 addReply()에서 예외 발생 : " + e);
		} finally {
			close(pstmt);
		}

		return result;
	}

	// boardNum, commentId가 같은 모든 댓글, 대댓글에 childNum 1 증가(모듈)
	public int increaseChildNum(BoardComment boardComment) {
		int result = 0;
		String sql = "UPDATE boardComment SET childNum=childNum+1 WHERE boardNum=? AND commentId=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardComment.getBoardNum());
			pstmt.setInt(2, boardComment.getCommentId());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("CommentDAO 클래스의 increaseChildNum()에서 예외 발생 : " + e);
		} finally {
			close(pstmt);
		}

		return result;
	}

	// 댓글 수
	public int countComment(int boardNum) {
		int count = 0;
		String sql = "SELECT count(boardNum) AS count FROM boardComment WHERE boardNum=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardNum);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt("count");
			}

		} catch (Exception e) {
			System.out.println("CommentDAO 클래스의 countComment()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return count;
	}

	// 댓글 출력
	public ArrayList<BoardComment> selectCommentMainPageView(int boardNum, int startIndex, int endIndex,
			String sortingOption) {
		ArrayList<BoardComment> boardCommentList = null;
		String sql = null;

		if (sortingOption.equals("register")) {// 등록순
			sql = "SELECT * FROM (SELECT boardNum, commentId, refId, refOrder, childNum, isDelete, writer, content, createdDate,"
					+ " ROW_NUMBER() OVER (ORDER BY commentId ASC, refId ASC, refOrder ASC) AS subRowNum"
					+ " FROM boardComment WHERE boardNum=?) WHERE subRowNum BETWEEN ? AND ? ORDER BY subRowNum ASC";
		}
		if (sortingOption.equals("latest")) {// 최신순
			sql = "SELECT * FROM (SELECT boardNum, commentId, refId, refOrder, childNum, isDelete, writer, content, createdDate,"
					+ " ROW_NUMBER() OVER (ORDER BY commentId DESC, refId ASC, refOrder ASC) AS subRowNum"
					+ " FROM boardComment WHERE boardNum=?) WHERE subRowNum BETWEEN ? AND ? ORDER BY subRowNum ASC";
		}
		if (sortingOption.equals("reply")) {// 답글순
			sql = "SELECT * FROM (SELECT boardNum, commentId, refId, refOrder, childNum, isDelete, writer, content, createdDate,"
					+ " ROW_NUMBER() OVER (ORDER BY childNum DESC, commentId ASC, refId ASC, refOrder ASC) AS subRowNum"
					+ " FROM boardComment WHERE boardNum=?) WHERE subRowNum BETWEEN ? AND ? ORDER BY subRowNum ASC";
		}

		LocalDateTime currentDateTime = LocalDateTime.now();// 오늘 날짜
		DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy");// 년도 비교용
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MM.dd HH:mm:ss");
		DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

		String dateStr1;
		String dateStr2;
		String dateStr3;

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardNum);
			pstmt.setInt(2, startIndex);
			pstmt.setInt(3, endIndex);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				if (boardCommentList == null) {
					boardCommentList = new ArrayList<>();
				}

				BoardComment boardComment = new BoardComment();

				boardComment.setBoardNum(rs.getInt("boardNum"));
				boardComment.setCommentId(rs.getInt("commentId"));
				boardComment.setRefId(rs.getInt("refId"));
				boardComment.setRefOrder(rs.getInt("refOrder"));
				boardComment.setChildNum(rs.getInt("childNum"));
				boardComment.setIsDelete(rs.getString("isDelete"));
				boardComment.setWriter(rs.getString("writer"));
				boardComment.setContent(rs.getString("content"));

				LocalDateTime createdDateTime = rs.getTimestamp("createdDate").toLocalDateTime();
				dateStr1 = createdDateTime.format(dtf1);

				if (dateStr1.equals(currentDateTime.format(dtf1))) {
					// 이번 년도면 "MM.dd HH:mm:ss"
					dateStr2 = createdDateTime.format(dtf2);
					boardComment.setCreatedDate(dateStr2);
				} else {
					// 다른 년도면 "yyyy.MM.dd HH:mm:ss"
					dateStr3 = createdDateTime.format(dtf3);
					boardComment.setCreatedDate(dateStr3);
				}

				boardCommentList.add(boardComment);
			}

		} catch (Exception e) {
			System.out.println("CommentDAO 클래스의 selectCommentMainPageView()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return boardCommentList;
	}

	// 댓글 삭제할 때 비밀번호 확인.
	public String checkCommentPw(BoardComment boardComment) {
		String pw = null;
		String sql = "SELECT pw FROM boardComment WHERE boardNum=? AND commentId=? AND refId=? AND reforder=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardComment.getBoardNum());
			pstmt.setInt(2, boardComment.getCommentId());
			pstmt.setInt(3, boardComment.getRefId());
			pstmt.setInt(4, boardComment.getRefOrder());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				pw = rs.getString("pw");
			}

		} catch (Exception e) {
			System.out.println("CommentDAO 클래스의 checkCommentPw()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return pw;
	}

	// 댓글, 대댓글 삭제 (통합 메서드)
	public int deleteComment(BoardComment boardComment) {
		int result = 0;

		// 부모 댓글, 자식 댓글 구분.
		if (boardComment.getRefId() > 0) {// 자식 댓글
			// 자식 댓글 삭제
			result = removeComment(boardComment);
			if (result == 0) {
				return result;
			}

			// 자식 댓글 삭제 후, boardNum, commentId가 같은 모든 댓글, 대댓글에 childNum 1 감소
			result = decreaseChildNum(boardComment);
			if (result == 0) {
				return result;
			}

			// 부모 댓글 childNum, isDelete 조회
			BoardComment parentBoardComment = selectCommentChildNumAndIsDelete(boardComment);

			// 부모 댓글의 childNum이 0이고 isDelete가 'Y'이면 부모 댓글도 삭제
			if (parentBoardComment.getChildNum() == 0 && parentBoardComment.getIsDelete().equals("Y")) {

				parentBoardComment.setBoardNum(boardComment.getBoardNum());
				parentBoardComment.setCommentId(boardComment.getCommentId());
				parentBoardComment.setRefId(0);
				parentBoardComment.setRefOrder(0);

				result = removeComment(parentBoardComment);
			}

		} else {// 부모 댓글
			// 자식 댓글이 있다면 isDelete를 'Y'로 업데이트
			if (boardComment.getChildNum() > 0) {
				result = updateIsDelete(boardComment);
			} else {
				// 부모 댓글 삭제
				result = removeComment(boardComment);
			}

		}

		return result;
	}

	// 댓글 삭제(모듈)
	public int removeComment(BoardComment boardComment) {
		int result = 0;
		String sql = "DELETE FROM boardComment WHERE boardNum=? AND commentId=? AND refId=? AND refOrder=?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardComment.getBoardNum());
			pstmt.setInt(2, boardComment.getCommentId());
			pstmt.setInt(3, boardComment.getRefId());
			pstmt.setInt(4, boardComment.getRefOrder());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("CommentDAO 클래스의 removeComment()에서 예외 발생 : " + e);
		} finally {
			close(pstmt);
		}

		return result;
	}

	// boardNum, commentId가 같은 모든 댓글, 대댓글에 childNum 1 감소(모듈)
	public int decreaseChildNum(BoardComment boardComment) {
		int result = 0;
		String sql = "UPDATE boardComment SET childNum=childNum-1 WHERE boardNum=? AND commentId=?";
		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardComment.getBoardNum());
			pstmt.setInt(2, boardComment.getCommentId());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("CommentDAO 클래스의 decreaseChildNum()에서 예외 발생 : " + e);
		} finally {
			close(pstmt);
		}

		return result;
	}

	// 부모 댓글 childNum, isDelete 조회(모듈)
	public BoardComment selectCommentChildNumAndIsDelete(BoardComment boardComment) {
		BoardComment parentBoardComment = null;
		String sql = "SELECT childNum, isDelete FROM boardComment WHERE boardNum=? AND commentId=? AND refId=0";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardComment.getBoardNum());
			pstmt.setInt(2, boardComment.getCommentId());

			rs = pstmt.executeQuery();

			if (rs.next()) {

				if (parentBoardComment == null) {
					parentBoardComment = new BoardComment();
				}

				parentBoardComment.setChildNum(rs.getInt("childNum"));
				parentBoardComment.setIsDelete(rs.getString("isDelete"));

			}

		} catch (Exception e) {
			System.out.println("CommentDAO 클래스의 selectCommentChildNumAndIsDelete()에서 예외 발생 : " + e);
		} finally {
			close(rs);
			close(pstmt);
		}

		return parentBoardComment;
	}

	// 부모 댓글 isDelete를 'Y'로 업데이트(모듈)
	public int updateIsDelete(BoardComment boardComment) {
		int result = 0;
		String sql = "UPDATE boardComment SET isDelete='Y' WHERE boardNum=? AND commentId=? AND refId=0";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, boardComment.getBoardNum());
			pstmt.setInt(2, boardComment.getCommentId());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("CommentDAO 클래스의 updateIsDelete()에서 예외 발생 : " + e);
		} finally {
			close(pstmt);
		}

		return result;
	}

}
