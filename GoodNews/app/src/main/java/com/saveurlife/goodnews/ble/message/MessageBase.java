package com.saveurlife.goodnews.ble.message;
public class MessageBase {
    private String messageType;
    private String senderId;
    private String senderName;
    private String sendTime;
    private String healthStatus;
    private String location;



    public String getMessageType() {
        return messageType;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public String getSenderName() {
        return senderName;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    public String getSendTime() {
        return sendTime;
    }
    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
    public String getHealthStatus() {
        return healthStatus;
    }
    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }



    @Override
    public String toString() {
        return messageType + '/' + senderId + '/' + senderName + '/' + sendTime + '/' + healthStatus + '/' + location;
    }
}
