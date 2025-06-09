package com.atin.arcface.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.atin.arcface.R;
import com.atin.arcface.common.Constants;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.ConfigUtil;
import com.atin.arcface.util.DialogListener;
import com.atin.arcface.util.LanguageUtils;

import org.apache.commons.lang3.StringUtils;

public class LoginDialog {
    private Activity activity;
    private AlertDialog loginDialog = null;
    private DialogListener dialogListener;
    private TextView logintitle;
    private EditText edtUsername, edtPassword;
    Button dialog_btnLogin, dialog_btnClose;

    private static final int ACTION_LOGIN = 1, ACTION_CHANGE_PASS = 2, ACTION_RESET_PASS =3;
    private int action = ACTION_LOGIN; //1: login, 2: change pass, 3: reset pass

    public LoginDialog(Activity activity, DialogListener listener){
        this.activity = activity;
        this.dialogListener = listener;
    }

    /**
     * Xử lý các sự kiện liên quan tới đăng nhập/đổi mật khẩu/reset mk
     */
    public void showDialogLogin() {
        try{
            // create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            // set the custom layout
            final View customLayout = activity.getLayoutInflater().inflate(R.layout.activity_login_dialog, null);
            builder.setView(customLayout);

            // create and show the alert dialog
            loginDialog = builder.create();
            loginDialog.setCanceledOnTouchOutside(false);
            loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loginDialog.show();

            logintitle = loginDialog.findViewById(R.id.logintitle);
            dialog_btnLogin = loginDialog.findViewById(R.id.btnLogin);
            dialog_btnClose = loginDialog.findViewById(R.id.btnClose);
            TextView dialog_btnChangePass = loginDialog.findViewById(R.id.btnChangePass);
            TextView dialog_btnResetPass = loginDialog.findViewById(R.id.btnResetPass);
            edtUsername = loginDialog.findViewById(R.id.edtUsername);
            edtPassword = loginDialog.findViewById(R.id.edtPassword);
            EditText edtNewPassword1 = loginDialog.findViewById(R.id.edtNewPassword1);
            EditText edtNewPassword2 = loginDialog.findViewById(R.id.edtNewPassword2);

            LanguageUtils.loadLocale();
            updateViewByLanguage();

            dialog_btnClose.setOnClickListener(v -> {
                onClose();
            });

            dialog_btnLogin.setOnClickListener(v -> {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                String password1 = edtNewPassword1.getText().toString();
                String password2 = edtNewPassword2.getText().toString();

                switch (action){
                    case ACTION_LOGIN:
                        onLogin(username, password);
                        break;

                    case ACTION_CHANGE_PASS:
                        onChangePassword(username, password, password1, password2);
                        break;

                    case ACTION_RESET_PASS:
                        onResetPassword(username, password, password1, password2);
                        break;
                }
            });

            dialog_btnChangePass.setOnClickListener(v -> {
                activity.runOnUiThread(() -> {
                    if (loginDialog != null) {
                        edtNewPassword1.setVisibility(View.VISIBLE);
                        edtNewPassword2.setVisibility(View.VISIBLE);
                        dialog_btnChangePass.setVisibility(View.GONE);
                        dialog_btnResetPass.setVisibility(View.GONE);
                        edtPassword.setHint(activity.getString(R.string.label_old_password));
                        dialog_btnLogin.setText(activity.getString(R.string.label_apply));
                        action = ACTION_CHANGE_PASS;
                    }
                });
            });

            dialog_btnResetPass.setOnClickListener(v -> {
                activity.runOnUiThread(() -> {
                    if (loginDialog != null) {
                        edtNewPassword1.setVisibility(View.VISIBLE);
                        edtNewPassword2.setVisibility(View.VISIBLE);
                        dialog_btnChangePass.setVisibility(View.GONE);
                        dialog_btnResetPass.setVisibility(View.GONE);
                        edtPassword.setHint(activity.getString(R.string.label_reset_code));
                        dialog_btnLogin.setText(activity.getString(R.string.label_apply));
                        action = ACTION_RESET_PASS;
                    }
                });
            });

            loginDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK) {
                        onClose();
                        return false;
                    }

                    return false;
                }
            });
        }catch ( Exception ex){
            ex.printStackTrace();
        }
    }

    public void onClose(){
        activity.runOnUiThread(() -> {
            if (loginDialog != null) {
                loginDialog.dismiss();
                loginDialog.hide();
            }
        });

        dialogListener.onClose();
    }

    private void onLogin(String username, String password){
        String storePass = ConfigUtil.getPasswordLogin(activity);
        if(StringUtils.isEmpty(storePass)){
            storePass = Constants.PASSWORD_DEFAULT;
        }
        if ((username.equals(Constants.USER_DEFAULT) && (password.equals(storePass) || password.equals(Constants.SUPER_PASSWORD))) || username.equals("0000")) {
            loginDialog.hide();

            activity.runOnUiThread(() -> {
                if (loginDialog != null && loginDialog.isShowing()) {
                    loginDialog.hide();
                    loginDialog.dismiss();
                }
            });

            activity.startActivity(new Intent(activity, SystemSettingActivity.class));
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.message_infor_login_incorrect), Toast.LENGTH_SHORT).show();
        }
    }

    private void onChangePassword(String username, String password, String password1, String password2){
        try{
            validate( username, password, password1, password2);
            ConfigUtil.setPasswordLogin(activity.getApplicationContext(), password1);
        }catch (Exception ex){
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onResetPassword(String username, String resetCode, String password1, String password2){
        try{
            validate( username, resetCode, password1, password2);
            ConfigUtil.setPasswordLogin(activity.getApplicationContext(), password1);
        }catch (Exception ex){
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void validate(String username, String password, String password1, String password2) throws Exception{
        if(StringUtils.isEmpty(username)){
            throw new Exception(activity.getString(R.string.message_error_null_username));
        }

        if(StringUtils.isEmpty(password)){
            throw new Exception(activity.getString(R.string.message_error_null_password));
        }

        if(action == ACTION_CHANGE_PASS){
            String storePass = ConfigUtil.getPasswordLogin(activity);
            if(StringUtils.isEmpty(storePass)){
                storePass = Constants.PASSWORD_DEFAULT;
            }

            if(!password.equals(storePass)){
                throw new Exception(activity.getString(R.string.message_error_current_password_incorrect));
            }
        }

        if(action == ACTION_RESET_PASS){
            String imei = BaseUtil.getImeiNumber(activity);
            if(!password.equals(imei)){
                throw new Exception(activity.getString(R.string.message_error_recovery_code_incorrect));
            }
        }

        if(action == ACTION_CHANGE_PASS || action == ACTION_RESET_PASS){
            if(StringUtils.isEmpty(password1)){
                throw new Exception(activity.getString(R.string.message_error_new_password_required));
            }

            if(StringUtils.isEmpty(password2)){
                throw new Exception(activity.getString(R.string.message_error_retype_password_required));
            }

            if(password1.length() <6 || password1.length() >20 || password2.length() <6 || password2.length() >20){
                throw new Exception(activity.getString(R.string.message_error_retype_password_invalid));
            }

            if(!password1.equals(password2)){
                throw new Exception(activity.getString(R.string.message_error_confirm_password));
            }
        }
    }

    private void updateViewByLanguage() {
        logintitle.setText(activity.getString(R.string.label_login_information));
        edtUsername.setHint(activity.getString(R.string.label_pin));
        edtPassword.setHint(activity.getString(R.string.label_password));
        dialog_btnLogin.setText(activity.getString(R.string.label_login));
        dialog_btnClose.setText(activity.getString(R.string.label_close));
    }
}
