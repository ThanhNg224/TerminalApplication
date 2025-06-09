package com.atin.arcface.model;

public class CompanyConstantParamModel {
    private boolean checkProfing;
    private boolean useLed;
    private boolean useSound;
    private boolean autoStart;
    private boolean checkAccessControl;
    private boolean checkMask;
    private boolean checkTemperature;
    private boolean saveGuest;
    private boolean showPersonCode;
    private boolean showVaccine;
    private float faceThreshold;
    private float temperatureThreshold;
    private int delayTime;
    private int distance;
    private String serverUrlApi;
    private String passwordApi;

    public CompanyConstantParamModel(boolean checkProfing, boolean useLed, boolean useSound, boolean autoStart, boolean checkAccessControl, boolean checkMask, boolean checkTemperature, boolean saveGuest, boolean showPersonCode, boolean showVaccine, float faceThreshold, float temperatureThreshold, int delayTime, int distance, String serverUrlApi, String passwordApi) {
        this.checkProfing = checkProfing;
        this.useLed = useLed;
        this.useSound = useSound;
        this.autoStart = autoStart;
        this.checkAccessControl = checkAccessControl;
        this.checkMask = checkMask;
        this.checkTemperature = checkTemperature;
        this.saveGuest = saveGuest;
        this.showPersonCode = showPersonCode;
        this.showVaccine = showVaccine;
        this.faceThreshold = faceThreshold;
        this.temperatureThreshold = temperatureThreshold;
        this.delayTime = delayTime;
        this.distance = distance;
        this.serverUrlApi = serverUrlApi;
        this.passwordApi = passwordApi;
    }

    public boolean isCheckProfing() {
        return checkProfing;
    }

    public boolean isUseLed() {
        return useLed;
    }

    public boolean isUseSound() {
        return useSound;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public boolean isCheckAccessControl() {
        return checkAccessControl;
    }

    public boolean isCheckMask() {
        return checkMask;
    }

    public boolean isCheckTemperature() {
        return checkTemperature;
    }

    public boolean isSaveGuest() {
        return saveGuest;
    }

    public boolean isShowPersonCode() {
        return showPersonCode;
    }

    public boolean isShowVaccine() {
        return showVaccine;
    }

    public float getFaceThreshold() {
        return faceThreshold;
    }

    public float getTemperatureThreshold() {
        return temperatureThreshold;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public int getDistance() {
        return distance;
    }

    public String getServerUrlApi() {
        return serverUrlApi;
    }

    public String getPasswordApi() {
        return passwordApi;
    }
}
