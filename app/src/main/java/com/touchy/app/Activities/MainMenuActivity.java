package com.touchy.app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textview.MaterialTextView;
import com.touchy.app.R;
import com.touchy.app.Utils.Common;
import com.touchy.app.Utils.ScreenHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String selectedHandingTechnique = "";

    MaterialTextView selectedLanguage;

    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 5;
    private static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ScreenHelper.setActivityInitialLayout(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        selectedLanguage = findViewById(R.id.languageText);

        if (sharedPreferences.getString("applicationLanguage", getString(R.string.croatian_locale)).equals(getString(R.string.croatian_locale))) {
            selectedLanguage.setText(sharedPreferences.getString("applicationLanguage", getString(R.string.english_locale)));
        } else {
            selectedLanguage.setText(sharedPreferences.getString("applicationLanguage", getString(R.string.croatian_locale)));
        }


        verifyPermissions();

        RadioGroup radioGroup = findViewById(R.id.handednessRadioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            MaterialRadioButton radioButton = findViewById(checkedId);

            selectedHandingTechnique = (String) radioButton.getText();
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
            subjectNameLayout.setError(getString(R.string.name_error_message));
            return;
        }

        if (selectedHandingTechnique.equals("")) {
            showErrorMessage(getString(R.string.holding_technique_error_message));
            return;
        }

        editor.putString("subjectName", subjectName);
        editor.putString("subjectHandingTechnique", selectedHandingTechnique);
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
            subjectNameLayout.setError(getString(R.string.name_error_message));
            return;
        }

        if (selectedHandingTechnique.equals("")) {
            showErrorMessage(getString(R.string.holding_technique_error_message));
            return;
        }

        if (!Common.hasPassedCalibration()) {
            showErrorMessage(getString(R.string.calibration_not_passed_error_message));
            return;
        }

        editor.putString("subjectName", subjectName);
        editor.putString("subjectHandingTechnique", selectedHandingTechnique);
        editor.putString("screenResolution", Common.getScreenResolution(getWindowManager().getDefaultDisplay()));
        editor.apply();

        startActivity(intent);
        finish();
    }

    public void setLocale(View v) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

        Locale myLocale = new Locale(selectedLanguage.getText().toString());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        if (selectedLanguage.getText().equals("hr")) {
            editor.putString("applicationLanguage", getString(R.string.english_locale));
        } else {
            editor.putString("applicationLanguage", getString(R.string.croatian_locale));
        }
        editor.apply();

        Intent refresh = new Intent(this, MainMenuActivity.class);
        finish();
        startActivity(refresh);
    }

    private void showErrorMessage(String errorMessage) {
        MaterialTextView errorMessageTextView = findViewById(R.id.errorMessage);
        errorMessageTextView.setText(errorMessage);
        errorMessageTextView.setVisibility(View.VISIBLE);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(() -> errorMessageTextView.setVisibility(View.GONE));
                }
            }
        };
        timer.start();
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