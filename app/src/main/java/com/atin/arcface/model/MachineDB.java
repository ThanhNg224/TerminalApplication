package com.atin.arcface.model;

public class MachineDB {
    private int machineId;
    private int compId;
    private String deviceName;
    private int deviceType;
    private int deviceFunction;
    private String ipaddress;
    private String imei;
    private String mac;
    private String serverIp;
    private int serverPort;
    private int fraudProof;
    private int angleDetect;
    private int autoStart;
    private int autoSaveVisitor;
    private int distanceDetect;
    private String username;
    private String password;
    private String logo;
    private int volume;
    private int brightness;
    private int delay;
    private int led;
    private double fingerThreshold;
    private double faceThreshold;
    private double temperatureThreshold;
    private String firmwareVersion;
    private String usernameServer;
    private String passwordServer;
    private int useMask;
    private int useTemperature;
    private int useVaccine;
    private int usePCCovid;
    private String pccovidPhone;
    private String pccovidLocation;
    private String pccovidToken;
    private int dailyReboot;
    private String restartTime;
    private String language;
    private int noDelay;
    private double noMaskQualityThreshold;
    private double maskQualityThreshold;
    private double regQualityThreshold;
    private int autoSleep;

    public MachineDB(int machineId, int compId, String deviceName, int deviceType, int deviceFunction, String ipaddress, String imei, String mac, String serverIp, int serverPort, int fraudProof, int angleDetect, int autoStart, int autoSaveVisitor, int distanceDetect, String username, String password, String logo, int volume, int brightness, int delay, int led, double fingerThreshold, double faceThreshold, double temperatureThreshold, String firmwareVersion, String usernameServer, String passwordServer, int useMask, int useTemperature, int useVaccine, int usePCCovid, String pccovidPhone, String pccovidLocation, String pccovidToken, int dailyReboot, String restartTime, String language, int noDelay, double noMaskQualityThreshold, double maskQualityThreshold, double regQualityThreshold, int autoSleep) {
        this.machineId = machineId;
        this.compId = compId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.deviceFunction = deviceFunction;
        this.ipaddress = ipaddress;
        this.imei = imei;
        this.mac = mac;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.fraudProof = fraudProof;
        this.angleDetect = angleDetect;
        this.autoStart = autoStart;
        this.autoSaveVisitor = autoSaveVisitor;
        this.distanceDetect = distanceDetect;
        this.username = username;
        this.password = password;
        this.logo = logo;
        this.volume = volume;
        this.brightness = brightness;
        this.delay = delay;
        this.led = led;
        this.fingerThreshold = fingerThreshold;
        this.faceThreshold = faceThreshold;
        this.temperatureThreshold = temperatureThreshold;
        this.firmwareVersion = firmwareVersion;
        this.usernameServer = usernameServer;
        this.passwordServer = passwordServer;
        this.useMask = useMask;
        this.useTemperature = useTemperature;
        this.useVaccine = useVaccine;
        this.usePCCovid = usePCCovid;
        this.pccovidPhone = pccovidPhone;
        this.pccovidLocation = pccovidLocation;
        this.pccovidToken = pccovidToken;
        this.dailyReboot = dailyReboot;
        this.restartTime = restartTime;
        this.language = language;
        this.noDelay = noDelay;
        this.noMaskQualityThreshold = noMaskQualityThreshold;
        this.maskQualityThreshold = maskQualityThreshold;
        this.regQualityThreshold = regQualityThreshold;
        this.autoSleep = autoSleep;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public int getCompId() {
        return compId;
    }

    public void setCompId(int compId) {
        this.compId = compId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceFunction() {
        return deviceFunction;
    }

    public void setDeviceFunction(int deviceFunction) {
        this.deviceFunction = deviceFunction;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getFraudProof() {
        return fraudProof;
    }

    public void setFraudProof(int fraudProof) {
        this.fraudProof = fraudProof;
    }

    public int getAngleDetect() {
        return angleDetect;
    }

    public void setAngleDetect(int angleDetect) {
        this.angleDetect = angleDetect;
    }

    public int getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(int autoStart) {
        this.autoStart = autoStart;
    }

    public int getAutoSaveVisitor() {
        return autoSaveVisitor;
    }

    public void setAutoSaveVisitor(int autoSaveVisitor) {
        this.autoSaveVisitor = autoSaveVisitor;
    }

    public int getDistanceDetect() {
        return distanceDetect;
    }

    public void setDistanceDetect(int distanceDetect) {
        this.distanceDetect = distanceDetect;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getLed() {
        return led;
    }

    public void setLed(int led) {
        this.led = led;
    }

    public double getFingerThreshold() {
        return fingerThreshold;
    }

    public void setFingerThreshold(double fingerThreshold) {
        this.fingerThreshold = fingerThreshold;
    }

    public double getFaceThreshold() {
        return faceThreshold;
    }

    public void setFaceThreshold(double faceThreshold) {
        this.faceThreshold = faceThreshold;
    }

    public double getTemperatureThreshold() {
        return temperatureThreshold;
    }

    public void setTemperatureThreshold(double temperatureThreshold) {
        this.temperatureThreshold = temperatureThreshold;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getUsernameServer() {
        return usernameServer;
    }

    public void setUsernameServer(String usernameServer) {
        this.usernameServer = usernameServer;
    }

    public String getPasswordServer() {
        return passwordServer;
    }

    public void setPasswordServer(String passwordServer) {
        this.passwordServer = passwordServer;
    }

    public int getUseMask() {
        return useMask;
    }

    public void setUseMask(int useMask) {
        this.useMask = useMask;
    }

    public int getUseTemperature() {
        return useTemperature;
    }

    public void setUseTemperature(int useTemperature) {
        this.useTemperature = useTemperature;
    }

    public int getUseVaccine() {
        return useVaccine;
    }

    public void setUseVaccine(int useVaccine) {
        this.useVaccine = useVaccine;
    }

    public int getUsePCCovid() {
        return usePCCovid;
    }

    public void setUsePCCovid(int usePCCovid) {
        this.usePCCovid = usePCCovid;
    }

    public String getPccovidPhone() {
        return pccovidPhone;
    }

    public void setPccovidPhone(String pccovidPhone) {
        this.pccovidPhone = pccovidPhone;
    }

    public String getPccovidLocation() {
        return pccovidLocation;
    }

    public void setPccovidLocation(String pccovidLocation) {
        this.pccovidLocation = pccovidLocation;
    }

    public String getPccovidToken() {
        return pccovidToken;
    }

    public void setPccovidToken(String pccovidToken) {
        this.pccovidToken = pccovidToken;
    }

    public int getDailyReboot() {
        return dailyReboot;
    }

    public void setDailyReboot(int dailyReboot) {
        this.dailyReboot = dailyReboot;
    }

    public String getRestartTime() {
        return restartTime;
    }

    public void setRestartTime(String restartTime) {
        this.restartTime = restartTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getNoDelay() {
        return noDelay;
    }

    public void setNoDelay(int noDelay) {
        this.noDelay = noDelay;
    }

    public double getNoMaskQualityThreshold() {
        return noMaskQualityThreshold;
    }

    public void setNoMaskQualityThreshold(double noMaskQualityThreshold) {
        this.noMaskQualityThreshold = noMaskQualityThreshold;
    }

    public double getMaskQualityThreshold() {
        return maskQualityThreshold;
    }

    public void setMaskQualityThreshold(double maskQualityThreshold) {
        this.maskQualityThreshold = maskQualityThreshold;
    }

    public double getRegQualityThreshold() {
        return regQualityThreshold;
    }

    public void setRegQualityThreshold(double regQualityThreshold) {
        this.regQualityThreshold = regQualityThreshold;
    }

    public int getAutoSleep() {
        return autoSleep;
    }

    public void setAutoSleep(int autoSleep) {
        this.autoSleep = autoSleep;
    }
}
