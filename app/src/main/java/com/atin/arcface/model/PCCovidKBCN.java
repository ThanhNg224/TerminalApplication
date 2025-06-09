package com.atin.arcface.model;

public class PCCovidKBCN {
    private String identify;
    private String province;
    private String district;
    private String town;
    private String address;

    public PCCovidKBCN() {
    }

    public PCCovidKBCN(String identify, String province, String district, String town, String address) {
        this.identify = identify;
        this.province = province;
        this.district = district;
        this.town = town;
        this.address = address;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
