package svc.comment;

import java.sql.Connection;

import dao.CommentDAO;

import static db.JdbcUtil.*;

import vo.BoardComment;

public class InsertCommentService {

	public boolean insertComment(BoardComment boardComment) {
		Connection con = getConnection();
		CommentDAO commentDAO = CommentDAO.getInstance();
		commentDAO.setConnection(con);

		int result = commentDAO.insertComment(boardComment);
		boolean insertSuccess = false;

		if (result > 0) {
			insertSuccess = true;
			commit(con);
		} else {
			rollback(con);
		}

		close(con);

		return insertSuccess;
	}

}
