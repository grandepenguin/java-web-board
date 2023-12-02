function goBack() {
	history.back();
}

function checkAndSubmit() {
	let pw = f.pw.value;

	if (!pw) {
		alert('비밀번호를 입력하세요.');
		f.pw.focus();
		return;
	} else if (pw.trim() !== pw) {
		alert('[비밀번호] 항목은 처음과 마지막에 공백을 사용할 수 없습니다.');
		f.pw.focus();
		return;
	} else {
		if (confirm('게시글을 삭제하면 복구가 안됩니다.\n게시글을 삭제하시겠습니까?')) {
			f.submit();
			return;
		} else {
			return;
		}
	}
}