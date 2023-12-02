package svc.board;

import java.sql.Connection;

import dao.BoardDAO;

import static db.JdbcUtil.*;

import vo.Board;

public class ModifyBoardService {

	public boolean modifyBoard(Board board) {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		int result = boardDAO.modifyBoard(board);
		boolean modifySuccess = false;

		if (result > 0) {
			modifySuccess = true;
			commit(con);
		} else {
			rollback(con);
		}

		close(con);

		return modifySuccess;
	}

}
