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
    private double ram = 0.0;
    private double cpu = 0.0;
    private double upload = 0.0;
    private double download = 0.0;
    private double rating = 0.0;
    private int pid = 0;

    private long startTime = 0;
    private Drawable imgItem;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getUpload() {
        return upload;
    }

    public void setUpload(double upload) {
        this.upload = upload;
    }

    public double getDownload() {
        return download;
    }

    public void setDownload(double download) {
        this.download = download;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
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