package com.example.location_display;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {
    public static Thread triggerService = null;
    private ImageView imageView;
    public static final String TAG = MainActivity.class.getCanonicalName();
    long animationDuration = 500; //1초
    float LocationX = 0;
    float LocationY = 0;
    float width;
    float height;
    EditText X_edit;
    EditText Y_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        X_edit = (EditText) imageView.findViewById(R.id.X_EditText);
        Y_edit = (EditText) imageView.findViewById(R.id.Y_EditText);
    }

    //가로방향
    public void ToTheRight(View view) {
       width = view.getMeasuredWidth();
       height = view.getMeasuredHeight();
        Log.e(TAG, "width is: " + width);
        Log.e(TAG, "height is: " + height);
//        //ValueAnimator animatorX = ObjectAnimator.ofFloat(imageView, "translationX", 100f, 200f, 50f);
//        ObjectAnimator animatorX = ObjectAnimator.ofFloat(
//                imageView,
//                "translationX",
//                100
//        );
//
//        animatorX.setDuration(animationDuration);
//        animatorX.start();
        LocationX = LocationX + 100;
        image_move(LocationX, LocationY);
    }
    //가로방향
    public void ToTheLeft(View view) {
//        //ValueAnimator animatorX = ObjectAnimator.ofFloat(imageView, "translationX", 100f, 200f, 50f);
//        ObjectAnimator animatorX = ObjectAnimator.ofFloat(
//                imageView,
//                "translationX",
//                100
//        );
//
//        animatorX.setDuration(animationDuration);
//        animatorX.start();
        LocationX = LocationX - 100;
        image_move(LocationX, LocationY);
    }

    //세로방향
    public void GoDown(View view) {

//        //ValueAnimator animatorY = ObjectAnimator.ofFloat(imageView, "translationY", 100f, 200f, 50f);
//        ObjectAnimator animatorY = ObjectAnimator.ofFloat(
//                imageView,
//                "translationY",
//                100
//        );
//
//        animatorY.setDuration(animationDuration);
//        animatorY.start();
        LocationY = LocationY + 100;
        image_move(LocationX, LocationY);
    }
    //세로방향
    public void GoUp(View view) {

//        //ValueAnimator animatorY = ObjectAnimator.ofFloat(imageView, "translationY", 100f, 200f, 50f);
//        ObjectAnimator animatorY = ObjectAnimator.ofFloat(
//                imageView,
//                "translationY",
//                100
//        );
//
//        animatorY.setDuration(animationDuration);
//        animatorY.start();
        LocationY = LocationY - 100;
        image_move(LocationX, LocationY);
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

    //나타남
    public void MoveTo(View view) {
        LocationX = Float.parseFloat(X_edit.getText().toString());
        LocationY = Float.parseFloat(Y_edit.getText().toString());
        image_move(LocationX, LocationY);
    }

    public void image_move(float PosX, float PosY){

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(
                imageView,
                "translationX",
                PosX
        );
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(
                imageView,
                "translationY",
                PosY
        );
        animatorX.setDuration(animationDuration);
        animatorX.start();
        animatorY.setDuration(animationDuration);
        animatorY.start();
    }
}