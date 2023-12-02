package svc.comment;

import java.sql.Connection;

import dao.CommentDAO;

import static db.JdbcUtil.*;

import vo.BoardComment;

public class DeleteCommentService {

	public boolean deleteComment(BoardComment boardComment) {
		Connection con = getConnection();
		CommentDAO commentDAO = CommentDAO.getInstance();
		commentDAO.setConnection(con);

		int result = commentDAO.deleteComment(boardComment);
		boolean deleteSuccess = false;

		if (result > 0) {
			deleteSuccess = true;
			commit(con);
		} else {
			rollback(con);
		}

		close(con);

		return deleteSuccess;
	}

}
