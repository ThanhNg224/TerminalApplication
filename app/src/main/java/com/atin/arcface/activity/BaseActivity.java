package com.atin.arcface.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.atin.arcface.R;
import com.atin.arcface.model.Language;
import com.atin.arcface.service.ContextWrapper;
import com.atin.arcface.util.ConfigUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class BaseActivity extends AppCompatActivity {

    private static ExecutorService executor;
    private static List<Activity> activityList;
    private static final int SNACK_BAR_MAX_LINES = 50;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (activityList == null) {
            activityList = new ArrayList<>();
        }
        if (executor == null) {
            executor = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    r -> {
                        Thread t = new Thread(r);
                        t.setName("activity-sub-thread-" + t.getId());
                        return t;
                    });
        }
        activityList.add(this);
    }

    /**
     * Kiểm tra các quyên yêu cầu được cấp
     *
     * @param neededPermissions Mảng các quyền cần cấp
     * @return true/false
     */
    protected boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    /**
     * Kiểm tra quyền ghi bộ nhớ hệ thống
     * @return
     */
    protected boolean requestWriteSystemPermissions(){
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this);
        }
        return retVal;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;
        for (int grantResult : grantResults) {
            isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
        }
        afterRequestPermission(requestCode, isAllGranted);
    }

    protected void showToast(final String s) {
        Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            toast.show();
        } else {
            runOnUiThread(toast::show);
        }
    }


    /**
     * Kết quả từ việc cấp quyền
     *
     * @param requestCode
     * @param isAllGranted
     */
    abstract void afterRequestPermission(int requestCode, boolean isAllGranted);

    protected void showLongSnackBar(final View view, final String s) {
        Snackbar snackbar = Snackbar.make(view, s, Snackbar.LENGTH_LONG);
        enableSnackBarShowMultiLines(snackbar, SNACK_BAR_MAX_LINES);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            snackbar.show();
        } else {
            runOnUiThread(snackbar::show);
        }
    }

    private void enableSnackBarShowMultiLines(Snackbar snackbar, int maxLines) {
        final SnackbarContentLayout contentLayout = (SnackbarContentLayout) ((ViewGroup) snackbar.getView()).getChildAt(0);
        @SuppressLint("RestrictedApi") final TextView tv = contentLayout.getMessageView();
        tv.setMaxLines(maxLines);
    }

    /**
     * Chạy một runnable trong pool
     *
     * @param runnable
     */
    public void runOnSubThread(Runnable runnable) {
        executor.execute(runnable);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Language language = ConfigUtil.getLanguage();
        Locale locale = new Locale(language == null ? Application.getInstance().getString(R.string.language_vietnamese_code) : language.getCode());
        Context context = ContextWrapper.wrap(newBase, locale);
        super.attachBaseContext(context);
    }

}
