package com.atin.arcface.common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.atin.arcface.util.BaseUtil;

public class NavigationUtils {
    private static final String ACTION_SHOW_NAVIGATIONBAR = "com.android.internal.policy.impl.showNavigationBar";
    private static final String ACTION_HIDE_NAVIGATIONBAR = "com.android.internal.policy.impl.hideNavigationBar";
    private static final String ACTION_OPEN_STATUSBAR = "com.android.systemui.statusbar.phone.statusopen";
    private static final String ACTION_CLOSE_STATUSBAR = "com.android.systemui.statusbar.phone.statusclose";

    public static void hideNavigationBar(Context context, boolean hide){
        switch (Build.MODEL){
            case MachineName.TELPO_F8:
                context.sendBroadcast(hide ? new Intent(ACTION_HIDE_NAVIGATIONBAR) : new Intent(ACTION_SHOW_NAVIGATIONBAR));
                break;

            case MachineName.RAKINDA_A80M:
                if(hide){
                    BaseUtil.broadcastAction(context, "com.custom.hide_navigationbar");
                }else{
                    BaseUtil.broadcastAction(context, "com.custom.show_navigationbar");
                }
                break;

            default:
                break;
        }
    }

    public static void hideStatusBar(Context context, boolean hide){
        switch (Build.MODEL){
            case MachineName.TELPO_F8:
                context.sendBroadcast(hide ? new Intent(ACTION_CLOSE_STATUSBAR) : new Intent(ACTION_OPEN_STATUSBAR));
                break;

            case MachineName.RAKINDA_A80M:
                if(hide){
                    BaseUtil.broadcastAction(context, "com.custom.close.statubar");
                }else{
                    BaseUtil.broadcastAction(context, "com.custom.open.statubar");
                }
                break;

            default:
                break;
        }
    }
}
