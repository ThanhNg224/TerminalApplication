package com.atin.arcface.service;

import android.os.Environment;

import com.atin.arcface.util.Log4jHelper;
import com.atin.arcface.util.StringUtils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ThreadRemoveLogFile extends Thread{
    private static final int DIFF_DATE = 10;
    private Logger logger = Log4jHelper.getLogger("ThreadRemoveLogFile");

    @Override
    public void run() {
        while(true){
            try{
                doRemove();
            }catch (Exception ex){
                logger.error(ex.getMessage());
            }finally {
                try {
                    TimeUnit.HOURS.sleep(12);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRemove(){
        String logFilePath = Environment.getExternalStorageDirectory().toString();
        String sourceFilePath = logFilePath + File.separator + "ATINAccess";

        // Tạo đối tượng File từ đường dẫn thư mục
        File folder = new File(sourceFilePath);

        if(folder.exists() && folder.isDirectory()){
            try {
                // Sử dụng FileUtils.listFiles để lấy danh sách các tệp trong thư mục
                Collection<File> files = FileUtils.listFiles(folder, null, false);

                // In danh sách các tệp
                for (File file : files) {
                    String filenameFull = file.getName();
                    String[] arrFileName = filenameFull.split("\\.");
                    if(arrFileName.length > 0){
                        String fileName = arrFileName[0];
                        Date dtFileDate = StringUtils.convertStringToDate(fileName, "yyyyMMdd");
                        Date dtCurrentDate = Calendar.getInstance().getTime();

                        long millisecondsDifference = dtCurrentDate.getTime() - dtFileDate.getTime();
                        long daysDifference = millisecondsDifference / (24 * 60 * 60 * 1000);

                        if(dtCurrentDate.after(dtFileDate) &&  Math.abs(daysDifference) > DIFF_DATE){
                            FileUtils.forceDelete(file);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
