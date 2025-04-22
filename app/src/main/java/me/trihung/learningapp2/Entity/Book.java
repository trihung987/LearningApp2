package me.trihung.learningapp2.Entity;

import java.io.Serializable;

public class Book implements Serializable {
    private int resoutceId;
    private String title;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public Book(int resoutceId, String title) {
        this.resoutceId = resoutceId;
        this.title = title;
        this.type = "";
    }
    public Book(int resoutceId, String title, String type) {
        this.resoutceId = resoutceId;
        this.title = title;
        this.type = type;
    }

    public int getResoutceId() {
        return resoutceId;
    }

    public void setResoutceId(int resoutceId) {
        this.resoutceId = resoutceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
