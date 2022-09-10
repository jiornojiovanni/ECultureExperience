package com.jannuzzi.ecultureexperience.data.model;

import java.io.Serializable;

public class Route implements Serializable {
    String title;
    String description;
    Boolean checked;

    public Route(String title, String description) {
        this.title = title;
        this.description = description;
        checked = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void flip() { checked = !checked; }

    public Boolean getChecked() { return checked; }

    @Override
    public String toString() {
        return "Route{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", checked=" + checked +
                '}';
    }
}
