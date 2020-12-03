package com.touchy.app.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.touchy.app.Activities.StageTwoActivity;
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
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class StageOneView extends View {
    private TestSubject testSubject;
    private Target target = new Target();
    private StatisticsData statisticsData = new StatisticsData();

    private SharedPreferences sharedPreferences;
    private final Random random = new Random();
    private final MediaPlayer mediaPlayer;
    private final Paint paint = new Paint();
    private int sessionLengthInTouches, radius, touchCount = 0;
    private String screenResolution, subjectName, subjectHandedness;

    private static HashMap<String, Integer> touchedAreas = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAverageError = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAverageSize = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAveragePressure = new HashMap<>();
    private List<StatisticsData> lastCalibrationStatisticsData;

    public StageOneView(Context context) {
        super(context);

        init(null, 0);

        sharedPreferences = context.getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        this.radius = sharedPreferences.getInt("targetRadius", 50);
        this.sessionLengthInTouches = sharedPreferences.getInt("sessionLengthInTouches", 10);
        this.screenResolution = sharedPreferences.getString("screenResolution", "-");
        this.subjectName = sharedPreferences.getString("subjectName", "-");
        this.subjectHandedness = sharedPreferences.getString("subjectHandedness", "-");

        mediaPlayer = MediaPlayer.create(context, R.raw.success_hit);
        lastCalibrationStatisticsData = getLastCalibrationData();
        testSubject = new TestSubject(subjectName, subjectHandedness, screenResolution, Common.getFormattedDate(), sessionLengthInTouches);
        target.setRadius(radius);
    }

    public StageOneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);

        sharedPreferences = context.getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        this.radius = sharedPreferences.getInt("targetRadius", 50);
        this.sessionLengthInTouches = sharedPreferences.getInt("sessionLengthInTouches", 10);
        this.screenResolution = sharedPreferences.getString("screenResolution", "-");
        this.subjectName = sharedPreferences.getString("subjectName", "-");
        this.subjectHandedness = sharedPreferences.getString("subjectHandedness", "-");

        mediaPlayer = MediaPlayer.create(context, R.raw.success_hit);
        lastCalibrationStatisticsData = getLastCalibrationData();
        testSubject = new TestSubject(subjectName, subjectHandedness, screenResolution, Common.getFormattedDate(), sessionLengthInTouches);
        target.setRadius(radius);
    }

    public StageOneView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);

        sharedPreferences = context.getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        this.radius = sharedPreferences.getInt("targetRadius", 50);
        this.sessionLengthInTouches = sharedPreferences.getInt("sessionLengthInTouches", 10);
        this.screenResolution = sharedPreferences.getString("screenResolution", "-");
        this.subjectName = sharedPreferences.getString("subjectName", "-");
        this.subjectHandedness = sharedPreferences.getString("subjectHandedness", "-");

        mediaPlayer = MediaPlayer.create(context, R.raw.success_hit);
        lastCalibrationStatisticsData = getLastCalibrationData();
        testSubject = new TestSubject(subjectName, subjectHandedness, screenResolution, Common.getFormattedDate(), sessionLengthInTouches);
        target.setRadius(radius);
    }

    private void init(AttributeSet attrs, int defStyle) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor(Constants.APP_BACKGROUND_COLOR));

        target.setX((random.nextInt(Math.abs(getWidth()-radius/2)) + radius/2f));
        target.setY((random.nextInt((int)Math.abs((getHeight()- (radius >> 1) + radius/2f)))));

        canvas.drawCircle(target.getX(), target.getY(), radius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Touch touch = new Touch(event.getX(), event.getY());

        String touchedArea = Common.getTouchedArea(target, touch, mediaPlayer);
        double touchError = Common.getTouchError(target, touch);
        double touchSize = event.getSize();
        double touchPressure = event.getPressure();

        StatisticsData stats = lastCalibrationStatisticsData.stream()
                .filter(data -> touchedArea.equals(String.valueOf(data.getTouchedArea())))
                .findAny()
                .orElse(null);

        if (touchError > radius) {
            touchError -= radius;
        }

        System.out.println("Calibration average error: " + stats.getAverageError());
        System.out.println("Current touch error: " + touchError);

        if (touchError <= stats.getAverageError()) {
            touchError = 0;
//            move item to (x, y) coordinates to simulate user help
//            MotionEvent motionEvent = MotionEvent.obtain(10,10, MotionEvent.ACTION_DOWN, circle_x, circle_y, 0);
//            onTouchEvent(motionEvent);
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

        if (touchedArea.equals(String.valueOf(Constants.TOUCHED_AREA.CENTER)) || touchError <= stats.getAverageError()) {
            if (touchCount++ == sessionLengthInTouches) {
                System.out.println("Saving statistics...");

                statisticsData.setTouchedAreas(touchedAreas);
                statisticsData.setTouchedAreaAverageError(touchedAreaAverageError);
                statisticsData.setTouchedAreaAverageSize(touchedAreaAverageSize);
                statisticsData.setTouchedAreaAverageSize(touchedAreaAveragePressure);

                Common.saveStatistics(Constants.STAGE_ONE_LOG_FILENAME, testSubject, target, statisticsData);

                Intent intent = new Intent(getContext(), StageTwoActivity.class);
                getContext().startActivity(intent);
            }

            redrawTarget();
        }

        return super.onTouchEvent(event);
    }

    private void redrawTarget() {
        int index = random.nextInt(Constants.TARGET_COLORS.length);
        paint.setColor(Constants.TARGET_COLORS[index]);
        invalidate();
    }

    private List<StatisticsData> getLastCalibrationData() {
        Gson gson = new Gson();

        File sessionFilePath = new File(Common.createOutputDirectory("Touchy"), String.format("%s.txt", Constants.CALIBRATION_LOG_FILENAME));

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
}
