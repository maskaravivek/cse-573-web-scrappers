package com.twitter;

import com.opencsv.bean.CsvBindByPosition;

public class Tweet {
	@CsvBindByPosition(position = 0)
	private String body;
	
	@CsvBindByPosition(position = 1)
	private String commentCount;
	
	@CsvBindByPosition(position = 2)
	private String retweetCount;
	
	@CsvBindByPosition(position = 3)
	private String likeCount;
	
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
	public Tweet(String body, String commentCount, String retweetCount, String likeCount) {
		this.body = body;
		this.commentCount = commentCount;
		this.retweetCount = retweetCount;
		this.likeCount = likeCount;
	}
	
	public Tweet() {
		
	}
	
	
}
