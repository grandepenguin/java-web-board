/*게시판 테이블*/
CREATE TABLE board(
num NUMBER PRIMARY KEY,/*게시판 번호*/
writer NVARCHAR2(20) NOT NULL,/*작성자*/
pw VARCHAR2(20) NOT NULL,/*비밀번호*/
title NVARCHAR2(40) NOT NULL,/*제목*/
content NVARCHAR2(2000)NOT NULL,/*내용*/
createdDate DATE DEFAULT SYSDATE,/*작성일*/
views NUMBER DEFAULT 0,/*조회수*/
searchPos NUMBER/*검색 최적화를 위한 컬럼 ex) num 컬럼 1만부터 1씩 증가(0부터 시작)*/
);

/*검색 최적화를 위한 인덱스 생성, 사람들이 보는 글은 대부분 최신글이니 DESC(내림차순)정렬
  num은 기본키라서 자동 인덱스가 있지만 자동 인덱스는 오름차순임.*/
CREATE INDEX ix_board_num ON board (num DESC);


/*오라클은 여러 인덱스를 가진 컬럼이 들어오면 자동으로 최적의 인덱스 활용*/
CREATE INDEX ix_board_searchPos ON board (searchPos DESC);/*내림차순으로 정렬된 인덱스 생성*/


/*자동증가 시퀀스*/
CREATE SEQUENCE board_num/*board 테이블 (num)에 들어갈 값*/
START WITH 1
INCREMENT BY 1;


/*댓글 테이블*/
CREATE TABLE boardComment(
boardNum NUMBER,/*Board테이블의 Num참조*/
commentId NUMBER,/*댓글 순서*/
refId NUMBER,/*대댓글이 참조하는 id*/
refOrder NUMBER,/*대댓글 순서*/
childNum NUMBER DEFAULT 0,/*대댓글 수*/
isDelete char(1) DEFAULT 'N',/*삭제여부 Y, N*/
writer NVARCHAR2(20) NOT NULL,/*작성자*/
pw VARCHAR2(20) NOT NULL,/*비밀번호*/
content NVARCHAR2(400)NOT NULL,/*내용 400자 제한*/
createdDate DATE DEFAULT SYSDATE,/*작성일*/
FOREIGN KEY (boardNum) REFERENCES board(num) ON DELETE CASCADE,
/*ON DELETE CASCADE 속성을 넣어 게시물이 삭제되면 댓글도 전부 삭제되도록 설정*/
PRIMARY KEY (boardNum, commentId, refId, refOrder)
);


/*boardComment 검색최적화를 위한 인덱스 생성*/
CREATE INDEX ix_boardComment_num ON boardComment (boardNum DESC);


----------------------------------------------------------------
--테이블 확인
SELECT table_name FROM user_tables;/*유저 테이블 보기*/


--인덱스 확인
SELECT index_name, table_name, table_owner
FROM all_indexes
WHERE owner = 'BOARD';


--시퀀스 확인
SELECT sequence_name, min_value, max_value, increment_by, last_number
FROM all_sequences
WHERE sequence_owner = 'BOARD';


----------------------------------------------------------------
--테이블 드랍
DROP TABLE board;
DROP TABLE boardComment;

--시퀀스 드랍
DROP SEQUENCE board_num;

--인덱스 드랍
DROP INDEX 인덱스 명;
