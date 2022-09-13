package com.jannuzzi.ecultureexperience.data;

public class User {
    public String name, lastName, age, email;
    public int completedRoutes, completedGames;
    public User(){

    }

    public User(String name, String lastName, String age, String email){
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        completedRoutes = 0;
        completedGames = 0;
    }
}
