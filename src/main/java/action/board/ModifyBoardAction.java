package action.board;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import svc.board.ModifyBoardService;
import vo.ActionForward;
import vo.Board;

public class ModifyBoardAction implements Action {

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
			}
		} catch (NumberFormatException e) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('존재하지 않는 페이지입니다.');");
			out.println("history.back();");
			out.println("</script>");
		}

		String writer = request.getParameter("writer");
		String pw = request.getParameter("pw");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		int searchPos = num / 10000;

		Board board = new Board();

		board.setNum(num);
		board.setWriter(writer);
		board.setPw(pw);
		board.setTitle(title);
		board.setContent(content);
		board.setSearchPos(searchPos);

		ModifyBoardService modifyBoardService = new ModifyBoardService();
		boolean modifySuccess = modifyBoardService.modifyBoard(board);

		if (modifySuccess == false) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('글 수정 실패!');");
			out.println("history.back();");
			out.println("</script>");
			return null;
		} else {
			forward = new ActionForward("../index.jsp", true);
		}

		return forward;
	}

}
