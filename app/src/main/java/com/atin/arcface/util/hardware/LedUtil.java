package com.atin.arcface.util.hardware;

import android.util.Log;
import com.common.pos.api.util.PosUtil;
import com.telpo.tps550.api.serial.Serial;

import java.io.InputStream;
import java.io.OutputStream;

public class LedUtil {

	private Serial serial;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private boolean hasInited = false;
	public final static int RED = 1;
	public final static int YELLOW = 2;
	public final static int GREEN = 3;
	public final static int BLUE = 4;
	public final static int OPEN = 5;
	public final static int CLOSE = 6;
	private static LedUtil ledUtil = null;

	public static LedUtil getInstance() {
		if (ledUtil == null) {
			synchronized (LedUtil.class) {
				if (ledUtil == null) {
					ledUtil = new LedUtil();
				}
			}
		}
		return ledUtil;
	}

	public LedUtil(){
		init();
	}
	
	public void init(){
		if(!hasInited){
			if(initSerial()){
				hasInited = true;
			}
		}
	}
	
	public void release(){
		if(hasInited){
			if(destroySerial()){
				hasInited = false;
			}
		}
	}
	
	public String setLedLight(int ledColor, int status) {
		String msg=null;
		if(hasInited){
			if(ledColor == RED){
				if(status == OPEN){
					msg=sendData(toBytes("550055"));
				}else if(status == CLOSE){
					msg=sendData(toBytes("550156"));
				}
			}else if(ledColor == YELLOW){
				if(status == OPEN){
					msg=sendData(toBytes("550257"));
				}else if(status == CLOSE){
					msg=sendData(toBytes("550358"));
				}
			}else if(ledColor == GREEN){
				if(status == OPEN){
					msg=sendData(toBytes("550459"));
				}else if(status == CLOSE){
					msg=sendData(toBytes("55055A"));
				}
			}else if(ledColor == BLUE){
				if(status == OPEN){
					msg=sendData(toBytes("55065B"));
				}else if(status == CLOSE){
					msg=sendData(toBytes("55075C"));
				}
			}
			sleep(10);
		}else{
			//throw new Exception("serial not open");
		}
		return msg;
	}
	
	private boolean initSerial(){//初始化串口
		boolean openSerialResult = false;
		try {
			if(serial == null)
				serial = new Serial("/dev/ttyS4", 9600, 0);//串口号, 波特率
			if(mOutputStream == null)
				mOutputStream = serial.getOutputStream();
			if(mInputStream == null)
				mInputStream=serial.getInputStream();
			
			openSerialResult = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("initserial---",e.toString());
		}
		return openSerialResult;
	}
	
	private boolean destroySerial(){//释放资源
		boolean closeSerialResult = false;
		try {
			if(mOutputStream != null) {
				mOutputStream.close();
				mOutputStream = null;
			}
			if(mInputStream != null) {
				mInputStream.close();
				mInputStream = null;
			}
			if (serial != null) {
	            serial.close();
	            serial = null;
	        }
			closeSerialResult = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return closeSerialResult;
	}
	
	private String sendData(byte[] data){//发送命令 data
		String strmsg=null;
		try {
			//切换发送模式
			//PosUtil.setRs485Status(1);
			PosUtil.setRs485Status(0);//F8
			if(mOutputStream != null){
				mOutputStream.write(data);
			}
			if(mInputStream!=null){
				Thread.sleep(200);
				//切换接收模式
				//PosUtil.setRs485Status(0);
				PosUtil.setRs485Status(1);//f8
				byte[] buffer = new byte[64];
				int size = mInputStream.available();
				if (size > 0) {
					size = mInputStream.read(buffer);
					if (size > 0) {
						strmsg= byteToHexString(buffer, size);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("sendData---",e.toString());
		}
		return strmsg;
	}
	
	private String byteToHexString(byte[] b, int length) {
		String a = "";
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}

			a = a + hex;
		}
		return a;
	}
	
	private byte[] toBytes(String string) {
		int len;
		String str;
		String hexStr = "0123456789ABCDEF";
		
		String s = string.toUpperCase();
		
		len = s.length();
		if ((len % 2) == 1) {
			str = s + "0";
			len = (len + 1) >> 1;
		} else {
			str = s;
			len >>= 1;
		}
		
		byte[] bytes = new byte[len];
		byte high;
		byte low;
	
		for (int i = 0, j = 0; i < len; i++, j += 2) {
			high = (byte)(hexStr.indexOf(str.charAt(j)) << 4);
			low = (byte)hexStr.indexOf(str.charAt(j + 1));
			bytes[i] = (byte)(high | low);
		}
		
		return bytes;
	}
	
	private void sleep(int delay){
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
