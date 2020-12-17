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

    private final Random random = new Random();
    private MediaPlayer mediaPlayer;
    private final Paint paint = new Paint();
    private int sessionLengthInTouches, radius, touchCount = 1;
    private long startTime = System.currentTimeMillis();
    private boolean isHelpEnabled;

    private static HashMap<String, Integer> touchedAreas = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAverageError = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAverageSize = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAveragePressure = new HashMap<>();
    private List<StatisticsData> lastCalibrationStatisticsData;

    public StageOneView(Context context) {
        super(context);

        init(null, 0, context);
    }

    public StageOneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, context);
    }

    public StageOneView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle, context);
    }

    private void init(AttributeSet attrs, int defStyle, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        this.radius = sharedPreferences.getInt("targetRadius", 50);
        this.sessionLengthInTouches = sharedPreferences.getInt("sessionLengthInTouches", 10);
        String screenResolution = sharedPreferences.getString("screenResolution", "-");
        String screenDensity = sharedPreferences.getString("screenDensity", "-");
        String subjectName = sharedPreferences.getString("subjectName", "-");
        String subjectHandingTechnique = sharedPreferences.getString("subjectHandingTechnique", "-");
        this.isHelpEnabled = sharedPreferences.getBoolean("helpEnabled", false);

        mediaPlayer = MediaPlayer.create(context, R.raw.success_hit);
        lastCalibrationStatisticsData = Common.getLastCalibrationData();
        testSubject = new TestSubject(subjectName, subjectHandingTechnique, screenResolution, screenDensity, Common.getFormattedDate(), sessionLengthInTouches);
        testSubject.setHelpEnabled(isHelpEnabled);
        target.setRadius(radius);
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

        if (touchError <= stats.getAverageError() && isHelpEnabled) {
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

        if (touchedArea.equals(String.valueOf(Constants.TOUCHED_AREA.CENTER)) || (touchError <= stats.getAverageError() && isHelpEnabled)) {
            if (touchCount++ == sessionLengthInTouches) {
                System.out.println("Saving statistics...");

                statisticsData.setTouchedAreas(touchedAreas);
                statisticsData.setTouchedAreaAverageError(touchedAreaAverageError);
                statisticsData.setTouchedAreaAverageSize(touchedAreaAverageSize);
                statisticsData.setTouchedAreaAverageSize(touchedAreaAveragePressure);

                testSubject.setTimeSpent((System.currentTimeMillis() - startTime) / 1000);

                Common.saveStatistics(Constants.STAGE_ONE_LOG_FILENAME, testSubject, target, statisticsData, getContext().getResources().getDisplayMetrics());

                touchedAreas = new HashMap<>();
                touchedAreaAverageError = new HashMap<>();
                touchedAreaAverageSize = new HashMap<>();
                touchedAreaAveragePressure = new HashMap<>();

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
}
