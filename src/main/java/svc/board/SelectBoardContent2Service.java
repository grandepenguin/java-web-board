package svc.board;

import java.sql.Connection;

import dao.BoardDAO;

import static db.JdbcUtil.*;

import vo.Board;

public class SelectBoardContent2Service {

	public Board selectboardContent2(Board boardParam) {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		Board board = boardDAO.selectboardContent2(boardParam);

		close(con);

		return board;
	}

}
