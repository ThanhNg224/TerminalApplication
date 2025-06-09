package com.atin.arcface.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.atin.arcface.R;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.MachineName;
import com.atin.arcface.service.SingletonObject;
import com.common.pos.api.util.PosUtil;
import com.google.gson.JsonObject;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BaseUtil {

    public static void broadcastAction(Context context, String actionName){
        Intent intent = new Intent();
        intent.setAction(actionName);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(intent);
    }

    public static void broadUpdateSynchronzieStatus(Context context, boolean msg){
        Intent intent = new Intent(Constants.SYNCHRONIZE_STATUS);
        intent.putExtra("msg", msg);
        context.sendBroadcast(intent);
    }

    public static void broadcastShowMessage(Context context, String msg){
        Intent intent = new Intent(Constants.SHOW_NOTIFICATION);
        intent.putExtra("msg", msg);
        context.sendBroadcast(intent);
    }

    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public static boolean checkPoinInsideCircle(int x, int centerX, int y, int centerY, int r){
        int dx = Math.abs(x-centerX);
        int dy = Math.abs(y-centerY);

        if (dx + dy <= r){
            return true;
        }

        if (dx > r){
            return false;
        }

        if (dy > r){
            return false;
        }

        if (dx*dx + dy*dx <= r*r){
            return true;
        }

        return false;
    }

    public static void reboot() throws Exception {
        try {
            Runtime.getRuntime().exec("su");
            Runtime.getRuntime().exec("reboot");
        } catch (IOException e) {
            throw new Exception("Device is not support this function");
        }
    }

    public static boolean isConnectedToThisServer(String host) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + host);
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getCardNumber(Intent intent) {
        byte[] ID = new byte[20];
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        ID = tag.getId();
        String UID = NFCUtils.bytesToHexString(ID);
        String IDString = NFCUtils.bytearray2Str(NFCUtils.hexStringToBytes(UID.substring(2, UID.length())), 0, 4, 10);
        return IDString;
    }

    public static String removeFirstZeroNumber(String input){
        if(input == null || input.length() == 0){
            return "";
        }

        input = input.trim();
        if(input.startsWith("0")){
            input = input.substring(1, input.length());
            removeFirstZeroNumber(input);
        }else{
            return input;
        }

        return  input;
    }

    /**
     *
     * @param bitmap
     * @return
     */
    public static Result scanningImage(Bitmap bitmap) {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        bitmap = BaseUtil.changeBitmapContrastBrightness(bitmap, 1, -50);
        int brightness = BaseUtil.calculateBrightnessEstimate(bitmap, 1);
        while(brightness > 5){
            try{
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                com.google.zxing.RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);

                Result rawResult = null;
                if (source != null) {
                    try {
                        rawResult = multiFormatReader.decode(new BinaryBitmap(new HybridBinarizer(source)));
                        return rawResult;
                    } catch (ReaderException re) {
                        bitmap = BaseUtil.changeBitmapContrastBrightness(bitmap, 1, -5);
                        brightness = BaseUtil.calculateBrightnessEstimate(bitmap, 1);
                    } finally {
                        multiFormatReader.reset();
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    /*
    Calculates the estimated brightness of an Android Bitmap.
    pixelSpacing tells how many pixels to skip each pixel. Higher values result in better performance, but a more rough estimate.
    When pixelSpacing = 1, the method actually calculates the real average brightness, not an estimate.
    This is what the calculateBrightness() shorthand is for.
    Do not use values for pixelSpacing that are smaller than 1.
    */
    public static int calculateBrightnessEstimate(android.graphics.Bitmap bitmap, int pixelSpacing) {
        int R = 0; int G = 0; int B = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += pixelSpacing) {
            int color = pixels[i];
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            n++;
        }
        return (R + B + G) / (n * 3);
    }

    /**
     *
     * @param bmp input bitmap
     * @param contrast 0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap convertByteToBitmap(byte[] nv21bytearray, Camera.Size previewSize){
        YuvImage yuvImage = new YuvImage(nv21bytearray, ImageFormat.NV21, previewSize.width, previewSize.height, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, os);
        byte[] jpegByteArray = os.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);
        return bitmap;
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static String convertByteToBase64(byte[] bytearray){
        String codeBase64 = "";

        try{
            codeBase64 = Base64.encodeToString(bytearray, Base64.DEFAULT);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return codeBase64;
    }

    public static byte[] convertBase64ToByte(String codeBase64){
        byte[] bytearray = new byte[0];

        try{
            bytearray = Base64.decode(codeBase64, Base64.DEFAULT);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return bytearray;
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static boolean screenBrightness(int level, Context context) {
        try {
            android.provider.Settings.System.putInt(
                    context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS, level);


            android.provider.Settings.System.putInt(context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

            android.provider.Settings.System.putInt(
                    context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS,
                    level);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    public static boolean checkMD5(String TAG, String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Log.e(TAG, "MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = BaseUtil.calculateMD5(TAG, updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");
            return false;
        }

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    public static String calculateMD5(String TAG, File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    public static String getAppId(String model){
        return Constants.APP_ID;
    }

    public static String getSdkKey(String model){
        return Constants.SDK_KEY;
    }

    public static boolean regexIpv4(String ip) {
        if(ip == null || ip.length()==0)
            return false;

        Pattern p = Pattern.compile("^"
                + "(((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}" // Domain name
                + "|"
                + "localhost" // localhost
                + "|"
                + "(([0-9]{1,3}\\.){3})[0-9]{1,3})" // Ip
                + ":"
                + "[0-9]{1,5}$"); // Port

        return p.matcher(ip).matches();
    }

    public static int getAngleValue(int angleIndex, int rotationDefault){
        switch ( angleIndex){
            case Constants.ASF_OP_0_ONLY:
                return 0;
            case Constants.ASF_OP_90_ONLY:
                return 90;
            case Constants.ASF_OP_180_ONLY:
                return 180;
            case Constants.ASF_OP_270_ONLY:
                return 270;
            default:
                return rotationDefault;
        }
    }

    //Kiểm tra đã cài gói service cho phần nhiệt độ chưa
    public static boolean isInstalledPackage(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public static void deleteFile(File file){
        try{
            if(file.exists()){
                FileUtils.forceDelete(file);
            }
        }catch (Exception ex){

        }
    }

    public void deleteDirectory(String path){
        try{
            File file = new File(path);
            if(file.exists()){
                FileUtils.deleteDirectory(file);
            }
        }catch (Exception ex){

        }
    }

    public static boolean downloadImage(String urlLink, File file) throws Exception{
        HttpURLConnection connection = null;
        InputStream input = null;
        FileOutputStream out = null;
        Boolean blResult = true;

        try{
            URL url = new URL(urlLink);
            connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(5000); // 5 seconds
            connection.setReadTimeout(5000);    // 5 seconds

            int responseCode = connection.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK){
                return false;
            }

            input = connection.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(input);
            out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, out); // Compress Image
            out.flush();
        }catch(IOException e){
            blResult = false;
            e.printStackTrace();
        } finally {
            if(out != null){
                out.close();
            }
            if(input != null){
                input.close();
            }

            if(connection != null){
                connection.disconnect();
            }
        }
        return blResult;
    }

    public static boolean saveImage(String imageBase64, File file){
        Boolean blResult = true;

        byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
        try (OutputStream stream = new FileOutputStream(file)) {
            stream.write(decodedBytes);
        }catch (Exception e){
            blResult = false;
            e.printStackTrace();
        }
        return blResult;
    }

    public static boolean downloadImage(String urlLink, File file, String extension) throws Exception{
        HttpURLConnection connection = null;
        InputStream input = null;
        FileOutputStream out = null;
        Boolean blResult = true;
        Bitmap.CompressFormat formatFile = Bitmap.CompressFormat.JPEG;

        try{
            URL url = new URL(urlLink);
            connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK){
                return false;
            }

            if(extension.toUpperCase().contains("JPEG")){
                formatFile = Bitmap.CompressFormat.JPEG;
            }else if(extension.toUpperCase().contains("PNG")){
                formatFile = Bitmap.CompressFormat.PNG;
            }else if(extension.toUpperCase().contains("WEBP")){
                formatFile = Bitmap.CompressFormat.WEBP;
            }

            input = connection.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(input);
            out = new FileOutputStream(file);
            bm.compress(formatFile, 100, out); // Compress Image
            out.flush();
        }catch(IOException e){
            blResult = false;
            e.printStackTrace();
        } finally {
            if(out != null){
                out.close();
            }
            if(input != null){
                input.close();
            }

            if(connection != null){
                connection.disconnect();
            }
        }
        return blResult;
    }

    public static boolean downloadLogo(String urlLink, File file, String extension) throws Exception{
        HttpURLConnection connection = null;
        InputStream input = null;
        FileOutputStream out = null;
        Boolean blResult = true;
        Bitmap.CompressFormat formatFile = Bitmap.CompressFormat.JPEG;

        try{
            URL url = new URL(urlLink);
            connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK){
                return false;
            }

            if(extension.toUpperCase().contains("JPEG")){
                formatFile = Bitmap.CompressFormat.JPEG;
            }else if(extension.toUpperCase().contains("PNG")){
                formatFile = Bitmap.CompressFormat.PNG;
            }else if(extension.toUpperCase().contains("WEBP")){
                formatFile = Bitmap.CompressFormat.WEBP;
            }

            input = connection.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(input);

            int width = bm.getWidth();
            int height = bm.getHeight();

            if(width > 512 && height > 512){
                int newWidth = 512;
                int newHeight = ( newWidth * height )/width;
                bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, false);
            }else if(width > 512){
                int newWidth = 512;
                int newHeight = ( newWidth * height )/width;
                bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, false);
            }else if (height > 512){
                int newHeight = 512;
                int newWidth = ( newHeight * width )/height;
                bm = Bitmap.createScaledBitmap(bm, newWidth, newHeight, false);
            }

            out = new FileOutputStream(file);
            bm.compress(formatFile, 100, out); // Compress Image
            out.flush();
        }catch(IOException e){
            blResult = false;
            e.printStackTrace();
        } finally {
            if(out != null){
                out.close();
            }
            if(input != null){
                input.close();
            }

            if(connection != null){
                connection.disconnect();
            }
        }
        return blResult;
    }

    public static void resizeImage(File file) throws Exception {
        try {
            if (!file.exists()) {
                return;
            }

            BitmapFactory.Options options=new BitmapFactory.Options();
            InputStream fis = new FileInputStream(file) ;
            Bitmap bm = BitmapFactory.decodeStream(fis,null,options);

            int height = bm.getHeight();
            int width = bm.getWidth();
            boolean blResize = false;
            if (width > Constants.FACE_IMAGE_MAXIMUM_WIDTH) {
                height = Math.round((float) (Constants.FACE_IMAGE_MAXIMUM_WIDTH * height / width));
                width = Constants.FACE_IMAGE_MAXIMUM_WIDTH;
                blResize = true;
            }

            if (height > Constants.FACE_IMAGE_MAXIMUM_HEIGHT) {
                width = Math.round((float) (Constants.FACE_IMAGE_MAXIMUM_HEIGHT * width / height));
                height = Constants.FACE_IMAGE_MAXIMUM_HEIGHT;
                blResize = true;
            }

            int quarlity = 100;
            long fileSize = file.length() / 1024;
            if(fileSize > 5*1024){
                quarlity = 50;
            } else if(fileSize > 4*1024 && fileSize < 5*1024){
                quarlity = 60;
            } else if(fileSize > 3*1024 && fileSize < 4*1024){
                quarlity = 70;
            } else if(fileSize > 2*1024 && fileSize < 3*1024){
                quarlity = 80;
            } else if(fileSize > 1*1024 && fileSize < 2*1024){
                quarlity = 90;
            }else{
                quarlity = 100;
            }

            if (blResize) {
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, width, height,false);
                FileOutputStream out = new FileOutputStream(file);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quarlity, out);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            throw new Exception("Format of image is incorect");
        }
    }

    public static boolean downloadFile(String urlLink, File file) throws Exception{
        HttpURLConnection connection = null;
        InputStream input = null;
        FileOutputStream out = null;
        Boolean blResult = true;

        try{
            URL url = new URL(urlLink);
            connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK){
                return false;
            }
            input = connection.getInputStream();
            out = new FileOutputStream(file);

            int bytesRead = -1;
            byte[] buffer = new byte[Constants.BUFFER_SIZE];
            while ((bytesRead = input.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }catch(Exception e){
            blResult = false;
            e.printStackTrace();
        } finally {
            if(out != null){
                out.close();
            }

            if(input != null){
                input.close();
            }

            if(connection != null){
                connection.disconnect();
            }
        }
        return blResult;
    }

    public static int getMaxSyncId(){
        int syncId = 1;
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            syncId = Integer.parseInt(StringUtils.convertDateToString(cal.getTime(), "yyMMddHHmm"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return syncId;
    }

    public static int getSyncId(){
        int syncId = 1;
        try {
            syncId = Integer.parseInt(StringUtils.convertDateToString(Calendar.getInstance().getTime(), "yyMMddHHmm"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return syncId;
    }

    public static boolean compareTimeCheckOut (String strNextTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date nextTime = sdf.parse(strNextTime);
            Date currentTime = Calendar.getInstance().getTime();

            if(currentTime.before(nextTime)){
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static  int distanceToPixel(int distance, String model){
        int pixel = 0;

        if(model.equals(MachineName.RAKINDA_F3)){
            pixel = distanceToPixelF3(distance);
        }else if(model.equals(MachineName.TELPO_F8)
                || model.equals(MachineName.TELPO_TPS980P)){
            pixel = distanceToPixelF8(distance);
        }else{
            pixel = distanceToPixelF8(distance);
        }
        return pixel;
    }

    private static int distanceToPixelF8(int distance) {
        int pixel = 0;

        if (distance < 30) {
            pixel = 250;
        } else if (30 <= distance && distance < 40) {
            pixel = 230;
        } else if (40 <= distance && distance < 50) {
            pixel = 180;
        } else if (50 <= distance && distance < 60) {
            pixel = 140;
        } else if (60 <= distance && distance < 70) {
            pixel = 120;
        } else if (70 <= distance && distance < 80) {
            pixel = 100;
        } else if (80 <= distance && distance < 90) {
            pixel = 85;
        } else if (90 <= distance && distance < 100) {
            pixel = 75;
        } else {
            pixel = 50;
        }

        return pixel;
    }

    private static int distanceToPixelF3(int distance) {
        int pixel = 0;

        if (distance < 30) {
            pixel = 250;
        } else if (30 <= distance && distance < 40) {
            pixel = 230;
        } else if (40 <= distance && distance < 50) {
            pixel = 180;
        } else if (50 <= distance && distance < 60) {
            pixel = 140;
        } else if (60 <= distance && distance < 70) {
            pixel = 120;
        } else if (70 <= distance && distance < 80) {
            pixel = 100;
        } else if (80 <= distance && distance < 90) {
            pixel = 85;
        } else if (90 <= distance && distance < 100) {
            pixel = 75;
        } else {
            pixel = 50;
        }

        return pixel;
    }

    public static int countLines(String filePath) {
        File file = new File(filePath);

        if(!file.exists()){
            return 0;
        }

        try {
            String data = FileUtils.readFileToString(file);
            return new StringBuffer(data).toString().split("\n").length;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getLine(StringBuffer buf) throws Exception {
        try {
            int iIndex = buf.indexOf("\n");
            String strReturn;
            if (iIndex < 0) {
                strReturn = buf.toString();
                iIndex = buf.length() - 1;
                if (strReturn.equals("")) {
                    strReturn = null;
                }
            } else {
                strReturn = buf.substring(0, iIndex);
            }
            buf.delete(0, iIndex + 1);
            return strReturn;
        } catch (Exception ex) {
            throw new Exception("Get line: " + ex.toString());
        }
    }

    public static String getLocalIpv4() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return "";
    }

    public static boolean compareLanNetwork(String ip1, String ip2) {
        if (ip1 == null || ip1.equals("") || ip2 == null || ip2.equals(""))
            return false;

        String arrIp1[] = ip1.split("\\.");
        String arrIp2[] = ip2.split("\\.");

        if (arrIp1.length != 4 || arrIp2.length != 4)
            return false;

        String netip1 = arrIp1[0] + "." + arrIp1[1] + "." + arrIp1[2];
        String netip2 = arrIp2[0] + "." + arrIp2[1] + "." + arrIp2[2];

        if (netip1.equals(netip2)) {
            return true;
        }

        return false;
    }

    public String getLocalIpV6() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {
                        String ipaddress = inetAddress.getHostAddress().toString();
                        return ipaddress;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return "";
    }

    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    //Note cac may chay version cloud cu lay theo imei khong su dung dc ham nay neu khong se loi dong bo
    @SuppressLint("MissingPermission")
    public static String getImeiNumber(Context context) {
        String imeiNumber = "";

        switch (Build.MODEL){
            case MachineName.TELPO_F8:
                imeiNumber = getSerialNumber();
                break;

            case MachineName.TELPO_TPS950:
                imeiNumber = getSerialNumber();
                break;

            case MachineName.TELPO_TPS980P:
                imeiNumber = getSerialNumber();
                break;

            case MachineName.RAKINDA_F6:
                imeiNumber = SingletonObject.getInstance(context).getmYNHAPI().getSerialNo();
                break;

            case MachineName.RAKINDA_A80M:
                imeiNumber = getSerialNumber();
                break;

            default:
                try {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    if (null != tm) {
                        imeiNumber = tm.getDeviceId();
                    }
                    if (null == imeiNumber || 0 == imeiNumber.length() || "0".equals(imeiNumber)) {
                        imeiNumber = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    }

                    //imeiNumber = tm.getImei();
                }catch (Exception ex){
                    Log.e("IMEI", ex.getMessage());
                }
                break;

        }
        return imeiNumber;
    }

    /**
     * Get serial number of device
     * @return the serial number
     */
    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    public static String getHashMD5(String input){
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String jsonObjectToString(JsonObject jsonObject, String fieldName){
        return jsonObject.get(fieldName).toString().replaceAll("\"", "");
    }

    public static Bitmap decodeBase64Profile(String input) {
        Bitmap bitmap = null;
        if (input != null) {
            byte[] decodedByte = Base64.decode(input, 0);
            bitmap = BitmapFactory
                    .decodeByteArray(decodedByte, 0, decodedByte.length);
        }
        return bitmap;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return formatSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return formatSize(availableBlocks * blockSize);
        } else {
            return "";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return formatSize(totalBlocks * blockSize);
        } else {
            return "";
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = " KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = " MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = " GB";
                    size /= 1024;
                }
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static File[] getListFile(String filePath, final String extendFile) {
        File[] files = new File[0];

        try {
            File f = new File(filePath);

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.endsWith(extendFile);
                }
            };

            files = f.listFiles(filter);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return files;
    }

    public static void saveImageFile (File fileSave, byte[] data){
        if (fileSave.exists()) {
            fileSave.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(fileSave.getPath());
            fos.write(data);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
    }
}
