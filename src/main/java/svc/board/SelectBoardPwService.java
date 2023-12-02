package svc.board;

import java.sql.Connection;

import dao.BoardDAO;
import vo.Board;

import static db.JdbcUtil.*;

public class SelectBoardPwService {

	public String selectBoardPw(Board board) {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		String pwCheck = boardDAO.selectBoardPw(board);

		close(con);

		return pwCheck;
	}

}
