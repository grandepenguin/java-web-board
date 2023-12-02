package svc.board;

import java.sql.Connection;

import dao.BoardDAO;

import static db.JdbcUtil.*;

import vo.Board;

public class InsertBoardService {
	
	public boolean Insertboard(Board board) {
		Connection con=getConnection();
		BoardDAO boardDAO=BoardDAO.getInstance();
		boardDAO.setConnection(con);
		
		int result=boardDAO.Insertboard(board);
		boolean insertSuccess=false;
		
		if(result>0) {
			insertSuccess=true;
			commit(con);
		}else {
			rollback(con);
		}
		
		close(con);
		
		return insertSuccess;
	}
	
}
