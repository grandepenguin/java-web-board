package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import action.board.DeleteBoardAction;
import action.board.InsertBoardAction;
import action.board.ModifyBoardAction;
import action.board.SearchBoardAction;
import action.board.CheckBoardNumToModifyPageNavigatorAction;
import action.board.CheckBoardPwToModifyAction;
import vo.ActionForward;

/**
 * Servlet implementation class UserFrontController
 */
@WebServlet("*.board")
public class BoardFrontController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BoardFrontController() {
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

		/** 게시판 기능--------------------------------------------------------------- */
		// 글 쓰기
		if (command.equals("/board/insert.board")) {
			action = new InsertBoardAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				System.out.println("BoardFrontController 클래스의 /board/insert.board에서 예외 발생 : " + e);
			}
		}
		// 게시물 번호 확인, 게시물이 있으면 글 수정(비밀번호 입력) 페이지 이동
		else if (command.equals("/board/modify.board")) {
			action = new CheckBoardNumToModifyPageNavigatorAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				System.out.println("BoardFrontController 클래스의 /board/modify.board에서 예외 발생 : " + e);
			}
		}
		// 비밀번호 확인, 비밀번호가 맞으면 게시판 수정할 수 있는 html(form)태그 보이도록 설정
		else if (command.equals("/board/checkBoardPwToModify.board")) {
			action = new CheckBoardPwToModifyAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				System.out.println("BoardFrontController 클래스의 /board/boardPwCheckModify.board에서 예외 발생 : " + e);
			}
		}
		// 글 수정
		else if (command.equals("/board/realModify.board")) {
			action = new ModifyBoardAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				System.out.println("BoardFrontController 클래스의 /board/realModify.board에서 예외 발생 : " + e);
			}
		}
		// 글 삭제
		else if (command.equals("/board/delete.board")) {
			action = new DeleteBoardAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				System.out.println("BoardFrontController 클래스의 /board/delete.board에서 예외 발생 : " + e);
			}
		}
		// 검색
		else if (command.equals("/search.board")) {
			action = new SearchBoardAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				System.out.println("BoardFrontController 클래스의 /search.board에서 예외 발생 : " + e);
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

}// UserFrontController class
