package com.touchy.app.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.touchy.app.Activities.MainMenuActivity;
import com.touchy.app.Constants;
import com.touchy.app.Models.StatisticsData;
import com.touchy.app.Models.Target;
import com.touchy.app.Models.Touch;
import com.touchy.app.R;
import com.touchy.app.Utils.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class StageTwoView extends FrameLayout implements View.OnTouchListener {
    private TextInputEditText mPasswordField;
    private MaterialTextView materialTextView;

    private long startTime = System.currentTimeMillis();
    private int sessionLengthInTouches, radius, touchCount = 1;
    private List<StatisticsData> lastCalibrationStatisticsData;
    private boolean isHelpEnabled;

    public StageTwoView(Context context) {
        super(context);
        init();
    }

    public StageTwoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StageTwoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);
        isHelpEnabled = sharedPreferences.getBoolean("helpEnabled", false);
//        radius = (int) px2dp(getResources(), sharedPreferences.getInt("targetRadius", 50));

        inflate(getContext(), R.layout.view_stage_two, this);
        lastCalibrationStatisticsData = getLastCalibrationData();
        initViews();
    }

    private void initViews() {
        mPasswordField = $(R.id.password_field);
        materialTextView = $(R.id.pattern);

        for (int key : Constants.KEYBOARD_KEYS) {
            $(key).setOnTouchListener(this);
        }

        $(R.id.t9_key_clear).setOnTouchListener(this);
        $(R.id.t9_key_backspace).setOnTouchListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return  true;
        }

        Touch touch = new Touch(event.getX(), event.getY());

        View nearestTarget = getNearestTarget(touch);

        int [] location = new int[2];
        nearestTarget.getLocationOnScreen(location);

        Target target = new Target(location[0], location[1], 0);

        String touchedArea = Common.getTouchedArea(target, touch, null);
        double touchError = Common.getTouchError(target, touch);
        double touchSize = event.getSize();
        double touchPressure = event.getPressure();

        StatisticsData stats = lastCalibrationStatisticsData.stream()
                .filter(data -> touchedArea.equals(String.valueOf(data.getTouchedArea())))
                .findAny()
                .orElse(null);

        if (touchedArea.equals(String.valueOf(Constants.TOUCHED_AREA.CENTER)) || (touchError < stats.getAverageError() && isHelpEnabled)) {
            System.out.println(((TextView) nearestTarget).getText());
            mPasswordField.append(((TextView) nearestTarget).getText());
            touchError = 0;
        }

//        System.out.println(touchError + " " + stats.getAverageError());
        return false;
    }

    private View getNearestTarget(Touch touch) {
        double minimumTouchError = 9999999;
        View nearestTarget = null;
        for (int key : Constants.KEYBOARD_KEYS) {
            View targetView = $(key);

            float density = getResources().getDisplayMetrics().density;
            int radius = (int)(targetView.getWidth() / density);

            int [] location = new int[2];
            targetView.getLocationOnScreen(location);

            Target target = new Target(location[0], location[1], radius);

            double touchError = Common.getTouchError(target, touch);

            if (touchError < minimumTouchError) {
                minimumTouchError = touchError;
                nearestTarget = targetView;
            }
        }

        return nearestTarget;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return  true;
        }

        // handle number button click
        if (v.getTag() != null && "number_button".equals(v.getTag())) {
            mPasswordField.append(((TextView) v).getText());

            checkIfPhraseIsCorrect();

            return true;
        }

        switch (v.getId()) {
            case R.id.t9_key_clear: { // handle clear button
                mPasswordField.setText(null);
            }
            break;
            case R.id.t9_key_backspace: { // handle backspace button
                // delete one character
                Editable editable = mPasswordField.getText();
                assert editable != null;
                int charCount = editable.length();
                if (charCount > 0) {
                    editable.delete(charCount - 1, charCount);
                }
            }
            break;
        }

        return true;
    }

    private void checkIfPhraseIsCorrect() {
        if (materialTextView.getText().equals(getInputText())) {
            System.out.println("Total time: " + ((System.currentTimeMillis() - startTime) / 1000));

            // save statistics

            Intent intent = new Intent(getContext(), MainMenuActivity.class);
            getContext().startActivity(intent);
        }
    }

    private List<StatisticsData> getLastCalibrationData() {
        Gson gson = new Gson();

        File sessionFilePath = new File(Common.createOutputDirectory("Touchy"), String.format("%s.json", Constants.CALIBRATION_LOG_FILENAME));

        List<JSONObject> statistics;
        List<StatisticsData> statisticsData = null;
        if (sessionFilePath.length() > 0) {
            try {
                statistics = gson.fromJson(new JsonReader(new FileReader(sessionFilePath)), new TypeToken<List<JSONObject>>() {}.getType());

                // reading data from file
                String json = gson.toJson(statistics.get(statistics.size()-1).get("statistics"));

                statisticsData = gson.fromJson(json, new TypeToken<List<StatisticsData>>() {}.getType());
            } catch (FileNotFoundException | JSONException e) {
                e.printStackTrace();
            }
        }

        return statisticsData;
    }

    public String getInputText() {
        return Objects.requireNonNull(mPasswordField.getText()).toString();
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }
}
