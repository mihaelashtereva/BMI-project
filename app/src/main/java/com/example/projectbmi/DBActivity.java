package com.example.projectbmi;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DBActivity extends RESTactivity {

    protected  interface OnQuerySuccess{
        public  void OnSuccess();
    }

    protected  interface OnSelectSuccess{
        public  void OnElementSelected(
                String ID, Double Weight, Double Height, String Notes,Double Result
        );
    }

   // protected  String DBFILE = getFilesDir().getPath() + "/BMIDATABASE.db";

    protected boolean matchString(String string_, String regexp){
        final String regex = regexp;//"[A-Z]+\\w{0,}\\s[A-Z]+\\w{0,}";
        final String string = string_;

        final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.UNICODE_CASE);
        final Matcher matcher = pattern.matcher(string);

        while (matcher.find()){
            return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    protected  void SelectSQL(String SelectQ,
                              String[] args,
                              OnSelectSuccess success)
            throws  Exception
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().getPath() + "/BMIDATABASE.db", null);
        Cursor cursor = db.rawQuery(SelectQ, args);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String ID = cursor.getString(cursor.getColumnIndex("ID"));
            @SuppressLint("Range") Double Weight = cursor.getDouble(cursor.getColumnIndex("Weight"));
            @SuppressLint("Range") Double Height = cursor.getDouble(cursor.getColumnIndex("Height"));
            @SuppressLint("Range") String Notes = cursor.getString(cursor.getColumnIndex("Notes"));
            @SuppressLint("Range") Double Result = cursor.getDouble(cursor.getColumnIndex("Result"));
            success.OnElementSelected(ID,Weight,Height,Notes,Result);
        }
        db.close();
    }
    protected  void  ExecSQL(String SQL, Object[] args, OnQuerySuccess success)
            throws  Exception
    {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().getPath() + "/BMIDATABASE.db", null);
        if(args!=null)
            db.execSQL(SQL, args);
        else
            db.execSQL(SQL);
        db.close();
        success.OnSuccess();
    }

    protected  void  initDB() throws Exception{
        ExecSQL(
                "CREATE TABLE if not exists BMIDATABASE( " +
                        "ID integer PRIMARY KEY AUTOINCREMENT, " +
                        "Weight decimal not null, " +
                        "Height decimal not null, " +
                        "Notes text, " +
                        "Result decimal " +  ")",
                null,
                ()-> Toast.makeText(getApplicationContext(),"DB Init Successful. ",Toast.LENGTH_LONG).show()
        );
    }


}
