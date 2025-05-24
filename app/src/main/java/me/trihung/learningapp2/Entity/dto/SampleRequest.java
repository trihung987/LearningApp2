package me.trihung.learningapp2.Entity.dto;

public class SampleRequest {
    private String category;
    private String language;

    public SampleRequest(String category, String language) {
        this.category = category;
        this.language = language;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}