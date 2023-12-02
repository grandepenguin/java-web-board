package action.board;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import svc.board.DeleteBoardService;
import vo.ActionForward;
import vo.Board;

public class DeleteBoardAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward forward = null;

		int num = 0;
		try {
			num = Integer.parseInt(request.getParameter("num"));
			if (num <= 0) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('존재하지 않는 페이지입니다.');");
				out.println("history.back();");
				out.println("</script>");
				return null;
			}
		} catch (NumberFormatException e) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('존재하지 않는 페이지입니다.');");
			out.println("history.back();");
			out.println("</script>");
			return null;
		}

		String pw = request.getParameter("pw");
		// num 1만부터 searchPos 1
		int searchPos = num / 10000;

		Board board = new Board();
		board.setNum(num);
		board.setPw(pw);
		board.setSearchPos(searchPos);

		DeleteBoardService deleteBoardService = new DeleteBoardService();
		boolean deleteSuccess = deleteBoardService.deleteBoard(board);

		if (deleteSuccess == false) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('비밀번호가 맞지 않습니다. 다시 시도해 주세요.');");
			out.println("history.back();");
			out.println("</script>");
			return null;
		} else {
			forward = new ActionForward("../index.jsp", true);
		}

		return forward;
	}

}
