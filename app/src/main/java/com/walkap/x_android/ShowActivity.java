package com.walkap.x_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.FileNotFoundException;

public class ShowActivity extends AppCompatActivity {
    private String fileName = "hours";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        dbHelper db = new dbHelper(this);

    }

}
