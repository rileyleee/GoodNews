package com.saveurlife.goodnews.ble.message;

public class MessageHelp extends MessageBase{
    private String senderGender = "남";
    private String senderBloodType = "AB RH+";
    private String senderAddInfo = "멀쩡함";
    private String content = "구조요청";

    public String getSenderGender() {
        return senderGender;
    }
    public void setSenderGender(String senderGender) {
        this.senderGender = senderGender;
    }
    public String getSenderBloodType() {
        return senderBloodType;
    }
    public void setSenderBloodType(String senderBloodType) {
        this.senderBloodType = senderBloodType;
    }
    public String getSenderAddInfo() {
        return senderAddInfo;
    }
    public void setSenderAddInfo(String senderAddInfo) {
        this.senderAddInfo = senderAddInfo;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return super.toString() + '/' + senderGender + '/' + senderBloodType + '/' + senderAddInfo + '/' + content;
    }
}
