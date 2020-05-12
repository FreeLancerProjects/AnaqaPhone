package com.anaqaphone.activities_fragments.activity_splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_login.LoginActivity;
import com.anaqaphone.databinding.ActivitySplashBinding;
import com.anaqaphone.language.Language;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.tags.Tags;


import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private Animation animation;
    private Preferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        preferences = Preferences.getInstance();

        animation = AnimationUtils.loadAnimation(this,R.anim.lanuch);
        binding.image.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.image.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                new Handler().postDelayed(()->{

                    String session = preferences.getSession(SplashActivity.this);
                    if (session.equals(Tags.session_login)) {
                        /*Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();*/
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();


                    }
                },3000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
