package vo;

import java.io.Serializable;

import java.sql.Timestamp;

public class Board implements Serializable {

	private static final long serialVersionUID = 1L;

	private int num;
	private String writer;
	private String pw;
	private String title;
	private String content;
	private Timestamp createdDate;
	private int views;
	private int searchPos;

	public Board() {
	}

	public Board(int num, String writer, String pw, String title, String content, Timestamp createdDate, int views,
			int searchPos) {
		super();
		this.num = num;
		this.writer = writer;
		this.pw = pw;
		this.title = title;
		this.content = content;
		this.createdDate = createdDate;
		this.views = views;
		this.searchPos = searchPos;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getSearchPos() {
		return searchPos;
	}

	public void setSearchPos(int searchPos) {
		this.searchPos = searchPos;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
