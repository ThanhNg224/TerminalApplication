package com.atin.arcface.common;

import android.view.Surface;

import com.atin.arcface.model.DeviceConfigurationModel;

public class DeviceConfigurationParam {
    private static DeviceConfigurationParam deviceConfigurationParam = null;

    public static DeviceConfigurationParam getInstance() {
        if (deviceConfigurationParam == null) {
            synchronized (DeviceConfigurationParam.class) {
                if (deviceConfigurationParam == null) {
                    deviceConfigurationParam = new DeviceConfigurationParam();
                }
            }
        }
        return deviceConfigurationParam;
    }

    public DeviceConfigurationModel getModel(String deviceCode) {
        switch (deviceCode){
            case MachineName.RAKINDA_F3:
                return new DeviceConfigurationModel(deviceCode, Surface.ROTATION_0, 90,true,false,true);

            case MachineName.RAKINDA_F6:
                return new DeviceConfigurationModel(deviceCode, Surface.ROTATION_270, 90,false,false,false);

            case MachineName.RAKINDA_A80M:
                return new DeviceConfigurationModel(deviceCode, Surface.ROTATION_90, 270,true,true,false);

            case MachineName.TELPO_F8:
                return new DeviceConfigurationModel(deviceCode,Surface.ROTATION_0, 90,false,true,true);

            case MachineName.TELPO_TPS980P:
                return new DeviceConfigurationModel(deviceCode,Surface.ROTATION_0, 90,false,false,true);

            case MachineName.TELPO_TPS950:
                return new DeviceConfigurationModel(deviceCode,Surface.ROTATION_0, 90,false,false,true);

            default:
                return new DeviceConfigurationModel(deviceCode, Surface.ROTATION_0, 0,false,false,false);
        }
    }
}
