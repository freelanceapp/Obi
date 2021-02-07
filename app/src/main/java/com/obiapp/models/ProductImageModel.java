package com.obiapp.models;

import java.io.Serializable;

public class ProductImageModel implements Serializable {
    private int id;
    private String image;
    private String type = "image";

    public ProductImageModel() {
    }

    public ProductImageModel(int id, String name, String type) {
        this.id = id;
        this.image = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.image = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
