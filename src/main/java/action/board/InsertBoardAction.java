package action.board;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import svc.board.InsertBoardService;
import vo.ActionForward;
import vo.Board;

public class InsertBoardAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward forward = null;

		String writer = request.getParameter("writer");
		String pw = request.getParameter("pw");
		// 비밀번호 앞 뒤 공백 허용 X
		if (pw.startsWith(" ") || pw.endsWith(" ")) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('[비밀번호] 항목은 처음과 마지막에 공백을 사용할 수 없습니다.');");
			out.println("history.back();");
			out.println("</script>");
			return null;
			/*
			 * 자바스크립트에서 똑같은 유효성 검사를 했지만 서버에서도 하는 이유 :
			 * 
			 * 보안 및 데이터 신뢰성을 고려할 때, 주로 서버 측에서 데이터의 유효성을 확인하는 것이 더 안전합니다. 사용자 입력값에 대한 모든 유효성
			 * 검사를 클라이언트 측(JavaScript)에서만 수행하는 것은 안전하지 않을 수 있습니다. 사용자는 브라우저에서 JavaScript 코드를
			 * 쉽게 조작하거나 우회할 수 있기 때문입니다. 따라서, 클라이언트 측에서는 사용자 경험을 개선하고 사용자에게 실시간 피드백을 제공하기 위해
			 * 유효성을 간단하게 검사할 수 있지만, 중요한 보안 검사나 데이터 신뢰성 검사는 반드시 서버 측에서 이루어져야 합니다.
			 */
		}

		String title = request.getParameter("title");
		String content = request.getParameter("content");

		Board board = new Board();

		board.setWriter(writer);
		board.setPw(pw);
		board.setTitle(title);
		board.setContent(content);

		InsertBoardService insertBoardService = new InsertBoardService();
		boolean insertSuccess = insertBoardService.Insertboard(board);

		if (insertSuccess == false) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('글쓰기 실패');");
			out.println("history.back();");
			out.println("</script>");
			return null;
		} else {
			forward = new ActionForward("../index.jsp", true);
		}

		return forward;
	}

}
