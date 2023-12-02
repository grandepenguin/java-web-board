package vo;

import java.io.Serializable;

public class BoardComment implements Serializable {

	private static final long serialVersionUID = 1L;

	private int boardNum;
	private int commentId;
	private int refId;
	private int refOrder;
	private int childNum;
	private String isDelete;
	private String writer;
	private String pw;
	private String content;
	private String createdDate;

	public BoardComment() {
	}

	public BoardComment(int boardNum, int commentId, int refId, int refOrder, int childNum, String isDelete,
			String writer, String pw, String content, String createdDate) {
		super();
		this.boardNum = boardNum;
		this.commentId = commentId;
		this.refId = refId;
		this.refOrder = refOrder;
		this.childNum = childNum;
		this.isDelete = isDelete;
		this.writer = writer;
		this.pw = pw;
		this.content = content;
		this.createdDate = createdDate;
	}

	public int getBoardNum() {
		return boardNum;
	}

	public void setBoardNum(int boardNum) {
		this.boardNum = boardNum;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

	public int getRefOrder() {
		return refOrder;
	}

	public void setRefOrder(int refOrder) {
		this.refOrder = refOrder;
	}

	public int getChildNum() {
		return childNum;
	}

	public void setChildNum(int childNum) {
		this.childNum = childNum;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
