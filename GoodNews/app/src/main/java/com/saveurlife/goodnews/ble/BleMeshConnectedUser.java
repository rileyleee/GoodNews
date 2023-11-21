package com.saveurlife.goodnews.ble;


import java.io.Serializable;

public class BleMeshConnectedUser implements Serializable {
    private String userId;
    private String userName;
    private String updateTime;
    private String healthStatus;
    private double lat;
    private double lon;

    public BleMeshConnectedUser(String userId, String userName, String updateTime, String healthStatus, double lat, double lon) {
        this.userId = userId;
        this.userName = userName;
        this.updateTime = updateTime;
        this.healthStatus = healthStatus;
        this.lat = lat;
        this.lon = lon;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


    @Override
    public String toString() {
        return userId + '/' + userName + '/' + updateTime + '/' + healthStatus + '/' + lat + '/' + lon;
    }

    public String toInitString(){
        return userId + '-' + userName + '-' + updateTime + '-' + healthStatus + '-' + lat + '-' + lon;
    }
}