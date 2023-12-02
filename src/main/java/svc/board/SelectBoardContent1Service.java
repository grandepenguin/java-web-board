package svc.board;

import java.sql.Connection;

import dao.BoardDAO;

import static db.JdbcUtil.*;

import vo.Board;

public class SelectBoardContent1Service {

	public Board selectBoardContent1(int num) {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		int searchPos = num / 10000;
		Board board = boardDAO.selectBoardContent1(num, searchPos);

		close(con);

		return board;
	}

}
