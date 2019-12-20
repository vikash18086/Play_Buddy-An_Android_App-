package com.example.ashish.playbuddy;

import java.util.Date;

public class News {


    private String newsId;
    private String newsTitle;
    private String newsDescription;
    private Date newsDate;
    public String sportId;

    public News()
    {

    }

    public News(String newsTitle, String newsDescription,Date newsDate, String sportId) {
        this.newsTitle = newsTitle;
        this.newsDescription = newsDescription;
        this.newsDate=newsDate;
        this.sportId=sportId;
    }


    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsDescription() {
        return newsDescription;
    }

    public void setNewsDescription(String newsDescription) {
        this.newsDescription = newsDescription;
    }

    public Date getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(Date newsDate) {
        this.newsDate = newsDate;
    }

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }
}
