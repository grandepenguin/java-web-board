package svc.board;

import java.sql.Connection;

import dao.BoardDAO;

import static db.JdbcUtil.*;

public class SelectBoardMaxNumService {

	public int selectBoardMaxNum() {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		int maxNum = boardDAO.selectBoardMaxNum();

		close(con);

		return maxNum;
	}

}
