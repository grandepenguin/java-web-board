package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import action.comment.DeleteCommentAction;
import action.comment.InsertCommentAction;
import action.comment.SelectCommentAction;
import vo.ActionForward;

/**
 * Servlet implementation class UserFrontController
 */
@WebServlet("*.comment")
public class CommentFrontController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CommentFrontController() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcess(request, response);
	}

	protected void doProcess(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = requestURI.substring(contextPath.length());

		//System.out.println(requestURI);
		//System.out.println(contextPath);
		//System.out.println(command);

		Action action = null;
		ActionForward forward = null;

		/** 댓글 기능--------------------------------------------------------------- */
		// 댓글 쓰기
		if (command.equals("/insertComment.comment")) {
			action = new InsertCommentAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				System.out.println("CommentFrontController 클래스의 /insertComment.comment에서 예외 발생 : " + e);
			}
		}
		// 댓글 출력
		else if (command.equals("/selectComment.comment")) {
			action = new SelectCommentAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				System.out.println("CommentFrontController 클래스의 /selectComment.comment에서 예외 발생 : " + e);
			}
		}
		// 댓글 삭제
		else if (command.equals("/deleteComment.comment")) {
			action = new DeleteCommentAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				System.out.println("CommentFrontController 클래스의 /deletecomment.comment에서 예외 발생 : " + e);
			}
		}

		/** 페이지 이동--------------------------------------------------------------- */
		if (forward != null) {
			if (forward.isRedirect() == true) {
				response.sendRedirect(forward.getPath());
			} else {
				request.getRequestDispatcher(forward.getPath()).forward(request, response);
			}
		}

	}// doPorocess method

}// CommentFrontController class
