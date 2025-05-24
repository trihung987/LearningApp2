package me.trihung.learningapp2.Entity.dto;

public class ReviewRequest {
    private int level;
    private String requirement;
    private String content;

    public ReviewRequest(int level, String requirement, String content) {
        this.level = level;
        this.requirement = requirement;
        this.content = content;
    }

}
