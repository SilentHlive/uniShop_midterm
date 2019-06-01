package com.example.unishop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class LoginActivity extends AppCompatActivity {
    SQLiteDatabase dbapp =null;
    TextView tvreg;
    EditText edemail,edpassword;
    Button btnlogin;
    SharedPreferences sharedPreferences;
    CheckBox cbrem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edemail = findViewById(R.id.editTextEmail);
        edpassword = findViewById(R.id.editTextPassword);
        btnlogin = findViewById(R.id.buttonLogin);
        tvreg = findViewById(R.id.tvRegister);
        cbrem = findViewById(R.id.checkBox);
        createdb();
        tvreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        cbrem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbrem.isChecked()){
                    String email = edemail.getText().toString();
                    String pass = edpassword.getText().toString();
                    savePref(email,pass);
                }
            }
        });
        loadPref();
    }

    private void savePref(String e, String p) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", e);
        editor.putString("password", p);
        editor.commit();
        Toast.makeText(this, "Preferences has been saved", Toast.LENGTH_SHORT).show();
    }

    private void loadPref() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String premail = sharedPreferences.getString("email", "");
        String prpass = sharedPreferences.getString("password", "");
        if (premail.length()>0){
            cbrem.setChecked(true);
            edemail.setText(premail);
            edpassword.setText(prpass);
        }
    }

    public void loginUser(){
        String username = edemail.getText().toString();
        String password = edpassword.getText().toString();
        try{
            String sqlsearch = "SELECT * FROM USER WHERE EMAIL = '"+username+"' AND PASSWORD = '"+password+"'";
            Cursor c = dbapp.rawQuery(sqlsearch,null);
            if (c.getCount() > 0){
                Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Log.e("DB",e.toString());
        }
    }

    public void createdb(){
        dbapp = this.openOrCreateDatabase("dbuser",MODE_PRIVATE,null);
        String sqlcreate = "CREATE TABLE IF NOT EXISTS USER(USERNAME VARCHAR PRIMARY KEY NOT NULL,PHONE VARCHAR,EMAIL VARCHAR,PASSWORD VARCHAR);";
        dbapp.execSQL(sqlcreate);
    }
}
