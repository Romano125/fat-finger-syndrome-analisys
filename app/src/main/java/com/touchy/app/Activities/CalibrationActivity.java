package com.touchy.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.touchy.app.R;
import com.touchy.app.Utils.ScreenHelper;

public class CalibrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        ScreenHelper.setActivityInitialLayout(this);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);
        String subjectName = sharedPreferences.getString("subjectName", "");

        System.out.println(subjectName);
    }

    @Override
    public void onResume(){
        super.onResume();

        ScreenHelper.setActivityInitialLayout(this);
    }
}