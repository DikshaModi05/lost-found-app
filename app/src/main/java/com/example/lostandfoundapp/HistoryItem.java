package com.example.lostandfoundapp;

import java.io.Serializable;

public class HistoryItem implements Serializable {
    private String itemType;
    private String itemName;
    private String itemDate;
    private String imageUrl;
    private long timestamp;
    private String description;
    private String reportedBy;
    private String location;

    private String status; // NEW ✅
    private String userId; // NEW ✅
    private String id;

    public HistoryItem() {}

    public HistoryItem(String itemType, String itemName, String itemDate, String imageUrl,
                       long timestamp, String description, String reportedBy, String location,
                       String status, String userId, String id) {
        this.itemType = itemType;
        this.itemName = itemName;
        this.itemDate = itemDate;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.description = description;
        this.reportedBy = reportedBy;
        this.location = location;
        this.status = status;
        this.userId = userId;
        this.id = id;
    }

    public String getItemType() { return itemType; }
    public String getItemName() { return itemName; }
    public String getItemDate() { return itemDate; }
    public String getImageUrl() { return imageUrl; }
    public long getTimestamp() { return timestamp; }
    public String getDescription() { return description; }
    public String getReportedBy() { return reportedBy; }
    public String getLocation() { return location; }
    public String getStatus() { return status; } // ✅
    public String getUserId() { return userId; } // ✅
    public String getId() {
        return id;
    }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setItemDate(String itemDate) { this.itemDate = itemDate; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setDescription(String description) { this.description = description; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
    public void setLocation(String location) { this.location = location; }
    public void setStatus(String status) { this.status = status; }
    public void setUserId(String userId) { this.userId = userId; }

    public void setId(String id) { this.id = id; }
}
