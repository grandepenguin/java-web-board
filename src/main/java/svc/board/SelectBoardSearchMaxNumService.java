package svc.board;

import java.sql.Connection;

import dao.BoardDAO;

import static db.JdbcUtil.*;

public class SelectBoardSearchMaxNumService {

	public int selectBoardSearchMaxNum(String type, String keyword, int searchPos) {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		int maxNum = boardDAO.selectBoardSearchMaxNum(type, keyword, searchPos);

		close(con);

		return maxNum;
	}

}
