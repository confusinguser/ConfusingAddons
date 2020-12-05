package com.confusinguser.confusingaddons.core;

public class RuntimeInfo {
    private String downloadURL = "";
    private String liveGCIP = "58.178.120.172";
    private boolean sendUpdateNotification = false;
    private String version = "UNKNOWN";
    private String directDownloadURL = "";

    public RuntimeInfo() {}

    public RuntimeInfo(String downloadURL, String liveGCIP, boolean sendUpdateNotification, String version, String directDownloadURL) {
        this.downloadURL = downloadURL;
        this.liveGCIP = liveGCIP;
        this.sendUpdateNotification = sendUpdateNotification;
        this.version = version;
        this.directDownloadURL = directDownloadURL;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public String getLiveGCIP() {
        return liveGCIP;
    }

    public boolean shouldSendUpdateNotification() {
        return sendUpdateNotification;
    }

    public void setSendUpdateNotification(boolean sendUpdateNotification) {
        this.sendUpdateNotification = sendUpdateNotification;
    }

    public String getVersion() {
        return version;
    }


    public String getDirectDownloadURL() {
        return directDownloadURL;
    }
}
