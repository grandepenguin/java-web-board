<%@page import="java.io.UnsupportedEncodingException"%>
<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.util.ArrayList"%>

<%@page import="svc.board.*"%>
<%@page import="svc.comment.SelectCommentMainPageViewService"%>
<%@page import="svc.comment.CountCommentService"%>
<%@page import="vo.Board"%>
<%@page import="vo.BoardComment"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>게시판</title>
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/index.css">
</head>
<body>
	<div class="container mt-5 mb-5">
		<!-- 헤더 -->
		<div class="row mb-5">
			<div class="col-1"></div>
			<div class="col-9 text-center">
				<header>
					<h2>
						<a href="index.jsp" style="text-decoration: none; color: black;">코더펭귄
							게시판</a>
					</h2>
				</header>
			</div>
		</div>
		
		<!-- 두번 호출 하지 않도록 미리 호출하는 클래스 -->
		<%
		//url로 페이지 이동 시 존재하지 않는 페이지에 사용, 게시물의 페이지네이션(총 페이지 링크 수)에 사용
				SelectBoardMaxNumService selectBoardMaxNumService = new SelectBoardMaxNumService();
				int maxNum = selectBoardMaxNumService.selectBoardMaxNum();//maxNum = 게시물 수
		%>
		
		<!-- 게시물 내용 -->
		<%
		String numParam = request.getParameter("num");//board테이블의 게시물 번호 num

		if (numParam != null) {
			int num;
			try {
				num = Integer.parseInt(numParam);
			} catch (Exception e) {
				out.println("<script>");
				out.println("alert('존재하지 않는 페이지입니다.');");
				out.println("location.href='index.jsp';");
				out.println("</script>");
				return;
			}
			
			//url 이동 시, 존재하지 않는 페이지
			if(num > maxNum || num < 0){
				out.println("<script>");
				out.println("alert('존재하지 않는 페이지입니다.');");
				out.println("location.href='index.jsp';");
				out.println("</script>");
				return;
			}
			
			SelectBoardContent1Service selectBoardContent1Service = new SelectBoardContent1Service();
			Board board = selectBoardContent1Service.selectBoardContent1(num);

			if (board != null) {
				//조회수 증가
				UpdateBoardViewsService updateBoardViewsService = new UpdateBoardViewsService();
				boolean isSucess = updateBoardViewsService.updateBoardViews(num);

				if (isSucess == false) {
		%>
		<script>
			alert('조회수 오류 발생.');
			location.href = 'index.jsp';
		</script>
		<%
		}
		%>
		<!-- 선 -->
		<div id="boardInfo" class="row mb-3">
			<div class="col-1"></div>
			<div class="col-9 border-top border-2 border-primary"></div>
		</div>

		<!-- 제목 -->
		<div class="row">
			<div class="col-1"></div>
			<div class="col-9">
				<b><%=board.getTitle()%></b>
			</div>
		</div>

		<!-- 작성자, 작성일, 조회수 -->
		<div class="row">
			<div class="col-1"></div>
			<div class="col-4">
				<%=board.getWriter()%>&nbsp;|&nbsp;<%=board.getCreatedDate()%>
			</div>
			<div class="col-5 text-end">
				조회&nbsp;<%=board.getViews()%>
			</div>
		</div>

		<!-- 선 -->
		<div class="row mt-2 mb-2">
			<div class="col-1"></div>
			<div class="col-9 border-top border-1 border-secondary"></div>
		</div>

		<!-- 내용 -->
		<div class="row">
			<div class="col-1"></div>
			<div class="col-9" style="min-height: 300px; white-space: pre-wrap;"><%=board.getContent()%></div>
		</div>

		<!-- 전체 댓글, 정렬, 본문보기, 댓글닫기, 새로고침 -->
		<div id="commentInfo" class="row mb-3">
			<div class="col-1"></div>
			<div class="col-2">
				<b>전체 댓글 <span id="custom-commentCount" style="color: #FF5E00;">
						<%
						CountCommentService countCommentService = new CountCommentService();
						int commentCount = countCommentService.countComment(num);
						out.print(commentCount);
						%>
				</span> 개
				</b>
			</div>
			<div class="col-1">
				<select id="sortingOption" onchange="moveCommentPage(<%=num%>, 1)">
					<option value="register">등록순</option>
					<option value="latest">최신순</option>
					<option value="reply">답글순</option>
				</select>
			</div>
			<div class="col-3"></div>
			<div class="col-3">
				<a href="#boardInfo" style="text-decoration: none; color: black;"><b>본문
						보기</b></a> |&nbsp; <span onclick="" style="cursor: pointer;"><b
					id="toggleButton" onclick="toggleComment()">댓글 닫기▲</b></span> |&nbsp; <span
					onclick="moveCommentPage(<%=num%>, 1)" style="cursor: pointer;"><b>새로고침</b></span>
			</div>
		</div>

		<!-- 선 -->
		<div class="row mb-3">
			<div class="col-1"></div>
			<div class="col-9 border-bottom border-2 border-primary"></div>
		</div>

		<!-- 댓글 -->
		<div id="commentRow">
			<%
			//댓글 현재 페이지=commentPage
			int currentCommentPage = 1;//시작 페이지 1
			int commentPageSize = 50;//한 페이지에 보여줄 댓글 수

			//한 페이지에 보여줄 댓글 수 SELECT 범위 계산
			//currentCommentPage 값에 따라 startIndex, endIndex 계산
			//startIndex, endIndex로 SQL검색 범위 지정
			//currentCommentPage 1이면 1~50, 2이면 51~100, 3이면 101~150
			int startIndex = (currentCommentPage - 1) * commentPageSize + 1;
			int endIndex = startIndex + commentPageSize - 1;

			//writer, content, createdDate 담은 boardComment형태 ArrayList 가져오기
			SelectCommentMainPageViewService selectCommentMainPageViewService = new SelectCommentMainPageViewService();
			ArrayList<BoardComment> boardCommentList = selectCommentMainPageViewService.selectCommentMainPageView(num, startIndex,
					endIndex, "register");

			if (boardCommentList != null) {
			%>
			<div class="row mb-3">
				<div class="col-1"></div>
				<div class="col-9">
					<table class="table">
						<tbody>
							<%
							for (BoardComment boardComment : boardCommentList) {
								if (boardComment.getRefId() == 0) {//댓글
									if (boardComment.getIsDelete().equals("N")) {//대댓글이 있으면서 삭제가 안된 댓글
							%>
							<tr>
								<td class="custom-boardComment-writer_createdDate"><%=boardComment.getWriter()%></td>
								<td class="custom-boardComment-content" style="cursor: pointer;"
									onclick="toggleInsertSubComment(<%=boardComment.getCommentId()%>)"><%=boardComment.getContent()%></td>
								<td class="custom-boardComment-writer_createdDate"
									style="position: relative;"><%=boardComment.getCreatedDate()%>
									<span class="custom-span1"
									onclick="pwModal(<%=boardComment.getCommentId()%>, <%=boardComment.getRefId()%>, <%=boardComment.getRefOrder()%>)"
									style="cursor: pointer;">X</span>
									<div
										id="pwModal-<%=boardComment.getCommentId()%>-<%=boardComment.getRefId()%>-<%=boardComment.getRefOrder()%>">
										<div class="custom-input1-wrapper">
											<input
												id="deleteCommentInputPw-<%=boardComment.getCommentId()%>-<%=boardComment.getRefId()%>-<%=boardComment.getRefOrder()%>"
												class="custom-input1" type="password" placeholder="비밀번호">
											<div class="custom-checkAndX">
												<span class="custom-span2"
													onclick="deleteComment(<%=boardComment.getBoardNum()%>, <%=boardComment.getCommentId()%>, <%=boardComment.getRefId()%>, <%=boardComment.getRefOrder()%>, <%=boardComment.getChildNum()%>)">확인</span>
												<span class="custom-span3"
													onclick="pwModal(<%=boardComment.getCommentId()%>, <%=boardComment.getRefId()%>, <%=boardComment.getRefOrder()%>)">X</span>
											</div>
										</div>
									</div></td>
							</tr>
							<!-- 대댓글 쓰기, 댓글의 내용을 누르기 전에는 숨겨진 상태 -->
							<tr id="insertSubComment<%=boardComment.getCommentId()%>"
								class="table-secondary" style="display: none;">
								<td class="custom-pl-50px" colspan="3">
									<form action="insertComment.comment" method="post"
										name="f-comment-<%=boardComment.getCommentId()%>">
										<div class="row mb-3">
											<div class="col-2">
												<input type="hidden" name="num" value="<%=num%>"> <input
													type="hidden" name="startIndex" value="<%=startIndex%>">
												<input type="hidden" name="endIndex" value="<%=endIndex%>">
												<input type="hidden" name="commentId"
													value="<%=boardComment.getCommentId()%>"> <input
													type="hidden" name="refId"
													value="<%=boardComment.getCommentId()%>"> <input
													type="text"
													id="insertSubCommentInputWriter<%=boardComment.getCommentId()%>"
													class="form-control mb-2" name="writer" placeholder="닉네임"
													maxlength="20"> <input type="password"
													id="insertSubCommentInputPw<%=boardComment.getCommentId()%>"
													class="form-control" name="pw" placeholder="비밀번호"
													maxlength="20">
											</div>
											<div class="col-10">
												<textarea
													id="insertSubCommentTextarea<%=boardComment.getCommentId()%>"
													class="form-control" name="content"
													style="width: 100%; height: 100%; resize: none;"
													maxlength="400"></textarea>
												<small class="text-muted">최대 400자까지 입력 가능합니다.</small>
											</div>
										</div>

										<div class="row">
											<div class="col-9"></div>
											<div class="col-3">
												<button type="button" class="btn btn-primary"
													onclick="checkAndInsertComment(<%=boardComment.getCommentId()%>)">등록</button>
												<button type="reset" class="btn btn-secondary">다시
													쓰기</button>
											</div>
										</div>
									</form>
								</td>
							</tr>
							<%
							} else {//대댓글이 있으면서 삭제된 댓글
							%>
							<tr>
								<td colspan="3"><span class="custom-delete-comment">해당
										댓글은 삭제되었습니다.</span></td>
							</tr>
							<%
							}
							} else {//대댓글
							%>
							<tr class="table-secondary">
								<td
									class="custom-pl-50px custom-boardComment-writer_createdDate"><%=boardComment.getWriter()%></td>
								<td class="custom-pl-50px custom-boardComment-content">┗ <%=boardComment.getContent()%></td>
								<td class="custom-boardComment-writer_createdDate"
									style="position: relative;"><%=boardComment.getCreatedDate()%>
									<span class="custom-span1"
									onclick="pwModal(<%=boardComment.getCommentId()%>, <%=boardComment.getRefId()%>, <%=boardComment.getRefOrder()%>)"
									style="cursor: pointer;">X</span>
									<div
										id="pwModal-<%=boardComment.getCommentId()%>-<%=boardComment.getRefId()%>-<%=boardComment.getRefOrder()%>">
										<div class="custom-input1-wrapper">
											<input
												id="deleteCommentInputPw-<%=boardComment.getCommentId()%>-<%=boardComment.getRefId()%>-<%=boardComment.getRefOrder()%>"
												class="custom-input1" type="password" placeholder="비밀번호">
											<div class="custom-checkAndX">
												<span class="custom-span2"
													onclick="deleteComment(<%=boardComment.getBoardNum()%>, <%=boardComment.getCommentId()%>, <%=boardComment.getRefId()%>, <%=boardComment.getRefOrder()%>, <%=boardComment.getChildNum()%>)">확인</span>
												<span class="custom-span3"
													onclick="pwModal(<%=boardComment.getCommentId()%>, <%=boardComment.getRefId()%>, <%=boardComment.getRefOrder()%>)">X</span>
											</div>
										</div>
									</div></td>
							</tr>
							<%
							}
							}
							%>
						</tbody>
					</table>
				</div>
			</div>

			<!-- 댓글 페이지네이션 -->
			<%
			//댓글 총 페이지
			int totalCommentPageLink = (commentCount / commentPageSize) + 1;
			%>
			<div class="row mb-3">
				<div class="col-1"></div>
				<div class="col-9 text-center">
					<%
					//한 페이지에 보여줄 페이지 링크 수
					int commentPageLinkSize = 10;
					// 현재 페이지 범위 계산
					int startPage = ((currentCommentPage - 1) / commentPageLinkSize) * commentPageLinkSize + 1;
					int endPage = Math.min(startPage + commentPageLinkSize - 1, totalCommentPageLink);

					//이전 페이지 링크 생성
					if (currentCommentPage > commentPageLinkSize) {
					%>
					<span class="custom-commentPageLink"
						onclick="moveCommentPage(<%=num%>, <%=startPage - 1%>)">◁</span>
					<%
					}

					//페이지 링크 생성
					for (int i = startPage; i <= endPage; i++) {

					boolean isCurrentCommentPage = (i == currentCommentPage);

					if (isCurrentCommentPage) {//현재 페이지 링크는 onclick 속성 생성X, 글자: 빨강
					%>
					<span class="custom-currentCommentPageLink"><%=i%></span>
					<%
					} else {
					%>
					<span class="custom-commentPageLink"
						onclick="moveCommentPage(<%=num%>, <%=i%>)"><%=i%></span>
					<%
					}
					}

					//다른 페이지 링크 생성
					if (endPage < totalCommentPageLink) {
					%>
					<span class="custom-commentPageLink"
						onclick="moveCommentPage(<%=num%>, <%=endPage + 1%>)">▷</span>
					<%
					}
					%>
				</div>
			</div>

			<!-- 선 -->
			<div class="row mb-3">
				<div class="col-1"></div>
				<div class="col-9 border-bottom border-2 border-primary"></div>
			</div>

			<%
			} //if (boardCommentList != null)
			%>
		</div>

		<!-- 댓글 쓰기 -->
		<div class="row mb-3">
			<div class="col-1"></div>
			<div class="col-9">
				<form action="insertComment.comment" method="post"
					name="f-comment-parent">
					<div class="row mb-3">
						<div class="col-2">
							<input type="hidden" name="num" value="<%=num%>"> <input
								type="hidden" name="startIndex" value="<%=startIndex%>">
							<input type="hidden" name="endIndex" value="<%=endIndex%>">
							<input type="hidden" name="commentId" value="0"> <input
								type="hidden" name="refId" value="0"> <input type="text"
								class="form-control mb-2" name="writer" placeholder="닉네임"
								maxlength="20"> <input type="password"
								class="form-control" name="pw" placeholder="비밀번호" maxlength="20">
						</div>
						<div class="col-10">
							<textarea class="form-control" name="content"
								style="width: 100%; height: 100%; resize: none;" maxlength="400"></textarea>
							<small class="text-muted">최대 400자까지 입력 가능합니다.</small>
						</div>
					</div>

					<div class="row">
						<div class="col-9"></div>
						<div class="col-3">
							<button type="button" class="btn btn-primary"
								onclick="checkAndInsertComment('parent')">등록</button>
							<button type="reset" class="btn btn-secondary">다시 쓰기</button>
						</div>
					</div>
				</form>
			</div>
		</div>

		<!-- 선 -->
		<div class="row mb-3">
			<div class="col-1"></div>
			<div class="col-9 border-bottom border-2 border-primary"></div>
		</div>

		<!-- (수정, 삭제, 글쓰기) 버튼 -->
		<div class="row mb-3">
			<div class="col-1"></div>
			<div class="col-6"></div>
			<div class="col-3">
				<button type="button" class="btn btn-secondary"
					onclick="location.href='board/modify.board?num=<%=num%>'">수정</button>
				<button type="button" class="btn btn-secondary"
					onclick="location.href='board/delete.jsp?num=<%=num%>'">삭제</button>
				<button type="button" class="btn btn-primary"
					onclick="location.href='board/insert.jsp'">글쓰기</button>
			</div>
		</div>

		<%
		} //if(board!=null)
		} //if(numParam!=null)
		%>

		<!-- 게시판 -->
		<%
		//검색 옵션(제목+내용, 제목, 내용, 닉네임)
		//SearchBoardAction에서 받은 값(type)
		String type = request.getParameter("type");

		//type이 존재하지 않으면 게시판의 기존 게시물 보여주기
		if (type == null) {

			//현재 페이지
			String pageParam = request.getParameter("page");
			int currentPage = 1;//시작 페이지 1
			if (pageParam != null) {
				try {
			currentPage = Integer.parseInt(pageParam);
				} catch (Exception e) {
			currentPage = 1;//예외 발생시 현재 페이지를 1페이지로 설정
				}
			}

			//게시물 SELECT 범위 계산
			//currentPage  값에 따라 searchPos 계산
			/*searchPos는 board 테이블 num 1만부터 1씩 증가, 페이지는 1페이지당 pageSize(10)개를 보여줌.
			즉, 현재 페이지가 120 페이지라면 => 120(currentPage)x10(pageSize) => 1200개의 num 존재
			=>searchPos 계산 법 => 1200/10000 => 0.12 => searchPos = 0
			*/
			int pageSize = 10;//한 페이지에 보여줄 게시물 수
			int searchPos = (currentPage * pageSize) / 10000;

			//한 페이지에 보여줄 게시물 SELECT 범위 계산
			//currentPage 값에 따라 startIndex, endIndex 계산
			//currentPage가 1이면 1~10, 2이면 11~20, 3이면 21~30
			int startIndex = (currentPage - 1) * pageSize + 1;
			int endIndex = startIndex + pageSize - 1;

			//num, title, writer, createdDate, views를 담은 board형태 ArrayList 가져오기
			SelectBoardMainPageViewService selectBoardMainPageViewService = new SelectBoardMainPageViewService();
			ArrayList<Board> boardList = selectBoardMainPageViewService.selectBoardMainPageView(searchPos, startIndex,
			endIndex);
		%>
		<div class="row">
			<div class="col-1"></div>
			<div class="col-9 text-center">
				<section>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width: 10%">번호</th>
								<th style="width: 50%">제목</th>
								<th style="width: 15%">닉네임</th>
								<th style="width: 15%">작성일</th>
								<th style="width: 10%">조회수</th>
							</tr>
						</thead>

						<tbody>
							<%
							if (boardList != null) {
								LocalDateTime currentDateTime = LocalDateTime.now();//오늘 날짜
								//툴팁에 보여줄 날짜
								DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
								//다른 년도면 보여줄 날짜
								DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yy-MM-dd");
								//같은 년도 비교용 날짜
								DateTimeFormatter dtfYYYY = DateTimeFormatter.ofPattern("yyyy");
								//같은 년도면 보여줄 날짜
								DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("MM-dd");
								//같은 년, 월, 일이면 보여줄 날짜
								DateTimeFormatter dtf4 = DateTimeFormatter.ofPattern("HH:mm");

								String dateStr1;
								String dateStr2;
								String dateStr3;
								String dateStr4;
								for (Board board : boardList) {
							%>
							<tr>
								<td><%=board.getNum()%></td>
								<td align="left" style="padding-left: 3%;"><a
									href="index.jsp?num=<%=board.getNum()%>&page=<%=currentPage%>"
									style="text-decoration: none; color: black;"><%=board.getTitle()%></a>
								</td>
								<td title="<%=board.getWriter()%>">
									<%
									String writer = board.getWriter();
									if (writer.length() > 7) {
										writer = writer.substring(0, 7) + "...";
									}
									out.print(writer);
									%>
								</td>
								<%
								//서버에서 가져온 날짜 데이터
								LocalDateTime createdDateTime = board.getCreatedDate().toLocalDateTime();
								dateStr1 = createdDateTime.format(dtf1);//툴팁에 보여줄 날짜 yyyy-MM-dd HH:mm:ss
								%>
								<td title="<%=dateStr1%>">
									<%
									dateStr2 = createdDateTime.format(dtf2);//yy-MM-dd
									if (dateStr2.equals(currentDateTime.format(dtf2))) {
										//같은 년, 월, 일이면 HH:mm 으로 표시
										dateStr4 = createdDateTime.format(dtf4);
										out.print(dateStr4);
									} else if (createdDateTime.format(dtfYYYY).equals(currentDateTime.format(dtfYYYY))) {
										//같은 년도면 MM-dd 로 표시
										dateStr3 = createdDateTime.format(dtf3);
										out.print(dateStr3);
									} else {
										//다른 년도면 yy-MM-dd 로 표시
										out.print(dateStr2);
									}
									%>
								</td>
								<td><%=board.getViews()%></td>
							</tr>
							<%
							} //for (Board board : boardList)
							} //if (boardList != null)
							%>
						</tbody>
					</table>
				</section>
			</div>
		</div>

		<!-- 페이징 처리 -->
		<%
		/*(총 게시물 / 한 페이지에 보여줄 게시물 수)+1, 더하기 1하는 이유? ex) (12/10) => 1.2 => 소수점 절삭 1
		게시물이 12개라서 나머지 2개의 게시물은 다음 페이지 링크에서 보여줘야함. (1,2 페이지 필요)
		ex) (8/10) => 0.8 => 소수점 절삭 0, 게시물이 8개 있으니 적어도 하나의 페이지 링크가 필요함. (1페이지 필요)
		*/
		int totalPageLink = (maxNum / pageSize) + 1;
		%>
		<div class="row mb-2">
			<div class="col-1 col-md-3"></div>
			<div class="col-6 col-md-5">
				<ul class="pagination">
					<%
					//한 페이지에 보여줄 페이지 링크 수
					int pageLinkSize = 10;
					// 현재 페이지 범위 계산
					int startPage = ((currentPage - 1) / pageLinkSize) * pageLinkSize + 1;
					int endPage = Math.min(startPage + pageLinkSize - 1, totalPageLink);

					//이전 페이지 링크 생성
					if (currentPage > pageLinkSize) {
					%>
					<li class="page-item"><a class="page-link"
						href="index.jsp?page=<%=startPage - 1%>">&lt;</a></li>
					<%
					}

					//페이지 링크 생성
					for (int i = startPage; i <= endPage; i++) {
					boolean isCurrentPage = (i == currentPage);
					%>
					<li class="page-item">
						<%
						if (isCurrentPage) {//현재 페이지는 링크 생성X, 글자: 빨강
						%> <span class="page-link" style="color: red;"><%=i%></span> <%
						 } else {
						 %> <a class="page-link" href="index.jsp?page=<%=i%>"><%=i%></a> <%
						 }
						 %>
					</li>
					<!-- 다음 페이지 링크 생성 -->
					<%
					}
					if (endPage < totalPageLink) {
					%>
					<li class="page-item"><a class="page-link"
						href="index.jsp?page=<%=endPage + 1%>">&gt;</a></li>
					<%
					}
					%>
				</ul>
			</div>
			<!-- 글쓰기 -->
			<div class="col-3 col-md-2">
				<button type="button" class="btn btn-primary"
					onclick="location.href='board/insert.jsp'">글쓰기</button>
			</div>
		</div>
		<%
		} //if (type == null)
			//type이 존재한다면 검색한(type)의 게시물을 보여주기
		else {
		if (!type.equals("titleOrContent") && !type.equals("title") && !type.equals("content") && !type.equals("writer")) {
		%>
		<script>
			alert('type이 지정된 값(제목+내용, 제목, 내용, 닉네임)이 아닙니다.');
			location.href = 'index.jsp';
		</script>
		<%
		}
		//현재 페이지
		String pageParam = request.getParameter("page");
		int currentPage = 1;//시작 페이지 1
		if (pageParam != null) {
		try {
			currentPage = Integer.parseInt(pageParam);
		} catch (Exception e) {
			currentPage = 1;//예외 발생시 현재 페이지를 1페이지로 설정
		}
		}

		int pageSize = 10;//한 페이지에 보여줄 게시물 수
		//한 페이지에 보여줄 게시물 SELECT 범위 계산
		//currentPage 값에 따라 startIndex, endIndex 계산
		//currentPage가 1이면 1~10, 2이면 11~20, 3이면 21~30
		int startIndex = (currentPage - 1) * pageSize + 1;
		int endIndex = startIndex + pageSize - 1;

		//SearchBoardAction에서 받은 값(keyword, searchPos)
		String keyword = request.getParameter("keyword");//검색 키워드
		int searchPos = 0;//게시물은 searchPos의 단위인 1만개 씩 검색.
		try {
			searchPos = Integer.parseInt(request.getParameter("searchPos"));
		} catch (NumberFormatException e) {
		%>
		<script>
			alert('searchPos에서 NumberFormatException 발생.');
			location.href = 'index.jsp';
		</script>
		<%
		}
		
		//num, title, writer, createdDate, views를 담은 board형태 ArrayList 가져오기
		SelectBoardSearchMainPageViewService selectBoardSearchMainPageViewService = new SelectBoardSearchMainPageViewService();

		ArrayList<Board> boardList = selectBoardSearchMainPageViewService.SelectBoardSearchMainPageView(type, keyword,
				searchPos, startIndex, endIndex);
		
		//페이지 이동 후 keyword를 디코딩 없이 사용하기 위해 임시 변수에 할당(검색한 keyword 색 입힐 때 사용)
		String keywordTemp=keyword;
		
		try {
		//url 전송에 사용하기 위해 UTF-8로 인코딩
		//위에서 안하고 여기서 하는 이유? 미리 UTF-8로 인코딩해서 메서드의 파라미터에 전달하면 전달받은 메서드에서 UTF-8로 디코딩 하고 사용해야되서.
		keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		%>
		<script>
			alert('keyword에서 UnsupportedEncodingException 발생.');
			location.href = 'index.jsp';
		</script>
		<%
		}
		%>
		<div class="row">
			<div class="col-1"></div>
			<div class="col-9 text-center">
				<section>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width: 10%">번호</th>
								<th style="width: 50%">제목</th>
								<th style="width: 15%">닉네임</th>
								<th style="width: 15%">작성일</th>
								<th style="width: 10%">조회수</th>
							</tr>
						</thead>

						<tbody>
							<%
							if (boardList != null) {
								LocalDateTime currentDateTime = LocalDateTime.now();//오늘 날짜
								//툴팁에 보여줄 날짜
								DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
								//다른 년도면 보여줄 날짜
								DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yy-MM-dd");
								//같은 년도 비교용 날짜
								DateTimeFormatter dtfYYYY = DateTimeFormatter.ofPattern("yyyy");
								//같은 년도면 보여줄 날짜
								DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("MM-dd");
								//같은 년, 월, 일이면 보여줄 날짜
								DateTimeFormatter dtf4 = DateTimeFormatter.ofPattern("HH:mm");

								String dateStr1;
								String dateStr2;
								String dateStr3;
								String dateStr4;
								for (Board board : boardList) {
							%>
							<tr>
								<td><%=board.getNum()%></td>
								<td align="left" style="padding-left: 3%;"><a
									href="index.jsp?num=<%=board.getNum()%>&page=<%=currentPage%>&type=<%=type%>&searchPos=<%=searchPos%>&keyword=<%=keyword%>"
									style="text-decoration: none; color: black;"> <%
									 out.print(board.getTitle().replace(keywordTemp, "<span class=\"searchResult\">" + keywordTemp + "</span>"));
									 %>
								</a></td>
								<td title="<%=board.getWriter()%>">
									<%
									String writer = board.getWriter();
									if (writer.length() > 7) {
										writer = writer.substring(0, 7) + "...";
									}
									out.print(writer);
									%>
								</td>
								<%
								//서버에서 가져온 날짜 데이터
								LocalDateTime createdDateTime = board.getCreatedDate().toLocalDateTime();
								dateStr1 = createdDateTime.format(dtf1);//툴팁에 보여줄 날짜 yyyy-MM-dd HH:mm:ss
								%>
								<td title="<%=dateStr1%>">
									<%
									dateStr2 = createdDateTime.format(dtf2);//yy-MM-dd
									if (dateStr2.equals(currentDateTime.format(dtf2))) {
										//같은 년, 월, 일이면 HH:mm 으로 표시
										dateStr4 = createdDateTime.format(dtf4);
										out.print(dateStr4);
									} else if (createdDateTime.format(dtfYYYY).equals(currentDateTime.format(dtfYYYY))) {
										//같은 년도면 MM-dd 로 표시
										dateStr3 = createdDateTime.format(dtf3);
										out.print(dateStr3);
									} else {
										//다른 년도면 yy-MM-dd 로 표시
										out.print(dateStr2);
									}
									%>
								</td>
								<td><%=board.getViews()%></td>
							</tr>
							<%
							} //for (Board board : boardList)
							} //if (boardList != null)
							%>
						</tbody>
					</table>
				</section>
			</div>
		</div>

		<!-- 페이징 처리 -->
		<%
		//페이지네이션(총 페이지 링크 수 : 검색한 게시물들 총 갯수(만개 단위에서 총 갯수))
				SelectBoardSearchMaxNumService selectBoardSearchMaxNumService = new SelectBoardSearchMaxNumService();
				int searchMaxNum = selectBoardSearchMaxNumService.selectBoardSearchMaxNum(type, keyword, searchPos);//searchMaxNum = 검색한 게시물 수
				/*(총 게시물 / 한 페이지에 보여줄 게시물 수)+1, 더하기 1하는 이유? ex) (12/10) => 1.2 => 소수점 절삭 1
				게시물이 12개라서 나머지 2개의 게시물은 다음 페이지 링크에서 보여줘야함. (1,2 페이지 필요)
				ex) (8/10) => 0.8 => 소수점 절삭 0, 게시물이 8개 있으니 적어도 하나의 페이지 링크가 필요함. (1페이지 필요)
				*/
				int totalPageLink = (searchMaxNum / pageSize) + 1;
				
				//이전 검색, 다음 검색에 사용할 maxSeachNum(seachNum 최대값)
				SelectBoardMaxSearchPos selectBoardMaxSearchPos = new SelectBoardMaxSearchPos();
				int maxSearchPos = selectBoardMaxSearchPos.selectBoardMaxSearchPos();
		%>
		<div class="row mb-2">
			<div class="col-1 col-md-3"></div>
			<div class="col-6 col-md-5">
				<ul class="pagination">
					<%
					//한 페이지에 보여줄 페이지 링크 수
					int pageLinkSize = 10;
					// 현재 페이지 범위 계산
					int startPage = ((currentPage - 1) / pageLinkSize) * pageLinkSize + 1;
					int endPage = Math.min(startPage + pageLinkSize - 1, totalPageLink);
					
					//이전 검색 링크 생성(searchPos + 1)
					if(searchPos < maxSearchPos){
						%>
						<li class="page-item">
							<a class="page-link" href="index.jsp?page=1&type=<%=type%>&searchPos=<%=searchPos + 1%>&keyword=<%=keyword%>">◀ 이전 검색</a>
						</li>
						<%
					}

					//이전 페이지 링크 생성
					if (currentPage > pageLinkSize) {
					%>
					<li class="page-item"><a class="page-link"
						href="index.jsp?page=<%=startPage - 1%>&type=<%=type%>&searchPos=<%=searchPos%>&keyword=<%=keyword%>">&lt;</a></li>
					<%
					}

					//페이지 링크 생성
					for (int i = startPage; i <= endPage; i++) {
					boolean isCurrentPage = (i == currentPage);
					%>
					<li class="page-item">
						<%
						if (isCurrentPage) {//현재 페이지는 링크 생성X, 글자: 빨강
						%> <span class="page-link" style="color: red;"><%=i%></span> <%
						 } else {
						 %> <a class="page-link"
						href="index.jsp?page=<%=i%>&type=<%=type%>&searchPos=<%=searchPos%>&keyword=<%=keyword%>"><%=i%></a>
						<%
						}
						%>
					</li>
					<%
					}//for (int i = startPage; i <= endPage; i++)
					//다음 페이지 링크 생성
					if (endPage < totalPageLink) {
					%>
					<li class="page-item"><a class="page-link"
						href="index.jsp?page=<%=endPage + 1%>&type=<%=type%>&searchPos=<%=searchPos%>&keyword=<%=keyword%>">&gt;</a></li>
					<%
					}
					//다음 검색 링크 생성(searchPos - 1)
					if(searchPos > 0){
						%>
						<li class="page-item">
							<a class="page-link" href="index.jsp?page=1&type=<%=type%>&searchPos=<%=searchPos - 1%>&keyword=<%=keyword%>">다음 검색 ▶</a>
						</li>
						<%
					}
					%>
				</ul>
				<%
				//검색 링크 오류 처리
				if(searchPos < 0){
					%>
					<script>
					alert('searchPos가 0보다 작습니다.');
					location.href = 'index.jsp';
					</script>
					<%
				}
				if(searchPos > maxSearchPos){
					%>
					<script>
					alert('searchPos가 maxSearchPos보다 큽니다.');
					location.href = 'index.jsp';
					</script>
					<%
				}
				
				%>
			</div>
			<!-- 글쓰기 -->
			<div class="col-3 col-md-2">
				<button type="button" class="btn btn-primary"
					onclick="location.href='board/insert.jsp'">글쓰기</button>
			</div>
		</div>
		<%
		} //else(type==null)
		%>

		<!-- 검색 -->
		<div class="row">
			<div class="col-3"></div>
			<div class="col-7">
				<form action="search.board" method="get" name="f-search">
					<div class="row">
						<div class="col-3">
							<select class="form-select" name="searchOption">
								<option value="titleOrContent"
									<%=(type != null && "titleOrContent".equals(type)) ? "selected" : ""%>>제목+내용</option>
								<option value="title"
									<%=(type != null && "title".equals(type)) ? "selected" : ""%>>제목</option>
								<option value="content"
									<%=(type != null && "content".equals(type)) ? "selected" : ""%>>내용</option>
								<option value="writer"
									<%=(type != null && "writer".equals(type)) ? "selected" : ""%>>닉네임</option>
							</select>
						</div>

						<div class="col-5">
							<input type="text" class="form-control" name="keyword"
								value="<%=request.getParameter("keyword") != null ? request.getParameter("keyword") : ""%>"
								title="검색어 입력(최대 100글자)" maxlength="100">
						</div>

						<div class="col-4">
							<button type="button" class="btn btn-primary"
								onclick="checkAndSearch()">검색</button>
						</div>
					</div>
				</form>
			</div>
		</div>

	</div>
	<!-- container -->

	<script src="js/index.js"></script>
</body>
</html>