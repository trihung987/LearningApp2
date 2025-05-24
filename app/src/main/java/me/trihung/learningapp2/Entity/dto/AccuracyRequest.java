package me.trihung.learningapp2.Entity.dto;


public class AccuracyRequest {
    private String base64Audio;
    private String language;
    private String title;

    public AccuracyRequest(String base64Audio, String language, String title) {
        this.base64Audio = base64Audio;
        this.language = language;
        this.title = title;
    }

    public String getBase64Audio() {
        return base64Audio;
    }

    public void setBase64Audio(String base64Audio) {
        this.base64Audio = base64Audio;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}