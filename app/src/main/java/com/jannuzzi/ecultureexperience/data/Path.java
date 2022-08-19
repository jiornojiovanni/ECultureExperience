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
}
