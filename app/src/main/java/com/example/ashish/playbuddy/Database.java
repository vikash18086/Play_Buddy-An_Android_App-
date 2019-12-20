package com.example.ashish.playbuddy;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private DatabaseReference myDatabase;
    String newsId;
    public static ArrayList<News> allNews;

    public Database() {

        myDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void write(Object obj,String databaseName)
    {
        newsId=myDatabase.push().getKey();

        if(databaseName=="news")
        {
            News news=(News) obj;
            news.setNewsId(newsId);
        }
        myDatabase.child(databaseName).child(newsId).setValue(obj);
    }

    public void updateNews(String newsId,String newsDescription,String newsTitle,String sportId)
    {
        Map<String,Object> taskMap = new HashMap<String, Object>();
        taskMap.put("newsDescription", newsDescription);
        taskMap.put("newsTitle",newsTitle);
        taskMap.put("newsDate",new Date());
        taskMap.put("sportId",sportId);
        if(newsId!=null) {
            myDatabase.child("news").child(newsId).updateChildren(taskMap);
        }
        else
        {
            Log.e("indus","Data not updated as id is null");
        }
    }

    public void remove(String newsId)
    {

        if(newsId!=null) {
            myDatabase.child("news").child(newsId).removeValue();
        }
        else
        {
            Log.e("indus","Data not updated as id is null");
        }
    }

}