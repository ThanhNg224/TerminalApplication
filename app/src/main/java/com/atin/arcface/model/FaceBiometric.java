package com.atin.arcface.model;

public class FaceBiometric {
    private byte[] faceData;
    private FacePreviewInfo facePreviewInfo;

    public FaceBiometric(byte[] faceData, FacePreviewInfo facePreviewInfo) {
        this.faceData = faceData;
        this.facePreviewInfo = facePreviewInfo;
    }

    public byte[] getFaceData() {
        return faceData;
    }

    public FacePreviewInfo getFacePreviewInfo() {
        return facePreviewInfo;
    }
}