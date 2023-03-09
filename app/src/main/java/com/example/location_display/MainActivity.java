package com.example.location_display;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.TypedArrayUtils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.system.ErrnoException;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;


public class MainActivity extends AppCompatActivity {


    //설정 저장하는 변수 ( ip, port )
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String SETTINGS_PLAYER_JSON = "settings_item_json";

    public static Thread triggerService = null;
    private ImageView imageView;
    private ImageView imageView2;
    private ImageView background;
    public static final String TAG = MainActivity.class.getCanonicalName();
    long animationDuration = 0; //1초
    // 여기에는 px값으로 저장한다.
    float LocationX = 0;
    float LocationY = 0;
    float dest_x = 0;
    float dest_y = 0;
    float angle_to_turn = 0;
    float last_angle = 0;
    float dp_value = 3;
    EditText target_x_edit;
    EditText target_y_edit;
    EditText dest_x_edit;
    EditText dest_y_edit;
    EditText dest_x_edit2;
    EditText dest_y_edit2;
    TextView host_edit;
    EditText port_number_edit;
    Button connnect_btn;
    Button disconnnect_btn;
    Button enter_btn;
    //안드로이드의 dp값은 360dp, 640dp
    //이 폰은 1080px, 1920px이므로 3배수.
    private static Socket socket;
    private static ObjectOutputStream outstream;
    private static ObjectInputStream instream;
    private static InputStream is;
    String ip;
    ArrayList<String> ip_list = new ArrayList<String>();
    Handler handler = new Handler();
    int option = -1;
    String input = ".";
    Drawable drawable_background_green;
    Drawable drawable_background_blue;
    Drawable drawable_background_red;
    Drawable drawable_background_skyblue;
    private BluetoothAdapter mBluetoothAdapter;
    ArrayAdapter<String> ipadapter;
    Spinner ipSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        //설정 저장하는 부분
        //기본 SharedPreferences 환경과 관련된 객체를 얻어옵니다.
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // SharedPreferences 수정을 위한 Editor 객체를 얻어옵니다.
        editor = preferences.edit();

        //디스플레이 크기 구하는 부분
        Display display = getWindowManager().getDefaultDisplay();  // in Activity
        /* getActivity().getWindowManager().getDefaultDisplay() */ // in Fragment
        Point size = new Point();
        display.getRealSize(size); // or getSize(size)
        int width = size.x;
        int height = size.y;
        Log.e(TAG, "width is: " + width);
        Log.e(TAG, "height is: " + height);
        Log.d("My Ip Address is ", getLocalIpAddress());
        //EditText 리스너인데 작동을 안하네..
        target_x_edit = (EditText) findViewById(R.id.Target_X_EditText);
        target_y_edit = (EditText) findViewById(R.id.Target_Y_EditText);
        dest_x_edit = (EditText) findViewById(R.id.Dest_X_EditText1);
        dest_y_edit = (EditText) findViewById(R.id.Dest_Y_EditText1);
        dest_x_edit2 = (EditText) findViewById(R.id.Dest_X_EditText2);
        dest_y_edit2 = (EditText) findViewById(R.id.Dest_Y_EditText2);
        host_edit = (TextView) findViewById(R.id.host_TextView);
        port_number_edit = (EditText) findViewById(R.id.port_number_EditText);
        connnect_btn = (Button) findViewById(R.id.connect_btn);
        disconnnect_btn = (Button) findViewById(R.id.disconnect_btn);
        enter_btn = (Button) findViewById(R.id.enter_btn);
        drawable_background_green = getResources().getDrawable(R.drawable.btn_green);
        drawable_background_blue = getResources().getDrawable(R.drawable.btn_blue);
        drawable_background_red = getResources().getDrawable(R.drawable.btn_red);
        drawable_background_skyblue = getResources().getDrawable(R.drawable.btn_skyblue);
        port_number_edit.setText(preferences.getString("port_number",""));
//        ip_list.add(host);
        ip_list = getStringArrayPref(MainActivity.this, SETTINGS_PLAYER_JSON);

        target_x_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //원래 여기서 Target EditText값이 변화하면 받아와서 float형식으로 바꾼다음 좌표변환해줬었는데
                //값에 m단위 표시가 들어가면서 float변환이 불가능해져서 지금은 그냥 connect함수에서 값 받자마자 변환해서 LocationX에 넣어준다.
//                try {
//                    LocationX = coordinate_transform_to_dp(Float.parseFloat(s.toString()), 0)[0];
//                } catch(NumberFormatException e) {
//                    // Not float
//                }
                Log.e(TAG, "X is " + s.toString());
            }
        });
        target_y_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
//                try {
//                    LocationY = coordinate_transform_to_dp(0, Float.parseFloat(s.toString()))[1];
//                } catch(NumberFormatException e) {
//                    // Not float
//                }
                Log.e(TAG, "Y is " + s.toString());
            }
        });


        host_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(MainActivity.this);
                //EditText의 Inputtype을 Number로 해줌
                //et.setKeyListener(new DigitsKeyListener().getInstance(false,true));
                et.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("ip 세팅");

                builder.setIcon(R.drawable.black_gear).setView(et);
                builder.setMessage("추가할 ip를 입력해주세요");

                builder.setNegativeButton("추가",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(!et.getText().toString().equals("")) {
                                    String newdata = et.getText().toString();


                                    System.out.println("ip addresss added: " + newdata);
                                    //스피너에 추가
                                    ipadapter.add(newdata);
                                    ipSpinner = (Spinner)findViewById(R.id.spinner_ip);
                                    ipSpinner.setAdapter(ipadapter);
                                    //스피너 칸 새로 추가한 것으로 변경
                                    ipSpinner.setSelection(ip_list.size()-1);
                                    //설정데이터 저장
                                    setStringArrayPref(MainActivity.this, SETTINGS_PLAYER_JSON, ip_list);
                                    //여기서 adapter.add 할 떄 알 수 없는 오류가 난 적이 있었는데 String[] (Array)형식 대신에 ArrayList<String> 형식으로 대신해주니 오류가 해결됨.
                                }
                            }
                        });
                builder.setNeutralButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String data_to_remove = host_edit.getText().toString();
                                //EditText 변경
                                host_edit.setText("");
                                //스피너에서 삭제
                                ipadapter.remove(data_to_remove);
                                ipSpinner = (Spinner)findViewById(R.id.spinner_ip);
                                ipSpinner.setAdapter(ipadapter);
                                ipSpinner.setSelection(0);
                                //설정데이터 저장
                                setStringArrayPref(MainActivity.this, SETTINGS_PLAYER_JSON, ip_list);
                            }
                        });
                builder.setPositiveButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });


                builder.show();
            }

        });

        //스피너 정의 부분
        // Spinner

//        ArrayAdapter<CharSequence> ipadapter = ArrayAdapter.createFromResource(this, R.array.ip_array, android.R.layout.simple_spinner_item );
        ipadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ip_list);
        ipSpinner = (Spinner)findViewById(R.id.spinner_ip);
        ipadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ipSpinner.setAdapter(ipadapter);
        ipSpinner.setPrompt("연결할 ip를 선택해주세요");
        ipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "Selected ip: " + ip_list[position], Toast.LENGTH_SHORT).show();
//                parent.getItemIdAtPosition(position);
                ipSpinner.setSelection(position);
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
                host_edit.setText(ip_list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        target_x_edit.setOnEditorActionListener(X_Listener);
//        target_y_edit.setOnEditorActionListener(Y_Listener);
        imageView = findViewById(R.id.image_view);
        imageView2 = findViewById(R.id.image_view2);
        background= findViewById(R.id.back_ground);
        //이미지 맨 앞으로
        imageView.bringToFront();
        imageView2.bringToFront();
        //초기에 0,0으로 이동해주기
        image_move(180, 400, imageView);
        image_move(180, 400, imageView2);
        //초기 dest 값들 표시하기
        dest_x_edit.setText("0");
        dest_y_edit.setText("0");
        IpThread ipthread = new IpThread();
        ipthread.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket_open();
                    ClientThread clientThread = new ClientThread(socket, is);
                    clientThread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true /* show menu */;
    }
    // 원래 상단 바의 메뉴에 On/Off 를 넣어서 각각 s / f 의 string으로 바꾸어서 소켓에 보내는 역할을 수행했었음.
    // 참고로 메뉴의 버튼으로는 string 변수의 값만 바꿔주고 socket_send는 아래 루프문에서 해준다.
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_On) {
//            option = 1;
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    try {
////                        socket_send("s");
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }).start();
//            input = "s";
//            System.out.println("option Changed into: " + option);
//            return true;
//        } else if (item.getItemId() == R.id.action_Off) {
//            option = 0;
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    try {
////                        socket_send("f");
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }).start();
//            input = "f";
//            System.out.println("option Changed into: " + option);
//            return true;
//        }
//        return false;
//    }
    @Override
    protected void onStop(){
        super.onStop();
        try {
            instream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
    }


    //가로방향
    public void ToTheRight(View view) {
        //width = view.getMeasuredWidth();
        //height = view.getMeasuredHeight();


        //120dp 즉 360픽셀 씩 이동
        LocationX = LocationX + 120;
        image_move(LocationX, LocationY, imageView);
        float[] temp = coordinate_transform_from_dp(LocationX, LocationY);
        target_x_edit.setText(String.format("%.3f",temp[0]));
        target_y_edit.setText(String.format("%.3f",temp[1]));
    }
    //가로방향
    public void ToTheLeft(View view) {

        //120dp 즉 360픽셀 씩 이동
        LocationX = LocationX - 120;
        image_move(LocationX, LocationY, imageView);
        float[] temp = coordinate_transform_from_dp(LocationX, LocationY);
        target_x_edit.setText(String.format("%.3f",temp[0]));
        target_y_edit.setText(String.format("%.3f",temp[1]));
    }

    //세로방향
    public void GoDown(View view) {


        //120dp 즉 360픽셀 씩 이동
        LocationY = LocationY + Math.round(320/3*1000)/1000;
        image_move(LocationX, LocationY, imageView);
        float[] temp = coordinate_transform_from_dp(LocationX, LocationY);
        target_x_edit.setText(String.format("%.3f",temp[0]));
        target_y_edit.setText(String.format("%.3f",temp[1]));
    }
    //세로방향
    public void GoUp(View view) {


        //120dp 즉 360픽셀 씩 이동
        LocationY = LocationY - Math.round(320/3*1000)/1000;
        image_move(LocationX, LocationY, imageView);
        float[] temp = coordinate_transform_from_dp(LocationX, LocationY);
        target_x_edit.setText(String.format("%.3f",temp[0]));
        target_y_edit.setText(String.format("%.3f",temp[1]));
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

    //그냥 주어진 좌표로 계속 이동하고 setText를 동시에 해주는 버튼 함수
    public void MoveTo(View view) {
        float[] temp = coordinate_transform_from_dp(LocationX, LocationY);
        target_x_edit.setText(String.format("%.3f",temp[0]));
        target_y_edit.setText(String.format("%.3f",temp[1]));
        image_move(LocationX, LocationY, imageView);
        //여기선 imageView 즉 car.png를 이동시켜준다.

    }
    //EditText 속 좌표를 받아와서 그 좌표로 이미지를 이동시키는 버튼 함수
    public void DestinationSet(View view) {
        enter_btn.setBackgroundDrawable(drawable_background_skyblue);
        if(dest_x_edit.getText().toString().equals("")){
            dest_x_edit.setText("0");
            dest_x = 0;
        }else {
//            dest_x_edit2.setText(dest_x_edit.getText().toString());
            dest_x = Float.parseFloat(dest_x_edit.getText().toString());
        }
        if(dest_y_edit.getText().toString().equals("")){
            dest_y_edit.setText("0");
            dest_y = 0;
        }else {
//            dest_y_edit2.setText(dest_y_edit.getText().toString());
            dest_y = Float.parseFloat(dest_y_edit.getText().toString());
        }
        float[] temp = coordinate_transform_to_dp(dest_x, dest_y);
        image_move(temp[0], temp[1], imageView2);
        Toast.makeText(getApplicationContext(), "X: " + dest_x + "\nY: " + dest_y, Toast.LENGTH_SHORT).show();
        //여기선 imageView2 즉 destination.png를 이동시켜준다.
    }

    public void connect(View view){
        if(!host_edit.getText().toString().equals("") && !port_number_edit.getText().toString().equals("")) {
            input = ".";
            option = -1;
            System.out.println("option Changed into: " + option);
            String host = host_edit.getText().toString();
            //포트넘버 저장
            editor.putString("port_number", port_number_edit.getText().toString());
            editor.apply();
            System.out.println("Host is changed into : " + host);
            disconnnect_btn.setBackgroundDrawable(drawable_background_blue);
            connnect_btn.setBackgroundDrawable(drawable_background_green);
//            try {
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                outstream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            //다 끊은 다음 다시 소켓통신 시작
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket_open();
                        ClientThread clientThread = new ClientThread(socket, is);
                        connnect_btn.setBackgroundDrawable(drawable_background_green);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run(){
                                Toast.makeText(getApplicationContext(), host + " 로 연결합니다 ", Toast.LENGTH_SHORT).show();
                            }
                        }, 0);
                        clientThread.start();

                    } catch (Exception e) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run(){
                                Toast.makeText(getApplicationContext(), "서버가 열려있는지 확인해주세요", Toast.LENGTH_SHORT).show();
                            }
                        }, 0);
                        connnect_btn.setBackgroundDrawable(drawable_background_blue);
                        disconnnect_btn.setBackgroundDrawable(drawable_background_red);
                        e.printStackTrace();

                    }
                }
            }).start();
        }
        else{
            Toast.makeText(getApplicationContext(), "Ip와 Port를 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    public void disconnect(View view) throws IOException {
        connnect_btn.setBackgroundDrawable(drawable_background_blue);
        if(socket!=null && socket.isBound() && socket.isConnected()) {
            input = "f";
            option = 0;
            System.out.println("option Changed into: " + option);
            System.out.println("Socket closed");
            Toast.makeText(getApplicationContext(), "연결을 끊었습니다", Toast.LENGTH_SHORT).show();
        }else if(socket.isConnected()){
            System.out.println("Socket is not connected, so I couldn't close");
            Toast.makeText(getApplicationContext(), "연결되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }else if(socket.isBound()){
            System.out.println("Socket is not bound, so I couldn't close");
            Toast.makeText(getApplicationContext(), "연결되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }else{
            System.out.println("Neither bound nor connected, so I couldn't close");
            Toast.makeText(getApplicationContext(), "연결되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }


    public void image_move(float PosX, float PosY, ImageView imageView){
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





    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;

    }
    private class IpThread extends Thread {

        public IpThread() {
            // 초기화 작업
        }
        public void run() {
            int second = 0;
            while (second < 10) {
                second++;
                ip = getLocalIpAddress();
                Log.i("경과된 시간 : ", Integer.toString(second));
            }
            System.out.println("Ip is: " + ip);
        }
    }

    class ClientThread extends Thread{
        ClientThread(Socket socket2){
            socket = socket2;
        }
        ClientThread(Socket socket2, InputStream is2) throws IOException {
            socket = socket2;
            is = is2;
        }
        public void run(){
            try{
                final float[] xy = {0,0};
                while(true){
                    if (option == -1) {
                        String msg = socket_receive();
                        socket_send(input);
                        String[] msg_array = sort_msg(msg);
                        //스레드 안에서 UI 접근 -> 핸들러
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    xy[0] = Float.parseFloat(msg_array[0]);
                                    xy[1] = Float.parseFloat(msg_array[1]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                target_x_edit.setText(String.format("%.2f", xy[0]));
                                target_y_edit.setText(String.format("%.2f", xy[1]));
                                dest_x_edit2.setText(String.format("%.2f", dest_x-xy[0]));
                                dest_y_edit2.setText(String.format("%.2f", dest_y-xy[1]));
                                LocationX = coordinate_transform_to_dp(xy[0], 0)[0];
                                LocationY = coordinate_transform_to_dp(0, xy[1])[1];

                                image_move(LocationX, LocationY, imageView);
                            }
                        });

                    }else if(option == 1){
                        option = -1;
                        System.out.println("option Changed into: " + option);
                    }else if(option == 0){
                        if(socket.isBound()) {
                            try{
                                socket_send(input);
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        option = -1;
                        System.out.println("option Changed into: " + option);
                    }
                }
            }catch(Exception e){
                connnect_btn.setBackgroundDrawable(drawable_background_blue);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run(){
                        Toast.makeText(getApplicationContext(), "연결이 끊겼습니다", Toast.LENGTH_SHORT).show();
                    }
                }, 0);
                e.printStackTrace();
            }
        }
    }

    public void socket_open() throws IOException {
        int TIMEOUT = 3000;
        //socket = new Socket(host_edit.getText().toString(), Integer.parseInt(port_number_edit.getText().toString()));
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(host_edit.getText().toString(), Integer.parseInt(port_number_edit.getText().toString()));
        socket.connect(socketAddress, TIMEOUT);
        is = socket.getInputStream();
        outstream = new ObjectOutputStream(socket.getOutputStream());
        //서버로 데이터 주기
        System.out.println("연결 완료");
    }
    
    //받는 곳
    public String socket_receive() throws IOException, ClassNotFoundException {
        byte[] byteArr = new byte[1024];
        byte[] newbyte = null;
        is = socket.getInputStream();
        int length = is.read(byteArr, 0, 1024);
        newbyte = garbage_collector(byteArr);

        String msg_received = new String(newbyte);
        //final Object msg_received = instream.readObject();
        Log.d("ClientThread", "받은 데이터: " + msg_received);
        Log.d("ClientThread", "받은 배열: " + Arrays.toString(newbyte));
//        msg_recieved = new String(byteArr, 0, readByteCount, StandardCharsets.UTF_8);
        return msg_received;
     }

    //보내는 곳
    public void socket_send(String msg_to_send) throws IOException{
        //서버에서부터 데이터 받기
//        byte[] byteArr = null;
//        byteArr = msg_to_send.getBytes(StandardCharsets.UTF_8);
//        outstream.write(byteArr);


        outstream.writeObject(msg_to_send.getBytes(StandardCharsets.UTF_8));
        //string -> byte array

        outstream.flush();
        System.out.println("보낸 데이터 :" + Arrays.toString(msg_to_send.getBytes(StandardCharsets.UTF_8)));
        System.out.println("보낸 데이터 :" + msg_to_send);
        if(msg_to_send.equals("s") || msg_to_send.equals("f"))
            input = ".";
    }

    class ClientThread2 extends Thread{
        public void run(){
            String host = "192.168.0.9";
            int port = 8080;
            try{
                socket = new Socket(host, port);
                //서버로 데이터 주기
                outstream = new ObjectOutputStream(socket.getOutputStream());
                instream = new ObjectInputStream(socket.getInputStream());
                outstream.writeObject("안녕!");
                outstream.flush();
                Log.d("ClientThread","서버로 보냄");
                //서버에서부터 데이터 받기
                while(true) {
                    final Object input = instream.readObject();
                    Log.d("ClientThread", "받은 데이터: " + input);
                    //스레드 안에서 UI 접근 -> 핸들러
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(input);
                        }
                    });
                }
            }catch(Exception e){ e.printStackTrace();}
        }
    }

    //바이트를 flaot으로 
    public static float byteArrayToFloat(byte[] bytes) {
        int intBits = bytes[0] << 24
                | (bytes[1] & 0xFF) << 16
                | (bytes[2] & 0xFF) << 8
                | (bytes[3] & 0xFF);
        return Float.intBitsToFloat(intBits);
    }
    //float을 바이트로
    public static byte[] floatToByteArray(float value) {
        int intBits =  Float.floatToIntBits(value);
        return new byte[] {
                (byte) (intBits >> 24),
                (byte) (intBits >> 16),
                (byte) (intBits >> 8),
                (byte) (intBits) };
    }

    //byte 쓰레기 값 없애주는 함수..
    public static byte[] garbage_collector(byte[] array){
        int num = 1;
        byte[] answer = {array[0]};
        for(int i = 1; i < 1024; i++){
            if(array[i] != 0){
                num++;
                byte[] buffer = new byte[num];
                byte[] buffer2 = {array[i]};
                System.arraycopy(answer, 0, buffer, 0, answer.length);
                System.arraycopy(buffer2, 0, buffer, answer.length, buffer2.length);
                answer = buffer;
            }else{
                return answer;
            }
        }
        return array;
    }

    public static String[] sort_msg(String str){
        String[] strAry = str.split(",");
        String str1 = strAry[0].substring(1);
        String str2 = strAry[1].substring(0, strAry[1].length() - 1);
        return new String[] {str1, str2};
    }
    public static float[] coordinate_transform_to_dp(float x, float y){
        x = (float) (40*(x+4.5));
        y = (40)*(-1*y+10);
        return new float[] {x, y};
    }

    public static float[] coordinate_transform_from_dp(float x, float y){
        x = (float) (x/40-4.5);
        y = -1*(y/40 - 10);
        return new float[] {x, y};
    }



    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
}