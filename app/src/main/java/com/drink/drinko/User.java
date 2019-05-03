package com.drink.drinko;

class User {
    String name,email,userType,profile,contact,location,startTime,endTime,rating, numberOfRating;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getNumberOfRating() {
        return numberOfRating;
    }

    public void setNumberOfRating(String numberOfRating) {
        this.numberOfRating = numberOfRating;
    }

    public User(String name, String email, String userType, String profile, String contact, String location, String startTime, String endTime, String rating, String numberOfRating) {
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.profile = profile;
        this.contact = contact;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rating = rating;
        this.numberOfRating = numberOfRating;
    }

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
