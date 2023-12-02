function confirmGoBack() {
	let result = confirm('글 작성을 취소하시겠습니까?');

	if (result == true) {
		history.back();
	}
}

function checkAndSubmit() {

	let writer = f.writer.value;
	let pw = f.pw.value;
	let title = f.title.value;
	let content = f.content.value;

	if (!writer) {
		alert('닉네임을 입력하세요.');
		f.writer.focus();
		return;
	} else if (writer.trim().length < 2) {
		alert('닉네임은 최소 2자 이상이어야 합니다.');
		f.writer.focus();
		return;
	}

	if (!pw) {//공백도 비밀번호 가능, 단, 앞뒤에 공백은 x(서버에서 처리)
		/*
		앞 뒤 공백 허용x 이유 :
		
		사용자 경험 (UX): 비밀번호의 처음이나 끝에 공백을 허용하면, 사용자가 비밀번호를 입력할 때,
		뜻밖의 스페이스바 입력으로 인해 혼란이 생길 수 있습니다.
		이로 인해 로그인 시도 시 오류가 발생하거나 사용자가 왜 로그인에 실패했는지 혼란스러워할 수 있습니다.
		*/
		alert('비밀번호를 입력하세요.');
		f.pw.focus();
		return;
	} else if (pw.trim() !== pw) {
		alert('[비밀번호] 항목은 처음과 마지막에 공백을 사용할 수 없습니다.');
		f.pw.focus();
		return;
	}

	if (!title) {
		alert('제목을 입력하세요.');
		f.title.focus();
		return;
	} else if (title.trim().length < 2) {
		alert('제목은 최소 2자 이상이어야 합니다.');
		f.title.focus();
		return;
	}

	if (!content) {
		alert('내용을 입력하세요.');
		f.content.focus();
		return;
	} else if (content.trim().length < 2) {
		alert('내용은 최소 2자 이상이어야 합니다.');
		f.content.focus();
		return;
	}

	f.submit();
	return;
}