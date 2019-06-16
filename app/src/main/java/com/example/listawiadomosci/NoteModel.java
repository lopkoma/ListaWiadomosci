package com.example.listawiadomosci;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoteModel {

    @Expose
    @SerializedName("_id")
    private String id;

    @Expose
    @SerializedName("title")
    private String title;


    @Expose
    @SerializedName("content")
    private String content;


    public NoteModel() {
    }

    public NoteModel(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public NoteModel(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
