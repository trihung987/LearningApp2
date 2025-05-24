package me.trihung.learningapp2.Entity.dto;

public class SearchRequest {
    private String keyword;
    private String context;

    public SearchRequest() {}

    public SearchRequest(String keyword, String context) {
        this.keyword = keyword;
        this.context = context;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}