package svc.board;

import java.sql.Connection;
import java.util.ArrayList;

import dao.BoardDAO;

import static db.JdbcUtil.*;

import vo.Board;

public class SelectBoardMainPageViewService {
	
	public ArrayList<Board> selectBoardMainPageView(int searchPos, int startIndex, int endIndex) {
		Connection con=getConnection();
		BoardDAO boardDAO=BoardDAO.getInstance();
		boardDAO.setConnection(con);
		
		ArrayList<Board> boardList=boardDAO.selectBoardMainPageView(searchPos, startIndex, endIndex);
		
		close(con);
		
		return boardList;
	}
	
}
