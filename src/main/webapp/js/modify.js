function goBack() {
	history.back();
}

function checkAndFetch() {
	let num = f.num.value;
	let pw = f.pw.value;

	if (!pw) {
		alert('비밀번호를 입력하세요.');
		return;
	} else if (pw.trim() !== pw) {
		alert('[비밀번호] 항목은 처음과 마지막에 공백을 사용할 수 없습니다.');
		return;
	}

	// JSON 객체 생성
	let data = {
		num: num,
		pw: pw
	};

	// 서블릿 경로
	var url = "checkBoardPwToModify.board";

	fetch(url, {
		method: 'POST',
		body: JSON.stringify(data),
		headers: {
			'Content-Type': 'application/json'
		}
	})
		.then(response => response.json())
		.then(data => {
			if (data.jsonObjectError) {
				alert(data.jsonObjectError);
				return;
			}
			if (data.numError) {
				alert(data.numError);
				return;
			}
			if (data.pwError) {
				alert(data.pwError);
				return;
			}
			if (data.pwFalse) {
				alert(data.pwFalse);
				return;
			}
			if (data.writer || data.pw || data.title || data.content) {
				let pwCheckFormRow = document.getElementById('pwCheckFormRow');
				let realModifyForm = document.getElementById('realModifyForm');
				pwCheckFormRow.style.display = "none";
				realModifyForm.style.display = "block";

				document.forms['f2'].elements['writer'].value = data.writer;
				document.forms['f2'].elements['pw'].value = data.pw;
				document.forms['f2'].elements['title'].value = data.title;
				document.forms['f2'].elements['content'].value = data.content;

				return;
			}

			return;

		})
		.catch(error => {
			console.log(error);
			alert('네트워크 오류가 발생했습니다. 다시 시도해 주세요.');
			return;
		});
}

function confirmGoBack() {
	let result = confirm('글 작성을 취소하시겠습니까?');

	if (result == true) {
		history.back();
	}
}

function checkAndSubmit() {
	let title = f2.title.value;
	let content = f2.content.value;

	if (!title) {
		alert('제목을 입력해 주세요.');
		f2.title.focus();
		return;
	} else if (title.trim().length < 2) {
		alert('제목은 최소 2자 이상이어야 합니다.');
		f2.title.focus();
		return;
	}

	if (!content) {
		alert('내용을 입력해 주세요.');
		f2.content.focus();
		return;
	} else if (content.trim().length < 2) {
		alert('내용은 최소 2자 이상이어야 합니다.');
		f2.content.focus();
		return;
	}

	f2.submit();

	return;
}
