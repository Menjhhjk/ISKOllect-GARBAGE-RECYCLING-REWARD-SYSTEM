package com.iskollect.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {
    private int userId;
    private String name;
    private String username;
    private String webmail;
    private String password;
    private int age;
    private String profilePhoto;
    private double totalPoints;
    private int rawBottleCount;
    private int weeklyBottles;
    private int streak;
    private LocalDate lastSubmitDate;
    private String sessionToken;
    private LocalDateTime lastActivity;
    private LocalDateTime createdAt;

    //constructors
    public User() {}

    //for login
    public User(int id, String webmail, String password) {
        this.userId = id;
        this.webmail = webmail;
        this.password = password;
    }

    //for signup
    public User(String username, String webmail, String password) {
        this.username = username;
        this.name = username;
        this.webmail = webmail;
        this.password = password;
    }

    //complete User info
    public User(int userId, String username, String webmail, String password,
                   int age, String profilePhoto, double totalPoints,
                   int rawBottleCount,
                   String sessionToken, LocalDateTime lastActivity) {
        this.userId = userId;
        this.username = username;
        this.webmail = webmail;
        this.password = password;
        this.age = age;
        this.profilePhoto = profilePhoto;
        this.totalPoints = totalPoints;
        this.rawBottleCount = rawBottleCount;
        this.sessionToken = sessionToken;
        this.lastActivity = lastActivity;
    }

    //getters and setters
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name != null && !name.isBlank() ? name : username;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebmail() {
        return webmail;
    }
    public void setWebmail(String webmail) {
        this.webmail = webmail;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }
    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public double getTotalPoints() {
        return totalPoints;
    }
    public void setTotalPoints(double totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getRawBottleCount() {
        return rawBottleCount;
    }
    public void setRawBottleCount(int rawBottleCount) {
        this.rawBottleCount = rawBottleCount;
    }

    public int getWeeklyBottles() {
        return weeklyBottles;
    }
    public void setWeeklyBottles(int weeklyBottles) {
        this.weeklyBottles = weeklyBottles;
    }

    public int getStreak() {
        return streak;
    }
    public void setStreak(int streak) {
        this.streak = streak;
    }

    public LocalDate getLastSubmitDate() {
        return lastSubmitDate;
    }
    public void setLastSubmitDate(LocalDate lastSubmitDate) {
        this.lastSubmitDate = lastSubmitDate;
    }

    public String getSessionToken() {
        return sessionToken; }
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
