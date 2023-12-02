package action.comment;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import action.Action;
import svc.comment.CountCommentService;
import svc.comment.SelectCommentMainPageViewService;
import vo.ActionForward;
import vo.BoardComment;

public class SelectCommentAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int boardNum = 0;
		int commentPage = 0;
		String sortingOption = request.getParameter("sortingOption");

		if (sortingOption == null || !sortingOption.equals("register") && !sortingOption.equals("latest")
				&& !sortingOption.equals("reply")) {
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("{\"sortingOptionError\" : \"sortingOption이 지정된 값(등록순 or 최신순 or 답글순)이 아닙니다.\"}");
			return null;
		}

		try {
			boardNum = Integer.parseInt(request.getParameter("boardNum"));
			commentPage = Integer.parseInt(request.getParameter("commentPage"));

			if (boardNum <= 0 || commentPage <= 0) {
				response.setContentType("application/json; charset=UTF-8");
				PrintWriter out = response.getWriter();
				if (boardNum >= 0) {
					out.print("{\"numError\" : \"boardNum이 0보다 작거나 같습니다.\"}");
					return null;
				}
				if (commentPage >= 0) {
					out.print("{\"numError\" : \"commentPage이 0보다 작거나 같습니다.\"}");
					return null;
				}
			}

		} catch (NumberFormatException e) {
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("{\"numError\" : \"NumberFormatException 에러 발생.\"}");
			return null;
		}

		// 전체 댓글 갯수
		CountCommentService countCommentService = new CountCommentService();
		int commentCount = countCommentService.countComment(boardNum);

		int commentPageSize = 50;// 한 페이지에 보여줄 댓글 수
		int startIndex = (commentPage - 1) * commentPageSize + 1;
		int endIndex = startIndex + commentPageSize - 1;
		
		//댓글 출력
		SelectCommentMainPageViewService selectCommentMainPageViewService = new SelectCommentMainPageViewService();
		ArrayList<BoardComment> boardCommentList = selectCommentMainPageViewService.selectCommentMainPageView(boardNum,
				startIndex, endIndex, sortingOption);

		// combinedJson에 제이슨 객체 담아서 응답
		JsonObject combinedJson = new JsonObject();

		try {
			combinedJson.addProperty("commentCount", commentCount);// commentCount
			combinedJson.addProperty("commentPage", commentPage);// 페이지

			String jsonArray = new Gson().toJson(boardCommentList);

			combinedJson.addProperty("jsonArray", jsonArray);// jsonArray(댓글)
			combinedJson.addProperty("startIndex", startIndex);// startIndex
			combinedJson.addProperty("endIndex", endIndex);// endIndex
			combinedJson.addProperty("boardNum", boardNum);// boardNum

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
	}// execute method

}
