package svc.comment;

import java.sql.Connection;

import dao.CommentDAO;
import vo.BoardComment;

import static db.JdbcUtil.*;

public class CheckCommentPwService {

	public String checkCommentPw(BoardComment boardComment) {
		Connection con = getConnection();
		CommentDAO commentDAO = CommentDAO.getInstance();
		commentDAO.setConnection(con);

		String checkCommentPw = commentDAO.checkCommentPw(boardComment);

		close(con);

		return checkCommentPw;
	}

}
