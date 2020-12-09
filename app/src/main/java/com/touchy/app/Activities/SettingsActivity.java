package com.touchy.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.touchy.app.R;
import com.touchy.app.Utils.ScreenHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private TextInputEditText targetRadiusText, sessionLengthText;
    private String selectedHelpOption = "No";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ScreenHelper.setActivityInitialLayout(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        boolean isHelpEnabled = sharedPreferences.getBoolean("helpEnabled", false);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.helpRadioGroup);

        radioGroup.check(isHelpEnabled ? R.id.helpPositive : R.id.helpNegative);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            MaterialRadioButton radioButton = findViewById(checkedId);

            selectedHelpOption = (String) radioButton.getText();
        });

        int targetRadius = sharedPreferences.getInt("targetRadius", 50);
        int sessionLengthInTouches = sharedPreferences.getInt("sessionLengthInTouches", 10);

        targetRadiusText = findViewById(R.id.targetRadiusText);
        sessionLengthText = findViewById(R.id.sessionLengthText);

        targetRadiusText.setText(String.valueOf(targetRadius));
        sessionLengthText.setText(String.valueOf(sessionLengthInTouches));
    }

    public void saveSettings(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        final Intent intent = new Intent(this, MainMenuActivity.class);
        int targetRadius = Integer.parseInt(String.valueOf(targetRadiusText.getText()));
        int sessionLengthInTouches = Integer.parseInt(String.valueOf(sessionLengthText.getText()));

        editor.putInt("targetRadius", targetRadius);
        editor.putInt("sessionLengthInTouches", sessionLengthInTouches);
        editor.putBoolean("helpEnabled", selectedHelpOption.equals("Yes"));
        editor.apply();

        startActivity(intent);
        finish();
    }

    public void decreaseTargetRadius(View view) {
        int targetRadius = Integer.parseInt(String.valueOf(targetRadiusText.getText()));

        int minimumRadius = 5;
        if (targetRadius <= minimumRadius) {
            return;
        }

        targetRadiusText.setText(String.valueOf(targetRadius - 1));
    }

    public void increaseTargetRadius(View view) {
        int targetRadius = Integer.parseInt(String.valueOf(targetRadiusText.getText()));

        int maximumRadius = 100;
        if (targetRadius >= maximumRadius) {
            return;
        }

        targetRadiusText.setText(String.valueOf(targetRadius + 1));
    }

    public void decreaseSessionLength(View view) {
        int sessionLength = Integer.parseInt(String.valueOf(sessionLengthText.getText()));

        int minimumSessionLength = 15;
        if (sessionLength <= minimumSessionLength) {
            return;
        }

        sessionLengthText.setText(String.valueOf(sessionLength - 1));
    }

    public void increaseSessionLength(View view) {
        int sessionLength = Integer.parseInt(String.valueOf(sessionLengthText.getText()));

        int maximumSessionLength = 999;
        if (sessionLength >= maximumSessionLength) {
            return;
        }

        sessionLengthText.setText(String.valueOf(sessionLength + 1));
    }

    public void navigateBack(View view) {
        Intent intent = new Intent(SettingsActivity.this, MainMenuActivity.class);
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