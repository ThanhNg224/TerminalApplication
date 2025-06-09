package com.atin.arcface.model;

import com.arcsoft.face.FaceInfo;
import com.atin.arcface.faceserver.CompareResult;

public class FaceInfoCapture {
    private byte[] faceCapture;
    private FaceInfo faceInfo;
    private String personId;
    private String faceCaptureBase64;
    private AccessResult accessResult;
    private CompareResult compareResult;

    public FaceInfoCapture(byte[] faceCapture, FaceInfo faceInfo) {
        this.faceCapture = faceCapture;
        this.faceInfo = faceInfo;
    }

    public byte[] getFaceCapture() {
        return faceCapture;
    }

    public void setFaceCapture(byte[] faceCapture) {
        this.faceCapture = faceCapture;
    }

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFaceCaptureBase64() {
        return faceCaptureBase64;
    }

    public void setFaceCaptureBase64(String faceCaptureBase64) {
        this.faceCaptureBase64 = faceCaptureBase64;
    }

    public AccessResult getAccessResult() {
        return accessResult;
    }

    public void setAccessResult(AccessResult accessResult) {
        this.accessResult = accessResult;
    }

    public CompareResult getCompareResult() {
        return compareResult;
    }

    public void setCompareResult(CompareResult compareResult) {
        this.compareResult = compareResult;
    }
}