package com.atin.arcface.util;

import com.atin.arcface.model.LockSearchModel;

import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Date;

public class LockSearchUtil {
    private static Logger logger = Log4jHelper.getLogger("RegisterAndRecognizeDualActivity");

    public static boolean isAvailable(){
        LockSearchModel lockSearchModel = ConfigUtil.getLockTimeSearch();
        if(lockSearchModel == null || lockSearchModel.isDone){
            return true;
        }

        Date searchLastTime = lockSearchModel.searchTime;
        long milliseconds = Calendar.getInstance().getTime().getTime() - searchLastTime.getTime();
        if(milliseconds >= 5000){
            return true;
        }

        return false;
    }

    public static void doLock(){
        logger.info("LockSearchUtil.doLock()");
        ConfigUtil.setLockTimeSearch(new LockSearchModel(new Date(), false));
    }

    public static void unLock(){
        LockSearchModel lockSearchModel = ConfigUtil.getLockTimeSearch();
        if(lockSearchModel != null){
            logger.info("LockSearchUtil.unLock()");
            lockSearchModel.setDone(true);
            ConfigUtil.setLockTimeSearch(lockSearchModel);
        }
    }
}
