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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;


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
    EditText x_edit;
    EditText y_edit;
    //안드로이드의 dp값은 360dp, 640dp
    //이 폰은 1080px, 1920px이므로 3배수.
    private static Socket socket;
    private static ObjectOutputStream outstream;
    private static ObjectInputStream instream;
    private static InputStream is;
    String ip;
    Handler handler = new Handler();
    int option = -1;
    String input;
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
        Log.d("My Ip Address is ", getLocalIpAddress());
        //EditText 리스너인데 작동을 안하네..
        x_edit = (EditText) findViewById(R.id.X_EditText);
        y_edit = (EditText) findViewById(R.id.Y_EditText);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_On) {
            option = 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket_send("s");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            System.out.println("option Changed into: " + option);
            return true;
        } else if (item.getItemId() == R.id.action_Off) {
            option = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket_send("f");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            System.out.println("option Changed into: " + option);
            return true;
        }
        return false;
    }
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


    //가로방향
    public void ToTheRight(View view) {
        //width = view.getMeasuredWidth();
        //height = view.getMeasuredHeight();


        //120dp 즉 360픽셀 씩 이동
        LocationX = LocationX + 120;
        image_move(LocationX, LocationY);
        x_edit.setText(String.valueOf(LocationX));
        y_edit.setText(String.valueOf(LocationY));

    }
    //가로방향
    public void ToTheLeft(View view) {

        //120dp 즉 360픽셀 씩 이동
        LocationX = LocationX - 120;
        image_move(LocationX, LocationY);
        x_edit.setText(String.valueOf(LocationX));
        y_edit.setText(String.valueOf(LocationY));
    }

    //세로방향
    public void GoDown(View view) {


        //120dp 즉 360픽셀 씩 이동
        LocationY = LocationY + 120;
        image_move(LocationX, LocationY);
        x_edit.setText(String.valueOf(LocationX));
        y_edit.setText(String.valueOf(LocationY));
    }
    //세로방향
    public void GoUp(View view) {


        //120dp 즉 360픽셀 씩 이동
        LocationY = LocationY - 120;
        image_move(LocationX, LocationY);
        x_edit.setText(String.valueOf(LocationX));
        y_edit.setText(String.valueOf(LocationY));
    }

    //회전
    public void showRotate(View view) {
        last_angle = angle_to_turn;
        angle_to_turn = angle_to_turn + 90;
        image_rotate(last_angle, angle_to_turn);
        x_edit.setText(String.valueOf(LocationX));
        y_edit.setText(String.valueOf(LocationY));
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

    //좌표로 이동
    public void MoveTo(View view) {
        x_edit.setText(String.valueOf(LocationX));
        y_edit.setText(String.valueOf(LocationY));
        image_move(LocationX, LocationY);
        System.out.println("Ip is: " + ip);
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
        private static final String TAG = "ExampleThread";

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
                while(true){
                    if (option == -1) {
                        socket_receive();
//                        //스레드 안에서 UI 접근 -> 핸들러
//                        handler.post(new Runnable() {
//                            @Override public void run() {
//                                System.out.println(input);
//                            }
//                        });

                    }else if(option == 1){
                        option = -1;
                    }else if(option == 0){
                        option = -1;
                    }
                    Thread.sleep(100);
                }
            }catch(Exception e){ e.printStackTrace();}
        }
    }

    public void socket_open() throws IOException {
        String host = "192.168.0.9";
        int port = 5050;
        socket = new Socket(host, port);
        is = socket.getInputStream();
        outstream = new ObjectOutputStream(socket.getOutputStream());
        //서버로 데이터 주기
        System.out.println("연결 완료");
    }
    
    //받는 곳
    public void socket_receive() throws IOException, ClassNotFoundException {
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

     }

    //보내는 곳
    public void socket_send(String msg_to_send) throws IOException{
        //서버에서부터 데이터 받기
//        byte[] byteArr = null;
//        byteArr = msg_to_send.getBytes(StandardCharsets.UTF_8);
//        outstream.write(byteArr);

        outstream.writeObject(msg_to_send);
        outstream.flush();
        System.out.println("보낸 데이터 :" + msg_to_send);


    }

    class ClientThread2 extends Thread{
        public void run(){
            String host = "192.168.0.9";
            int port = 5050;
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

}