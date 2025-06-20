package com.example.lostandfoundapp;

import java.io.Serializable;
import com.google.firebase.database.PropertyName;

public class ItemModel implements Serializable {

    private String title, description, type, userEmail, userId, imageUrl, date, location, status, id;

    public ItemModel() {}

    @PropertyName("itemName")
    public String getTitle() { return title; }
    @PropertyName("itemName")
    public void setTitle(String title) { this.title = title; }

    @PropertyName("description")
    public String getDescription() { return description; }
    @PropertyName("description")
    public void setDescription(String description) { this.description = description; }

    @PropertyName("itemType")
    public String getType() { return type; }
    @PropertyName("itemType")
    public void setType(String type) { this.type = type; }

    @PropertyName("reportedBy")
    public String getUserEmail() { return userEmail; }
    @PropertyName("reportedBy")
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    @PropertyName("userId")
    public String getUserId() { return userId; }
    @PropertyName("userId")
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("imageUrl")
    public String getImageUrl() { return imageUrl; }
    @PropertyName("imageUrl")
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @PropertyName("itemDate")
    public String getDate() { return date; }
    @PropertyName("itemDate")
    public void setDate(String date) { this.date = date; }

    @PropertyName("location")
    public String getLocation() { return location; }
    @PropertyName("location")
    public void setLocation(String location) { this.location = location; }

    @PropertyName("status")
    public String getStatus() { return status; }
    @PropertyName("status")
    public void setStatus(String status) { this.status = status; }

    @PropertyName("id")
    public String getId() { return id; }
    @PropertyName("id")
    public void setId(String id) { this.id = id; }
}
