package com.touchy.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.touchy.app.R;
import com.touchy.app.Utils.ScreenHelper;
import com.google.android.material.textview.MaterialTextView;

public class SplashActivity extends AppCompatActivity {
    private ImageView appLogoIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ScreenHelper.setActivityInitialLayout(this);

        appLogoIcon = findViewById(R.id.logoIcon);
        MaterialTextView appLogoText = findViewById(R.id.appName);

        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.translation);
        appLogoIcon.startAnimation(logoAnimation);
        appLogoText.startAnimation(logoAnimation);

        logoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation logoAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulsing);
                appLogoIcon.startAnimation(logoAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final Intent intent = new Intent(this, CalibrationSetupActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }

    @Override
    public void onResume(){
        super.onResume();

        ScreenHelper.setActivityInitialLayout(this);
    }
}