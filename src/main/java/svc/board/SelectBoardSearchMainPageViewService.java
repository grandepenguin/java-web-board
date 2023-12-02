package svc.board;

import java.sql.Connection;
import java.util.ArrayList;

import dao.BoardDAO;

import static db.JdbcUtil.*;

import vo.Board;

public class SelectBoardSearchMainPageViewService {

	public ArrayList<Board> SelectBoardSearchMainPageView(String type, String keyword, int searchPos, int startIndex,
			int endIndex) {
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);

		ArrayList<Board> boardList = boardDAO.SelectBoardSearchMainPageView(type, keyword, searchPos, startIndex,
				endIndex);

		close(con);

		return boardList;
	}

}
