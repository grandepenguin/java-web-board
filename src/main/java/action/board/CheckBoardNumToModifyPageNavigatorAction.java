package action.board;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import svc.board.SelectBoardNumService;
import vo.ActionForward;
import vo.Board;

public class CheckBoardNumToModifyPageNavigatorAction implements Action {

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
		} catch (Exception e) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('존재하지 않는 페이지입니다.');");
			out.println("history.back();");
			out.println("</script>");
			return null;
		}

		int searchPos = num / 10000;

		Board board = new Board();

		board.setNum(num);
		board.setSearchPos(searchPos);

		SelectBoardNumService selectBoardNumService = new SelectBoardNumService();
		int numCheck = selectBoardNumService.selectBoardNum(board);

		if (num != numCheck) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('존재하지 않는 페이지입니다.');");
			out.println("history.back();");
			out.println("</script>");
			return null;
		} else {
			forward = new ActionForward("modify.jsp?num=" + num, false);
		}

		return forward;
	}

}
