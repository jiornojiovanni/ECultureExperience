package com.jannuzzi.ecultureexperience.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String age;
    private String lastName;
    private String email;


    public LoggedInUser(String userId, String displayName, String age, String lastName, String email) {
        this.userId = userId;
        this.displayName = displayName;
        this.age = age;
        this.lastName = lastName;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAge() {
        return age;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}