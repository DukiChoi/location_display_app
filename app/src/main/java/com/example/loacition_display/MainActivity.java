package com.example.loacition_display;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    long animationDuration = 1000; //1초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
    }

    //가로방향
    public void showTranslationX(View view) {

        ValueAnimator animatorX = ObjectAnimator.ofFloat(imageView, "translationX", 100f, 200f, 50f);
        animatorX.setDuration(animationDuration);
        animatorX.start();
    }

    //세로방향
    public void showTranslationY(View view) {

        ValueAnimator animatorY = ObjectAnimator.ofFloat(imageView, "translationY", 100f, 200f, 50f);
        animatorY.setDuration(animationDuration);
        animatorY.start();
    }

    //회전
    public void showRotate(View view) {

        ValueAnimator rotateAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotateAnimator.setDuration(animationDuration);
        rotateAnimator.start();
    }

    //사라짐
    public void showAlpha(View view) {

        ValueAnimator alphaAnimator = ObjectAnimator.ofFloat(imageView, View.ALPHA, 1.0f, 0f);
        alphaAnimator.setDuration(animationDuration);
        alphaAnimator.start();
    }

    //나타남
    public void showAlpha2(View view) {

        ValueAnimator alphaAnimator = ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f, 1.0f);
        alphaAnimator.setDuration(animationDuration);
        alphaAnimator.start();
    }

}