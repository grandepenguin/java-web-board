package svc.board;

import java.sql.Connection;

import dao.BoardDAO;

import static db.JdbcUtil.*;

public class UpdateBoardViewsService {

	public boolean updateBoardViews(int num) {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		int searchPos = num / 10000;
		int result = boardDAO.updateBoardViews(num, searchPos);
		boolean updateSuccess = false;

		if (result > 0) {
			updateSuccess = true;
			commit(con);
		} else {
			rollback(con);
		}

		close(con);

		return updateSuccess;
	}

}
