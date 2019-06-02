package com.drink.drinko;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String id;
    String location;
    String latitude;
    String longitude;
    String name,email,userType,profile,contact;
    public Storage(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences("user",Context.MODE_PRIVATE);
        id=sharedPreferences.getString("id",null);
        location=sharedPreferences.getString("location","please update location...");
        name=sharedPreferences.getString("name",null);
        email=sharedPreferences.getString("email",null);
        userType=sharedPreferences.getString("userType",null);
        profile=sharedPreferences.getString("profile",null);
        contact=sharedPreferences.getString("contact",null);
        latitude= sharedPreferences.getString("latitude",null);
        longitude=sharedPreferences.getString("longitude",null);
        editor=sharedPreferences.edit();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        editor.putString("id",id);
        editor.apply();
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        editor.putString("location",location);
        editor.apply();
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        editor.putString("latitude", latitude);
        editor.apply();
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        editor.putString("longitude", longitude);
        editor.apply();
        this.longitude = longitude;
    }

    public void setName(String name) {
        editor.putString("name",name);
        editor.apply();
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {

        editor.putString("email", email);
        editor.apply();
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        editor.putString("userType",userType);
        editor.apply();
        this.userType = userType;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        editor.putString("profile",profile);
        editor.apply();
        this.profile = profile;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        editor.putString("contact",contact);
        editor.apply();
        this.contact = contact;
    }

    public void clear() {
        editor.putString("id",null);
        editor.apply();
        this.id = null;
    }

    public String getString(String key) {
        return sharedPreferences.getString(key,null);
    }

    public void setString(String key, String value) {
        editor.putString(key,value);
        editor.apply();
    }
}
