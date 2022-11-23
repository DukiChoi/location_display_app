package com.example.location_display;

import androidx.annotation.ContentView;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    public static Thread triggerService = null;
    private ImageView imageView;
    private ImageView background;
    public static final String TAG = MainActivity.class.getCanonicalName();
    long animationDuration = 500; //1초
    float LocationX = 0;
    float LocationY = 0;
    float angle_to_turn = 0;
    float last_angle = 0;
    float dp_value = 3;
    //안드로이드의 dp값은 360dp, 640dp
    //이 폰은 1080px, 1920px이므로 3배수.


//    private final TextView.OnEditorActionListener X_Listener = new TextView.OnEditorActionListener() {
//        @Override
//        public boolean onEditorAction(TextView editText, int actionId, KeyEvent event) {
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                LocationX = Float.parseFloat(editText.getText().toString());
//            }
//
//            return false;
//        }
//    };
//    private final TextView.OnEditorActionListener Y_Listener = new TextView.OnEditorActionListener() {
//        @Override
//        public boolean onEditorAction(TextView editText, int actionId, KeyEvent event) {
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                LocationY = Float.parseFloat(editText.getText().toString());
//
//            }
//            return false;
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //디스플레이 크기 구하는 부분
        Display display = getWindowManager().getDefaultDisplay();  // in Activity
        /* getActivity().getWindowManager().getDefaultDisplay() */ // in Fragment
        Point size = new Point();
        display.getRealSize(size); // or getSize(size)
        int width = size.x;
        int height = size.y;
        Log.e(TAG, "width is: " + width);
        Log.e(TAG, "height is: " + height);

        //EditText 리스너인데 작동을 안하네..
        EditText x_edit = (EditText) findViewById(R.id.X_EditText);
        EditText y_edit = (EditText) findViewById(R.id.Y_EditText);

        x_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Float.parseFloat(s.toString());
                    LocationX = Float.parseFloat(s.toString());
                } catch(NumberFormatException e) {
                    // Not float
                }
                Log.e(TAG, "X is " + String.valueOf(LocationX));
            }
        });
        y_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Float.parseFloat(s.toString());
                    LocationY = Float.parseFloat(s.toString());
                } catch(NumberFormatException e) {
                    // Not float
                }
                Log.e(TAG, "Y is " + String.valueOf(LocationY));
            }
        });

//        x_edit.setOnEditorActionListener(X_Listener);
//        y_edit.setOnEditorActionListener(Y_Listener);
        imageView = findViewById(R.id.image_view);
        background= findViewById(R.id.back_ground);
        imageView.bringToFront();

    }


    //가로방향
    public void ToTheRight(View view) {
        //width = view.getMeasuredWidth();
        //height = view.getMeasuredHeight();


        //120dp 즉 360픽셀 씩 이동
        LocationX = LocationX + 120;
        image_move(LocationX, LocationY);
    }
    //가로방향
    public void ToTheLeft(View view) {

        //120dp 즉 360픽셀 씩 이동
        LocationX = LocationX - 120;
        image_move(LocationX, LocationY);
    }

    //세로방향
    public void GoDown(View view) {


        //120dp 즉 360픽셀 씩 이동
        LocationY = LocationY + 120;
        image_move(LocationX, LocationY);
    }
    //세로방향
    public void GoUp(View view) {


        //120dp 즉 360픽셀 씩 이동
        LocationY = LocationY - 120;
        image_move(LocationX, LocationY);
    }

    //회전
    public void showRotate(View view) {
        last_angle = angle_to_turn;
        angle_to_turn = angle_to_turn + 90;
        image_rotate(last_angle, angle_to_turn);
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
        image_move(LocationX, LocationY);
    }

    public void image_move(float PosX, float PosY){

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(
                imageView,
                "translationX",
                PosX*dp_value
        );
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(
                imageView,
                "translationY",
                PosY*dp_value
        );
        animatorX.setDuration(animationDuration);
        animatorX.start();
        animatorY.setDuration(animationDuration);
        animatorY.start();
    }

    public void image_rotate(float angle1, float angle2){
        ValueAnimator rotateAnimator = ObjectAnimator.ofFloat(imageView, "rotation", angle1, angle2);
        rotateAnimator.setDuration(animationDuration);
        rotateAnimator.start();
    }
}