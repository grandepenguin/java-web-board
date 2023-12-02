package action.board;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import svc.board.SelectBoardMaxSearchPos;
import vo.ActionForward;

public class SearchBoardAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward forward = null;

		String type = request.getParameter("searchOption");
		if (!type.equals("titleOrContent") && !type.equals("title") && !type.equals("content")
				&& !type.equals("writer")) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("<script>");
			out.print("alert('type이 지정된 값(제목+내용, 제목, 내용, 닉네임)이 아닙니다.');");
			out.print("history.back();");
			out.print("</script>");
			return null;
		}

		String keyword = null;
		try {
			keyword = URLEncoder.encode(request.getParameter("keyword"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("<script>");
			out.print("alert('keyword 인코딩 오류 발생.');");
			out.print("history.back();");
			out.print("</script>");
			return null;
		}
		if (keyword == null) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("<script>");
			out.print("alert('keyword가 null입니다.');");
			out.print("history.back();");
			out.print("</script>");
			return null;
		}

		// board 테이블의 searchPos의 최대값
		SelectBoardMaxSearchPos selectBoardMaxSearchPos = new SelectBoardMaxSearchPos();
		int searchPos = selectBoardMaxSearchPos.selectBoardMaxSearchPos();

		if (searchPos < 0) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("<script>");
			out.print("alert('searchPos가 0보다 작습니다.');");
			out.print("history.back();");
			out.print("</script>");
			return null;
		}

		forward = new ActionForward("index.jsp?type=" + type + "&searchPos=" + searchPos + "&keyword=" + keyword, true);

		return forward;
	}

}
