package com.example.fllodrab.measureappss;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by FllodraB.
 */
public class MyApp {
    private String name = "";
    private String packageName = "";
    private double ram = 0.0;
    private double cpu = 0.0;
    private long upload = 0;
    private long download = 0;
    private double rating = 0.0;
    private double numberOfDownloads = 0.0;
    private int pid = 0;
    private long startTime = 0;
    private Drawable imgItem;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public double getRam() {
        return ram;
    }

    public void setRam(double ram) {
        this.ram = ram;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public long getUpload() {
        return upload;
    }

    public void setUpload(long upload) {
        this.upload = upload;
    }

    public long getDownload() {
        return download;
    }

    public void setDownload(long download) {
        this.download = download;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getNumberOfDownloads() {
        return numberOfDownloads;
    }

    public void setNumberOfDownloads(double numberOfDownloads) {
        this.numberOfDownloads = numberOfDownloads;
    }

    public Drawable getImgItem() {
        return imgItem;
    }

    public void setImgItem(Drawable imgItem) {
        this.imgItem = imgItem;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}