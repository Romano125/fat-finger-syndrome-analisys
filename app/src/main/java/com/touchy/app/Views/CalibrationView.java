package com.touchy.app.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.touchy.app.Activities.MainMenuActivity;
import com.touchy.app.Constants;
import com.touchy.app.Models.StatisticsData;
import com.touchy.app.Models.Target;
import com.touchy.app.Models.TestSubject;
import com.touchy.app.Models.Touch;
import com.touchy.app.Utils.Common;

import java.util.HashMap;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class CalibrationView extends View {
    private Target target = new Target();
    private StatisticsData statisticsData = new StatisticsData();
    private TestSubject testSubject;

    private final Random random = new Random();
    private final Paint paint = new Paint();
    private int sessionLengthInTouches, radius, touchCounter = 1;
    private long startTime = System.currentTimeMillis();

    private static HashMap<String, Integer> touchedAreas = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAverageError = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAverageSize = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAveragePressure = new HashMap<>();

    public CalibrationView(Context context) {
        super(context);

        init(null, 0, context);
    }

    public CalibrationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, context);
    }

    public CalibrationView(Context context, AttributeSet attrs, int defStyle) {
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
        boolean isHelpEnabled = sharedPreferences.getBoolean("helpEnabled", false);

        testSubject = new TestSubject(subjectName, subjectHandingTechnique, screenResolution, screenDensity, Common.getFormattedDate(), sessionLengthInTouches);
        testSubject.setHelpEnabled(isHelpEnabled);
        target.setRadius(radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int minX = radius * 2 + 5;
        int maxX = getWidth() - (radius * 2) - 5;

        int minY = radius * 2 + 5;
        int maxY = getHeight() - (radius * 2) - 5;

        //Generate random numbers for x and y locations of the circle on screen
        int randomX = random.nextInt(maxX - minX + 1) + minX;
        int randomY = random.nextInt(maxY - minY + 1) + minY;

        canvas.drawColor(Color.parseColor(Constants.APP_BACKGROUND_COLOR));
        target.setX(randomX);
        target.setY(randomY);

        canvas.drawCircle(target.getX(), target.getY(), radius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Touch touch = new Touch(event.getX(), event.getY());

        String touchedArea = Common.getTouchedArea(target, touch, null);
        double touchError = Common.getTouchError(target, touch);
        double touchSize = event.getSize();
        double touchPressure = event.getPressure();

        if (touchError > target.getRadius()) {
            touchError -= target.getRadius();
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


        if (touchCounter++ >= sessionLengthInTouches) {
            System.out.println("Saving statistics...");

            statisticsData.setTouchedAreas(touchedAreas);
            statisticsData.setTouchedAreaAverageError(touchedAreaAverageError);
            statisticsData.setTouchedAreaAverageSize(touchedAreaAverageSize);
            statisticsData.setTouchedAreaAverageSize(touchedAreaAveragePressure);

            testSubject.setTimeSpent((System.currentTimeMillis() - startTime) / 1000);

            Common.saveStatistics(Constants.CALIBRATION_LOG_FILENAME, testSubject, target, statisticsData, getContext().getResources().getDisplayMetrics());

            touchedAreas = new HashMap<>();
            touchedAreaAverageError = new HashMap<>();
            touchedAreaAverageSize = new HashMap<>();
            touchedAreaAveragePressure = new HashMap<>();

            Intent intent = new Intent(getContext(), MainMenuActivity.class);
            getContext().startActivity(intent);
        }

        redrawTarget();

        return super.onTouchEvent(event);
    }

    private void redrawTarget() {
        int index = random.nextInt(Constants.TARGET_COLORS.length);
        paint.setColor(Constants.TARGET_COLORS[index]);
        invalidate();
    }
}
