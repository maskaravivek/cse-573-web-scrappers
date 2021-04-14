package com.reddit;

import com.opencsv.bean.CsvBindByPosition;

public class RedditPost {
    @CsvBindByPosition(position = 3)
    private String postedBy;

    @CsvBindByPosition(position = 4)
    private String postedAt;

    @CsvBindByPosition(position = 5)
    private String comments;

    @CsvBindByPosition(position = 0)
    private String postTitle;

    @CsvBindByPosition(position = 1)
    private String postUrl;

    @CsvBindByPosition(position = 2)
    private String votes;

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }


    public RedditPost(String postedBy, String postedAt, String comments, String postTitle,
                      String postUrl, String votes) {
        this.postedBy = postedBy;
        this.postedAt = postedAt;
        this.comments = comments;
        this.postTitle = postTitle;
        this.postUrl = postUrl;
        this.votes = votes;
    }

    public RedditPost() {

    }


}

