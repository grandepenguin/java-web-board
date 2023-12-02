package action.comment;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import action.Action;
import svc.comment.CountCommentService;
import svc.comment.InsertCommentService;
import svc.comment.SelectCommentMainPageViewService;
import vo.ActionForward;
import vo.BoardComment;

public class InsertCommentAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 요청 데이터를 reader에 대입
		BufferedReader reader = request.getReader();

		StringBuffer jsonBuffer = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			jsonBuffer.append(line);
		}

		// 외부라이브러리 구글의 Gson사용
		JsonObject jsonObject = new Gson().fromJson(jsonBuffer.toString(), JsonObject.class);

		String sortingOption;
		int boardNum = 0;
		int startIndex = 0;
		int endIndex = 0;
		int commentId = 0;
		int refId = 0;
		int refOrder = 0;// refOrder는 무조건 0으로 셋팅(try 블록에서 값 설정 안함.)
		String writer;
		String pw;
		String content;

		try {
			sortingOption = jsonObject.get("sortingOption").getAsString();
			if (!sortingOption.equals("register") && !sortingOption.equals("latest")
					&& !sortingOption.equals("reply")) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("{\"sortingOptionError\" : \"sortingOption이 지정된 값(등록순 or 최신순 or 답글순)이 아닙니다.\"}");
				return null;
			}

			boardNum = jsonObject.get("num").getAsInt();
			startIndex = jsonObject.get("startIndex").getAsInt();
			endIndex = jsonObject.get("endIndex").getAsInt();
			commentId = jsonObject.get("commentId").getAsInt();
			refId = jsonObject.get("refId").getAsInt();

			if (boardNum <= 0 || startIndex <= 0 || endIndex <= 0 || commentId < 0 || refId < 0) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();

				if (boardNum <= 0) {
					out.print("{\"numError\" : \"boardNum이 0보다 작거나 같습니다.\"}");
				} else if (startIndex <= 0) {
					out.print("{\"numError\" : \"startIndex가 0보다 작거나 같습니다.\"}");
				} else if (endIndex <= 0) {
					out.print("{\"numError\" : \"endIndex가 0보다 작거나 같습니다.\"}");
				} else if (commentId < 0) {
					// commentId는 0보다 작게 설정한 이유: 댓글 등록에서 commentId=0으로 셋팅해서 요청 함. sql에서 값 설정.
					out.print("{\"numError\" : \"commentId가 0보다 작습니다.\"}");
				} else if (refId < 0) {
					out.print("{\"numError\" : \"refId가 0보다 작습니다.\"}");
				}

				return null;
			}

			writer = jsonObject.get("writer").getAsString();
			pw = jsonObject.get("pw").getAsString();

			if (pw.startsWith(" ") || pw.endsWith(" ")) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("{\"pwError\" : \"[비밀번호] 항목은 처음과 마지막에 공백을 사용할 수 없습니다.\"}");
				return null;
			}

			content = jsonObject.get("content").getAsString();

			if (writer.equals("") || pw.equals("") || content.equals("")) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				if (writer.equals("")) {
					out.print("{\"writerError\" : \"닉네임을 입력하세요.\"}");
				} else if (pw.equals("")) {
					out.print("{\"pwError\" : \"비밀번호를 입력하세요.\"}");
				} else if (content.equals("")) {
					out.print("{\"contentError\" : \"내용을 입력하세요.\"}");
				}
				return null;
			}
		} catch (Exception e) {
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("{\"jsonObjectError\" : \"jsonObject객체 필드 추출 예외 발생\"}");
			return null;
		}

		// 댓글 객체 생성
		BoardComment boardComment = new BoardComment();

		boardComment.setBoardNum(boardNum);
		boardComment.setCommentId(commentId);
		boardComment.setRefId(refId);
		boardComment.setRefOrder(refOrder);
		boardComment.setWriter(writer);
		boardComment.setPw(pw);
		boardComment.setContent(content);

		// 댓글 등록
		InsertCommentService insertCommentService = new InsertCommentService();
		boolean insertSuccess = insertCommentService.insertComment(boardComment);

		if (insertSuccess == false) {
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("{\"insertError\" : \"댓글 등록 실패.\"}");
			return null;
		}

		// --------------여기까지 댓글, 대댓글 등록

		// 전체 댓글 갯수
		CountCommentService countCommentService = new CountCommentService();
		int commentCount = countCommentService.countComment(boardNum);

		// 댓글 출력, ArrayList<BoardComment> boardCommentList에 대입해 json객체로 응답
		SelectCommentMainPageViewService selectCommentMainPageViewService = new SelectCommentMainPageViewService();
		ArrayList<BoardComment> boardCommentList = selectCommentMainPageViewService.selectCommentMainPageView(boardNum,
				startIndex, endIndex, sortingOption);

		// combinedJson에 제이슨 객체 담아서 응답
		JsonObject combinedJson = new JsonObject();

		try {
			combinedJson.addProperty("commentCount", commentCount);

			String jsonArray = new Gson().toJson(boardCommentList);

			combinedJson.addProperty("jsonArray", jsonArray);
			combinedJson.addProperty("startIndex", startIndex);
			combinedJson.addProperty("endIndex", endIndex);
			combinedJson.addProperty("boardNum", boardNum);

		} catch (Exception e) {
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("{\"jsonParsingError\" : \"제이슨 변환 예외 발생\"}");
			return null;
		}

		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(combinedJson);

		return null;
	}

}
