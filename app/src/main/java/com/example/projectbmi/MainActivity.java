package com.example.projectbmi;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends DBActivity {
    protected EditText editWeight, editHeight, editNotes;
    protected Button btnCalculate, btnClear;
    protected ListView simpleList;
    protected void FillListView() throws Exception{
        final ArrayList<String> listResults=
                new ArrayList<>();
        SelectSQL(
                "SELECT * FROM BMIDATABASE",
                null,
                (ID, Width, Height, Notes, Result)->{
                    listResults.add(ID+"\t"+Width+"\t"+Height+"\t"+Notes+ "\t" + Result + "\n");
                }
        );
        simpleList.clearChoices();
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(
                getApplicationContext(),
                R.layout.activity_listview,
                R.id.textView4,
                listResults

        );
        simpleList.setAdapter(arrayAdapter);
    }


    @Override
    @CallSuper
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            FillListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        editNotes = findViewById(R.id.editNotes);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnClear = findViewById(R.id.btnClear);
        simpleList=findViewById(R.id.simpleList);

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView clickedText = view.findViewById((R.id.textView4));
                String selected = clickedText.getText().toString();
                String[] elements = selected.split("\t");
                String ID = elements[0];
                String Weight = elements[1];
                String Height = elements[2];
                String Notes = elements[3];
                String Result = elements[4].trim();
                Intent intent = new Intent(MainActivity.this , UpdateDelete.class);
                Bundle b = new Bundle();
                b.putString("ID", ID);
                b.putString("Weight", Weight);
                b.putString("Height", Height);
                b.putString("Notes", Notes);
                b.putString("Result", Result);
                intent.putExtras(b);
                startActivityForResult(intent, 200, b);

            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        try {
            initDB();
            FillListView();
        } catch (Exception e) {
            e.printStackTrace();
        }


        btnCalculate.setOnClickListener(this::onClick);
        btnClear.setOnClickListener(this::onClickClear);
    }

    private void onClickClear(View view) {
        try{
            editHeight.setText("");
            editWeight.setText("");
            editNotes.setText("");

        }catch(Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Insert Failed: " + e.getLocalizedMessage()
                    , Toast.LENGTH_SHORT).show();
        }

    }


    private void openPopUpWindow() {
        Intent popUpWindow = new Intent(MainActivity.this, PopUpWindow.class);
        startActivity(popUpWindow);
    }


    private void onClick(View view) {
        try {
            DecimalFormat df = new DecimalFormat("##.##");
            if (!matchString(editNotes.getText().toString(), "^[A-Z]+\\w{0,}\\s[A-Z]+\\w{0,}")) {
                Toast.makeText(getApplicationContext(), "Invalid Full Name", Toast.LENGTH_LONG).show();
                return;
            }
            ExecSQL(
                    "INSERT INTO BMIDATABASE (Weight, Height, Notes, Result) " +
                            "VALUES(?, ?, ?, ?) ",
                    new Object[]{
                            Double.parseDouble(String.valueOf(editWeight.getText())),
                            Double.parseDouble(String.valueOf(editHeight.getText())),
                            //editHeight.getText(),
                            editNotes.getText().toString(),
                            df.format(Double.parseDouble(editWeight.getText().toString()) / (Double.parseDouble(editHeight.getText().toString()) * Double.parseDouble(editHeight.getText().toString())))
                    },
                    () -> Toast.makeText(getApplicationContext(),
                            "Record Inserted", Toast.LENGTH_LONG).show()

            );


            Thread t = new Thread(new Runnable() {


                @Override
                public void run() {
                    final StringBuilder jsonObject = new StringBuilder();
                    jsonObject.append("{");
                    jsonObject.append("'username': '" + editNotes.getText().toString() + "', ");
                    jsonObject.append("'weight': '" + Double.parseDouble(String.valueOf(editWeight.getText())) + "', ");
                    jsonObject.append("'height': '" + Double.parseDouble(String.valueOf(editHeight.getText())) + "', ");
                    jsonObject.append("'result': '" + Double.parseDouble(editWeight.getText().toString()) / (Double.parseDouble(editHeight.getText().toString()) * Double.parseDouble(editHeight.getText().toString())) + "' ");
                    jsonObject.append("}");
                    final StringBuilder result = new StringBuilder();
                    try {
                        result.append(postData("SaveToFile",
                                editNotes.getText().toString(),
                                jsonObject.toString()
                        ));
                        JSONObject jo = (JSONObject) new JSONTokener(result.toString())
                                .nextValue();
                        final String message = jo.getString("message");
                        if (message == null) {
                            throw new Exception("SERVER FAULT: " + result.toString());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), message,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Exception: " + e.getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });

            Intent thirdscreen = new Intent(MainActivity.this, PopUpWindow.class);

            //Sending data to another Activity
            thirdscreen.putExtra("result", Double.parseDouble(editWeight.getText().toString()) / (Double.parseDouble(editHeight.getText().toString()) * Double.parseDouble(editHeight.getText().toString())));
            startActivity(thirdscreen);
            //openPopUpWindow();
            t.start();
            FillListView();


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Insert Failed: " + e.getLocalizedMessage()
                    , Toast.LENGTH_SHORT).show();
        }


    }
}