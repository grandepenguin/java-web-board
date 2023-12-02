package svc.board;

import java.sql.Connection;

import dao.BoardDAO;

import static db.JdbcUtil.*;

import vo.Board;

public class SelectBoardNumService {

	public int selectBoardNum(Board board) {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		int num = boardDAO.selectBoardNum(board);

		close(con);

		return num;
	}

}
