package com.atin.arcface.faceserver;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Base64;
import android.util.Log;

import com.arcsoft.face.enums.CompareModel;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.ExtractType;
import com.atin.arcface.activity.Application;
import com.atin.arcface.common.Constants;
import com.atin.arcface.model.CardDB;
import com.atin.arcface.model.FaceRegisterInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.util.ImageUtils;
import com.atin.arcface.model.MachineDB;
import com.atin.arcface.util.BaseUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp vận hành thư viện khuôn mặt, bao gồm đăng ký và tìm kiếm
 */
public class FaceServer {
    private static final String TAG = "FaceServer";
    private static FaceEngine faceEngine = null;
    private static FaceServer faceServer = null;
    private static List<FaceRegisterInfo> faceRegisterInfoList;
    public static String ROOT_PATH;
    public static String AVATAR_PATH;
    public static String FACE_PATH;
    public static String UPLOAD_PATH;
    private Database database;
    private static final int PERCEN_IMAGE_QUALITY = 50;
    private int initEnigineCode = 0;

    /**
     * Có tìm kiếm khuôn mặt hay không, để đảm bảo rằng thao tác tìm kiếm được thực hiện trong một chuỗi
     */
    private boolean isProcessing = false;

    public static FaceServer getInstance() {
        if (faceServer == null) {
            synchronized (FaceServer.class) {
                if (faceServer == null) {
                    faceServer = new FaceServer();
                }
            }
        }
        return faceServer;
    }

    public int getListSize(){
        return faceRegisterInfoList.size();
    }

    /**
     * Khởi tạo
     *
     * @param context
     * @return result
     */
    public boolean init(Context context) {
        synchronized (this) {

            if (ROOT_PATH == null) {
                ROOT_PATH = context.getFilesDir().getAbsolutePath();
            }

            if (AVATAR_PATH == null){
                AVATAR_PATH = ROOT_PATH + File.separator + "data" + File.separator + "avatar";
            }

            if (FACE_PATH == null){
                FACE_PATH = ROOT_PATH + File.separator + "data" + File.separator + "face";
            }

            if(UPLOAD_PATH == null ){
                UPLOAD_PATH = ROOT_PATH + File.separator + "upload";
            }

            if (faceEngine == null && context != null) {
                faceEngine = new FaceEngine();
                int engineCode = faceEngine.init(context, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                        1, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);
                initEnigineCode = engineCode;
                if (engineCode == ErrorInfo.MOK) {
                    initDatabase(context);
                    initFaceList(context);
                    return true;
                } else {
                    faceEngine = null;
                    Log.e(TAG, "init: failed! code = " + engineCode);
                    return false;
                }
            }

            return false;
        }
    }

    public int getInitEnigineCode() {
        return initEnigineCode;
    }

    private void initDatabase(Context context) {
        database = Application.getInstance().getDatabase();
    }

    /**
     * Hủy
     */
    public void unInit() {
        synchronized (this) {
            if (faceRegisterInfoList != null) {
                faceRegisterInfoList.clear();
                faceRegisterInfoList = null;
            }
            if (faceEngine != null) {
                faceEngine.unInit();
                faceEngine = null;
            }
        }
    }

    /**
     * Khởi tạo dữ liệu đặc điểm khuôn mặt và bản đồ đăng ký tương ứng với dữ liệu đặc điểm khuôn mặt
     *
     * @param context
     */
    public void initFaceList(Context context) {
        synchronized (this) {
            faceRegisterInfoList = database.getAllActivePerson();
        }
    }

    public void updatePersonRealtime(FaceRegisterInfo face, int action){
        switch (action){
            case Constants.SyncAction.ADD:
                faceRegisterInfoList.add(face);
                break;

            case Constants.SyncAction.UPDATE:
                for(int i=0; i<faceRegisterInfoList.size(); i++){
                    FaceRegisterInfo faceOfList = faceRegisterInfoList.get(i);
                    if(faceOfList.getPersonId().equals(face.getPersonId())){
                        faceOfList.setFeatureData(face.getFeatureData());
                        faceOfList.setFacePath(face.getFacePath());
                        return;
                    }
                }
                break;

            case Constants.SyncAction.REMOVE:
                for(int i=0; i<faceRegisterInfoList.size(); i++){
                    FaceRegisterInfo faceOfList = faceRegisterInfoList.get(i);
                    if(faceOfList.getPersonId().equals(face.getPersonId())){
                        faceRegisterInfoList.remove(i);
                        return;
                    }
                }
                break;
        }
    }

    public void updatePersonRealtime(String personId) {
        try{
            //Log.d("THAI", "Bắt đầu update: " + faceRegisterInfoList.size());
            FaceRegisterInfo faceRegisterInfo = database.getFaceRegisterInfo(personId);

            for(FaceRegisterInfo item : faceRegisterInfoList){
                if(item.getPersonId().equals(personId)){
                    if(faceRegisterInfo.getStatus() == 1){
                        faceRegisterInfoList.remove(item);
                        faceRegisterInfoList.add(faceRegisterInfo);
                    }else if(faceRegisterInfo.getStatus() == 0){
                        faceRegisterInfoList.remove(item);
                    }
                    return;
                }
            }

            faceRegisterInfoList.add(faceRegisterInfo);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }finally {
            //Log.d("THAI", "Kết thúc update: " + faceRegisterInfoList.size());
        }
    }

    /**
     * Trích xuất thuộc tính khuôn mặt từ base64 ra byte array và nén
     *
     * @param context
     * @return Map luu ten duong dan file anh va mang gia tri thuoc tinh khuon mat
     */
    public ContentValues extractFaceImageData(Context context, String anhKhuonMatBase64) throws  Exception {
        ContentValues mResult = new ContentValues();
        byte[] faceFeatureData = new byte[0];
        byte[] faceFeatureDataFaceMask = new byte[0];
        byte[] faceImage = new byte[0];
        ByteArrayOutputStream bos = null;

        try{
            byte[] imageBytes = Base64.decode(anhKhuonMatBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            bitmap = ImageUtils.alignBitmapForBgr24(bitmap);
            if (bitmap == null) {
                return mResult;
            }
            byte[] bgr24 = ImageUtils.bitmapToBgr24(bitmap);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            synchronized (this) {
                if (faceEngine == null || context == null || bgr24 == null || width % 4 != 0 || bgr24.length != width * height * 3) {
                    return mResult;
                }

                //Nhan dien khuon mat
                List<FaceInfo> faceInfoList = new ArrayList<>();
                int code = faceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
                if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                    FaceFeature faceFeature = new FaceFeature();

                    //Kiem tra du lieu khuon mat co du dieu kien dang ky khong
                    code = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), ExtractType.RECOGNIZE, 0, faceFeature);

                    //Luu anh
                    if (code == ErrorInfo.MOK) {

                        //Crop anh chi lay khuon mat
//                        Rect cropRect = getBestRect(width, height, faceInfoList.get(0).getRect());
//                        if (cropRect == null) {
//                            return mResult;
//                        }
//                        if ((cropRect.width() & 1) == 1) {
//                            cropRect.right--;
//                        }
//                        if ((cropRect.height() & 1) == 1) {
//                            cropRect.bottom--;
//                        }
//
//                        bos = new ByteArrayOutputStream();
//                        byte[] headBgr24 = ImageUtils.cropBgr24(bgr24, width, height, cropRect);
//                        Bitmap headBmp = ImageUtils.bgrToBitmap(headBgr24, cropRect.width(), cropRect.height());
//                        headBmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                        faceImage = bos.toByteArray();

                        //Xoay ảnh nếu ảnh khuôn mặt hiện tại không chuẩn
//                            headBmp = BitmapFactory.decodeByteArray(faceImage, 0, faceImage.length);
//                            if (headBmp != null) {
//                                switch (faceInfoList.get(0).getOrient()) {
//                                    case FaceEngine.ASF_OC_0:
//                                        headBmp = bitmap;
//                                        break;
//                                    case FaceEngine.ASF_OC_90:
//                                        headBmp = ImageUtils.rotateBitmap(headBmp, 90);
//                                        break;
//                                    case FaceEngine.ASF_OC_180:
//                                        headBmp = ImageUtils.rotateBitmap(headBmp, 180);
//                                        break;
//                                    case FaceEngine.ASF_OC_270:
//                                        headBmp = ImageUtils.rotateBitmap(headBmp, 270);
//                                        break;
//                                    default:
//                                        break;
//                                }
//                            }
                        //End detect

//                            Bitmap bmFaceMask = headBmp; //Biến xử lý khuôn mặt khi đeo khẩu trang

                        //Nén ảnh khuôn mặt hiện tại để giảm kích thước
//                            bos = new ByteArrayOutputStream();
//                            headBmp.compress(Bitmap.CompressFormat.JPEG, PERCEN_IMAGE_QUALITY, bos);
//                            faceImage = bos.toByteArray();
//                            bos.close();
                        //End compress

                        //Extract thuộc tính khuôn mặt khi đeo khẩu trang
//                        byte[] bgr24FM = ImageUtils.bitmapToBgr24(bmFaceMask);
//                        faceEngine.detectFaces(bgr24FM, bmFaceMask.getWidth(), bmFaceMask.getHeight(), FaceEngine.CP_PAF_BGR24, faceInfoList);
//                        cropRect = getBestRect(bmFaceMask.getWidth(), bmFaceMask.getHeight(), faceInfoList.get(0).getRect());
//                        cropRect = new Rect(cropRect.left, cropRect.top, cropRect.right, (cropRect.bottom - cropRect.top)*7/10);
//                        headBgr24 = ImageUtils.cropBgr24(bgr24FM, bmFaceMask.getWidth(), bmFaceMask.getHeight(), cropRect);
//                        bmFaceMask = ImageUtils.bgrToBitmap(headBgr24, cropRect.width(), cropRect.height());
//                        bmFaceMask = ImageUtils.alignBitmapForBgr24(bmFaceMask);
//                        if (bmFaceMask == null) {
//                            return mResult;
//                        }
//                        bgr24FM = ImageUtils.bitmapToBgr24(bmFaceMask);
//
//                        code = faceEngine.detectFaces(bgr24FM, bmFaceMask.getWidth(), bmFaceMask.getHeight(), FaceEngine.CP_PAF_BGR24, faceInfoList);
//                        if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
//                            FaceFeature faceFeatureFaceMask = new FaceFeature();
//                            code = faceEngine.extractFaceFeature(bgr24FM, bmFaceMask.getWidth(), bmFaceMask.getHeight(), FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), faceFeatureFaceMask);
//                            if(code== ErrorInfo.MOK){
//                                faceFeatureDataFaceMask = faceFeatureFaceMask.getFeatureData();
//                            }
//                        }
                        //End extract

                        faceFeatureData = faceFeature.getFeatureData();
                    }
                }
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        finally {
            if(bos != null){
                bos.close();
            }

            mResult.put("FACE_IMAGE", faceImage);
            mResult.put("FACE_FEATURE", faceFeatureData);
            mResult.put("FACE_FEATURE_FACE_MASK", faceFeatureDataFaceMask);
        }
        return mResult;
    }

    /**
     * Trích xuất thuộc tính khuôn mặt từ file ra byte array và nén
     *
     * @param
     * @return Map luu ten duong dan file anh va mang gia tri thuoc tinh khuon mat
     */
    public byte[] extractFaceFeature(Context context, File file) throws  Exception {
        byte[] faceFeatureData = new byte[0];

        try{
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            bitmap = ImageUtils.alignBitmapForBgr24(bitmap);
            if (bitmap == null) {
                return faceFeatureData;
            }
            byte[] bgr24 = ImageUtils.bitmapToBgr24(bitmap);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            synchronized (this) {
                if (faceEngine == null || context == null || bgr24 == null || width % 4 != 0 || bgr24.length != width * height * 3) {
                    return faceFeatureData;
                }

                //Nhan dien khuon mat
                List<FaceInfo> faceInfoList = new ArrayList<>();
                int code = faceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
                if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                    FaceFeature faceFeature = new FaceFeature();

                    //Kiem tra du lieu khuon mat co du dieu kien dang ky khong
                    code = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), ExtractType.RECOGNIZE, 0, faceFeature);

                    //Crop anh chi lay khuon mat
                    Rect cropRect = getBestRect(width, height, faceInfoList.get(0).getRect());
                    if (cropRect == null) {
                        return faceFeatureData;
                    }
                    if ((cropRect.width() & 1) == 1) {
                        cropRect.right--;
                    }
                    if ((cropRect.height() & 1) == 1) {
                        cropRect.bottom--;
                    }

//                    bos = new ByteArrayOutputStream();
//                    byte[] headBgr24 = ImageUtils.cropBgr24(bgr24, width, height, cropRect);
//                    Bitmap headBmp = ImageUtils.bgrToBitmap(headBgr24, cropRect.width(), cropRect.height());
//                    headBmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                    faceImage = bos.toByteArray();
//
//                    //Xoay ảnh nếu ảnh khuôn mặt hiện tại không chuẩn
//                    headBmp = BitmapFactory.decodeByteArray(faceImage, 0, faceImage.length);
//                    if (headBmp != null) {
//                        switch (faceInfoList.get(0).getOrient()) {
//                            case FaceEngine.ASF_OC_0:
//                                headBmp = bitmap;
//                                break;
//                            case FaceEngine.ASF_OC_90:
//                                headBmp = ImageUtils.rotateBitmap(headBmp, 90);
//                                break;
//                            case FaceEngine.ASF_OC_180:
//                                headBmp = ImageUtils.rotateBitmap(headBmp, 180);
//                                break;
//                            case FaceEngine.ASF_OC_270:
//                                headBmp = ImageUtils.rotateBitmap(headBmp, 270);
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                    //End detect
//
//                    //Nén ảnh khuôn mặt hiện tại để giảm kích thước
//                    bos = new ByteArrayOutputStream();
//                    headBmp.compress(Bitmap.CompressFormat.JPEG, PERCEN_IMAGE_QUALITY, bos);
//                    faceImage = bos.toByteArray();
//                    bos.close();
//                    //End compress

                    //Luu anh
                    if (code == ErrorInfo.MOK) {
                        faceFeatureData = faceFeature.getFeatureData();
                    }
                }
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        finally {
            //bos.close();
            //mResult.put("FACE_IMAGE", faceImage);
            //mResult.put("FACE_FEATURE", faceFeatureData);
        }
        return faceFeatureData;
    }

    /**
     * Nén ảnh từ base64 ra byte array
     *
     * @param
     * @return
     */
    public byte[] compressImage(String anhDaiDienBase64) throws  Exception {
        byte []imageBytes = Base64.decode(anhDaiDienBase64, Base64.DEFAULT);

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        if (bitmap == null) {
            return imageBytes;
        }

        synchronized (this) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, PERCEN_IMAGE_QUALITY, byteArrayOutputStream);
            imageBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return imageBytes;
        }
    }

    /**
     * Tìm kiếm khuôn mặt giống nhất
     *
     * @param faceFeature đặc điểm khuôn mặt
     * @return Kết quả so sánh
     */
    public CompareResult getTopOfFaceLib(FaceFeature faceFeature) {
        if (faceEngine == null || isProcessing || faceFeature == null
            //|| faceRegisterInfoList == null || faceRegisterInfoList.size() == 0
        ) {
            return null;
        }
        FaceFeature tempFaceFeature = new FaceFeature();
        FaceSimilar faceSimilar = new FaceSimilar();
        float maxSimilar = 0;
        int maxSimilarIndex = -1;
        isProcessing = true;
        for (int i = 0; i < faceRegisterInfoList.size(); i++) {
            tempFaceFeature.setFeatureData(faceRegisterInfoList.get(i).getFeatureData());
            faceEngine.compareFaceFeature(faceFeature, tempFaceFeature, faceSimilar);
            if (faceSimilar.getScore() > maxSimilar) {
                maxSimilar = faceSimilar.getScore();
                maxSimilarIndex = i;
            }
        }
        isProcessing = false;
        if (maxSimilarIndex != -1) {
            return new CompareResult(faceRegisterInfoList.get(maxSimilarIndex).getPersonId(), maxSimilar, faceRegisterInfoList.get(maxSimilarIndex).getFacePath());
        }

        return new CompareResult(Constants.DEFAULT_PERSON_ID_NOT_FOUND, 0, "");
    }

    /**
     * Tìm ra khuôn mặt tốt nhất và quay ảnh nếu lệch
     *
     * @param width
     * @param height
     * @param srcRect
     * @return
     */
    public static Rect getBestRect(int width, int height, Rect srcRect) {

        if (srcRect == null) {
            return null;
        }
        Rect rect = new Rect(srcRect);
        //1.Ranh giới rect ban đầu đã tràn qua chiều rộng và chiều cao
        int maxOverFlow = 0;
        int tempOverFlow = 0;
        if (rect.left < 0) {
            maxOverFlow = -rect.left;
        }
        if (rect.top < 0) {
            tempOverFlow = -rect.top;
            if (tempOverFlow > maxOverFlow) {
                maxOverFlow = tempOverFlow;
            }
        }
        if (rect.right > width) {
            tempOverFlow = rect.right - width;
            if (tempOverFlow > maxOverFlow) {
                maxOverFlow = tempOverFlow;
            }
        }
        if (rect.bottom > height) {
            tempOverFlow = rect.bottom - height;
            if (tempOverFlow > maxOverFlow) {
                maxOverFlow = tempOverFlow;
            }
        }
        if (maxOverFlow != 0) {
            rect.left += maxOverFlow;
            rect.top += maxOverFlow;
            rect.right -= maxOverFlow;
            rect.bottom -= maxOverFlow;
            return rect;
        }
        //2.Khi ranh giới rect ban đầu không lớn hơn chiều rộng và chiều cao
        int padding = rect.height() / 2;
        //Nếu phần đệm này được sử dụng để mở rộng phần chỉnh lưu, nó sẽ tràn ra. Lấy phần đệm tối đa làm mức tối thiểu của bốn lề.
        if (!(rect.left - padding > 0 && rect.right + padding < width && rect.top - padding > 0 && rect.bottom + padding < height)) {
            padding = Math.min(Math.min(Math.min(rect.left, width - rect.right), height - rect.bottom), rect.top);
        }

        rect.left -= padding;
        rect.top -= padding;
        rect.right += padding;
        rect.bottom += padding;
        return rect;
    }

    /**
     * Lay khuon mat capture, luu file, luu base64string ra bien toan cuc de update len server
     */
    public byte[] captureFacePhoto(byte[] photo, FaceInfo faceInfo, Camera.Size previewSize) {
        Bitmap bitmap;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayOutputStream bos = null;

        try {
            int code = faceEngine.extractFaceFeature(photo, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfo, ExtractType.RECOGNIZE, 0, new FaceFeature());
            if (code == ErrorInfo.MOK) {
                YuvImage yuvImage = new YuvImage(photo, ImageFormat.NV21, previewSize.width, previewSize.height, null);

                Rect cropRect = FaceServer.getInstance().getBestRect(previewSize.width, previewSize.height, faceInfo.getRect());
                if (cropRect == null) {
                    return null;
                }

                bos = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(cropRect, 50, bos);

                bitmap = BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.toByteArray().length);

                //Kiem tra khuon mat co can dieu chinh khong
                if (bitmap != null) {
                    switch (faceInfo.getOrient()) {
                        case FaceEngine.ASF_OC_0:
                            break;
                        case FaceEngine.ASF_OC_90:
                            bitmap = ImageUtils.rotateBitmap(bitmap, 90);
                            break;
                        case FaceEngine.ASF_OC_180:
                            bitmap = ImageUtils.rotateBitmap(bitmap, 180);
                            break;
                        case FaceEngine.ASF_OC_270:
                            bitmap = ImageUtils.rotateBitmap(bitmap, 270);
                            break;
                        default:
                            break;
                    }
                }

                //Lay ra anh dang base64 string de cap nhat len server
                byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }

            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * So sánh 2 feature
     *
     * @param faceFeature1
     * @param faceFeature2
     * @return Kết quả so sánh
     */
    public float compareFeature(FaceFeature faceFeature1, FaceFeature faceFeature2) {
        if (faceEngine == null || faceFeature1 == null || faceFeature2 == null ) {
            return 0;
        }

        FaceSimilar faceSimilar = new FaceSimilar();
        int ret = faceEngine.compareFaceFeature(faceFeature1, faceFeature2, CompareModel.ID_CARD, faceSimilar);
        return faceSimilar.getScore();
    }
}