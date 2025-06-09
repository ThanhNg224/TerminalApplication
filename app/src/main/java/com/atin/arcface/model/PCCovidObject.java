package com.atin.arcface.model;

public class PCCovidObject {
    private String Name;
    private String NameBHYT;
    private String Phone;
    private String TimeScan;
    private int TypeRegister;
    private int UserStatus;
    private PCCovidHTTInfo HTTInfo;

    public PCCovidObject() {
    }

    public PCCovidObject(String name, String nameBHYT, String phone, String timeScan, int typeRegister, int userStatus, PCCovidHTTInfo HTTInfo) {
        Name = name;
        NameBHYT = nameBHYT;
        Phone = phone;
        TimeScan = timeScan;
        TypeRegister = typeRegister;
        UserStatus = userStatus;
        this.HTTInfo = HTTInfo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNameBHYT() {
        return NameBHYT;
    }

    public void setNameBHYT(String nameBHYT) {
        NameBHYT = nameBHYT;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getTimeScan() {
        return TimeScan;
    }

    public void setTimeScan(String timeScan) {
        TimeScan = timeScan;
    }

    public int getTypeRegister() {
        return TypeRegister;
    }

    public void setTypeRegister(int typeRegister) {
        TypeRegister = typeRegister;
    }

    public int getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(int userStatus) {
        UserStatus = userStatus;
    }

    public PCCovidHTTInfo getHTTInfo() {
        return HTTInfo;
    }

    public void setHTTInfo(PCCovidHTTInfo HTTInfo) {
        this.HTTInfo = HTTInfo;
    }
}
