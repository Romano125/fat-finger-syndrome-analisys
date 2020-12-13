package com.touchy.app.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.touchy.app.Models.TestSubject;
import com.touchy.app.Models.Touch;
import com.touchy.app.R;
import com.touchy.app.Utils.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class StageTwoView extends FrameLayout implements View.OnTouchListener {
    private TextInputEditText codePhrase;
    private MaterialTextView codePattern;

    private TestSubject testSubject;
    private Target target = new Target();
    private StatisticsData statisticsData = new StatisticsData();

    private long startTime = System.currentTimeMillis();
    private boolean isHelpEnabled, hasError = false;

    private static HashMap<String, Integer> touchedAreas = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAverageError = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAverageSize = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAveragePressure = new HashMap<>();
    private List<StatisticsData> lastCalibrationStatisticsData;

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
        int sessionLengthInTouches = sharedPreferences.getInt("sessionLengthInTouches", 10);
        int radius = sharedPreferences.getInt("targetRadius", 50);
        String screenResolution = sharedPreferences.getString("screenResolution", "-");
        String subjectName = sharedPreferences.getString("subjectName", "-");
        String subjectHandingTechnique = sharedPreferences.getString("subjectHandingTechnique", "-");

        lastCalibrationStatisticsData = getLastCalibrationData();
        testSubject = new TestSubject(subjectName, subjectHandingTechnique, screenResolution, Common.getFormattedDate(), sessionLengthInTouches);
        testSubject.setHelpEnabled(isHelpEnabled);
        target.setRadius(radius);

        inflate(getContext(), R.layout.view_stage_two, this);
        initViews();
    }

    private void initViews() {
        codePhrase = $(R.id.password_field);
        codePattern = $(R.id.pattern);

        for (int key : Constants.KEYBOARD_KEYS) {
            $(key).setOnTouchListener(this);
        }

        $(R.id.t9_key_backspace).setOnTouchListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN || hasError) {
            return true;
        }

        if (!codePattern.getText().subSequence(0, getInputText().length()).equals(getInputText())) {
            codePhrase.setTextColor(Color.parseColor("#DA0323"));
            hasError = true;

            return true;
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
            codePhrase.append(((TextView) nearestTarget).getText());
            touchError = 0;
        }

        touchedAreas.put(touchedArea, touchedAreas.containsKey(touchedArea) ? touchedAreas.get(touchedArea) + 1 : 1);
        touchedAreaAverageError.put(touchedArea, touchedAreaAverageError.containsKey(touchedArea) ?
                (touchedAreaAverageError.get(touchedArea) + touchError ) / touchedAreas.get(touchedArea)
                : touchError);
        touchedAreaAverageSize.put(touchedArea, touchedAreaAverageSize.containsKey(touchedArea) ?
                (touchedAreaAverageSize.get(touchedArea) + touchSize ) / touchedAreas.get(touchedArea)
                : touchSize);
        touchedAreaAveragePressure.put(touchedArea, touchedAreaAveragePressure.containsKey(touchedArea) ?
                (touchedAreaAveragePressure.get(touchedArea) + touchPressure ) / touchedAreas.get(touchedArea)
                : touchPressure);

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
        if (v.getTag() != null && "number_button".equals(v.getTag()) && !hasError) {
            codePhrase.append(((TextView) v).getText());

            if (!codePattern.getText().subSequence(0, getInputText().length()).equals(getInputText())) {
                codePhrase.setTextColor(Color.parseColor("#DA0323"));
                hasError = true;
            }

            checkIfPhraseIsCorrect();

            return true;
        }

        if (v.getId() == R.id.t9_key_backspace) {
            Editable editable = codePhrase.getText();
            assert editable != null;
            int charCount = editable.length();
            if (charCount > 0) {
                editable.delete(charCount - 1, charCount);
            }

            if (codePattern.getText().subSequence(0, getInputText().length()).equals(getInputText())) {
                codePhrase.setTextColor(Color.parseColor("#424041"));
                hasError = false;
            }
        }

        return true;
    }

    private void checkIfPhraseIsCorrect() {
        if (codePattern.getText().equals(getInputText())) {
            System.out.println("Saving statistics...");

            statisticsData.setTouchedAreas(touchedAreas);
            statisticsData.setTouchedAreaAverageError(touchedAreaAverageError);
            statisticsData.setTouchedAreaAverageSize(touchedAreaAverageSize);
            statisticsData.setTouchedAreaAverageSize(touchedAreaAveragePressure);

            testSubject.setTimeSpent((System.currentTimeMillis() - startTime) / 1000);

            Common.saveStatistics(Constants.STAGE_TWO_LOG_FILENAME, testSubject, target, statisticsData);

            touchedAreas = new HashMap<>();
            touchedAreaAverageError = new HashMap<>();
            touchedAreaAverageSize = new HashMap<>();
            touchedAreaAveragePressure = new HashMap<>();

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
        return Objects.requireNonNull(codePhrase.getText()).toString();
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }
}
