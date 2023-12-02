<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>게시판 글 수정</title>
<link rel="stylesheet" href="../css/bootstrap.min.css">
</head>
<body>
	<div class="container mt-5 mb-5">
		<!-- 헤더 -->
		<div class="row mb-5">
			<div class="col-1"></div>
			<div class="col-9 text-center">
				<header>
					<h2>
						<a href="../index.jsp"
							style="text-decoration: none; color: black;">코더펭귄 게시판</a>
					</h2>
				</header>
			</div>
		</div>
		<%
		String numParam = request.getParameter("num");
		%>
		<div id="pwCheckFormRow" class="row">
			<div class="col-3"></div>
			<!-- 테두리 -->
			<div class="col-5 border border-primary border-2 p-4">

				<!-- 폼 -->
				<form action="checkBoardPwToModify.board" method="post" name="f">

					<div class="row mb-2">
						<div class="col-12 text-center">
							<b>비밀번호를 입력하세요.</b>
						</div>
					</div>

					<div class="row mb-2">
						<div class="col-3"></div>
						<div class="col-6">
							<input type="hidden" name="num" value="<%=numParam%>"> <input
								type="password" class="form-control" name="pw">
						</div>
					</div>

					<div class="row mb-2">
						<div class="col-4"></div>
						<div class="col-2">
							<button type="button" class="btn btn-secondary"
								onclick="goBack();">취소</button>
						</div>
						<div class="col-2">
							<button type="button" class="btn btn-primary"
								onclick="checkAndFetch()">확인</button>
						</div>
					</div>

				</form>

			</div>
			<!-- 테두리 -->
		</div>
		<!-- row -->

		<div class="row" >
			<div class="col-1"></div>
			<!-- 테두리 -->
			<div id="realModifyForm" class="col-9 border border-primary border-2 p-4" style="display: none;">

				<!-- 폼 -->
				<form action="realModify.board" method="post" name="f2">
					<div class="row mb-2">
						<div class="col-5 col-md-2">
							<input type="hidden" name="num" value="<%=numParam%>"> <input
								type="text" class="form-control" name="writer" value=""
								placeholder="닉네임" readonly="readonly">
						</div>
						<div class="col-5 col-md-2">
							<input type="password" class="form-control" name="pw" value=""
								placeholder="비밀번호" readonly="readonly">
						</div>
					</div>

					<div class="row mb-2">
						<div class="col-10 col-md-6">
							<input type="text" class="form-control" name="title" value=""
								placeholder="제목" maxlength="40">
						</div>
					</div>

					<div class="row mb-2">
						<div class="col-12">
							<textarea class="form-control" name="content" rows="15"
								maxlength="2000">내용</textarea>
						</div>
						<small class="text-muted">최대 2000자까지 입력 가능합니다.</small>
					</div>

					<div class="row mb-2">
						<div class="col-6 col-md-9"></div>
						<div class="col-6 col-md-3">
							<button type="button" class="btn btn-secondary"
								onclick="confirmGoBack()">취소</button>
							<button type="button" class="btn btn-primary"
								onclick="checkAndSubmit()">등록</button>
						</div>
					</div>
				</form>
			</div>
			<!-- 테두리 -->
		</div>

	</div>
	<!-- container -->

	<script src="../js/modify.js"></script>
</body>
</html>