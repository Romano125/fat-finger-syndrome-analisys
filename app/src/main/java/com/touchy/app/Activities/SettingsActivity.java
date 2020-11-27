package com.touchy.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.touchy.app.R;
import com.touchy.app.Utils.ScreenHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ScreenHelper.setActivityInitialLayout(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        int targetRadius = sharedPreferences.getInt("targetRadius", 50);
        int sessionLengthInTouches = sharedPreferences.getInt("sessionLengthInTouches", 10);

        ((TextInputEditText) findViewById(R.id.sessionLengthText)).setText(String.valueOf(sessionLengthInTouches));
        ((TextInputEditText) findViewById(R.id.targetRadiusText)).setText(String.valueOf(targetRadius));
    }

    public void saveSettings(View view) {
        final Intent intent = new Intent(this, CalibrationSetupActivity.class);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int sessionLengthInTouches = Integer.parseInt(String.valueOf(((TextInputEditText) findViewById(R.id.sessionLengthText)).getText())) ;
        int targetRadius = Integer.parseInt(String.valueOf(((TextInputEditText) findViewById(R.id.targetRadiusText)).getText()));

        editor.putInt("targetRadius", targetRadius);
        editor.putInt("sessionLengthInTouches", sessionLengthInTouches);
        editor.apply();

        startActivity(intent);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onResume(){
        super.onResume();

        ScreenHelper.setActivityInitialLayout(this);
    }
}