package com.themparksdetermined.smartparkdisney.Model;

/**
 * Created by Crist on 8/7/2017.
 */

public class PlanItem {

    String title;
    String description;
    String time;
    int img;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}