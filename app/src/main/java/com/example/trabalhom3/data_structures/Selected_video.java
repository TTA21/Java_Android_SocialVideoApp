package com.example.trabalhom3.data_structures;

import android.net.Uri;

public class Selected_video {

    private String Date = "";
    private long Dislike_Num = 0;
    private long Like_Num = 0;
    private String Time = "";
    private String URL = "";
    private long VideoLenght = 0;
    private long ViewCount = 0;
    private String VideoName = "";
    private String Category = "";
    private String This_username = "";
    private android.net.Uri Uri = null;


    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }


    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getVideoName() {
        return VideoName;
    }

    public void setVideoName(String videoName) {
        VideoName = videoName;
    }

    public long getDislike_Num() {
        return Dislike_Num;
    }

    public void setDislike_Num(long dislike_Num) {
        Dislike_Num = dislike_Num;
    }

    public long getLike_Num() {
        return Like_Num;
    }

    public void setLike_Num(long like_Num) {
        Like_Num = like_Num;
    }


    public long getViewCount() {
        return ViewCount;
    }

    public void setViewCount(long viewCount) {
        ViewCount = viewCount;
    }

    public long getVideoLenght() {
        return VideoLenght;
    }

    public void setVideoLenght(long videoLenght) {
        VideoLenght = videoLenght;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getThis_username() {
        return This_username;
    }

    public void setThis_username(String this_username) {
        This_username = this_username;
    }

    public android.net.Uri getUri() {
        return Uri;
    }

    public void setUri(android.net.Uri uri) {
        Uri = uri;
    }
}
