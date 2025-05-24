package me.trihung.learningapp2.Entity.dto;

public class SearchResponse {
    private String content;

    public SearchResponse() {}

    public SearchResponse(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}