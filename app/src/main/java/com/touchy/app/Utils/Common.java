package com.touchy.app.Utils;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.touchy.app.Constants;
import com.touchy.app.Models.StatisticsData;
import com.touchy.app.Models.Target;
import com.touchy.app.Models.TestSubject;
import com.touchy.app.Models.Touch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Common {
    public static String getScreenResolution(Display display) {
        int height = display.getWidth();
        int width = display.getHeight();

        return String.format(Locale.ENGLISH,"%d x %d", width, height);
    }

    public static String getScreenDensity(DisplayMetrics metrics) {
        return String.format(Locale.ENGLISH,"xdpi: %f ydpi: %f, density: %f, densityDpi: %d", metrics.xdpi, metrics.ydpi, metrics.density, metrics.densityDpi);
    }

    public static File createOutputDirectory(String directoryName) {
        File statisticsDirectory = new File(String.format("%s/%s/%s/", Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM, directoryName));

        if (!statisticsDirectory.exists()) {
            boolean isCreated = statisticsDirectory.mkdirs();
            if(!isCreated) return null;
        }

        return statisticsDirectory;
    }

    public static void saveStatistics(String filename, TestSubject testSubject, Target target, StatisticsData stats, DisplayMetrics displayMetrics) {
        Gson gson = new Gson();

        int radiusPx = target.getRadius();
        double radiusMm = pxToMm(radiusPx, displayMetrics);
        double radiusDp = pxToDp(radiusPx, displayMetrics);

        String subjectString = String.format(Locale.ENGLISH, "%s;%s;%s;%s;%d;%s;px;%d;mm;%f;dp;%f;%d;",
                testSubject.getName(),
                testSubject.getHandingTechnique(),
                testSubject.getScreenResolution(),
                testSubject.getScreenDensity(),
                testSubject.getSessionLengthInTouches(),
                testSubject.getDate(),
                radiusPx,
                radiusMm,
                radiusDp,
                testSubject.getTimeSpent());

        File sessionFilePath = new File(Common.createOutputDirectory("Touchy"), String.format("%s.json", filename));
        File sessionFilePathCSV = new File(Common.createOutputDirectory("Touchy"), String.format("%s.csv", filename));

        List<JSONObject> statistics = new ArrayList<>();
        if (sessionFilePath.length() > 0) {
            try {
                statistics = gson.fromJson(new JsonReader(new FileReader(sessionFilePath)), new TypeToken<List<JSONObject>>() {}.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            List<StatisticsData> statisticsDataList = new ArrayList<>();
            StringBuilder dataString = new StringBuilder();
            for (Constants.TOUCHED_AREA touchedArea : Constants.TOUCHED_AREA.values()) {
                int touches = stats.getTouchedAreas().get(String.valueOf(touchedArea)) == null ? 0 : stats.getTouchedAreas().get(String.valueOf(touchedArea));
                double averageErrorPx = stats.getTouchedAreaAverageError().get(String.valueOf(touchedArea)) == null ? 0 : stats.getTouchedAreaAverageError().get(String.valueOf(touchedArea));
                double averageSize = stats.getTouchedAreaAverageSize().get(String.valueOf(touchedArea)) == null ? 0 : stats.getTouchedAreaAverageSize().get(String.valueOf(touchedArea));
                double averagePressure = stats.getTouchedAreaAveragePressure().get(String.valueOf(touchedArea)) == null ? 0 : stats.getTouchedAreaAveragePressure().get(String.valueOf(touchedArea));

                StatisticsData statisticsData = new StatisticsData(String.valueOf(touchedArea), touches, averageErrorPx, averageSize, averagePressure);
                statisticsDataList.add(statisticsData);

                double averageErrorMm = pxToMm(averageErrorPx, displayMetrics);
                double averageErrorDp = pxToDp(averageErrorPx, displayMetrics);

                dataString.append(touchedArea).append(";").append(touches).append(";").append("px").append(";").append(averageErrorPx).append(";").append(averageSize).append(";").append(averagePressure).append(";");
                dataString.append(touchedArea).append(";").append(touches).append(";").append("mm").append(";").append(averageErrorMm).append(";").append(averageSize).append(";").append(averagePressure).append(";");
                dataString.append(touchedArea).append(";").append(touches).append(";").append("dp").append(";").append(averageErrorDp).append(";").append(averageSize).append(";").append(averagePressure).append(";");
            }

            JSONObject sessionData = new JSONObject();
            sessionData.put("subject", testSubject);
            sessionData.put("target", target);
            sessionData.put("statistics", statisticsDataList);

            statistics.add(sessionData);

            String json = gson.toJson(statistics);

            String csvData = subjectString + dataString + "\n";

            writeToAFile(sessionFilePathCSV, csvData, true);
            writeToAFile(sessionFilePath, json, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double pxToDp(double px, DisplayMetrics displayMetrics) {
        return px / displayMetrics.density;
    }

    public static double pxToMm(double px, DisplayMetrics displayMetrics) {
        return px / (displayMetrics.density * 25.4f);
    }

    private static void writeToAFile(File file, String data, boolean append) {
        try {
            FileWriter out = new FileWriter(file, append);
            out.write(data);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("FileWriter", "Couldn't create file to store data.");
        }
    }

    public static String getFormattedDate() {
        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        return dateFormatter.format(date);
    }

    public static double getTouchError(Target target, Touch touch) {
        return Math.sqrt(Math.pow(target.getX() - touch.getX(), 2) + Math.pow(target.getY() - touch.getY(), 2));
    }

    public static String getTouchedArea(Target target, Touch touch, MediaPlayer mediaPlayer) {
        if (getTouchError(target, touch) <= target.getRadius()) {
            System.out.println("Hit!");

            if (mediaPlayer != null) {
                mediaPlayer.start();
            }

            return String.valueOf(Constants.TOUCHED_AREA.CENTER);
        }

        if (target.getX() > touch.getX() && target.getY() < touch.getY()) {
            return String.valueOf(Constants.TOUCHED_AREA.SOUTH_WEST);
        } else if (target.getX() > touch.getX() && target.getY() > touch.getY()) {
            return String.valueOf(Constants.TOUCHED_AREA.NORTH_WEST);
        } else if (target.getX() < touch.getX() && target.getY() < touch.getY()) {
            return String.valueOf(Constants.TOUCHED_AREA.SOUTH_EAST);
        }

        return String.valueOf(Constants.TOUCHED_AREA.NORTH_EAST);
    }

    public static boolean hasPassedCalibration() {
        File sessionFilePath = new File(createOutputDirectory("Touchy"), String.format("%s.json", Constants.CALIBRATION_LOG_FILENAME));

        return sessionFilePath.length() > 0;
    }

    public static List<StatisticsData> getLastCalibrationData() {
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
}
