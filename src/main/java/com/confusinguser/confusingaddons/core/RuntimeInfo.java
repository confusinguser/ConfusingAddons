package com.confusinguser.confusingaddons.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RuntimeInfo {
    private String downloadURL = "";
    private InetAddress liveGCIP = null;
    private String latestVersion = "UNKNOWN";
    private String directDownloadURL = "";

    public RuntimeInfo() {}

    public RuntimeInfo(String downloadURL, String liveGCIP, String version, String directDownloadURL) {
        this.downloadURL = downloadURL;
        try {
//            this.liveGCIP = InetAddress.getByName(liveGCIP);
            this.liveGCIP = InetAddress.getByName("127.0.0.1"); // TODO
        } catch (UnknownHostException ignored) {
        }
        this.latestVersion = version;
        this.directDownloadURL = directDownloadURL;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public InetAddress getLiveGCIP() {
        return liveGCIP;
    }

    public String getLatestVersion() {
        return latestVersion;
    }


    public String getDirectDownloadURL() {
        return directDownloadURL;
    }
}
