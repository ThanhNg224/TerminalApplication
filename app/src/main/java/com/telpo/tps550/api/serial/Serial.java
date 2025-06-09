package com.telpo.tps550.api.serial;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Serial
{
	public static final int MODE_PRINTER = 0;
	public static final int MODE_ICC = 1;
	public static final int MODE_PINPAD = 2;
	
    private native static FileDescriptor open(String path, int baudrate, int flags);
    public native void close();
    public native void clearbuffer(int clearType);
    private native static int switch_mode(int mode);
    
    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    
	public Serial(String path, int baud, int flags) throws Exception {
		
		File device = new File(path);
		if (!device.exists()) {
			throw new FileNotFoundException();
		}
		
        mFd = open(path, baud, flags);
        if (mFd == null) {
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
	}
	
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }
    
    public int switchMode(int mode) {
    	return switch_mode(mode);
    }
    
    static {
    	System.loadLibrary("telpo_serial");
    }
}
