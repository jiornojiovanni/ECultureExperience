package com.jannuzzi.ecultureexperience.data;

public class Path {
    private String name, description, tag;

    public Path(String name, String description, String tag) {
        this.name = name;
        this.description = description;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Path{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
