package com.example.projectbmi;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PopUpWindow extends MainActivity {

    protected TextView popUp;
    protected EditText editWeight, editHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_window);
        popUp = findViewById(R.id.popUp);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Intent i = getIntent();
        double value = i.getDoubleExtra("result", 1);
        DecimalFormat df = new DecimalFormat("##.##");
        String result = df.format(value);

        getWindow().setLayout((int) (width*.7), (int) (height*.5));
        //Double bmi = Double.parseDouble(editWeight.getText().toString()) / (Double.parseDouble(editHeight.getText().toString()) * Double.parseDouble(editHeight.getText().toString()));

        if(value>=25){
            popUp.setText("Your BMI is " + "\n" + result + "\n" + "You are overweight!");
        }
        else if(value<=18)
        {
            popUp.setText("Your BMI is " + "\n" + result + "\n" + "You are underweight!");
        }
        else {
            popUp.setText("Your BMI is " + "\n" + result + "\n" + "You are at a healthy weight!");
        }



        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }
}