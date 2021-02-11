package com.obiapp.models.google_models;

import java.io.Serializable;

public class KeywordModel implements Serializable {
    private String keyword;
    private String name;

    public KeywordModel(String keyword, String name) {
        this.keyword = keyword;
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
