package com.atin.arcface.model;


public class FaceDB {
    private String faceId;
    private String personId;
    private String faceUrl;
    private String facePath;
    private byte[] faceFeature;
    private String featurePath;
    private int faceStatus;
    private String imageBase64;

    public FaceDB() {
    }

    public FaceDB(String faceId, String personId, String faceUrl, String facePath, byte[] faceFeature, String featurePath, int faceStatus) {
        this.faceId = faceId;
        this.personId = personId;
        this.faceUrl = faceUrl;
        this.facePath = facePath;
        this.faceFeature = faceFeature;
        this.featurePath = featurePath;
        this.faceStatus = faceStatus;
    }

    public FaceDB(String faceId, String personId, String faceUrl, String facePath, byte[] faceFeature, String featurePath, int faceStatus, String imageBase64) {
        this.faceId = faceId;
        this.personId = personId;
        this.faceUrl = faceUrl;
        this.facePath = facePath;
        this.faceFeature = faceFeature;
        this.featurePath = featurePath;
        this.faceStatus = faceStatus;
        this.imageBase64 = imageBase64;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public byte[] getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

    public String getFeaturePath() {
        return featurePath;
    }

    public void setFeaturePath(String featurePath) {
        this.featurePath = featurePath;
    }

    public int getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(int faceStatus) {
        this.faceStatus = faceStatus;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
