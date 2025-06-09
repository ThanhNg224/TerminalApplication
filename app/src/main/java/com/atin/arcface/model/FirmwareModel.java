package com.atin.arcface.model;


public class FirmwareModel {
    private int id;
    private int firmwareNumber;
    private String firmwareVersion;
    private String firmwareUrl;
    private String fileName;
    private String fix;
    private String releaseDate;
    private String md5;

    public FirmwareModel() {
    }

    public FirmwareModel(int id, int firmwareNumber, String firmwareVersion, String firmwareUrl, String fileName, String fix, String releaseDate, String md5) {
        this.id = id;
        this.firmwareNumber = firmwareNumber;
        this.firmwareVersion = firmwareVersion;
        this.firmwareUrl = firmwareUrl;
        this.fileName = fileName;
        this.fix = fix;
        this.releaseDate = releaseDate;
        this.md5 = md5;
    }

    public int getId() {
        return id;
    }

    public int getFirmwareNumber() {
        return firmwareNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getFirmwareUrl() {
        return firmwareUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFix() {
        return fix;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getMd5() {
        return md5;
    }
}
