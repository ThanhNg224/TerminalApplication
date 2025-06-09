package com.atin.arcface.util.camera;

import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.atin.arcface.common.MachineName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Sử dụng thư viện camera lấy ra hình ảnh dạng N21
 *
 * Vì độ phân giải của camera IR và RGB có thể khách nhau nên cần resize:
 * 1. Nhận danh sách độ phân giải từ 2 biến {@link DualCameraHelper#rgbSupportedPreviewSizes} và {@link DualCameraHelper#irSupportedPreviewSizes},
 * 2. Sử dụng {@link DualCameraHelper#getCommonSupportedPreviewSize()} lấy được độ phân giải cùng kích thước，
 * 3. Sử dụng {@link DualCameraHelper#getBestSupportedSize(List, Point)} Lấy độ phân giải tốt nhất hỗ trợ
 */
public class DualCameraHelper implements Camera.PreviewCallback {
    private static List<Camera.Size> rgbSupportedPreviewSizes;
    private static List<Camera.Size> irSupportedPreviewSizes;
    private static final String TAG = "CameraHelper";
    private Camera mCamera;
    private int mCameraId;
    private Point previewViewSize;
    private View previewDisplayView;
    private Camera.Size previewSize;
    private Point specificPreviewSize;
    private int displayOrientation = 0;
    private int rotation;
    private int additionalRotation;
    private boolean isMirror = false;

    private Integer specificCameraId = null;
    private CameraListener cameraListener;

    private DualCameraHelper(DualCameraHelper.Builder builder) {
        previewDisplayView = builder.previewDisplayView;
        specificCameraId = builder.specificCameraId;
        cameraListener = builder.cameraListener;
        rotation = builder.rotation;
        additionalRotation = builder.additionalRotation;
        previewViewSize = builder.previewViewSize;
        specificPreviewSize = builder.previewSize;
        if (builder.previewDisplayView instanceof TextureView) {
            isMirror = builder.isMirror;
        } else if (isMirror) {
            throw new RuntimeException("mirror is effective only when the preview is on a textureView");
        }
    }

    public void init() {
        if (previewDisplayView instanceof TextureView) {
            ((TextureView) this.previewDisplayView).setSurfaceTextureListener(textureListener);
        } else if (previewDisplayView instanceof SurfaceView) {
            ((SurfaceView) previewDisplayView).getHolder().addCallback(surfaceCallback);
        }

        if (isMirror) {
            previewDisplayView.setScaleX(-1);
        }
    }

    private List<Camera.Size> getCommonSupportedPreviewSize() {
        /**
         * irSupportedPreviewSizes và rgbSupportedPreviewSizes nhận null，
         * Nếu không phải là null thì không cần lấy, có thể lúc này máy ảnh đã được mở và không mở được máy ảnh.
         */
        if (irSupportedPreviewSizes == null) {
            Camera irCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            irSupportedPreviewSizes = irCamera.getParameters().getSupportedPreviewSizes();
            irCamera.release();
        }
        if (rgbSupportedPreviewSizes == null) {
            Camera rgbCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            rgbSupportedPreviewSizes = rgbCamera.getParameters().getSupportedPreviewSizes();
            rgbCamera.release();
        }
        List<Camera.Size> commonPreviewSizes = new ArrayList<>();
        for (Camera.Size rgbPreviewSize : rgbSupportedPreviewSizes) {
            for (Camera.Size irPreviewSize : irSupportedPreviewSizes) {
                if (irPreviewSize.width == rgbPreviewSize.width && irPreviewSize.height == rgbPreviewSize.height) {
                    commonPreviewSizes.add(rgbPreviewSize);
                }
            }
        }
        return commonPreviewSizes;
    }

    public void start() {
        synchronized (this) {
            if (mCamera != null) {
                return;
            }
            List<Camera.Size> supportedPreviewSize = getCommonSupportedPreviewSize();
            //Nếu số lượng camera là 2 sau đó bật 1 rồi 0 thì cameraid 1 ở phía trước, 0 ở phía sau
            mCameraId = Camera.getNumberOfCameras() - 1;
            //Nếu ID máy ảnh được chỉ định và máy ảnh tồn tại, hãy mở máy ảnh được chỉ định
            if (specificCameraId != null && specificCameraId <= mCameraId) {
                mCameraId = specificCameraId;
            }

            //Không có máy ảnh
            if (mCameraId == -1) {
                if (cameraListener != null) {
                    cameraListener.onCameraError(new Exception("camera not found"));
                }
                return;
            }
            if (mCamera == null) {
                mCamera = Camera.open(mCameraId);
            }
            displayOrientation = getCameraOri(rotation);
            mCamera.setDisplayOrientation(displayOrientation);
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);

                //Xem trước kích thước thiết lập
                previewSize = parameters.getPreviewSize();
                if (supportedPreviewSize != null && supportedPreviewSize.size() > 0) {
                    previewSize = getBestSupportedSize(supportedPreviewSize, previewViewSize);
                }
                parameters.setPreviewSize(previewSize.width, previewSize.height);

                //Thiết lập chế độ lấy nét
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (supportedFocusModes != null && supportedFocusModes.size() > 0) {
                    if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }
                }
                mCamera.setParameters(parameters);
                if (previewDisplayView instanceof TextureView) {
                    mCamera.setPreviewTexture(((TextureView) previewDisplayView).getSurfaceTexture());
                } else {
                    mCamera.setPreviewDisplay(((SurfaceView) previewDisplayView).getHolder());
                }
                mCamera.setPreviewCallback(this);
                mCamera.startPreview();
                if (cameraListener != null) {
                    cameraListener.onCameraOpened(mCamera, mCameraId, displayOrientation, isMirror);
                }
            } catch (Exception e) {
                if (cameraListener != null) {
                    cameraListener.onCameraError(e);
                }
            }
        }
    }

    private int getCameraOri(int rotation) {
        int degrees = rotation * 90;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }
        additionalRotation /= 90;
        additionalRotation *= 90;
        degrees += additionalRotation;
        int result;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * Dừng xem
     */
    public void stop() {
        synchronized (this) {
            if (mCamera == null) {
                return;
            }
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            if (cameraListener != null) {
                cameraListener.onCameraClosed();
            }
        }
    }

    public boolean isStopped() {
        synchronized (this) {
            return mCamera == null;
        }
    }

    /**
     * Giải phóng
     */
    public void release() {
        synchronized (this) {
            stop();
            previewDisplayView = null;
            specificCameraId = null;
            cameraListener = null;
            previewViewSize = null;
            specificPreviewSize = null;
            previewSize = null;
        }
    }

    /**
     * Lấy độ phân giải hỗ trợ tốt nhất
     * @param sizes Danh sách độ phân giải hỗ trợ
     * @param previewViewSize
     * @return
     */
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, Point previewViewSize) {
        if (sizes == null || sizes.size() == 0) {
            return mCamera.getParameters().getPreviewSize();
        }
        Camera.Size[] tempSizes = sizes.toArray(new Camera.Size[0]);
        Arrays.sort(tempSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size o1, Camera.Size o2) {
                if (o1.width > o2.width) {
                    return -1;
                } else if (o1.width == o2.width) {
                    return o1.height > o2.height ? -1 : 1;
                } else {
                    return 1;
                }
            }
        });
        sizes = Arrays.asList(tempSizes);

        Camera.Size bestSize = sizes.get(0);
        float previewViewRatio;
        if (previewViewSize != null) {
            previewViewRatio = (float) previewViewSize.x / (float) previewViewSize.y;
        } else {
            previewViewRatio = (float) bestSize.width / (float) bestSize.height;
        }

        if (previewViewRatio > 1) {
            previewViewRatio = 1 / previewViewRatio;
        }
        boolean isNormalRotate = (additionalRotation % 180 == 0);

        for (Camera.Size s : sizes) {
            //Thaidd fix độ phân giải camera sẽ sử dụng
            if(Build.MODEL.equals(MachineName.RAKINDA_A80M)){
                if(s.height == 720 && s.width == 1280){
                    return s;
                }
            }else{
                if(s.height == 480 && s.width == 640){
                    return s;
                }
            }
            //End thaidd

            if (specificPreviewSize != null && specificPreviewSize.x == s.width && specificPreviewSize.y == s.height) {
                return s;
            }
            if (isNormalRotate) {
                if (Math.abs((s.height / (float) s.width) - previewViewRatio) < Math.abs(bestSize.height / (float) bestSize.width - previewViewRatio)) {
                    bestSize = s;
                }
            } else {
                if (Math.abs((s.width / (float) s.height) - previewViewRatio) < Math.abs(bestSize.width / (float) bestSize.height - previewViewRatio)) {
                    bestSize = s;
                }
            }
        }
        return bestSize;
    }

    public List<Camera.Size> getSupportedPreviewSizes() {
        if (mCamera == null) {
            return null;
        }
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public List<Camera.Size> getSupportedPictureSizes() {
        if (mCamera == null) {
            return null;
        }
        return mCamera.getParameters().getSupportedPictureSizes();
    }


    @Override
    public void onPreviewFrame(byte[] nv21, Camera camera) {
        try{
            if (cameraListener != null) {
                cameraListener.onPreview(nv21, camera);
            }
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }
    }

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            if (mCamera != null) {
                try {
                    mCamera.setPreviewTexture(surfaceTexture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            Log.i(TAG, "onSurfaceTextureSizeChanged: " + width + "  " + height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            stop();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (mCamera != null) {
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stop();
        }
    };

    public void changeDisplayOrientation(int rotation) {
        if (mCamera != null) {
            this.rotation = rotation;
            displayOrientation = getCameraOri(rotation);
            mCamera.setDisplayOrientation(displayOrientation);
            if (cameraListener != null) {
                cameraListener.onCameraConfigurationChanged(mCameraId, displayOrientation);
            }
        }
    }

    public static final class Builder {

        /**
         * Hình ảnh nhận được
         */
        private View previewDisplayView;

        /**
         * Hình ảnh có bị phản chiếu giống gương
         */
        private boolean isMirror;
        /**
         * CameraID được chọn
         */
        private Integer specificCameraId;
        /**
         * Sự kiện lắng nghe
         */
        private CameraListener cameraListener;
        /**
         * Chiều dài và chiều rộng của màn hình, được sử dụng khi chọn tỷ lệ máy ảnh tốt nhất
         */
        private Point previewViewSize;
        /**
         * Góc quay hình ảnh
         */
        private int rotation;
        /**
         * Chiều rộng và chiều cao xem trước được chỉ định, nếu hệ thống hỗ trợ, chiều rộng và chiều cao xem trước sẽ được sử dụng để xem trước
         */
        private Point previewSize;

        /**
         * Góc quay bổ sung (được sử dụng để thích ứng với một số thiết bị tùy chỉnh)
         */
        private int additionalRotation;

        public Builder() {
        }


        public Builder previewOn(View val) {
            if (val instanceof SurfaceView || val instanceof TextureView) {
                previewDisplayView = val;
                return this;
            } else {
                throw new RuntimeException("you must preview on a textureView or a surfaceView");
            }
        }


        public Builder isMirror(boolean val) {
            isMirror = val;
            return this;
        }

        public Builder previewSize(Point val) {
            previewSize = val;
            return this;
        }

        public Builder previewViewSize(Point val) {
            previewViewSize = val;
            return this;
        }

        public Builder rotation(int val) {
            rotation = val;
            return this;
        }

        public Builder additionalRotation(int val) {
            additionalRotation = val;
            return this;
        }

        public Builder specificCameraId(Integer val) {
            specificCameraId = val;
            return this;
        }

        public Builder cameraListener(CameraListener val) {
            cameraListener = val;
            return this;
        }

        public DualCameraHelper build() {
            if (previewViewSize == null) {
                Log.e(TAG, "previewViewSize is null, now use default previewSize");
            }
            if (cameraListener == null) {
                Log.e(TAG, "cameraListener is null, callback will not be called");
            }
            if (previewDisplayView == null) {
                throw new RuntimeException("you must preview on a textureView or a surfaceView");
            }
            return new DualCameraHelper(this);
        }
    }

}
