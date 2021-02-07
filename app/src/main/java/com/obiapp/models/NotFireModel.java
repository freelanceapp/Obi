package com.obiapp.models;

import java.io.Serializable;

public class NotFireModel implements Serializable {
    boolean update;

    public NotFireModel(boolean update) {
        this.update = update;
    }

    public boolean isUpdate() {
        return update;
    }
}
