<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>게시판 글 삭제</title>
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
		<div class="row">
			<div class="col-1"></div>
			<div class="col-2"></div>
			<!-- 테두리 -->
			<div class="col-5 border border-primary border-2 p-4">

				<!-- 폼 -->
				<form action="delete.board" method="post" name="f">
					<div class="row mb-2">
						<div class="col-12 text-center">
							<b>비밀번호를 입력하세요.</b>
						</div>
					</div>
					<div class="row mb-2">
						<div class="col-3"></div>
						<div class="col-6">
							<input type="password" class="form-control" name="pw"> <input
								type="hidden" name="num" value="<%=numParam%>">
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
								onclick="checkAndSubmit()">확인</button>
						</div>
					</div>
				</form>
			</div>
			<!-- 테두리 -->
		</div>

	</div>

	<script src="../js/delete.js"></script>
</body>
</html>