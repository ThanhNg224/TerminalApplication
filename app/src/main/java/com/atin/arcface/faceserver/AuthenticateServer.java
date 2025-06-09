package com.atin.arcface.faceserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.atin.arcface.R;
import com.atin.arcface.activity.Application;
import com.atin.arcface.activity.RegisterAndRecognizeDualActivity;
import com.atin.arcface.common.CompanyConstantParam;
import com.atin.arcface.common.Constants;
import com.atin.arcface.common.VolleySingleton;
import com.atin.arcface.model.AuthenticateRequestModel;
import com.atin.arcface.model.AuthenticateResponseModel;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.BaseUtil;
import com.atin.arcface.util.Log4jHelper;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import static android.content.Context.MODE_PRIVATE;

//Class thực hiện lấy token
public class AuthenticateServer {
    private static final String TAG = "AuthenticateServer";
    private Context mContext;
    private SharedPreferences pref;
    private String domain;
    private static final Logger logger = Log4jHelper.getLogger( TAG );

    public AuthenticateServer(Context context) {
        this.mContext = context;
        pref = SingletonObject.getInstance(mContext).getSharedPreferences();
        domain = SingletonObject.getInstance(mContext).getDomain();
    }

    public void getToken(){
        try{
            String username = BaseUtil.getImeiNumber(mContext);
            String password = pref.getString(Constants.PREF_PASSWORD_AUTH, "password");
            String token = pref.getString(Constants.PREF_TOKEN_AUTH, "");

            String requestTokenUrl = domain + "/authenticate";
            if(username == null || username.equals("") || password == null || password.equals("")){
                showToast(mContext.getString(R.string.msg_null_auth_server));
                return;
            }

            AuthenticateRequestModel authenticateModel = new AuthenticateRequestModel(username, password, token);
            authenticate(requestTokenUrl, authenticateModel);
        }catch (Exception ex){
            logger.error("Error getToken " + ex.getMessage());
        }
    }

    public void authenticate (String url, final AuthenticateRequestModel model) throws Exception {
        String jsonObject = SingletonObject.getInstance(mContext).getGSon().toJson(model, AuthenticateRequestModel.class);
        JSONObject jObject = new JSONObject(jsonObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jObject,
                response -> {
                    AuthenticateResponseModel responseModel = SingletonObject.getInstance(mContext).getGSon().fromJson(String.valueOf(response), AuthenticateResponseModel.class);
                    pref.edit().putString(Constants.PREF_TOKEN_AUTH, StringUtils.nvl(responseModel.getToken())).apply();
                },
                error -> {
                    String cause = StringUtils.getThrowCause(error);
                    logger.error("Error authenticate api " + cause);
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new LinkedHashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", Constants.PREFIX_TOKEN + pref.getString(Constants.PREF_TOKEN_AUTH, ""));
                return super.getHeaders();
            }
        };

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void showToast(String message){
        SingletonObject.getInstance(mContext).getMainActivity().runOnUiThread(() -> Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show());
    }
}
