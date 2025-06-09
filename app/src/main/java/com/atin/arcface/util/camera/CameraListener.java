package com.atin.arcface.util.camera;

import android.hardware.Camera;


public interface CameraListener {
    /**
     * Thực thi khi mở
     * @param camera Camera
     * @param cameraId Camera ID
     * @param displayOrientation Góc quay camera xem trước
     * @param isMirror Cho dù gương hiển thị
     */
    void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror);

    /**
     * Xem trước dữ liệu gọi lại
     * @param data Xem trước dữ liệu
     * @param camera Camera
     */
    void onPreview(byte[] data, Camera camera);

    /**
     * Thực hiện khi máy ảnh tắt
     */
    void onCameraClosed();

    /**
     * Được thực hiện khi có ngoại lệ xảy ra
     * @param e Máy ảnh bất thường liên quan
     */
    void onCameraError(Exception e);

    /**
     * Được gọi khi một tài sản thay đổi
     * @param cameraID cameraID
     * @param displayOrientation    Hướng quay của camera
     */
    void onCameraConfigurationChanged(int cameraID, int displayOrientation);
}
