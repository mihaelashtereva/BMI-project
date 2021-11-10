package com.example.projectbmi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;

public class UpdateDelete extends DBActivity {
    protected EditText editWeight, editHeight, editNotes;
    protected Button btnDelete, btnUpdate;
    protected  String ID;
    private void BackToMain(){
        finishActivity(200);
        Intent i = new Intent(UpdateDelete.this, MainActivity.class);
        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        editNotes = findViewById(R.id.editNotes);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        Bundle b = getIntent().getExtras();
        if(b!=null){
           ID = b.getString("ID");
           editWeight.setText(b.getString("Weight"));
           editHeight.setText(b.getString("Height"));
           editNotes.setText(b.getString("Notes"));
        }
        btnDelete.setOnClickListener(view ->{
            try {
               ExecSQL("DELETE FROM BMIDATABASE WHERE " + "ID = ?",
                       new Object[]{ID},()->Toast.makeText(getApplicationContext(),"Delete Succesful", Toast.LENGTH_LONG).show());
            }catch (Exception exception){
                Toast.makeText(getApplicationContext(), "Delete Error: " + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            finally {
                BackToMain();
            }
        });

        btnUpdate.setOnClickListener(view ->{

            try {
                DecimalFormat df = new DecimalFormat("##.##");
                if(!matchString(editNotes.getText().toString(), "^[A-Z]+\\w{0,}\\s[A-Z]+\\w{0,}" )){
                    Toast.makeText(getApplicationContext(), "Invalid Full Name", Toast.LENGTH_LONG).show();
                    return;
                }
                ExecSQL("UPDATE BMIDATABASE SET " +
                                "Weight = ?, " +
                                "Height = ?, " +
                                "Notes = ?, " +
                                "Result = ? " +
                                "WHERE ID = ?",
                        new Object[]{
                                Double.parseDouble(String.valueOf(editWeight.getText())),
                                Double.parseDouble(String.valueOf(editHeight.getText())),
                                editNotes.getText().toString(),
                                df.format(Double.parseDouble(editWeight.getText().toString()) / (Double.parseDouble(editHeight.getText().toString()) * Double.parseDouble(editHeight.getText().toString()))),
                                ID},
                        ()->Toast.makeText(getApplicationContext(),"Update Succesful", Toast.LENGTH_LONG).show());
            }catch (Exception exception){
                Toast.makeText(getApplicationContext(), "Update Error: " + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            finally {
                BackToMain();
            }
        });

    }
}