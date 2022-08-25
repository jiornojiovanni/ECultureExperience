package com.jannuzzi.ecultureexperience.data;

public class Path {
    private String name;
    private String description;
    private String tag;
    private final String imagePath;

    public Path(String name, String description, String tag, String imagePath) {
        this.name = name;
        this.description = description;
        this.tag = tag;
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Path{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tag='" + tag + '\'' +
                ", imagePath='" + imagePath + '\'' +
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

    public String getImagePath() {
        return imagePath;
    }
}
