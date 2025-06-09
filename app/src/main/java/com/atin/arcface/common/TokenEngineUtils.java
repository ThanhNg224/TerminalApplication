package com.atin.arcface.common;

import android.util.Log;

import com.atin.arcface.model.TokenModel;
import com.atin.arcface.util.StringUtils;
import com.google.gson.Gson;

public class TokenEngineUtils {
    private static String TAG = "TokenEngineUtils";
    private static Gson gson = new Gson();

    public static String getToken() {
        TokenModel tokenModel = getTokenFaceEngine();
        String tokenJson = gson.toJson(tokenModel);
        String tokenEncrypt = "";
        try{
            tokenEncrypt = RSAAlgorithm.doEncryptionRSA(tokenJson);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        return tokenEncrypt;
    }

    private static TokenModel getTokenFaceEngine()
    {
        TokenModel token = new TokenModel();
        token.setUnitName(HashInfo.UNIT);
        token.setHashKey(HashInfo.RANDOM_KEY);
        token.setExpireTime(StringUtils.currentDatetimeSQLiteformat());
        return token;
    }
}
