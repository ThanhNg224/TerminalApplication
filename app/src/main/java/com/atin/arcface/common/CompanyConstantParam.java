package com.atin.arcface.common;

import com.atin.arcface.model.CompanyConstantParamModel;

public class CompanyConstantParam {
    private static CompanyConstantParam deviceConfigurationParam = null;

    public static CompanyConstantParam getInstance() {
        if (deviceConfigurationParam == null) {
            synchronized (CompanyConstantParam.class) {
                if (deviceConfigurationParam == null) {
                    deviceConfigurationParam = new CompanyConstantParam();
                }
            }
        }
        return deviceConfigurationParam;
    }

    public CompanyConstantParamModel getParam (String partnerName) {
        switch (partnerName){
            case Constants.PartnerName.DEVELOP:
                return new CompanyConstantParamModel(true,true, true,true,true,false, false, true, true, true, 0.75f, 37.5f, 3000, 80, "http://192.168.1.150:42055", "password");

            case Constants.PartnerName.TEST:
                return new CompanyConstantParamModel(true,true, true,true,true,false, false, true, true, true, 0.75f, 37.5f, 3000, 80, "http://192.168.1.210:42055", "password");

            case Constants.PartnerName.GELEXIMCO:
                return new CompanyConstantParamModel(true,true, true,true,true,false, false, true, false, false, 0.75f, 37.5f, 3000, 80, "https://smartfaceengine.geleximco.vn", "password");

            default:
                return new CompanyConstantParamModel(true,true, true,true,true,false, false, true, false, false, 0.75f, 37.5f, 3000, 80, "https://smartfaceengine.dragondoson.vn", "password");
        }
    }
}
