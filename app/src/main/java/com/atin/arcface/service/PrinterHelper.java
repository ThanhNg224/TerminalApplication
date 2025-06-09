package com.atin.arcface.service;

import android.content.Context;
import android.util.Log;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;

public class PrinterHelper {
    private static final String TAG = "PrinterHelper";

    private Printer mPrinter;
    private final Context mContext;
    private static PrinterHelper mInstance;

    /**
     * Singleton construct design pattern.
     *
     * @param context parent context
     * @return single instance of LogResponseServer
     */
    public static synchronized PrinterHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PrinterHelper(context);
        }
        return mInstance;
    }

    public PrinterHelper(Context context) {
        this.mContext = context;
    }

    private void initPrinter() {
        try {
            if (mPrinter == null) {
                mPrinter = new Printer(Printer.TM_T82, Printer.MODEL_ANK, mContext);
                mPrinter.connect("USB:", Printer.PARAM_DEFAULT);
                Log.d(TAG, "Printer connected");
            }
        } catch (Epos2Exception e) {
            Log.e(TAG, "Printer connection failed", e);
            mPrinter = null;
        }
    }

    public Printer getPrinter() {
        if(mPrinter != null){
            if(mPrinter.getStatus().getConnection() != Printer.TRUE || mPrinter.getStatus().getOnline() != Printer.TRUE){
                disconnect();
            }
        }

        initPrinter();
        return mPrinter;
    }

    public boolean isConnected() {
        getPrinter();

        if (mPrinter == null) return false;
        try {
            PrinterStatusInfo status = mPrinter.getStatus();
            return status.getConnection() == Printer.TRUE && status.getOnline() == Printer.TRUE;
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized void disconnect() {
        try {
            if (mPrinter != null) {
                Log.d(TAG, "Safe disconnect triggered");
                mPrinter.clearCommandBuffer();
                mPrinter.disconnect();
                mPrinter = null;
            }
        } catch (Epos2Exception e) {
            Log.e(TAG, "Printer disconnection failed", e);
        }
    }
}