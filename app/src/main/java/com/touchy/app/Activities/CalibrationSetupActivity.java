package com.touchy.app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.touchy.app.R;
import com.touchy.app.Utils.Common;
import com.touchy.app.Utils.ScreenHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class CalibrationSetupActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String selectedHandedness;

    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 5;
    private static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_setup);

        ScreenHelper.setActivityInitialLayout(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        verifyPermissions();

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.handednessRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MaterialRadioButton radioButton = findViewById(checkedId);

                selectedHandedness = (String) radioButton.getText();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void startCalibration(View view) {
        final Intent intent = new Intent(this, CalibrationActivity.class);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        TextInputLayout subjectNameLayout = findViewById(R.id.subjectNameLayout);
        String subjectName = String.valueOf(((TextInputEditText) findViewById(R.id.subjectNameText)).getText());

        if (subjectName.equals("")) {
            subjectNameLayout.setError("Name is required!");
            return;
        }

        editor.putString("subjectName", subjectName);
        editor.putString("subjectHandedness", selectedHandedness);
        editor.putString("screenResolution", Common.getScreenResolution(getWindowManager().getDefaultDisplay()));
        editor.apply();

        startActivity(intent);
        finish();
    }

    public void goToSettings(View view) {
        final Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
        finish();
    }

    public void goToStageOne(View view) {
        final Intent intent = new Intent(this, StageOneActivity.class);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        TextInputLayout subjectNameLayout = findViewById(R.id.subjectNameLayout);
        String subjectName = String.valueOf(((TextInputEditText) findViewById(R.id.subjectNameText)).getText());

        if (subjectName.equals("")) {
            subjectNameLayout.setError("Name is required!");
            return;
        }

        editor.putString("subjectName", subjectName);
        editor.putString("subjectHandedness", selectedHandedness);
        editor.putString("screenResolution", Common.getScreenResolution(getWindowManager().getDefaultDisplay()));
        editor.apply();

        startActivity(intent);
        finish();
    }

    private void verifyPermissions() {
        int readExternalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED || readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, STORAGE_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        ScreenHelper.setActivityInitialLayout(this);
    }
}