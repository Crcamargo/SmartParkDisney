package com.themparksdetermined.smartparkdisney.Model;

/**
 * Created by Crist on 8/2/2017.
 */

public class ListItem {
    private String nameOfRide;
    private String waitTime;
    private int rideImg;
    private String status;
    private boolean active;
    private boolean fastPass;
    private String fastOpen;
    private String fastClose;
    private long avg;
    private String location;

    public String getLocation(){ return location; }

    public void setLocation(String location){ this.location = location; }

    public long getAvg(){ return avg; }

    public void setAvg(long avg){ this.avg = avg; }

    public boolean isFastPass() {
        return fastPass;
    }

    public void setFastPass(boolean fatsPass) {
        this.fastPass = fatsPass;
    }

    public String getFastOpen() {
        return fastOpen;
    }

    public void setFastOpen(String fastOpen) {
        this.fastOpen = fastOpen;
    }

    public String getFastClose() {
        return fastClose;
    }

    public void setFastClose(String fastClose) {
        this.fastClose = fastClose;
    }

    public boolean isActive(){ return active; }

    public void setActive(boolean active){ this.active = active; }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    private boolean open;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRideImg() {
        return rideImg;
    }

    public void setRideImg(int rideImg) {
        this.rideImg = rideImg;
    }

    public String getNameOfRide() {
        return nameOfRide;
    }

    public void setNameOfRide(String nameOfRide) {
        this.nameOfRide = nameOfRide;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) { this.waitTime = waitTime;}
}
