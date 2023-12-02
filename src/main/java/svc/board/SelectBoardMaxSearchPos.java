package svc.board;

import java.sql.Connection;

import dao.BoardDAO;

import static db.JdbcUtil.*;

public class SelectBoardMaxSearchPos {

	public int selectBoardMaxSearchPos() {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		int maxSearchPos = boardDAO.selectBoardMaxSearchPos();

		close(con);

		return maxSearchPos;
	}

}
