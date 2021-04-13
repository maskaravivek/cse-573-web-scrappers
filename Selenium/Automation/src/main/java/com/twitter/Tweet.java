package com.twitter;

import com.opencsv.bean.CsvBindByPosition;

public class Tweet {
	@CsvBindByPosition(position = 4)
	private String body;
	
	@CsvBindByPosition(position = 6)
	private String commentCount;
	
	@CsvBindByPosition(position = 7)
	private String retweetCount;
	
	@CsvBindByPosition(position = 5)
	private String likeCount;
	
	@CsvBindByPosition(position = 0)
	private String link;
	
	@CsvBindByPosition(position = 1)
	private String id;
	
	@CsvBindByPosition(position = 2)
	private String username;
	
	@CsvBindByPosition(position = 3)
	private String time;
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}
	public String getRetweetCount() {
		return retweetCount;
	}
	public void setRetweetCount(String retweetCount) {
		this.retweetCount = retweetCount;
	}
	public String getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(String likeCount) {
		this.likeCount = likeCount;
	}
	
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
	public Tweet(String body, String commentCount, String retweetCount, String likeCount, String link, String id,
			String username, String time) {
		this.body = body;
		this.commentCount = commentCount;
		this.retweetCount = retweetCount;
		this.likeCount = likeCount;
		this.link = link;
		this.id = id;
		this.username = username;
		this.time = time;
	}
	public Tweet() {
		
	}
	
	
}
