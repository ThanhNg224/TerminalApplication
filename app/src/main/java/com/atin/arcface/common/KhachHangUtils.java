package com.atin.arcface.common;

public class KhachHangUtils {
    public static String layDuongDanUpdate(Enum khachHang){
        if(khachHang == DanhSachKhachHang.IAC_CLOUD){
            return "firmware-signed";
        }if(khachHang == DanhSachKhachHang.IAC_CLOUD_NEW || khachHang == DanhSachKhachHang.IERP){
            return "firmware-cloud";
        }else if(khachHang == DanhSachKhachHang.HIPT || khachHang == DanhSachKhachHang.VIWASEEN){
            return "smartface-cloud";
        }else if (khachHang == DanhSachKhachHang.DOIRONG){
            return "firmware-doirong";
        }else{
            return "firmware-signed";
        }
    }
}
