package com.atin.arcface.common;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAAlgorithm {
//    public static PublicKey getPublicKey() throws Exception {
//        byte[] keyBytes = BaseUtil.convertBase64ToByte(HashInfo.PUBLIC_KEY);
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        return kf.generatePublic(spec);
//    }

    public static byte[] convertBase64ToByte(String codeBase64){
        byte[] bytearray = new byte[0];

        try{
            bytearray = Base64.decode(codeBase64, Base64.NO_WRAP);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return bytearray;
    }

    public static String convertByteToBase64(byte[] bytearray){
        String codeBase64 = "";

        try{
            codeBase64 = Base64.encodeToString(bytearray, Base64.NO_WRAP);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return codeBase64;
    }

    public static PublicKey getPublicKey() throws Exception{
        PublicKey publicKey = null;
        byte[] keyBytes = convertBase64ToByte(HashInfo.PUBLIC_KEY);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static String doEncryptionRSA(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
        return convertByteToBase64(cipher.doFinal(data.getBytes()));
    }
}
