package svc.comment;

import java.sql.Connection;

import dao.CommentDAO;

import static db.JdbcUtil.*;

public class CountCommentService {

	public int countComment(int boardNum) {
		Connection con = getConnection();
		CommentDAO commentDAO = CommentDAO.getInstance();
		commentDAO.setConnection(con);

		int count = commentDAO.countComment(boardNum);

		close(con);

		return count;
	}

}
