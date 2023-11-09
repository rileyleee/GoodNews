package com.saveurlife.goodnews.ble.message;

public class MessageChat extends MessageBase{
    private String receiverId;
    private String content;

    public String getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return super.toString() + '/' + receiverId + '/' + content;
    }
}
