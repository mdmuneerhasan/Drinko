package com.drink.drinko;

class User {
    String name,email,userType,profile,contact,location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User(String name, String email, String userType, String profile, String contact, String location) {
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.profile = profile;
        this.contact = contact;
        this.location = location;
    }

    public User() {
    }
}
