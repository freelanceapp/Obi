package com.obiapp.models;

import java.io.Serializable;
import java.util.List;

public class DepartmentDataModel extends StatusResponse  implements Serializable {
    private List<DepartmentModel> date;

    public List<DepartmentModel> getData() {
        return date;
    }
}
