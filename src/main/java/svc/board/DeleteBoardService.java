package svc.board;

import java.sql.Connection;

import dao.BoardDAO;
import vo.Board;

import static db.JdbcUtil.*;

public class DeleteBoardService {

	public boolean deleteBoard(Board board) {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		int result=boardDAO.deleteBoard(board);
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
