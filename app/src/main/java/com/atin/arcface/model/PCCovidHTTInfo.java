package com.atin.arcface.model;

import java.util.List;

public class PCCovidHTTInfo {
    private PCCovidKBCN KBCN;
    private Object KBND;
    private Object QR6Info;
    private String userId;
    private String fullName;
    private String phone;
    private String gender;
    private int yearOfBirthday;
    private String checkinId;
    private boolean checkReason;
    private String temporaryToken;
    private String userStatus;
    private int configDeclaration;
    private int healthStatus;
    private boolean checkDeclaration;
    private Object healthState;
    private boolean inEpidemicArea;
    private boolean contactHistory;
    private boolean travelHistory;
    private long lastTime;
    private PCCovidHistoryInjection historyInjection;
    private List<PCCovidHistoryInjection> historyInjectionList;
    private PCCovidCovidTest covidTest;
    private Object locationTravels;

    public PCCovidHTTInfo() {
    }

    public PCCovidHTTInfo(PCCovidKBCN KBCN, Object KBND, Object QR6Info, String userId, String fullName, String phone, String gender, int yearOfBirthday, String checkinId, boolean checkReason, String temporaryToken, String userStatus, int configDeclaration, int healthStatus, boolean checkDeclaration, Object healthState, boolean inEpidemicArea, boolean contactHistory, boolean travelHistory, long lastTime, PCCovidHistoryInjection historyInjection, List<PCCovidHistoryInjection> historyInjectionList, PCCovidCovidTest covidTest, Object locationTravels) {
        this.KBCN = KBCN;
        this.KBND = KBND;
        this.QR6Info = QR6Info;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.gender = gender;
        this.yearOfBirthday = yearOfBirthday;
        this.checkinId = checkinId;
        this.checkReason = checkReason;
        this.temporaryToken = temporaryToken;
        this.userStatus = userStatus;
        this.configDeclaration = configDeclaration;
        this.healthStatus = healthStatus;
        this.checkDeclaration = checkDeclaration;
        this.healthState = healthState;
        this.inEpidemicArea = inEpidemicArea;
        this.contactHistory = contactHistory;
        this.travelHistory = travelHistory;
        this.lastTime = lastTime;
        this.historyInjection = historyInjection;
        this.historyInjectionList = historyInjectionList;
        this.covidTest = covidTest;
        this.locationTravels = locationTravels;
    }

    public PCCovidKBCN getKBCN() {
        return KBCN;
    }

    public void setKBCN(PCCovidKBCN KBCN) {
        this.KBCN = KBCN;
    }

    public Object getKBND() {
        return KBND;
    }

    public void setKBND(Object KBND) {
        this.KBND = KBND;
    }

    public Object getQR6Info() {
        return QR6Info;
    }

    public void setQR6Info(Object QR6Info) {
        this.QR6Info = QR6Info;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getYearOfBirthday() {
        return yearOfBirthday;
    }

    public void setYearOfBirthday(int yearOfBirthday) {
        this.yearOfBirthday = yearOfBirthday;
    }

    public String getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(String checkinId) {
        this.checkinId = checkinId;
    }

    public boolean isCheckReason() {
        return checkReason;
    }

    public void setCheckReason(boolean checkReason) {
        this.checkReason = checkReason;
    }

    public String getTemporaryToken() {
        return temporaryToken;
    }

    public void setTemporaryToken(String temporaryToken) {
        this.temporaryToken = temporaryToken;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public int getConfigDeclaration() {
        return configDeclaration;
    }

    public void setConfigDeclaration(int configDeclaration) {
        this.configDeclaration = configDeclaration;
    }

    public int getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(int healthStatus) {
        this.healthStatus = healthStatus;
    }

    public boolean isCheckDeclaration() {
        return checkDeclaration;
    }

    public void setCheckDeclaration(boolean checkDeclaration) {
        this.checkDeclaration = checkDeclaration;
    }

    public Object getHealthState() {
        return healthState;
    }

    public void setHealthState(Object healthState) {
        this.healthState = healthState;
    }

    public boolean isInEpidemicArea() {
        return inEpidemicArea;
    }

    public void setInEpidemicArea(boolean inEpidemicArea) {
        this.inEpidemicArea = inEpidemicArea;
    }

    public boolean isContactHistory() {
        return contactHistory;
    }

    public void setContactHistory(boolean contactHistory) {
        this.contactHistory = contactHistory;
    }

    public boolean isTravelHistory() {
        return travelHistory;
    }

    public void setTravelHistory(boolean travelHistory) {
        this.travelHistory = travelHistory;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public PCCovidHistoryInjection getHistoryInjection() {
        return historyInjection;
    }

    public void setHistoryInjection(PCCovidHistoryInjection historyInjection) {
        this.historyInjection = historyInjection;
    }

    public List<PCCovidHistoryInjection> getHistoryInjectionList() {
        return historyInjectionList;
    }

    public void setHistoryInjectionList(List<PCCovidHistoryInjection> historyInjectionList) {
        this.historyInjectionList = historyInjectionList;
    }

    public PCCovidCovidTest getCovidTest() {
        return covidTest;
    }

    public void setCovidTest(PCCovidCovidTest covidTest) {
        this.covidTest = covidTest;
    }

    public Object getLocationTravels() {
        return locationTravels;
    }

    public void setLocationTravels(Object locationTravels) {
        this.locationTravels = locationTravels;
    }
}
