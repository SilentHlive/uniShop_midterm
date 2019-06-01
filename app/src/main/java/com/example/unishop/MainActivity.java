package com.example.unishop;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase dbapp;
    ListView lvuser;
    //ImageView  imgp;
    ArrayList<HashMap<String, String>> userlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvuser = findViewById(R.id.listviewUser);
        userlist = new ArrayList<>();
        createdb();
        viewUser();
    }
    public void viewUser(){
        String sqlview = "SELECT * FROM USER";
        Cursor c = dbapp.rawQuery(sqlview, null);
        userlist.clear();
        if(c.getCount()>0){
            c.moveToFirst();
            for(int i=0;i<c.getCount();i++){
                String vName = c.getString(c.getColumnIndex("USERNAME"));
                String vPhone = c.getString(c.getColumnIndex("PHONE"));
                String vEmail = c.getString(c.getColumnIndex("EMAIL"));
                HashMap<String,String> viewUser = new HashMap<>();
                viewUser.put("image",imageloc(vName));
                viewUser.put("username",vName);
                viewUser.put("phone",vPhone);
                viewUser.put("email",vEmail);
                userlist.add(viewUser);
                c.moveToNext();
            }
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this,userlist,
                    R.layout.display_user, new String[]
                    {"image","username","phone","email"}, new int[]{R.id.imageView2,R.id.name,R.id.phone,R.id.email});
            lvuser.setAdapter(adapter);
        }else {
            Toast.makeText(MainActivity.this, "No Data", Toast.LENGTH_SHORT).show();
        }

    }

    public String imageloc(String username){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File pictureFileDir = cw.getDir("unishop", Context.MODE_PRIVATE);
        File mypath = new File(pictureFileDir,"/"+username+".jpg");
        return mypath.toString();
    }

    public void createdb(){
        dbapp = this.openOrCreateDatabase("dbuser",MODE_PRIVATE,null);
        String sqlcreate = "CREATE TABLE IF NOT EXISTS USER(USERNAME VARCHAR PRIMARY KEY NOT NULL,PHONE VARCHAR,EMAIL VARCHAR,PASSWORD VARCHAR);";
        dbapp.execSQL(sqlcreate);
    }

}

