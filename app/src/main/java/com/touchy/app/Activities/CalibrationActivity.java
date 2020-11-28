package com.touchy.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.touchy.app.R;
import com.touchy.app.Utils.ScreenHelper;

public class CalibrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        ScreenHelper.setActivityInitialLayout(this);
    }

    @Override
    public void onResume(){
        super.onResume();

        ScreenHelper.setActivityInitialLayout(this);
    }
}