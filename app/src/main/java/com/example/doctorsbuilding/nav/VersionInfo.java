package com.example.doctorsbuilding.nav;

/**
 * Created by hossein on 1/3/2017.
 */
public class VersionInfo {
    double versionName;
    String details;
    boolean force;
    String url;

    public double getVersionName() {
        return versionName;
    }

    public void setVersionName(double versionName) {
        this.versionName = versionName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
