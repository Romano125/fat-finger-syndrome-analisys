package com.touchy.app.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.touchy.app.Activities.CalibrationSetupActivity;
import com.touchy.app.Constants;
import com.touchy.app.Models.StatisticsData;
import com.touchy.app.Models.Target;
import com.touchy.app.Models.TestSubject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class CalibrationView extends View {
    private float circle_x, circle_y, realCenterOffset = 5;
    private final Random random = new Random();
    private final Paint paint = new Paint();
    private int sessionLengthInTouches, radius, touchCount = 0;
    private String screenResolution, subjectName, subjectHandedness;
    private static int[] colors = {
            Color.GREEN,
            Color.RED,
            Color.parseColor("#9900ff"),
            Color.CYAN,
            Color.BLACK,
            Color.parseColor("#00FF7F"),
            Color.BLUE,
            Color.parseColor("#7FFF00"),
    };

    private static HashMap<String, Integer> touchedAreas = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAverageError = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAverageSize = new HashMap<>();
    private static HashMap<String, Double> touchedAreaAveragePressure = new HashMap<>();
    private SharedPreferences sharedPreferences;

    public CalibrationView(Context context) {
        super(context);

        init(null, 0);

        sharedPreferences = context.getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        this.radius = sharedPreferences.getInt("targetRadius", 50);
        this.sessionLengthInTouches = sharedPreferences.getInt("sessionLengthInTouches", 10);
        this.screenResolution = sharedPreferences.getString("screenResolution", "-");
        this.subjectName = sharedPreferences.getString("subjectName", "-");
        this.subjectHandedness = sharedPreferences.getString("subjectHandedness", "-");
    }

    public CalibrationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);

        sharedPreferences = context.getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        this.radius = sharedPreferences.getInt("targetRadius", 50);
        this.sessionLengthInTouches = sharedPreferences.getInt("sessionLengthInTouches", 10);
        this.screenResolution = sharedPreferences.getString("screenResolution", "-");
        this.subjectName = sharedPreferences.getString("subjectName", "-");
        this.subjectHandedness = sharedPreferences.getString("subjectHandedness", "-");
    }

    public CalibrationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);

        sharedPreferences = context.getSharedPreferences("calibrationSetupPreference", MODE_PRIVATE);

        this.radius = sharedPreferences.getInt("targetRadius", 50);
        this.sessionLengthInTouches = sharedPreferences.getInt("sessionLengthInTouches", 10);
        this.screenResolution = sharedPreferences.getString("screenResolution", "-");
        this.subjectName = sharedPreferences.getString("subjectName", "-");
        this.subjectHandedness = sharedPreferences.getString("subjectHandedness", "-");
    }

    private void init(AttributeSet attrs, int defStyle) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#E4E0E0"));
        circle_x = (int) (random.nextInt(Math.abs(getWidth()-radius/2)) + radius/2f);
        circle_y = (random.nextInt((int)Math.abs((getHeight()- (radius >> 1) + radius/2f))));
        canvas.drawCircle(circle_x, circle_y, radius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String touchedArea = getTouchedArea(event.getX(), event.getY());
        double touchError = getTouchError(event.getX(), event.getY());
        double touchSize = event.getSize();
        double touchPressure = event.getPressure();

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

        if (touchCount++ == sessionLengthInTouches) {
            System.out.println("Saving statistics");

            saveStatistics();

            Intent intent = new Intent(getContext(), CalibrationSetupActivity.class);
            getContext().startActivity(intent);
        }

        redrawCircle();

        return super.onTouchEvent(event);
    }

    private double getTouchError(float x_coordinate, float y_coordinate) {
        return Math.sqrt(Math.pow(circle_x - x_coordinate, 2) + Math.pow(circle_y - y_coordinate, 2));
    }

    private String getTouchedArea(float x_coordinate, float y_coordinate) {
        if (Math.abs(circle_x - x_coordinate) <= realCenterOffset && Math.abs(circle_y - y_coordinate) <= realCenterOffset) {
            System.out.println("In the middle!");
            return String.valueOf(Constants.TOUCHED_AREA.CENTER);
        }

        if (circle_x > x_coordinate && circle_y < y_coordinate) {
            return String.valueOf(Constants.TOUCHED_AREA.SOUTH_WEST);
        } else if (circle_x > x_coordinate && circle_y > y_coordinate) {
            return String.valueOf(Constants.TOUCHED_AREA.NORTH_WEST);
        } else if (circle_x < x_coordinate && circle_y < y_coordinate) {
            return String.valueOf(Constants.TOUCHED_AREA.SOUTH_EAST);
        }

        return String.valueOf(Constants.TOUCHED_AREA.NORTH_EAST);
    }

    private void redrawCircle() {
        int index = random.nextInt(colors.length);
        paint.setColor(colors[index]);
        invalidate();
    }

    private void saveStatistics() {
        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String currentDate = dateFormatter.format(date);

        Gson gson = new Gson();

        File sessionFilePath = new File(createOutputDirectory("Touchy"),"calibrationStatistics.txt");

        List<JSONObject> statistics = new ArrayList<>();
        if (sessionFilePath.length() > 0) {
            try {
                statistics = gson.fromJson(new JsonReader(new FileReader(sessionFilePath)), new TypeToken<List<JSONObject>>() {}.getType());

                // reading data from file
                String json = gson.toJson(statistics.get(0).get("subject"));

                TestSubject testSubject = gson.fromJson(json, new TypeToken<TestSubject>() {}.getType());

                System.out.println(testSubject.getName());
            } catch (FileNotFoundException | JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            TestSubject testSubject = new TestSubject(subjectName, subjectHandedness, screenResolution, currentDate, sessionLengthInTouches);
//            subjectData.put("highestPrecisionArea", currentDate);

            Target target = new Target(radius);

            List<StatisticsData> statisticsDataList = new ArrayList<>();
            for (Constants.TOUCHED_AREA touchedArea : Constants.TOUCHED_AREA.values()) {
                int touches = touchedAreas.get(String.valueOf(touchedArea)) == null ? 0 : touchedAreas.get(String.valueOf(touchedArea));
                double averageError = touchedAreaAverageError.get(String.valueOf(touchedArea)) == null ? 0 : touchedAreaAverageError.get(String.valueOf(touchedArea));
                double averageSize = touchedAreaAverageSize.get(String.valueOf(touchedArea)) == null ? 0 : touchedAreaAverageSize.get(String.valueOf(touchedArea));
                double averagePressure = touchedAreaAveragePressure.get(String.valueOf(touchedArea)) == null ? 0 : touchedAreaAveragePressure.get(String.valueOf(touchedArea));

                StatisticsData statisticsData = new StatisticsData(String.valueOf(touchedArea), touches, averageError, averageSize, averagePressure);
                statisticsDataList.add(statisticsData);
            }

            JSONObject sessionData = new JSONObject();
            sessionData.put("subject", testSubject);
            sessionData.put("target", target);
            sessionData.put("statistics", statisticsDataList);

            statistics.add(sessionData);

            String json = gson.toJson(statistics);

            try {
                FileWriter out = new FileWriter(sessionFilePath);
                out.write(json);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.w("FileWriter", "Couldn't create file to store calibration statistics data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createOutputDirectory(String directoryName) {
        File statisticsDirectory = new File(String.format("%s/%s/%s/", Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM, directoryName));

        if (!statisticsDirectory.exists()) {
            boolean isCreated = statisticsDirectory.mkdirs();
            if(!isCreated) return null;
        }

        return statisticsDirectory;
    }
}
