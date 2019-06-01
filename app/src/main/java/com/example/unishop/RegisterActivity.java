package com.example.unishop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.database.sqlite.SQLiteDatabase;

public class RegisterActivity extends AppCompatActivity {
    SQLiteDatabase dbapp = null;
    EditText edName,edPhone,edEmail,edPass;
    Button btnReg;
    TextView tvlogin;
    ImageView imgprofile;
    User user;
    String username,phone,email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        createdb();
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUserInput();
            }
        });
        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        imgprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTakePicture();
            }
        });
    }

    public void dialogTakePicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(this.getResources().getString(R.string.dialogtakepicture));

        alertDialogBuilder
                .setMessage(this.getResources().getString(R.string.dialogtakepicturea))
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.yesbutton),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, 1);
                        }
                    }
                })
                .setNegativeButton(this.getResources().getString(R.string.nobutton),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap,400,500);
            imgprofile.setImageBitmap(imageBitmap);
            imgprofile.buildDrawingCache();
        }
    }

    public void registerUserInput() {
        username =edName.getText().toString();
        phone =edPhone.getText().toString();
        email =edEmail.getText().toString();
        password =edPass.getText().toString();
        user = new User(username,phone,email,password);
        registerUserDialog();
        if(TextUtils.isEmpty(username)){
            edName.setError("Please input name");
            return;
        }
        if(TextUtils.isEmpty(phone)){
            edPhone.setError("Please input phone");
            return;
        }
        if(TextUtils.isEmpty(email)){
            edEmail.setError("Please input email");
            return;
        }
        if(TextUtils.isEmpty(password)){
            edPass.setError("Please input password");
            return;
        }

        if(imgprofile.getDrawable()== null){
            Toast.makeText(this, "NO PICTURE TAKEN. TRY TO TAKE PICTURE AGAIN.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void checkUser(String username,String phone,String email,String password) {
        if(username.isEmpty()||phone.isEmpty()||email.isEmpty()||password.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Please fill the information", Toast.LENGTH_SHORT).show();
        }
        else{
        try {
            searchUser(username);
            String sqlregister = "INSERT INTO USER(USERNAME,PHONE,EMAIL,PASSWORD)VALUES('"+username+"','"+phone+"','"+email+"','"+password+"');";
            dbapp.execSQL(sqlregister);
            Toast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();
            saveImg(username);
            loadimage(username);
        }catch (Exception e){
            Log.e("DB",e.toString());
        }
        }
    }

    public void searchUser(String username) {
        try{
            String sqlregister = "SELECT USER.USERNAME FROM USER WHERE USERNAME = '"+username+"';";
            dbapp.execSQL(sqlregister);
        }catch (Exception e){
            Toast.makeText(this, "DUPLICATE USERNAME", Toast.LENGTH_SHORT).show();
            edName.setError("DUPLICATE!");
        }
    }

    public void saveImg(String username){
        BitmapDrawable drawable = (BitmapDrawable) imgprofile.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File pictureFileDir = cw.getDir("unishop", Context.MODE_PRIVATE);
        if (!pictureFileDir.exists()) {
            pictureFileDir.mkdir();
        }
        Log.e("FILE NAME", "" + pictureFileDir.toString());
        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            return;
        }
        File mypath = new File(pictureFileDir, "/" +username+ ".jpg");
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            //hasimage = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadimage(String username){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File pictureFileDir = cw.getDir("unishop", Context.MODE_PRIVATE);
        File mypath = new File(pictureFileDir,"/"+username+".jpg");
        if(mypath.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(mypath.getAbsolutePath());
            imgprofile.setImageBitmap(myBitmap);
        }
        return mypath.toString();
    }

    public void registerUserDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(this.getResources().getString(R.string.registerfor)+" "+user.name+"?");
        alertDialogBuilder
                .setMessage(this.getResources().getString(R.string.registerdialognew))
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.yesbutton),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        checkUser(username,phone,email,password);
                    }})
                .setNegativeButton(this.getResources().getString(R.string.nobutton),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void initView() {
        edName = findViewById(R.id.txtname);
        edPhone =findViewById(R.id.txtphone);
        edEmail = findViewById(R.id.txtEmail);
        edPass = findViewById(R.id.txtpassword);
        btnReg = findViewById(R.id.btn_register);
        tvlogin = findViewById(R.id.tvregister);
        imgprofile = findViewById(R.id.imageView);
    }

    public void createdb(){
        dbapp = this.openOrCreateDatabase("dbuser",MODE_PRIVATE,null);
        String sqlcreate = "CREATE TABLE IF NOT EXISTS USER"+"(USERNAME VARCHAR NOT NULL,PHONE VARCHAR,EMAIL VARCHAR,PASSWORD VARCHAR,PRIMARY KEY(USERNAME));";
        dbapp.execSQL(sqlcreate);
    }


}