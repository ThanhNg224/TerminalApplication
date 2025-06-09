package com.atin.arcface.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.hardware.Camera;

import com.atin.arcface.model.DrawInfo;
import com.atin.arcface.widget.FaceRectView;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;

import java.util.List;

/**
 * Vẽ lớp trình trợ giúp khung mặt để vẽ hình chữ nhật trên {@link com.atin.arcface.widget.FaceRectView}
 */
public class DrawHelper {
    private int previewWidth, previewHeight, canvasWidth, canvasHeight, cameraDisplayOrientation, cameraId;
    private boolean isMirror;
    private boolean mirrorHorizontal = false, mirrorVertical = false;

    /**
     * Tạo một đối tượng trợ giúp bản vẽ và thiết lập các tham số liên quan đến bản vẽ
     *
     * @param previewWidth             Chiều rộng xem trước
     * @param previewHeight            Xem trước chiều cao
     * @param canvasWidth              Vẽ chiều rộng điều khiển
     * @param canvasHeight             Vẽ chiều cao điều khiển
     * @param cameraDisplayOrientation Góc quay
     * @param cameraId                 ID camera
     * @param isMirror                 Có phản chiếu màn hình theo chiều ngang hay không (nếu máy ảnh được nhân đôi, đặt thành đúng để chỉnh sửa)
     * @param mirrorHorizontal         Để tương thích với một số thiết bị, hãy nhân bản lại theo chiều ngang
     * @param mirrorVertical           Để tương thích với một số thiết bị, hãy nhân bản theo chiều dọc một lần nữa
     */
    public DrawHelper(int previewWidth, int previewHeight, int canvasWidth,
                      int canvasHeight, int cameraDisplayOrientation, int cameraId,
                      boolean isMirror, boolean mirrorHorizontal, boolean mirrorVertical) {
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.cameraDisplayOrientation = cameraDisplayOrientation;
        this.cameraId = cameraId;
        this.isMirror = isMirror;
        this.mirrorHorizontal = mirrorHorizontal;
        this.mirrorVertical = mirrorVertical;
    }

    public void draw(FaceRectView faceRectView, List<DrawInfo> drawInfoList) {
        if (faceRectView == null) {
            return;
        }
        faceRectView.clearFaceInfo();
        if (drawInfoList == null || drawInfoList.size() == 0) {
            return;
        }
        faceRectView.addFaceInfo(drawInfoList);
    }

    /**
     * Điều chỉnh khung mặt để vẽ
     *
     * @param ftRect FT Khung mặt
     * @return Các yê cầu điều chỉnh đưa View rect
     */
    public Rect adjustRect(Rect ftRect) {
        int previewWidth = this.previewWidth;
        int previewHeight = this.previewHeight;
        int canvasWidth = this.canvasWidth;
        int canvasHeight = this.canvasHeight;
        int cameraDisplayOrientation = this.cameraDisplayOrientation;
        int cameraId = this.cameraId;
        boolean isMirror = this.isMirror;
        boolean mirrorHorizontal = this.mirrorHorizontal;
        boolean mirrorVertical = this.mirrorVertical;

        if (ftRect == null) {
            return null;
        }

        Rect rect = new Rect(ftRect);
        float horizontalRatio;
        float verticalRatio;
        if (cameraDisplayOrientation % 180 == 0) {
            horizontalRatio = (float) canvasWidth / (float) previewWidth;
            verticalRatio = (float) canvasHeight / (float) previewHeight;
        } else {
            horizontalRatio = (float) canvasHeight / (float) previewWidth;
            verticalRatio = (float) canvasWidth / (float) previewHeight;
        }
        rect.left *= horizontalRatio;
        rect.right *= horizontalRatio;
        rect.top *= verticalRatio;
        rect.bottom *= verticalRatio;

        Rect newRect = new Rect();
        switch (cameraDisplayOrientation) {
            case 0:
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.left = canvasWidth - rect.right;
                    newRect.right = canvasWidth - rect.left;
                } else {
                    newRect.left = rect.left;
                    newRect.right = rect.right;
                }
                newRect.top = rect.top;
                newRect.bottom = rect.bottom;
                break;
            case 90:
                newRect.left = canvasWidth - rect.bottom;
                newRect.right = canvasWidth - rect.top;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.top = canvasHeight - rect.right;
                    newRect.bottom = canvasHeight - rect.right;
                } else {
                    newRect.top = rect.left;
                    newRect.bottom = rect.right;
                }
                break;
            case 180:
                newRect.top = canvasHeight - rect.bottom;
                newRect.bottom = canvasHeight - rect.top;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.left = rect.left;
                    newRect.right = rect.right;
                } else {
                    newRect.left = canvasWidth - rect.right;
                    newRect.right = canvasWidth - rect.left;
                }
                break;
            case 270:
                newRect.left = rect.top;
                newRect.right = rect.bottom;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.top = rect.left;
                    newRect.bottom = rect.right;
                } else {
                    newRect.top = canvasHeight - rect.right;
                    newRect.bottom = canvasHeight - rect.left;
                }
                break;
            default:
                break;
        }

        /**
         * isMirror mirrorHorizontal finalIsMirrorHorizontal
         * true         true                false
         * false        false               false
         * true         false               true
         * false        true                true
         *
         * XOR
         */
        if (isMirror ^ mirrorHorizontal) {
            int left = newRect.left;
            int right = newRect.right;
            newRect.left = canvasWidth - right;
            newRect.right = canvasWidth - left;
        }
        if (mirrorVertical) {
            int top = newRect.top;
            int bottom = newRect.bottom;
            newRect.top = canvasHeight - bottom;
            newRect.bottom = canvasHeight - top;
        }
        return newRect;
    }

    /**
     * Vẽ thông tin dữ liệu vào chế độ xem, nếu {@link DrawInfo # name} không rỗng thì vẽ {@link DrawInfo # name}
     *
     * @param canvas
     * @param drawInfo
     * @param color
     * @param faceRectThickness Độ dày khung mặt
     * @param paint
     */
    public static void drawFaceRect(Canvas canvas, DrawInfo drawInfo, int color, int faceRectThickness, Paint paint) {
        if (canvas == null || drawInfo == null) {
            return;
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(faceRectThickness);
        if (drawInfo.getLiveness() == LivenessInfo.NOT_ALIVE) {
            paint.setColor(Color.YELLOW);
        } else if (drawInfo.getLiveness() == LivenessInfo.ALIVE) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(color);
        }
        Path mPath = new Path();
        //Trên cùng bên trái
        Rect rect = drawInfo.getRect();
        mPath.moveTo(rect.left, rect.top + rect.height() / 4);
        mPath.lineTo(rect.left, rect.top);
        mPath.lineTo(rect.left + rect.width() / 4, rect.top);
        //Trên cùng bên phải
        mPath.moveTo(rect.right - rect.width() / 4, rect.top);
        mPath.lineTo(rect.right, rect.top);
        mPath.lineTo(rect.right, rect.top + rect.height() / 4);
        //Dưới cùng bên phải
        mPath.moveTo(rect.right, rect.bottom - rect.height() / 4);
        mPath.lineTo(rect.right, rect.bottom);
        mPath.lineTo(rect.right - rect.width() / 4, rect.bottom);
        //Dưới cùng bên trái
        mPath.moveTo(rect.left + rect.width() / 4, rect.bottom);
        mPath.lineTo(rect.left, rect.bottom);
        mPath.lineTo(rect.left, rect.bottom - rect.height() / 4);
        canvas.drawPath(mPath, paint);


        if (drawInfo.getName() == null) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setTextSize(rect.width() / 8);

            String str = (drawInfo.getSex() == GenderInfo.MALE ? "MALE" : (drawInfo.getSex() == GenderInfo.FEMALE ? "FEMALE" : "UNKNOWN"))
                    + ","
                    + (drawInfo.getAge() == AgeInfo.UNKNOWN_AGE ? "UNKNWON" : drawInfo.getAge())
                    + ","
                    + (drawInfo.getLiveness() == LivenessInfo.ALIVE ? "ALIVE" : (drawInfo.getLiveness() == LivenessInfo.NOT_ALIVE ? "NOT_ALIVE" : "UNKNOWN"));
            canvas.drawText(str, rect.left, rect.top - 10, paint);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            paint.setTextSize(rect.width() / 8);
            canvas.drawText(drawInfo.getName(), rect.left, rect.top - 10, paint);
        }
    }

    public void setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
    }

    public void setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public void setCameraDisplayOrientation(int cameraDisplayOrientation) {
        this.cameraDisplayOrientation = cameraDisplayOrientation;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public void setMirror(boolean mirror) {
        isMirror = mirror;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public int getCameraDisplayOrientation() {
        return cameraDisplayOrientation;
    }

    public int getCameraId() {
        return cameraId;
    }

    public boolean isMirror() {
        return isMirror;
    }

    public boolean isMirrorHorizontal() {
        return mirrorHorizontal;
    }

    public void setMirrorHorizontal(boolean mirrorHorizontal) {
        this.mirrorHorizontal = mirrorHorizontal;
    }

    public boolean isMirrorVertical() {
        return mirrorVertical;
    }

    public void setMirrorVertical(boolean mirrorVertical) {
        this.mirrorVertical = mirrorVertical;
    }
}