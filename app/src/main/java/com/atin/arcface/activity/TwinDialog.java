package com.atin.arcface.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Person;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.atin.arcface.R;
import com.atin.arcface.common.Constants;
import com.atin.arcface.faceserver.CompareResult;
import com.atin.arcface.faceserver.Database;
import com.atin.arcface.model.PersonDB;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.DialogListener;
import com.atin.arcface.util.LanguageUtils;
import com.atin.arcface.widget.ListTwinsAdapter;
import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TwinDialog {
    private Activity activity;
    private AlertDialog dialog = null;
    private AlertDialog confirmDialog = null;
    private DialogListener dialogListener;
    private ListView list;
    private ListTwinsAdapter adapter;
    private Button btnClose;
    private boolean visible;

    public TwinDialog(Activity activity, DialogListener listener) {
        this.activity = activity;
        this.dialogListener = listener;
    }

    /**
     * Xử lý các sự kiện liên quan tới đăng nhập/đổi mật khẩu/reset mk
     */
    public void showDialog(List<CompareResult> lsData) {
        try {
            // create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            // set the custom layout
            final View customLayout = activity.getLayoutInflater().inflate(R.layout.dialog_twins, null);
            builder.setView(customLayout);

            // create and show the alert dialog
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            dialogListener.onShow();

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onClose();
                }
            });

            btnClose = dialog.findViewById(R.id.btnCloseTwinDialog);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClose();
                }
            });

            LanguageUtils.loadLocale();
            loadData(lsData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadData(List<CompareResult> lsData) {
        try {
            adapter = new ListTwinsAdapter(activity, lsData);
            list = dialog.findViewById(R.id.lvCallSupport);
            list.setAdapter(adapter);
            list.setOnItemClickListener(itemClick);
        } catch (Exception ex) {
            Log.e("ListCallSupportDialog", ex.getMessage());
        }
    }

    public void onClose() {
        activity.runOnUiThread(() -> {
            if (dialog != null) {
                dialog.dismiss();
                dialog.hide();
            }
        });

        dialogListener.onClose();
    }

    private AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CompareResult itemSelected = (CompareResult) list.getItemAtPosition(position);
            confirmSelected(itemSelected);
        }
    };

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private void confirmSelected(CompareResult itemSelected){
        // Tạo đối tượng AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(dialog.getContext());

        // Lấy LayoutInflater để inflate layout custom_dialog_layout.xml
        LayoutInflater inflater = (LayoutInflater) dialog.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_confirm_twins, null);

        // Thiết lập layout của AlertDialog từ dialogView
        builder.setView(dialogView);

        // Tùy chỉnh các thành phần giao diện bên trong AlertDialog
        TextView titleTextView = dialogView.findViewById(R.id.txtTitleConfirmTwins);
        TextView bodyTextView = dialogView.findViewById(R.id.txtBodyConfirmTwins);
        Button okButton = dialogView.findViewById(R.id.btnOkConfirmTwin);
        Button cancelButton = dialogView.findViewById(R.id.btnCancelConfirmTwin);

        titleTextView.setText(LanguageUtils.getString(R.string.label_confirm_selected));
        bodyTextView.setText(LanguageUtils.getString(R.string.message_confirm_selected));
        cancelButton.setText(LanguageUtils.getString(R.string.button_no));
        okButton.setText(LanguageUtils.getString(R.string.button_yes));

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.onResponse(itemSelected);
                onClose();
                dialog.dismiss();
                confirmDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClose();
                dialog.dismiss();
                confirmDialog.dismiss();
            }
        });

        dialog.hide();

        // Tạo AlertDialog từ AlertDialog.Builder
        confirmDialog = builder.create();

        // Hiển thị AlertDialog lên màn hình
        confirmDialog.show();
    }
}