package com.atin.arcface.util.face;

import androidx.annotation.Nullable;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.LivenessInfo;

public interface FaceListener {
    void onFail(Exception e);

    void onFaceFeatureInfoGet(@Nullable FaceFeature faceFeature, Integer requestId, Integer errorCode);

    void onFaceLivenessInfoGet(@Nullable LivenessInfo livenessInfo, Integer requestId, Integer errorCode);

}
