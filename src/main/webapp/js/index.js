//함수 위치(line)
//toggleComment(8), moveCommentPage(31), checkAndInsertComment(472)
//toggleInsertSubComment(984), pwModal(1017), deleteComment(1038)
//checkAndSearch(1514)


/*** 댓글 닫기▲, 댓글 열기▼ */
function toggleComment() {
	let toggleButton = document.getElementById('toggleButton');
	let commentRow = document.getElementById('commentRow');

	if (commentRow != null) {
		if (commentRow.style.display === "block" || commentRow.style.display === "") {
			commentRow.style.display = "none";
			toggleButton.textContent = "댓글 열기▼";
		} else {
			commentRow.style.display = "block";
			toggleButton.textContent = "댓글 닫기▲";
		}
	} else {
		if (toggleButton.textContent === "댓글 닫기▲") {
			toggleButton.textContent = "댓글 열기▼";
		} else {
			toggleButton.textContent = "댓글 닫기▲";
		}
	}
}


/*** 댓글 페이지 이동 */
function moveCommentPage(boardNum, commentPage) {

	let sortingOption = document.getElementById('sortingOption');

	let url = `selectComment.comment?boardNum=${boardNum}&commentPage=${commentPage}&sortingOption=${sortingOption.value}`;

	fetch(url)
		.then(response => response.json())
		.then(data => {
			if (data.sortingOptionError) {
				alert(data.sortingOptionError);
				return;
			}
			if (data.numError) {
				alert(data.numError);
				return;
			}
			if (data.jsonParsingError) {
				alert(data.jsonParsingError);
				return;
			}

			//댓글 개수
			let commentCount = document.getElementById('custom-commentCount');
			commentCount.textContent = data.commentCount;

			//댓글 출력
			let jsonArray = JSON.parse(data.jsonArray);//"jsonArray" 키의 문자열을 파싱하여 배열로 변환

			let commentRow = document.getElementById('commentRow');
			commentRow.innerHTML = ``;

			if (jsonArray !== null) {

				let rowDiv1 = document.createElement('div');
				rowDiv1.classList.add('row', 'mb-3');

				let col_1_div1 = document.createElement('div');
				col_1_div1.classList.add('col-1');

				let col_9_div1 = document.createElement('div');
				col_9_div1.classList.add('col-9');

				let table = document.createElement('table');
				table.classList.add('table');

				let tbody = document.createElement('tbody');

				jsonArray.forEach(item => {
					if (item.refId == 0) {//댓글
						if (item.isDelete === 'N') {//대댓글이 있으면서 삭제안된 댓글
							let tr1 = document.createElement('tr');

							let td1 = document.createElement('td');
							td1.classList.add('custom-boardComment-writer_createdDate');
							td1.textContent = item.writer;

							let td2 = document.createElement('td');
							td2.classList.add('custom-boardComment-content');
							td2.style.cursor = 'pointer';
							td2.onclick = function() { toggleInsertSubComment(item.commentId); };
							td2.textContent = item.content;

							let td3 = document.createElement('td');
							td3.classList.add('custom-boardComment-writer_createdDate');
							td3.style.position = 'relative';
							td3.textContent = item.createdDate + '\u00A0';

							let span1 = document.createElement('span');
							span1.classList.add('custom-span1');
							span1.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
							span1.style.cursor = 'pointer';
							span1.textContent = 'X';

							let div1 = document.createElement('div');
							div1.id = `pwModal-${item.commentId}-${item.refId}-${item.refOrder}`;

							let div2 = document.createElement('div');
							div2.classList.add('custom-input1-wrapper');

							let input1 = document.createElement('input');
							input1.id = `deleteCommentInputPw-${item.commentId}-${item.refId}-${item.refOrder}`;
							input1.classList.add('custom-input1');
							input1.type = 'password';
							input1.placeholder = '비밀번호';

							let div3 = document.createElement('div');
							div3.classList.add('custom-checkAndX');

							let span2 = document.createElement('span');
							span2.classList.add('custom-span2');
							span2.onclick = function() { deleteComment(item.boardNum, item.commentId, item.refId, item.refOrder, item.childNum); };
							span2.textContent = '확인';

							let span3 = document.createElement('span');
							span3.classList.add('custom-span3');
							span3.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
							span3.textContent = 'X';

							//노드 추가
							div3.appendChild(span2);
							div3.appendChild(span3);

							div2.appendChild(input1);
							div2.appendChild(div3);

							div1.appendChild(div2);

							td3.appendChild(span1);
							td3.appendChild(div1);

							tr1.appendChild(td1);
							tr1.appendChild(td2);
							tr1.appendChild(td3);

							//대댓글 쓰기, 댓글의 내용을 누르기 전에는 숨겨진 상태
							let tr2 = document.createElement('tr');
							tr2.id = `insertSubComment${item.commentId}`;
							tr2.classList.add('table-secondary');
							tr2.style.display = 'none';

							let td4 = document.createElement('td');
							td4.classList.add('custom-pl-50px');
							td4.colSpan = 3;

							let form = document.createElement('form');
							form.action = 'insertComment.comment';
							form.method = 'post';
							form.name = `f-comment-${item.commentId}`;

							let rowDiv2 = document.createElement('div');
							rowDiv2.classList.add('row', 'mb-3');

							let col_2_div1 = document.createElement('div');
							col_2_div1.classList.add('col-2');

							let input_num = document.createElement('input');
							input_num.type = 'hidden';
							input_num.name = 'num';
							input_num.value = item.boardNum;

							let input_startIndex = document.createElement('input');
							input_startIndex.type = 'hidden';
							input_startIndex.name = 'startIndex';
							input_startIndex.value = data.startIndex;

							let input_endIndex = document.createElement('input');
							input_endIndex.type = 'hidden';
							input_endIndex.name = 'endIndex';
							input_endIndex.value = data.endIndex;

							let input_commentId = document.createElement('input');
							input_commentId.type = 'hidden';
							input_commentId.name = 'commentId';
							input_commentId.value = item.commentId;

							let input_refId = document.createElement('input');
							input_refId.type = 'hidden';
							input_refId.name = 'refId';
							input_refId.value = item.commentId;

							let input_writer = document.createElement('input');
							input_writer.type = 'text';
							input_writer.id = `insertSubCommentInputWriter${item.commentId}`;
							input_writer.classList.add('form-control', 'mb-2');
							input_writer.name = 'writer';
							input_writer.placeholder = '닉네임';
							input_writer.maxLength = 20;

							let input_pw = document.createElement('input');
							input_pw.type = 'password';
							input_pw.id = `insertSubCommentInputPw${item.commentId}`;
							input_pw.classList.add('form-control');
							input_pw.name = 'pw';
							input_pw.placeholder = '비밀번호';
							input_pw.maxLength = 20;

							let col_10_div1 = document.createElement('div');
							col_10_div1.classList.add('col-10');

							let textarea = document.createElement('textarea');
							textarea.id = `insertSubCommentTextarea${item.commentId}`;
							textarea.classList.add('form-control');
							textarea.name = 'content';
							textarea.style.width = '100%';
							textarea.style.height = '100%';
							textarea.style.resize = 'none';
							textarea.maxLength = 400;

							let small = document.createElement('small');
							small.classList.add('text-muted');
							small.textContent = '최대 400자까지 입력 가능합니다.';

							let rowDiv3 = document.createElement('div');
							rowDiv3.classList.add('row');

							let col_9_div2 = document.createElement('div');
							col_9_div2.classList.add('col-9');

							let col_3_div1 = document.createElement('div');
							col_3_div1.classList.add('col-3');

							let button1 = document.createElement('button');
							button1.type = 'button';
							button1.classList.add('btn', 'btn-primary');
							button1.onclick = function() { checkAndInsertComment(item.commentId); };
							button1.textContent = '등록';

							let button2 = document.createElement('button');
							button2.type = 'reset';
							button2.classList.add('btn', 'btn-secondary');
							button2.textContent = '다시쓰기';

							//노드 추가
							col_3_div1.appendChild(button1);
							col_3_div1.appendChild(button2);

							rowDiv3.appendChild(col_9_div2);
							rowDiv3.appendChild(col_3_div1);

							col_10_div1.appendChild(textarea);
							col_10_div1.appendChild(small);

							col_2_div1.appendChild(input_num);
							col_2_div1.appendChild(input_startIndex);
							col_2_div1.appendChild(input_endIndex);
							col_2_div1.appendChild(input_commentId);
							col_2_div1.appendChild(input_refId);
							col_2_div1.appendChild(input_writer);
							col_2_div1.appendChild(input_pw);

							rowDiv2.appendChild(col_2_div1);
							rowDiv2.appendChild(col_10_div1);

							form.appendChild(rowDiv2);
							form.appendChild(rowDiv3);

							td4.appendChild(form);

							tr2.appendChild(td4);

							tbody.appendChild(tr1);
							tbody.appendChild(tr2);
						} else {//대댓글이 있으면서 삭제된 댓글
							let tr1 = document.createElement('tr');

							let td1 = document.createElement('td');
							td1.colSpan = '3';

							let span1 = document.createElement('span');
							span1.classList.add('custom-delete-comment');
							span1.textContent = '해당 댓글은 삭제되었습니다.';

							//노드 추가
							td1.appendChild(span1);

							tr1.appendChild(td1);

							tbody.appendChild(tr1);
						}
					} else {//대댓글

						let tr1 = document.createElement('tr');
						tr1.classList.add('table-secondary');

						let td1 = document.createElement('td');
						td1.classList.add('custom-pl-50px', 'custom-boardComment-writer_createdDate');
						td1.textContent = item.writer;

						let td2 = document.createElement('td');
						td2.classList.add('custom-pl-50px', 'custom-boardComment-content');
						td2.textContent = `┗ ${item.content}`;

						let td3 = document.createElement('td');
						td3.classList.add('custom-boardComment-writer_createdDate');
						td3.style.position = 'relative';
						td3.textContent = item.createdDate + '\u00A0';

						let span1 = document.createElement('span');
						span1.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
						span1.classList.add('custom-span1');
						span1.style.cursor = 'pointer';
						span1.textContent = 'X';

						let div1 = document.createElement('div');
						div1.id = `pwModal-${item.commentId}-${item.refId}-${item.refOrder}`;

						let div2 = document.createElement('div');
						div2.classList.add('custom-input1-wrapper');

						let input1 = document.createElement('input');
						input1.id = `deleteCommentInputPw-${item.commentId}-${item.refId}-${item.refOrder}`;
						input1.classList.add('custom-input1');
						input1.type = 'password';
						input1.placeholder = '비밀번호';

						let div3 = document.createElement('div');
						div3.classList.add('custom-checkAndX');

						let span2 = document.createElement('span');
						span2.classList.add('custom-span2');
						span2.onclick = function() { deleteComment(item.boardNum, item.commentId, item.refId, item.refOrder, item.childNum); };
						span2.textContent = '확인';

						let span3 = document.createElement('span');
						span3.classList.add('custom-span3');
						span3.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
						span3.textContent = 'X';

						//노드 추가
						div3.appendChild(span2);
						div3.appendChild(span3);

						div2.appendChild(input1);
						div2.appendChild(div3);

						div1.appendChild(div2);

						td3.appendChild(span1);
						td3.appendChild(div1);

						tr1.appendChild(td1);
						tr1.appendChild(td2);
						tr1.appendChild(td3);

						tbody.appendChild(tr1);
					}
				});

				//댓글 페이지네이션
				let rowDiv2 = document.createElement('div');
				rowDiv2.classList.add('row', 'mb-3');

				let col_1_div2 = document.createElement('div');
				col_1_div2.classList.add('col-1');

				let col_9_div2 = document.createElement('div');
				col_9_div2.classList.add('col-9', 'text-center');

				//댓글 페이지네이션 계산에 필요한 변수들.
				let currentCommentPage = data.commentPage;//댓글 페이지
				let commentPageLinkSize = 10; //한 페이지에 보여줄 페이지 링크 수
				let commentPageSize = 50;//한 페이지에 보여줄 댓글 수
				let totalCommentPageLink = Math.floor(data.commentCount / commentPageSize) + 1;//전체 페이지 링크 수

				// 현재 페이지 범위 계산
				let startPage = (Math.floor((currentCommentPage - 1) / commentPageLinkSize)) * commentPageLinkSize + 1;
				let endPage = Math.min(startPage + commentPageLinkSize - 1, totalCommentPageLink);

				//이전 페이지 링크 생성
				if (currentCommentPage > commentPageLinkSize) {
					let span1 = document.createElement('span');
					span1.classList.add('custom-commentPageLink');
					span1.onclick = function() { moveCommentPage(data.boardNum, (startPage - 1)); };
					span1.textContent = '◁';

					//노드 추가(이전 페이지)
					col_9_div2.appendChild(span1);
				}

				//페이지 링크 생성
				for (let i = startPage; i <= endPage; i++) {

					let isCurrentCommentPage = (i === currentCommentPage);

					if (isCurrentCommentPage) {//현재 페이지 링크는 onclick 속성 생성X, 글자: 빨강
						let span1 = document.createElement('span');
						span1.classList.add('custom-currentCommentPageLink');
						span1.textContent = i;

						//노드 추가(현재 페이지 링크)
						col_9_div2.appendChild(span1);
					} else {//다른 페이지 링크
						let span1 = document.createElement('span');
						span1.classList.add('custom-commentPageLink');
						span1.onclick = function() { moveCommentPage(data.boardNum, i); };
						span1.textContent = i;

						//노드 추가(다른 페이지 링크)
						col_9_div2.appendChild(span1);
					}
				}

				//다른 페이지 링크 생성
				if (endPage < totalCommentPageLink) {
					let span1 = document.createElement('span');
					span1.classList.add('custom-commentPageLink');
					span1.onclick = function() { moveCommentPage(data.boardNum, (endPage + 1)); };
					span1.textContent = '▷';

					//노드 추가(다음 페이지 링크)
					col_9_div2.appendChild(span1);
				}

				//노드 추가(페이지네이션)
				rowDiv2.appendChild(col_1_div2);
				rowDiv2.appendChild(col_9_div2);

				//선
				let rowDiv3 = document.createElement('div');
				rowDiv3.classList.add('row', 'mb-3');

				let col_1_div3 = document.createElement('div');
				col_1_div3.classList.add('col-1');

				let col_9_div3 = document.createElement('div');
				col_9_div3.classList.add('col-9', 'border-bottom', 'border-2', 'border-primary');

				//노드 추가(선)
				rowDiv3.appendChild(col_1_div3);
				rowDiv3.appendChild(col_9_div3);

				//노드 추가(전체)
				table.appendChild(tbody);
				col_9_div1.appendChild(table);

				rowDiv1.appendChild(col_1_div1);
				rowDiv1.appendChild(col_9_div1);

				commentRow.appendChild(rowDiv1);
				commentRow.appendChild(rowDiv2);
				commentRow.appendChild(rowDiv3);

				//페이지 이동 후, 화면 전체 댓글쪽으로 스크롤
				document.querySelector('#commentInfo').scrollIntoView();

			}//if (jsonArray !== null)

			return;

		})
		.catch(error => {
			console.log(error);
			alert('네트워크 오류가 발생했습니다. 다시 시도해 주세요.');
			return;
		})

}//moveCommentPage() function


/*** 댓글 등록 */
function checkAndInsertComment(commentId) {
	let sortingOption = document.getElementById('sortingOption');
	let f = document.forms[`f-comment-${commentId}`];

	if (!f.writer.value) {
		alert('닉네임을 입력하세요.');
		f.writer.focus();
		return;
	} else if (f.writer.value.trim().length < 2) {
		alert('닉네임은 최소 2자 이상이어야 합니다.');
		f.writer.focus();
		return;
	}

	if (!f.pw.value) {
		alert('비밀번호를 입력하세요.');
		f.pw.focus();
		return;
	} else if (f.pw.value.trim() !== f.pw.value) {
		alert('[비밀번호] 항목은 처음과 마지막에 공백을 사용할 수 없습니다.');
		f.pw.focus();
		return;
	}

	if (!f.content.value) {
		alert('내용을 입력하세요.');
		f.content.focus();
		return;
	} else if (f.content.value.trim().length < 2) {
		alert('내용은 최소 2자 이상이어야 합니다.');
		f.content.focus();
		return;
	}

	// JSON 객체 생성
	let data = {
		sortingOption: sortingOption.value,
		num: f.num.value,
		startIndex: f.startIndex.value,
		endIndex: f.endIndex.value,
		commentId: f.commentId.value,
		refId: f.refId.value,
		writer: f.writer.value,
		pw: f.pw.value,
		content: f.content.value
	};

	// 서블릿 경로
	let url = "insertComment.comment";

	fetch(url, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(data)
	})
		.then(response => response.json())
		.then(data => {
			if (data.sortingOptionError) {
				alert(data.sortingOptionError);
				return;
			}
			if (data.numError) {
				alert(data.numError);
				return;
			}
			if (data.writerError) {
				alert(data.writerError);
				return;
			}
			if (data.pwError) {
				alert(data.pwError);
				return;
			}
			if (data.contentError) {
				alert(data.contentError);
				return;
			}
			if (data.jsonObjectError) {
				alert(data.jsonObjectError);
				return;
			}
			if (data.insertError) {
				alert(data.insertError);
				return;
			}
			if (data.jsonParsingError) {
				alert(data.jsonParsingError);
				return;
			}

			//댓글 개수
			let commentCount = document.getElementById('custom-commentCount');
			commentCount.textContent = data.commentCount;

			//댓글 출력
			let jsonArray = JSON.parse(data.jsonArray);//"jsonArray" 키의 문자열을 파싱하여 배열로 변환

			let commentRow = document.getElementById('commentRow');
			commentRow.innerHTML = ``;

			if (jsonArray !== null) {

				let rowDiv1 = document.createElement('div');
				rowDiv1.classList.add('row', 'mb-3');

				let col_1_div1 = document.createElement('div');
				col_1_div1.classList.add('col-1');

				let col_9_div1 = document.createElement('div');
				col_9_div1.classList.add('col-9');

				let table = document.createElement('table');
				table.classList.add('table');

				let tbody = document.createElement('tbody');

				jsonArray.forEach(item => {
					if (item.refId == 0) {//댓글
						if (item.isDelete === 'N') {//대댓글이 있으면서 삭제안된 댓글
							let tr1 = document.createElement('tr');

							let td1 = document.createElement('td');
							td1.classList.add('custom-boardComment-writer_createdDate');
							td1.textContent = item.writer;

							let td2 = document.createElement('td');
							td2.classList.add('custom-boardComment-content');
							td2.style.cursor = 'pointer';
							td2.onclick = function() { toggleInsertSubComment(item.commentId); };
							td2.textContent = item.content;

							let td3 = document.createElement('td');
							td3.classList.add('custom-boardComment-writer_createdDate');
							td3.style.position = 'relative';
							td3.textContent = item.createdDate + '\u00A0';

							let span1 = document.createElement('span');
							span1.classList.add('custom-span1');
							span1.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
							span1.style.cursor = 'pointer';
							span1.textContent = 'X';

							let div1 = document.createElement('div');
							div1.id = `pwModal-${item.commentId}-${item.refId}-${item.refOrder}`;

							let div2 = document.createElement('div');
							div2.classList.add('custom-input1-wrapper');

							let input1 = document.createElement('input');
							input1.id = `deleteCommentInputPw-${item.commentId}-${item.refId}-${item.refOrder}`;
							input1.classList.add('custom-input1');
							input1.type = 'password';
							input1.placeholder = '비밀번호';

							let div3 = document.createElement('div');
							div3.classList.add('custom-checkAndX');

							let span2 = document.createElement('span');
							span2.classList.add('custom-span2');
							span2.onclick = function() { deleteComment(item.boardNum, item.commentId, item.refId, item.refOrder, item.childNum); };
							span2.textContent = '확인';

							let span3 = document.createElement('span');
							span3.classList.add('custom-span3');
							span3.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
							span3.textContent = 'X';

							//노드 추가
							div3.appendChild(span2);
							div3.appendChild(span3);

							div2.appendChild(input1);
							div2.appendChild(div3);

							div1.appendChild(div2);

							td3.appendChild(span1);
							td3.appendChild(div1);

							tr1.appendChild(td1);
							tr1.appendChild(td2);
							tr1.appendChild(td3);

							//대댓글 쓰기, 댓글의 내용을 누르기 전에는 숨겨진 상태
							let tr2 = document.createElement('tr');
							tr2.id = `insertSubComment${item.commentId}`;
							tr2.classList.add('table-secondary');
							tr2.style.display = 'none';

							let td4 = document.createElement('td');
							td4.classList.add('custom-pl-50px');
							td4.colSpan = 3;

							let form = document.createElement('form');
							form.action = 'insertComment.comment';
							form.method = 'post';
							form.name = `f-comment-${item.commentId}`;

							let rowDiv2 = document.createElement('div');
							rowDiv2.classList.add('row', 'mb-3');

							let col_2_div1 = document.createElement('div');
							col_2_div1.classList.add('col-2');

							let input_num = document.createElement('input');
							input_num.type = 'hidden';
							input_num.name = 'num';
							input_num.value = item.boardNum;

							let input_startIndex = document.createElement('input');
							input_startIndex.type = 'hidden';
							input_startIndex.name = 'startIndex';
							input_startIndex.value = data.startIndex;

							let input_endIndex = document.createElement('input');
							input_endIndex.type = 'hidden';
							input_endIndex.name = 'endIndex';
							input_endIndex.value = data.endIndex;

							let input_commentId = document.createElement('input');
							input_commentId.type = 'hidden';
							input_commentId.name = 'commentId';
							input_commentId.value = item.commentId;

							let input_refId = document.createElement('input');
							input_refId.type = 'hidden';
							input_refId.name = 'refId';
							input_refId.value = item.commentId;

							let input_writer = document.createElement('input');
							input_writer.type = 'text';
							input_writer.id = `insertSubCommentInputWriter${item.commentId}`;
							input_writer.classList.add('form-control', 'mb-2');
							input_writer.name = 'writer';
							input_writer.placeholder = '닉네임';
							input_writer.maxLength = 20;

							let input_pw = document.createElement('input');
							input_pw.type = 'password';
							input_pw.id = `insertSubCommentInputPw${item.commentId}`;
							input_pw.classList.add('form-control');
							input_pw.name = 'pw';
							input_pw.placeholder = '비밀번호';
							input_pw.maxLength = 20;

							let col_10_div1 = document.createElement('div');
							col_10_div1.classList.add('col-10');

							let textarea = document.createElement('textarea');
							textarea.id = `insertSubCommentTextarea${item.commentId}`;
							textarea.classList.add('form-control');
							textarea.name = 'content';
							textarea.style.width = '100%';
							textarea.style.height = '100%';
							textarea.style.resize = 'none';
							textarea.maxLength = 400;

							let small = document.createElement('small');
							small.classList.add('text-muted');
							small.textContent = '최대 400자까지 입력 가능합니다.';

							let rowDiv3 = document.createElement('div');
							rowDiv3.classList.add('row');

							let col_9_div2 = document.createElement('div');
							col_9_div2.classList.add('col-9');

							let col_3_div1 = document.createElement('div');
							col_3_div1.classList.add('col-3');

							let button1 = document.createElement('button');
							button1.type = 'button';
							button1.classList.add('btn', 'btn-primary');
							button1.onclick = function() { checkAndInsertComment(item.commentId); };
							button1.textContent = '등록';

							let button2 = document.createElement('button');
							button2.type = 'reset';
							button2.classList.add('btn', 'btn-secondary');
							button2.textContent = '다시쓰기';

							//노드 추가
							col_3_div1.appendChild(button1);
							col_3_div1.appendChild(button2);

							rowDiv3.appendChild(col_9_div2);
							rowDiv3.appendChild(col_3_div1);

							col_10_div1.appendChild(textarea);
							col_10_div1.appendChild(small);

							col_2_div1.appendChild(input_num);
							col_2_div1.appendChild(input_startIndex);
							col_2_div1.appendChild(input_endIndex);
							col_2_div1.appendChild(input_commentId);
							col_2_div1.appendChild(input_refId);
							col_2_div1.appendChild(input_writer);
							col_2_div1.appendChild(input_pw);

							rowDiv2.appendChild(col_2_div1);
							rowDiv2.appendChild(col_10_div1);

							form.appendChild(rowDiv2);
							form.appendChild(rowDiv3);

							td4.appendChild(form);

							tr2.appendChild(td4);

							tbody.appendChild(tr1);
							tbody.appendChild(tr2);
						} else {//대댓글이 있으면서 삭제된 댓글
							let tr1 = document.createElement('tr');

							let td1 = document.createElement('td');
							td1.colSpan = '3';

							let span1 = document.createElement('span');
							span1.classList.add('custom-delete-comment');
							span1.textContent = '해당 댓글은 삭제되었습니다.';

							//노드 추가
							td1.appendChild(span1);

							tr1.appendChild(td1);

							tbody.appendChild(tr1);
						}
					} else {//대댓글

						let tr1 = document.createElement('tr');
						tr1.classList.add('table-secondary');

						let td1 = document.createElement('td');
						td1.classList.add('custom-pl-50px', 'custom-boardComment-writer_createdDate');
						td1.textContent = item.writer;

						let td2 = document.createElement('td');
						td2.classList.add('custom-pl-50px', 'custom-boardComment-content');
						td2.textContent = `┗ ${item.content}`;

						let td3 = document.createElement('td');
						td3.classList.add('custom-boardComment-writer_createdDate');
						td3.style.position = 'relative';
						td3.textContent = item.createdDate + '\u00A0';

						let span1 = document.createElement('span');
						span1.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
						span1.classList.add('custom-span1');
						span1.style.cursor = 'pointer';
						span1.textContent = 'X';

						let div1 = document.createElement('div');
						div1.id = `pwModal-${item.commentId}-${item.refId}-${item.refOrder}`;

						let div2 = document.createElement('div');
						div2.classList.add('custom-input1-wrapper');

						let input1 = document.createElement('input');
						input1.id = `deleteCommentInputPw-${item.commentId}-${item.refId}-${item.refOrder}`;
						input1.classList.add('custom-input1');
						input1.type = 'password';
						input1.placeholder = '비밀번호';

						let div3 = document.createElement('div');
						div3.classList.add('custom-checkAndX');

						let span2 = document.createElement('span');
						span2.classList.add('custom-span2');
						span2.onclick = function() { deleteComment(item.boardNum, item.commentId, item.refId, item.refOrder, item.childNum); };
						span2.textContent = '확인';

						let span3 = document.createElement('span');
						span3.classList.add('custom-span3');
						span3.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
						span3.textContent = 'X';

						//노드 추가
						div3.appendChild(span2);
						div3.appendChild(span3);

						div2.appendChild(input1);
						div2.appendChild(div3);

						div1.appendChild(div2);

						td3.appendChild(span1);
						td3.appendChild(div1);

						tr1.appendChild(td1);
						tr1.appendChild(td2);
						tr1.appendChild(td3);

						tbody.appendChild(tr1);
					}
				});

				//댓글 페이지네이션
				let rowDiv2 = document.createElement('div');
				rowDiv2.classList.add('row', 'mb-3');

				let col_1_div2 = document.createElement('div');
				col_1_div2.classList.add('col-1');

				let col_9_div2 = document.createElement('div');
				col_9_div2.classList.add('col-9', 'text-center');

				//댓글 페이지네이션 계산에 필요한 변수들.
				let currentCommentPage = 1;//댓글, 대댓글 등록 후, 초기 댓글 페이지는 1페이지로 설정
				let commentPageLinkSize = 10; //한 페이지에 보여줄 페이지 링크 수
				let commentPageSize = 50;//한 페이지에 보여줄 댓글 수
				let totalCommentPageLink = Math.floor(data.commentCount / commentPageSize) + 1;//전체 페이지 링크 수

				// 현재 페이지 범위 계산
				let startPage = (Math.floor((currentCommentPage - 1) / commentPageLinkSize)) * commentPageLinkSize + 1;
				let endPage = Math.min(startPage + commentPageLinkSize - 1, totalCommentPageLink);

				//이전 페이지 링크 생성
				if (currentCommentPage > commentPageLinkSize) {
					let span1 = document.createElement('span');
					span1.classList.add('custom-commentPageLink');
					span1.onclick = function() { moveCommentPage(data.boardNum, (startPage - 1)); };
					span1.textContent = '◁';

					//노드 추가(이전 페이지)
					col_9_div2.appendChild(span1);
				}

				//페이지 링크 생성
				for (let i = startPage; i <= endPage; i++) {

					let isCurrentCommentPage = (i === currentCommentPage);

					if (isCurrentCommentPage) {//현재 페이지 링크는 onclick 속성 생성X, 글자: 빨강
						let span1 = document.createElement('span');
						span1.classList.add('custom-currentCommentPageLink');
						span1.textContent = i;

						//노드 추가(현재 페이지 링크)
						col_9_div2.appendChild(span1);
					} else {//다른 페이지 링크
						let span1 = document.createElement('span');
						span1.classList.add('custom-commentPageLink');
						span1.onclick = function() { moveCommentPage(data.boardNum, i); };
						span1.textContent = i;

						//노드 추가(다른 페이지 링크)
						col_9_div2.appendChild(span1);
					}
				}

				//다른 페이지 링크 생성
				if (endPage < totalCommentPageLink) {
					let span1 = document.createElement('span');
					span1.classList.add('custom-commentPageLink');
					span1.onclick = function() { moveCommentPage(data.boardNum, (endPage + 1)); };
					span1.textContent = '▷';

					//노드 추가(다음 페이지 링크)
					col_9_div2.appendChild(span1);
				}

				//노드 추가(페이지네이션)
				rowDiv2.appendChild(col_1_div2);
				rowDiv2.appendChild(col_9_div2);

				//선
				let rowDiv3 = document.createElement('div');
				rowDiv3.classList.add('row', 'mb-3');

				let col_1_div3 = document.createElement('div');
				col_1_div3.classList.add('col-1');

				let col_9_div3 = document.createElement('div');
				col_9_div3.classList.add('col-9', 'border-bottom', 'border-2', 'border-primary');

				//노드 추가(선)
				rowDiv3.appendChild(col_1_div3);
				rowDiv3.appendChild(col_9_div3);

				//노드 추가(전체)
				table.appendChild(tbody);
				col_9_div1.appendChild(table);

				rowDiv1.appendChild(col_1_div1);
				rowDiv1.appendChild(col_9_div1);

				commentRow.appendChild(rowDiv1);
				commentRow.appendChild(rowDiv2);
				commentRow.appendChild(rowDiv3);

				//댓글을 등록하고 난 후, 입력상자 reset
				if (commentId === 'parent') {
					f.reset();
				}

			}//if (jsonArray !== null)

			return;
		})
		.catch(error => {
			console.log(error);
			alert('네트워크 오류가 발생했습니다. 다시 시도해 주세요.');
			return;
		})

}//checkAndInsertComment() function


/*** 대댓글 form(열기, 닫기) */
function toggleInsertSubComment(commentId) {
	let insertSubComments = document.querySelectorAll(`tr[id^="insertSubComment"]`);
	let inputWriters = document.querySelectorAll(`input[id^="insertSubCommentInputWriter"]`);
	let inputPw = document.querySelectorAll(`input[id^="insertSubCommentInputPw"]`);
	let contentTextareas = document.querySelectorAll(`textarea[id^="insertSubCommentTextarea"]`);

	//새로운 댓글 폼 닫거나 열 때 닉네임(input) 초기화
	inputWriters.forEach(input => {
		input.value = ``;
	});

	//새로운 댓글 폼 닫거나 열 때 비밀번호(input) 초기화
	inputPw.forEach(input => {
		input.value = ``;
	});

	//새로운 댓글 폼 닫거나 열 때 댓글 내용(textarea) 초기화
	contentTextareas.forEach(textarea => {
		textarea.value = ``;
	});

	//새로운 댓글 폼 열 때 다른 댓글 폼 닫기
	insertSubComments.forEach(tr => {
		if (tr.id === `insertSubComment${commentId}`) {
			tr.style.display = tr.style.display === 'none' ? 'table-row' : 'none';
		} else {
			tr.style.display = 'none';
		}
	});
}


/*** 모달(댓글 삭제) */
function pwModal(commentId, refId, refOrder) {
	let initPwModals = document.querySelectorAll('[id^="pwModal-"]');
	let initInputPws = document.querySelectorAll('[id^="deleteCommentInputPw-"]');

	//새로운 모달 열 때 다른 모달 입력한 내용 지우기
	initInputPws.forEach(input => {
		input.value = ``;
	});

	//새로운 모달 열 때 다른 모달 닫기.
	initPwModals.forEach(modal => {
		if (modal.id === `pwModal-${commentId}-${refId}-${refOrder}`) {
			modal.style.display = (modal.style.display === 'none' || modal.style.display === '') ? 'block' : 'none';
		} else {

			modal.style.display = 'none';
		}
	});
}

/*** 댓글 삭제 */
function deleteComment(boardNum, commentId, refId, refOrder, childNum) {
	let sortingOption = document.getElementById('sortingOption');
	let pw = document.getElementById(`deleteCommentInputPw-${commentId}-${refId}-${refOrder}`);

	if (!pw.value) {
		alert('비밀번호를 입력하세요.');
		return;
	} else if (pw.value.trim() !== pw.value) {
		alert('[비밀번호] 항목은 처음과 마지막에 공백을 사용할 수 없습니다.');
		return;
	}

	// JSON 객체 생성
	let data = {
		sortingOption: sortingOption.value,
		pw: pw.value,
		boardNum: boardNum,
		commentId: commentId,
		refId: refId,
		refOrder: refOrder,
		childNum: childNum
	};

	// 서블릿 경로
	let url = "deleteComment.comment";

	fetch(url, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(data)
	})
		.then(response => response.json())
		.then(data => {
			if (data.sortingOptionError) {
				alert(data.sortingOptionError);
				return;
			}
			if (data.pwError) {
				alert(data.pwError);
				return;
			}
			if (data.numError) {
				alert(data.numError);
				return;
			}
			if (data.jsonObjectError) {
				alert(data.jsonObjectError);
				return;
			}
			if (data.pwMismatch) {
				alert(data.pwMismatch);
				return;
			}
			if (data.deleteFalse) {
				alert(data.deleteFalse);
				return;
			}

			alert('삭제되었습니다');

			//댓글 개수
			let commentCount = document.getElementById('custom-commentCount');
			commentCount.textContent = data.commentCount;

			//댓글 출력
			let jsonArray = JSON.parse(data.jsonArray);//"jsonArray" 키의 문자열을 파싱하여 배열로 변환

			let commentRow = document.getElementById('commentRow');
			commentRow.innerHTML = ``;

			if (jsonArray !== null) {

				let rowDiv1 = document.createElement('div');
				rowDiv1.classList.add('row', 'mb-3');

				let col_1_div1 = document.createElement('div');
				col_1_div1.classList.add('col-1');

				let col_9_div1 = document.createElement('div');
				col_9_div1.classList.add('col-9');

				let table = document.createElement('table');
				table.classList.add('table');

				let tbody = document.createElement('tbody');

				jsonArray.forEach(item => {
					if (item.refId == 0) {//댓글
						if (item.isDelete === 'N') {//대댓글이 있으면서 삭제안된 댓글
							let tr1 = document.createElement('tr');

							let td1 = document.createElement('td');
							td1.classList.add('custom-boardComment-writer_createdDate');
							td1.textContent = item.writer;

							let td2 = document.createElement('td');
							td2.classList.add('custom-boardComment-content');
							td2.style.cursor = 'pointer';
							td2.onclick = function() { toggleInsertSubComment(item.commentId); };
							td2.textContent = item.content;

							let td3 = document.createElement('td');
							td3.classList.add('custom-boardComment-writer_createdDate');
							td3.style.position = 'relative';
							td3.textContent = item.createdDate + '\u00A0';

							let span1 = document.createElement('span');
							span1.classList.add('custom-span1');
							span1.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
							span1.style.cursor = 'pointer';
							span1.textContent = 'X';

							let div1 = document.createElement('div');
							div1.id = `pwModal-${item.commentId}-${item.refId}-${item.refOrder}`;

							let div2 = document.createElement('div');
							div2.classList.add('custom-input1-wrapper');

							let input1 = document.createElement('input');
							input1.id = `deleteCommentInputPw-${item.commentId}-${item.refId}-${item.refOrder}`;
							input1.classList.add('custom-input1');
							input1.type = 'password';
							input1.placeholder = '비밀번호';

							let div3 = document.createElement('div');
							div3.classList.add('custom-checkAndX');

							let span2 = document.createElement('span');
							span2.classList.add('custom-span2');
							span2.onclick = function() { deleteComment(item.boardNum, item.commentId, item.refId, item.refOrder, item.childNum); };
							span2.textContent = '확인';

							let span3 = document.createElement('span');
							span3.classList.add('custom-span3');
							span3.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
							span3.textContent = 'X';

							//노드 추가
							div3.appendChild(span2);
							div3.appendChild(span3);

							div2.appendChild(input1);
							div2.appendChild(div3);

							div1.appendChild(div2);

							td3.appendChild(span1);
							td3.appendChild(div1);

							tr1.appendChild(td1);
							tr1.appendChild(td2);
							tr1.appendChild(td3);

							//대댓글 쓰기, 댓글의 내용을 누르기 전에는 숨겨진 상태
							let tr2 = document.createElement('tr');
							tr2.id = `insertSubComment${item.commentId}`;
							tr2.classList.add('table-secondary');
							tr2.style.display = 'none';

							let td4 = document.createElement('td');
							td4.classList.add('custom-pl-50px');
							td4.colSpan = 3;

							let form = document.createElement('form');
							form.action = 'insertComment.comment';
							form.method = 'post';
							form.name = `f-comment-${item.commentId}`;

							let rowDiv2 = document.createElement('div');
							rowDiv2.classList.add('row', 'mb-3');

							let col_2_div1 = document.createElement('div');
							col_2_div1.classList.add('col-2');

							let input_num = document.createElement('input');
							input_num.type = 'hidden';
							input_num.name = 'num';
							input_num.value = item.boardNum;

							let input_startIndex = document.createElement('input');
							input_startIndex.type = 'hidden';
							input_startIndex.name = 'startIndex';
							input_startIndex.value = data.startIndex;

							let input_endIndex = document.createElement('input');
							input_endIndex.type = 'hidden';
							input_endIndex.name = 'endIndex';
							input_endIndex.value = data.endIndex;

							let input_commentId = document.createElement('input');
							input_commentId.type = 'hidden';
							input_commentId.name = 'commentId';
							input_commentId.value = item.commentId;

							let input_refId = document.createElement('input');
							input_refId.type = 'hidden';
							input_refId.name = 'refId';
							input_refId.value = item.commentId;

							let input_writer = document.createElement('input');
							input_writer.type = 'text';
							input_writer.id = `insertSubCommentInputWriter${item.commentId}`;
							input_writer.classList.add('form-control', 'mb-2');
							input_writer.name = 'writer';
							input_writer.placeholder = '닉네임';
							input_writer.maxLength = 20;

							let input_pw = document.createElement('input');
							input_pw.type = 'password';
							input_pw.id = `insertSubCommentInputPw${item.commentId}`;
							input_pw.classList.add('form-control');
							input_pw.name = 'pw';
							input_pw.placeholder = '비밀번호';
							input_pw.maxLength = 20;

							let col_10_div1 = document.createElement('div');
							col_10_div1.classList.add('col-10');

							let textarea = document.createElement('textarea');
							textarea.id = `insertSubCommentTextarea${item.commentId}`;
							textarea.classList.add('form-control');
							textarea.name = 'content';
							textarea.style.width = '100%';
							textarea.style.height = '100%';
							textarea.style.resize = 'none';
							textarea.maxLength = 400;

							let small = document.createElement('small');
							small.classList.add('text-muted');
							small.textContent = '최대 400자까지 입력 가능합니다.';

							let rowDiv3 = document.createElement('div');
							rowDiv3.classList.add('row');

							let col_9_div2 = document.createElement('div');
							col_9_div2.classList.add('col-9');

							let col_3_div1 = document.createElement('div');
							col_3_div1.classList.add('col-3');

							let button1 = document.createElement('button');
							button1.type = 'button';
							button1.classList.add('btn', 'btn-primary');
							button1.onclick = function() { checkAndInsertComment(item.commentId); };
							button1.textContent = '등록';

							let button2 = document.createElement('button');
							button2.type = 'reset';
							button2.classList.add('btn', 'btn-secondary');
							button2.textContent = '다시쓰기';

							//노드 추가
							col_3_div1.appendChild(button1);
							col_3_div1.appendChild(button2);

							rowDiv3.appendChild(col_9_div2);
							rowDiv3.appendChild(col_3_div1);

							col_10_div1.appendChild(textarea);
							col_10_div1.appendChild(small);

							col_2_div1.appendChild(input_num);
							col_2_div1.appendChild(input_startIndex);
							col_2_div1.appendChild(input_endIndex);
							col_2_div1.appendChild(input_commentId);
							col_2_div1.appendChild(input_refId);
							col_2_div1.appendChild(input_writer);
							col_2_div1.appendChild(input_pw);

							rowDiv2.appendChild(col_2_div1);
							rowDiv2.appendChild(col_10_div1);

							form.appendChild(rowDiv2);
							form.appendChild(rowDiv3);

							td4.appendChild(form);

							tr2.appendChild(td4);

							tbody.appendChild(tr1);
							tbody.appendChild(tr2);
						} else {//대댓글이 있으면서 삭제된 댓글
							let tr1 = document.createElement('tr');

							let td1 = document.createElement('td');
							td1.colSpan = '3';

							let span1 = document.createElement('span');
							span1.classList.add('custom-delete-comment');
							span1.textContent = '해당 댓글은 삭제되었습니다.';

							//노드 추가
							td1.appendChild(span1);

							tr1.appendChild(td1);

							tbody.appendChild(tr1);
						}
					} else {//대댓글

						let tr1 = document.createElement('tr');
						tr1.classList.add('table-secondary');

						let td1 = document.createElement('td');
						td1.classList.add('custom-pl-50px', 'custom-boardComment-writer_createdDate');
						td1.textContent = item.writer;

						let td2 = document.createElement('td');
						td2.classList.add('custom-pl-50px', 'custom-boardComment-content');
						td2.textContent = `┗ ${item.content}`;

						let td3 = document.createElement('td');
						td3.classList.add('custom-boardComment-writer_createdDate');
						td3.style.position = 'relative';
						td3.textContent = item.createdDate + '\u00A0';

						let span1 = document.createElement('span');
						span1.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
						span1.classList.add('custom-span1');
						span1.style.cursor = 'pointer';
						span1.textContent = 'X';

						let div1 = document.createElement('div');
						div1.id = `pwModal-${item.commentId}-${item.refId}-${item.refOrder}`;

						let div2 = document.createElement('div');
						div2.classList.add('custom-input1-wrapper');

						let input1 = document.createElement('input');
						input1.id = `deleteCommentInputPw-${item.commentId}-${item.refId}-${item.refOrder}`;
						input1.classList.add('custom-input1');
						input1.type = 'password';
						input1.placeholder = '비밀번호';

						let div3 = document.createElement('div');
						div3.classList.add('custom-checkAndX');

						let span2 = document.createElement('span');
						span2.classList.add('custom-span2');
						span2.onclick = function() { deleteComment(item.boardNum, item.commentId, item.refId, item.refOrder, item.childNum); };
						span2.textContent = '확인';

						let span3 = document.createElement('span');
						span3.classList.add('custom-span3');
						span3.onclick = function() { pwModal(item.commentId, item.refId, item.refOrder); };
						span3.textContent = 'X';

						//노드 추가
						div3.appendChild(span2);
						div3.appendChild(span3);

						div2.appendChild(input1);
						div2.appendChild(div3);

						div1.appendChild(div2);

						td3.appendChild(span1);
						td3.appendChild(div1);

						tr1.appendChild(td1);
						tr1.appendChild(td2);
						tr1.appendChild(td3);

						tbody.appendChild(tr1);
					}
				});

				//댓글 페이지네이션
				let rowDiv2 = document.createElement('div');
				rowDiv2.classList.add('row', 'mb-3');

				let col_1_div2 = document.createElement('div');
				col_1_div2.classList.add('col-1');

				let col_9_div2 = document.createElement('div');
				col_9_div2.classList.add('col-9', 'text-center');

				//댓글 페이지네이션 계산에 필요한 변수들.
				let currentCommentPage = 1;//댓글, 대댓글 등록 후, 초기 댓글 페이지는 1페이지로 설정
				let commentPageLinkSize = 10; //한 페이지에 보여줄 페이지 링크 수
				let commentPageSize = 50;//한 페이지에 보여줄 댓글 수
				let totalCommentPageLink = Math.floor(data.commentCount / commentPageSize) + 1;//전체 페이지 링크 수

				// 현재 페이지 범위 계산
				let startPage = (Math.floor((currentCommentPage - 1) / commentPageLinkSize)) * commentPageLinkSize + 1;
				let endPage = Math.min(startPage + commentPageLinkSize - 1, totalCommentPageLink);

				//이전 페이지 링크 생성
				if (currentCommentPage > commentPageLinkSize) {
					let span1 = document.createElement('span');
					span1.classList.add('custom-commentPageLink');
					span1.onclick = function() { moveCommentPage(data.boardNum, (startPage - 1)); };
					span1.textContent = '◁';

					//노드 추가(이전 페이지)
					col_9_div2.appendChild(span1);
				}

				//페이지 링크 생성
				for (let i = startPage; i <= endPage; i++) {

					let isCurrentCommentPage = (i === currentCommentPage);

					if (isCurrentCommentPage) {//현재 페이지 링크는 onclick 속성 생성X, 글자: 빨강
						let span1 = document.createElement('span');
						span1.classList.add('custom-currentCommentPageLink');
						span1.textContent = i;

						//노드 추가(현재 페이지 링크)
						col_9_div2.appendChild(span1);
					} else {//다른 페이지 링크
						let span1 = document.createElement('span');
						span1.classList.add('custom-commentPageLink');
						span1.onclick = function() { moveCommentPage(data.boardNum, i); };
						span1.textContent = i;

						//노드 추가(다른 페이지 링크)
						col_9_div2.appendChild(span1);
					}
				}

				//다른 페이지 링크 생성
				if (endPage < totalCommentPageLink) {
					let span1 = document.createElement('span');
					span1.classList.add('custom-commentPageLink');
					span1.onclick = function() { moveCommentPage(data.boardNum, (endPage + 1)); };
					span1.textContent = '▷';

					//노드 추가(다음 페이지 링크)
					col_9_div2.appendChild(span1);
				}

				//노드 추가(페이지네이션)
				rowDiv2.appendChild(col_1_div2);
				rowDiv2.appendChild(col_9_div2);

				//선
				let rowDiv3 = document.createElement('div');
				rowDiv3.classList.add('row', 'mb-3');

				let col_1_div3 = document.createElement('div');
				col_1_div3.classList.add('col-1');

				let col_9_div3 = document.createElement('div');
				col_9_div3.classList.add('col-9', 'border-bottom', 'border-2', 'border-primary');

				//노드 추가(선)
				rowDiv3.appendChild(col_1_div3);
				rowDiv3.appendChild(col_9_div3);

				//노드 추가(전체)
				table.appendChild(tbody);
				col_9_div1.appendChild(table);

				rowDiv1.appendChild(col_1_div1);
				rowDiv1.appendChild(col_9_div1);

				commentRow.appendChild(rowDiv1);
				commentRow.appendChild(rowDiv2);
				commentRow.appendChild(rowDiv3);

			}//if (jsonArray !== null)

			return;
		})
		.catch(error => {
			console.log(error);
			alert('네트워크 오류가 발생했습니다. 다시 시도해 주세요.');
			return;
		})
}


/*** f-search 폼 유효성 검사, 제출 */
function checkAndSearch() {
	let f = document.forms['f-search'];

	if (!f.keyword.value) {
		alert('검색어를 입력하세요.');
		f.keyword.focus();
		return;
	}

	f.submit();
	return;
}
