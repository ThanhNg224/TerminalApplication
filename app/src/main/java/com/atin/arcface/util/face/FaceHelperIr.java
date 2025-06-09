package com.atin.arcface.util.face;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.util.Log;

import androidx.annotation.Nullable;

import com.arcsoft.face.ImageQualitySimilar;
import com.arcsoft.face.MaskInfo;
import com.arcsoft.face.enums.ExtractType;
import com.atin.arcface.common.Constants;
import com.atin.arcface.model.FacePreviewInfo;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.TrackUtil;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.LivenessInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class FaceHelperIr implements FaceListener{
    private static final String TAG = "FaceHelperIr";
    /**
     * Trạng thái luồng xử lý
     */
    private static final int ERROR_BUSY = -1;
    /**
     * Lỗi trích xuất đặc điểm khuôn mặt
     */
    private static final int ERROR_FR_ENGINE_IS_NULL = -2;
    /**
     * Lỗi phát hiện sự sống trên khuôn mặt
     */
    private static final int ERROR_FL_ENGINE_IS_NULL = -3;
    /**
     * Engine detect khuôn mặt
     */
    private FaceEngine ftEngine;
    /**
     * Engine trích xuất đặc điểm khuôn mặt
     */
    private FaceEngine frEngine;
    /**
     * Engine phát hiện sự sống
     */
    private FaceEngine flEngine;

    private Camera.Size previewSize;

    private List<FaceInfo> faceInfoList = new ArrayList<>();
    private List<MaskInfo> maskInfoList = new CopyOnWriteArrayList<>();
    /**
     * Luồng trích thực hiện xuất thuộc tính khuôn mặt
     */
    private ExecutorService frExecutor;
    /**
     * Luồng thực hiện phát hiện sự sống
     */
    private ExecutorService flExecutor;
    /**
     * Hàng đợi của luồng thực hiện trích xuất thuộc tính khuôn mặt
     */
    private LinkedBlockingQueue<Runnable> frThreadQueue = null;
    /**
     * Hàng đợi của luồng thực hiện phát hiện sự sống
     */
    private LinkedBlockingQueue<Runnable> flThreadQueue = null;

    private FaceListener faceListener;
    /**
     * Số lượng khuôn mặt lần cuối cùng được phát hiện trước khi thoát chương trình
     */
    private int trackedFaceCount = 0;
    /**
     * Số lượng khuôn mặt tối đa được xử lý trong một khung hình
     */
    private int currentMaxFaceId = 0;

    private List<Integer> currentTrackIdList = new ArrayList<>();
    private List<FacePreviewInfo> facePreviewInfoList = new ArrayList<>();
    /**
     * Map lưu id và tên khuôn mặt
     */
    private ConcurrentHashMap<Integer, String> nameMap = new ConcurrentHashMap<>();

    private SharedPreferences pref;
    private Context mContext;
    private int distancePixel = Constants.MIN_FACE_SIZE_IR;;

    private FaceHelperIr (Builder builder) {
        ftEngine = builder.ftEngine;
        faceListener = builder.faceListener;
        trackedFaceCount = builder.trackedFaceCount;
        previewSize = builder.previewSize;
        frEngine = builder.frEngine;
        flEngine = builder.flEngine;
        mContext = builder.context;
        /**
         * fr Kích thước hàng đợi
         */
        int frQueueSize = 4;
        if (builder.frQueueSize > 0) {
            frQueueSize = builder.frQueueSize;
        } else {
            Log.e(TAG, "frThread num must > 0,now using default value:" + frQueueSize);
        }
        frThreadQueue = new LinkedBlockingQueue<>(frQueueSize);
        frExecutor = new ThreadPoolExecutor(1, frQueueSize, 0, TimeUnit.MILLISECONDS, frThreadQueue);

        /**
         * fl Kích thước hàng đợi
         */
        int flQueueSize = 4;
        if (builder.flQueueSize > 0) {
            flQueueSize = builder.flQueueSize;
        } else {
            Log.e(TAG, "flThread num must > 0,now using default value:" + flQueueSize);
        }
        flThreadQueue = new LinkedBlockingQueue<Runnable>(flQueueSize);
        flExecutor = new ThreadPoolExecutor(1, flQueueSize, 0, TimeUnit.MILLISECONDS, flThreadQueue);
        if (previewSize == null) {
            throw new RuntimeException("previewSize must be specified!");
        }

        pref = mContext.getSharedPreferences(Constants.SHARE_PREFERENCE, MODE_PRIVATE);
        distancePixel = pref.getInt(Constants.PREF_DISTANCE_DETECT_PIXEL, Constants.MIN_FACE_SIZE_IR);
    }

    public LivenessInfo getLiveness( FaceInfo faceInfo, byte[] nv21Data, int width, int height, int format, int trackId ){
        List<LivenessInfo> livenessInfoList = new ArrayList<>();
        int flCode;
        synchronized (flEngine) {
            flCode = flEngine.processIr(nv21Data, width, height, format, Arrays.asList(faceInfo), FaceEngine.ASF_IR_LIVENESS);
        }
        if (flCode == ErrorInfo.MOK) {
            flCode = flEngine.getIrLiveness(livenessInfoList);
        }

        if (flCode == ErrorInfo.MOK && livenessInfoList.size() > 0) {
            return livenessInfoList.get(0);
        }

        return null;
    }

    /**
     * Yêu cầu lấy đặc điểm khuôn mặt
     *
     * @param nv21     Dữ liệu hình ảnh dạng byte
     * @param faceInfo Thông tin khuôn mặt
     * @param width    Chiều rộng
     * @param height   Chiều cao
     * @param format   Định dạng dữ liệu
     * @param trackId  Mã id được gán cho dữ liệu ảnh
     */
    public void requestFaceFeature(byte[] nv21, FaceInfo faceInfo, int width, int height, int format, Integer trackId) {
        if (faceListener != null) {
            if (frEngine != null && frThreadQueue.remainingCapacity() > 0) {
                frExecutor.execute(new FaceRecognizeRunnable(nv21, faceInfo, width, height, format, trackId));
            } else {
                faceListener.onFaceFeatureInfoGet(null, trackId, ERROR_BUSY);
            }
        }
    }

    /**
     * Kiểm tra lấy thông tin sự sống
     *
     * @param nv21         NV21 dữ liệu hình ảnh dạng byte
     * @param faceInfo     Thông tin khuôn mặt
     * @param width        Chiều rộng
     * @param height       Chiều cao
     * @param format       Định dạng dữ liệu
     * @param trackId      Mã ID được gán cho hình ảnh
     * @param livenessType Thông tin sự sống
     */
    public void requestFaceLiveness(byte[] nv21, FaceInfo faceInfo, int width, int height, int format, Integer trackId, LivenessType livenessType) {
        if (faceListener != null) {
            if (flEngine != null && flThreadQueue.remainingCapacity() > 0) {
                flExecutor.execute(new FaceLivenessDetectRunnable(nv21, faceInfo, width, height, format, trackId, livenessType));
            } else {
                faceListener.onFaceLivenessInfoGet(null, trackId, ERROR_BUSY);
            }
        }
    }

    /**
     * Phát hành
     */
    public void release() {
        if (!frExecutor.isShutdown()) {
            frExecutor.shutdownNow();
            frThreadQueue.clear();
        }
        if (!flExecutor.isShutdown()) {
            flExecutor.shutdownNow();
            flThreadQueue.clear();
        }
        if (faceInfoList != null) {
            faceInfoList.clear();
        }
        if (frThreadQueue != null) {
            frThreadQueue.clear();
            frThreadQueue = null;
        }
        if (flThreadQueue != null) {
            flThreadQueue.clear();
            flThreadQueue = null;
        }
        if (nameMap != null) {
            nameMap.clear();
        }
        nameMap = null;
        faceListener = null;
        faceInfoList = null;
    }

    /**
     * Xử lý dữ liệu khung
     *
     * @param nv21 Dữ liệu NV21 được trả về bởi bản xem trước của máy ảnh
     * @return Kết quả xử lý khuôn mặt theo thời gian thực, được đóng gói và thêm trackId, việc thu thập trackId phụ thuộc vào faceId, được sử dụng để ghi lại số khuôn mặt và lưu
     */
    public List<FacePreviewInfo> onPreviewIrFrame(byte[] nv21) {
        if (faceListener != null) {
            if (ftEngine != null) {
                faceInfoList.clear();
                maskInfoList.clear();
                facePreviewInfoList.clear();

                int code = ftEngine.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);
                if (code != ErrorInfo.MOK) {
                    faceListener.onFail(new Exception("ft failed,code is " + code));
                }

                /*
                 * Nếu bạn cần tìm kiếm nhiều khuôn mặt, hãy xóa dòng mã này
                 */
                TrackUtil.keepMaxFace(faceInfoList);
                refreshTrackId(faceInfoList);

                if (faceInfoList.isEmpty()) {
                    return facePreviewInfoList;
                }

                for (int i = 0; i < faceInfoList.size(); i++) {
                    int width = faceInfoList.get(i).getRect().right - faceInfoList.get(i).getRect().left;
                    int heigh = faceInfoList.get(i).getRect().bottom - faceInfoList.get(i).getRect().top;
                    if (width > Constants.MIN_FACE_SIZE_IR && heigh > Constants.MIN_FACE_SIZE_IR)
                    {
                        facePreviewInfoList.add(new FacePreviewInfo(faceInfoList.get(i), currentTrackIdList.get(i)));
                    }
                }
            }

            return facePreviewInfoList;
        } else {
            facePreviewInfoList.clear();
            return facePreviewInfoList;
        }
    }

    @Override
    public void onFail(Exception e) {
        Log.e(TAG, "onFail:" + e.getMessage());
    }

    @Override
    public void onFaceFeatureInfoGet(@Nullable FaceFeature faceFeature, Integer requestId, Integer errorCode) {

    }

    @Override
    public void onFaceLivenessInfoGet(@Nullable LivenessInfo livenessInfo, Integer requestId, Integer errorCode) {

    }

    /**
     * Thực hiện trích xuất đặc điểm khuôn mặt
     */
    public class FaceRecognizeRunnable implements Runnable {
        private FaceInfo faceInfo;
        private int width;
        private int height;
        private int format;
        private Integer trackId;
        private byte[] nv21Data;

        private FaceRecognizeRunnable(byte[] nv21Data, FaceInfo faceInfo, int width, int height, int format, Integer trackId) {
            if (nv21Data == null) {
                return;
            }
            this.nv21Data = nv21Data;
            this.faceInfo = new FaceInfo(faceInfo);
            this.width = width;
            this.height = height;
            this.format = format;
            this.trackId = trackId;
        }

        @Override
        public void run() {
            if (faceListener != null && nv21Data != null) {
                if (frEngine != null) {
                    FaceFeature faceFeature = new FaceFeature();
                    int frCode;
                    synchronized (frEngine) {
                        frCode = frEngine.extractFaceFeature(nv21Data, width, height, format, faceInfo, ExtractType.RECOGNIZE, 0, faceFeature);
                    }
                    if (frCode == ErrorInfo.MOK) {
                        faceListener.onFaceFeatureInfoGet(faceFeature, trackId, frCode);
                    } else {
                        faceListener.onFaceFeatureInfoGet(null, trackId, frCode);
                        faceListener.onFail(new Exception("fr failed errorCode is " + frCode));
                    }
                } else {
                    faceListener.onFaceFeatureInfoGet(null, trackId, ERROR_FR_ENGINE_IS_NULL);
                    faceListener.onFail(new Exception("fr failed ,frEngine is null"));
                }
            }
            nv21Data = null;
        }
    }

    /**
     * Thực hiện phát hiện sự sống
     */
    public class FaceLivenessDetectRunnable implements Runnable {
        private FaceInfo faceInfo;
        private int width;
        private int height;
        private int format;
        private Integer trackId;
        private byte[] nv21Data;
        private LivenessType livenessType;

        private FaceLivenessDetectRunnable(byte[] nv21Data, FaceInfo faceInfo, int width, int height, int format, Integer trackId, LivenessType livenessType) {
            if (nv21Data == null) {
                return;
            }
            this.nv21Data = nv21Data;
            this.faceInfo = new FaceInfo(faceInfo);
            this.width = width;
            this.height = height;
            this.format = format;
            this.trackId = trackId;
            this.livenessType = livenessType;
        }

        @Override
        public void run() {
            if (faceListener != null && nv21Data != null) {
                if (flEngine != null) {
                    List<LivenessInfo> livenessInfoList = new ArrayList<>();
                    int flCode;
                    synchronized (flEngine) {
                        if (livenessType == LivenessType.RGB) {
                            flCode = flEngine.process(nv21Data, width, height, format, Arrays.asList(faceInfo), FaceEngine.ASF_LIVENESS);
                        } else {
                            flCode = flEngine.processIr(nv21Data, width, height, format, Arrays.asList(faceInfo), FaceEngine.ASF_IR_LIVENESS);
                        }

                        if (flCode == ErrorInfo.MOK) {
                            if (livenessType == LivenessType.RGB) {
                                flCode = flEngine.getLiveness(livenessInfoList);
                            } else {
                                flCode = flEngine.getIrLiveness(livenessInfoList);
                            }
                        }

                        if (flCode == ErrorInfo.MOK && livenessInfoList.size() > 0) {
                            faceListener.onFaceLivenessInfoGet(livenessInfoList.get(0), trackId, flCode);
                        } else {
                            faceListener.onFaceLivenessInfoGet(null, trackId, flCode);
                            faceListener.onFail(new Exception("fl failed errorCode is " + flCode));
                        }
                    }
                } else {
                    faceListener.onFaceLivenessInfoGet(null, trackId, ERROR_FL_ENGINE_IS_NULL);
                    faceListener.onFail(new Exception("fl failed ,frEngine is null"));
                }
            }
            nv21Data = null;
        }
    }


    /**
     * Làm mới trackId
     *
     * @param ftFaceList Dánh sách khuôn mặt được tìm thấy
     */
    private void refreshTrackId(List<FaceInfo> ftFaceList) {
        currentTrackIdList.clear();

        for (FaceInfo faceInfo : ftFaceList) {
            currentTrackIdList.add(faceInfo.getFaceId() + trackedFaceCount);
        }
        if (ftFaceList.size() > 0) {
            currentMaxFaceId = ftFaceList.get(ftFaceList.size() - 1).getFaceId();
        }

        clearLeftName(currentTrackIdList);
    }

    /**
     * Lấy giá trị trackid hiện tại
     *
     * @return trackId
     */
    public int getTrackedFaceCount() {
        return trackedFaceCount + currentMaxFaceId + 1;
    }

    /**
     * Gán tên cho một khuôn mặt được tìm thấy thành công
     *
     * @param trackId
     * @param name
     */
    public void setName(int trackId, String name) {
        if (nameMap != null) {
            nameMap.put(trackId, name);
        }
    }

    /**
     * Xóa các khuôn mặt còn lại trên màn hình
     *
     * @param trackIdList trackIdList mới nhất
     */
    private void clearLeftName(List<Integer> trackIdList) {
        try{
            Set<Integer> keySet = nameMap.keySet();
            for (Integer integer : keySet) {
                if (!trackIdList.contains(integer)) {
                    nameMap.remove(integer);
                }
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    public static final class Builder {
        private FaceEngine ftEngine;
        private FaceEngine frEngine;
        private FaceEngine flEngine;
        private Camera.Size previewSize;
        private FaceListener faceListener;
        private int frQueueSize;
        private int flQueueSize;
        private int trackedFaceCount;
        private Context context;

        public Builder() {
        }

        public Builder ftEngine(FaceEngine val) {
            ftEngine = val;
            return this;
        }

        public Builder frEngine(FaceEngine val) {
            frEngine = val;
            return this;
        }

        public Builder flEngine(FaceEngine val) {
            flEngine = val;
            return this;
        }


        public Builder previewSize(Camera.Size val) {
            previewSize = val;
            return this;
        }


        public Builder faceListener(FaceListener val) {
            faceListener = val;
            return this;
        }

        public Builder frQueueSize(int val) {
            frQueueSize = val;
            return this;
        }

        public Builder flQueueSize(int val) {
            flQueueSize = val;
            return this;
        }

        public Builder trackedFaceCount(int val) {
            trackedFaceCount = val;
            return this;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public FaceHelperIr build() {
            return new FaceHelperIr(this);
        }
    }
}