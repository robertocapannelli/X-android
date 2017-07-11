package com.walkap.x_android;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.system.Os.read;

public class Options extends AppCompatActivity {

    static private String FILENAME = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }

    public void saveData(View view){

        byte[] buffer = new byte[256];

        EditText university = (EditText) findViewById(R.id.universityEditText);
        EditText faculty = (EditText) findViewById(R.id.facultyEditText);

        String universityString = university.getText().toString();
        String facultyString = faculty.getText().toString();

        String fileString = universityString + "-" + facultyString + ";";

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);

            fos.write(fileString.getBytes());
            fos.close();
            Log.d("hey", "ho scritto il file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fis = openFileInput(FILENAME);
            fis.read(buffer);
            fis.close();
            fileString = new String(buffer);
            int endString = fileString.indexOf(';');
            Log.d("hey", fileString.substring(0, endString));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
