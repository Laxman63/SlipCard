package com.silpe.vire.slip.dtos;

import android.support.annotation.NonNull;

public class GeoMessage {

    private String uid;
    private String mid;
    private String message;
    private Double latitude;
    private Double longitude;
    private Long timestamp;

    public GeoMessage() {
        uid = null;
        mid = null;
        message = null;
        latitude = null;
        longitude = null;
        timestamp = null;
    }

    public GeoMessage(@NonNull String uid,
                      @NonNull String mid, @NonNull String message,
                      @NonNull Double latitude, @NonNull Double longitude,
                      @NonNull Long timestamp) {
        this();
        this.uid = uid;
        this.mid = mid;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
