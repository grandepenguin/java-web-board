package action.board;

import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import action.Action;
import svc.board.SelectBoardContent2Service;
import svc.board.SelectBoardPwService;
import vo.ActionForward;
import vo.Board;

public class CheckBoardPwToModifyAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 요청 데이터를 reader에 대입
		BufferedReader reader = request.getReader();

		// StringBuffer 멀티 스레드, StringBuilder 단일 스레드
		StringBuffer jsonBuffer = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			jsonBuffer.append(line);
		}

		// 외부라이브러리 구글의 Gson사용
		JsonObject jsonObject = new Gson().fromJson(jsonBuffer.toString(), JsonObject.class);

		int num = 0;
		String pw = null;
		try {
			num = jsonObject.get("num").getAsInt();
			if (num <= 0) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("{\"numError\" : \"존재하지 않는 페이지입니다.\"}");
				return null;
			}
			pw = jsonObject.get("pw").getAsString();
			if (pw.equals("")) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("{\"pwError\" : \"비밀번호를 입력하세요.\"}");
				return null;
			} else if (pw.startsWith(" ") || pw.endsWith(" ")) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("{\"pwError\" : \"[비밀번호] 항목은 처음과 마지막에 공백을 사용할 수 없습니다.\"}");
				return null;
			}
		} catch (Exception e) {
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("{\"jsonObjectError\" : \"jsonObject객체 필드 추출 예외 발생\"}");
			return null;
		}

		int searchPos = num / 10000;

		Board board = new Board();

		board.setNum(num);
		board.setPw(pw);
		board.setSearchPos(searchPos);

		SelectBoardPwService selectBoardPwService = new SelectBoardPwService();

		String pwCheck = selectBoardPwService.selectBoardPw(board);

		if (pwCheck == null || !pw.equals(pwCheck)) {
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("{\"pwFalse\" : \"비밀번호가 맞지 않습니다. 다시 시도해 주세요.\"}");
			return null;
		}

		SelectBoardContent2Service boardSelectContent2Service = new SelectBoardContent2Service();
		board = boardSelectContent2Service.selectboardContent2(board);

		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print("{\"writer\" : \"" + board.getWriter() + "\", \"pw\" : \"" + board.getPw() + "\", \"title\" : \""
				+ board.getTitle() + "\", \"content\" : \"" + board.getContent() + "\"}");

		return null;
	}

}
