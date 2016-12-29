package com.example.doctorsbuilding.nav;

/**
 * Created by hossein on 12/27/2016.
 */
public class PaymentInfo {
    private String url;
    private String redirecturl;
    private int mid;
    private int resNum;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRedirecturl() {
        return redirecturl;
    }

    public void setRedirecturl(String redirecturl) {
        this.redirecturl = redirecturl;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getResNum() {
        return resNum;
    }

    public void setResNum(int resNum) {
        this.resNum = resNum;
    }
}
