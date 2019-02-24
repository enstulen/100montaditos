package com.uc3m.a100montaditos;

public class User {
    String uid;
    String email;
    String displayName;

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public User(String uid, String email, String displayName) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
