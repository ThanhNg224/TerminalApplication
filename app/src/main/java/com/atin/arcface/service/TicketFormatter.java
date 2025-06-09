package com.atin.arcface.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.atin.arcface.R;
import com.atin.arcface.faceserver.CompareResult;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class TicketFormatter {

    public static void printFormattedCanteenTicket(Context context, CompareResult result) {
        Printer printer = null;

        try {
            printer = PrinterHelper.getInstance(context).getPrinter();
            if(printer == null){
                return;
            }

            Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
            int widthDots = 384;
            int heightDots = (int) (logo.getHeight() * (widthDots / (double) logo.getWidth()));
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, widthDots, heightDots, false);



            printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addImage(
                    scaledLogo, 0, 0, widthDots, heightDots,
                    Printer.COLOR_1, Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO
            );
            printer.addFeedLine(1);
            printer.clearCommandBuffer();


            printer.addTextAlign(Printer.ALIGN_CENTER);
            printer.addTextStyle(Printer.FALSE, Printer.TRUE, Printer.FALSE, Printer.COLOR_1);
            printer.addText("THÔNG TIN PHIẾU ĂN\n\n");
            printer.addTextStyle(Printer.FALSE, Printer.FALSE, Printer.FALSE, Printer.COLOR_1);


            printer.addTextAlign(Printer.ALIGN_LEFT);
            SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String date = dfDate.format(new Date());
            String time = dfTime.format(new Date());

            printer.addText("Họ và tên              : " + result.getFullName() + "\n");
            printer.addText("Mã nhân viên           : " + result.getPersonCode() + "\n");
            printer.addText("Chức danh              : " + result.getPosition() + "\n");
            printer.addText("Phòng ban              : " + result.getJobDuties() + "\n");
            printer.addText("Ngày sử dụng           : " + date + "\n");
            printer.addText("Thời gian xuất phiếu   : " + date + " " + time + "\n");

            printer.addFeedLine(2);


            printer.addCut(Printer.CUT_FEED);
            printer.sendData(Printer.PARAM_DEFAULT);

        } catch (Epos2Exception e) {
            Log.e("TicketFormatter", "Printer error: ", e);
            try { printer.clearCommandBuffer(); } catch (Exception ignored) {}
        }

    }
}