package action.comment;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import action.Action;
import svc.comment.CheckCommentPwService;
import svc.comment.CountCommentService;
import svc.comment.DeleteCommentService;
import svc.comment.SelectCommentMainPageViewService;
import vo.ActionForward;
import vo.BoardComment;

public class DeleteCommentAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		BufferedReader reader = request.getReader();

		StringBuffer jsonBuffer = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			jsonBuffer.append(line);
		}

		// 외부라이브러리 구글의 Gson 사용
		JsonObject jsonObject = new Gson().fromJson(jsonBuffer.toString(), JsonObject.class);

		String sortingOption;
		String pw;
		int boardNum = 0;
		int commentId = 0;
		int refId = 0;
		int refOrder = 0;
		int childNum = 0;

		try {
			sortingOption = jsonObject.get("sortingOption").getAsString();
			if (!sortingOption.equals("register") && !sortingOption.equals("latest")
					&& !sortingOption.equals("reply")) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("{\"sortingOptionError\" : \"sortingOption이 지정된 값(등록순 or 최신순 or 답글순)이 아닙니다.\"}");
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

			boardNum = jsonObject.get("boardNum").getAsInt();
			commentId = jsonObject.get("commentId").getAsInt();
			refId = jsonObject.get("refId").getAsInt();
			refOrder = jsonObject.get("refOrder").getAsInt();
			childNum = jsonObject.get("childNum").getAsInt();

			if (boardNum <= 0 || commentId <= 0 || refId < 0 || refOrder < 0 || childNum < 0) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				if (boardNum <= 0) {
					out.print("{\"numError\" : \"boardNum이 0보다 작거나 같습니다.\"}");
				} else if (commentId <= 0) {
					out.print("{\"numError\" : \"commentId이 0보다 작거나 같습니다.\"}");
				} else if (refId < 0) {
					out.print("{\"numError\" : \"refId이 0보다 작습니다.\"}");
				} else if (refOrder < 0) {
					out.print("{\"numError\" : \"refOrder이 0보다 작습니다.\"}");
				} else if (childNum < 0) {
					out.print("{\"numError\" : \"childNum이 0보다 작습니다.\"}");
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

		boardComment.setPw(pw);
		boardComment.setBoardNum(boardNum);
		boardComment.setCommentId(commentId);
		boardComment.setRefId(refId);
		boardComment.setRefOrder(refOrder);
		boardComment.setChildNum(childNum);

		// 비밀번호 확인.
		CheckCommentPwService checkCommentPwService = new CheckCommentPwService();

		String checkCommentPw = checkCommentPwService.checkCommentPw(boardComment);

		if (!pw.equals(checkCommentPw)) {
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("{\"pwMismatch\" : \"비밀번호가 틀립니다.\"}");
			return null;
		}

		// 댓글 삭제
		DeleteCommentService deleteCommentService = new DeleteCommentService();

		boolean deleteSuccess = deleteCommentService.deleteComment(boardComment);

		if (deleteSuccess == false) {
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("{\"deleteFalse\" : \"댓글 삭제 실패.\"}");
			return null;
		}

		// --------------여기까지 댓글, 대댓글 삭제

		// 전체 댓글 갯수
		CountCommentService countCommentService = new CountCommentService();
		int commentCount = countCommentService.countComment(boardNum);

		// 댓글 삭제 후, 댓글 1페이지 출력
		int commentPage = 1;// 시작 페이지 1

		// 한 페이지에 보여줄 댓글 수 SELECT 범위 계산
		// commentPage 값에 따라 startIndex, endIndex 계산
		// startIndex, endIndex로 SQL검색 범위 지정
		// currentCommentPage 1이면 1~50, 2이면 51~100, 3이면 101~150
		int commentPageSize = 50;// 한 페이지에 보여줄 댓글 수
		int startIndex = (commentPage - 1) * commentPageSize + 1;
		int endIndex = startIndex + commentPageSize - 1;

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
