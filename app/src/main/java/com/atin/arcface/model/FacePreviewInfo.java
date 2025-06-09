package com.atin.arcface.model;

import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.LivenessInfo;

public class FacePreviewInfo {
    private FaceInfo faceInfo;
    private LivenessInfo livenessInfo;
    private int trackId;
    private boolean qualityPass = true;
    private int mask;

    /**
     * Khu vực hợp lệ
     */
    private boolean recognizeAreaValid;

    public FacePreviewInfo(FaceInfo faceInfo, int trackId) {
        this.faceInfo = faceInfo;
        this.trackId = trackId;
    }

    public FacePreviewInfo(FaceInfo faceInfo, LivenessInfo livenessInfo, int trackId) {
        this.faceInfo = faceInfo;
        this.livenessInfo = livenessInfo;
        this.trackId = trackId;
    }

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }

    public LivenessInfo getLivenessInfo() {
        return livenessInfo;
    }

    public void setLivenessInfo(LivenessInfo livenessInfo) {
        this.livenessInfo = livenessInfo;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public boolean isRecognizeAreaValid() {
        return recognizeAreaValid;
    }

    public void setRecognizeAreaValid(boolean recognizeAreaValid) {
        this.recognizeAreaValid = recognizeAreaValid;
    }

    public boolean isQualityPass() {
        return qualityPass;
    }

    public void setQualityPass(boolean qualityPass) {
        this.qualityPass = qualityPass;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }
}
