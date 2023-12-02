package svc.comment;

import java.sql.Connection;
import java.util.ArrayList;

import dao.CommentDAO;

import static db.JdbcUtil.*;

import vo.BoardComment;

public class SelectCommentMainPageViewService {

	public ArrayList<BoardComment> selectCommentMainPageView(int boardNum, int startIndex, int endIndex, String sortingOption) {
		Connection con = getConnection();
		CommentDAO commentDAO = CommentDAO.getInstance();
		commentDAO.setConnection(con);

		ArrayList<BoardComment> boardCommentList = commentDAO.selectCommentMainPageView(boardNum, startIndex, endIndex, sortingOption);

		close(con);

		return boardCommentList;
	}

}
