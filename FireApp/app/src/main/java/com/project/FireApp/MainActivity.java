package com.project.FireApp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import pl.polidea.view.ZoomView;

public class MainActivity extends AppCompatActivity {
    TextView r11_red, r11_green, r11_purple, r21, r22, r23, r24;
    TextView time11, time12, time21, time22, time23, time24;
    Switch window1, window2, door1, door2;
    String W1, W2, D1, D2;

    private static final String TAG_fire11 = "fire1", TAG_doppler11 = "doppler1";
    private static final String TAG_time11 = "time";
    private static final String TAG_window1 = "W1", TAG_window2 = "W2", TAG_door1 = "D1", TAG_door2 = "D2";

    private static final String TAG_fire21 = "f1", TAG_fire22 = "f2", TAG_fire23 = "f3", TAG_fire24 = "f4";
    private static final String TAG_time21 = "t1", TAG_time22 = "t2", TAG_time23 = "t3", TAG_time24 = "t4";

    String myJSON1 = null, myJSON2 = null, myJSON3 = null;
    JSONArray signals = null;
    String url1 = "http://155.230.184.64/DB_A1.php";
    String url2 = "http://155.230.184.64/DB_A2.php";
    String url3 = "http://155.230.184.64/DB_A3.php";

    FrameLayout container;
    LinearLayout.LayoutParams layoutParams;
    Button floor1, floor2; TextView c1,c2; //버튼 아래 색
    LayoutInflater inflator;
    View screen1, screen2;
    ZoomView zoomView1, zoomView2;
//    PowerManager powerManager;
//    PowerManager.WakeLock wakeLock; //WakeLock: PowerManager 클래스의 메소드. 앱이 항상 켜져있음을 나타내는 메커니즘

//    private Vibrator vibrator;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.time_refresh:
                time11.setText("time"); time12.setText("time"); time21.setText("time"); time22.setText("time"); time23.setText("time"); time24.setText("time");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//-----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Button button = findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createNotification("101호 화재 발생", "초록색 빛을 따라 신속히 대피하시길 바랍니다.", 0);
//            }
//        });

        //자동 새로고침
        (new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted())
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getData(url1, url2, url3);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        })).start();

        container = findViewById(R.id.container);
        inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        screen1 = inflator.inflate(R.layout.fragment_server1, null);
        screen2 = inflator.inflate(R.layout.fragment_server2, null);

        r11_red = screen1.findViewById(R.id.r11_red);
        r11_green = screen1.findViewById(R.id.r11_green);
        r11_purple = screen1.findViewById(R.id.r11_purple);
        time11 = screen1.findViewById(R.id.time11);
        time12 = screen1.findViewById(R.id.time12);
        window1 = screen1.findViewById(R.id.window11);
        window2 = screen1.findViewById(R.id.window12);
        door1 = screen1.findViewById(R.id.door11);
        door2 = screen1.findViewById(R.id.door12);

        r21 = screen2.findViewById(R.id.r21);
        r22 = screen2.findViewById(R.id.r22);
        r23 = screen2.findViewById(R.id.r23);
        r24 = screen2.findViewById(R.id.r24);
        time21 = screen2.findViewById(R.id.time21);
        time22 = screen2.findViewById(R.id.time22);
        time23 = screen2.findViewById(R.id.time23);
        time24 = screen2.findViewById(R.id.time24);

        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //앱바에 아이콘 표시하기
        //ActionBar ab = getSupportActionBar();
        //ab.setIcon(R.drawable.~);
        //ab.setDisplayUseLogoEnabled(true);
        //ab.setDisplayShowHomeEnabled(true);

        zoomView1 = new ZoomView(MainActivity.this);
        zoomView1.addView(screen1);
        zoomView1.setLayoutParams(layoutParams);
        zoomView1.setMiniMapEnabled(true); //좌측 상단 미니맵
        zoomView1.setMaxZoom(4f); //줌 Max 배율 설정. 1f로 설정 시 줌 안됨
        zoomView1.setMiniMapCaption("Mini Map");  //미니맵 내용
        zoomView1.setMiniMapCaptionSize(20); //미니맵 내용 글씨 크기

        zoomView2 = new ZoomView(MainActivity.this);
        zoomView2.addView(screen2);
        zoomView2.setLayoutParams(layoutParams);
        zoomView2.setMiniMapEnabled(true);
        zoomView2.setMaxZoom(4f);
        zoomView2.setMiniMapCaption("Mini Map");
        zoomView2.setMiniMapCaptionSize(20);

        container.addView(zoomView1);

        ///////1,2층 버튼 코드//////////////////////////////////////////////////////////////////////////////
        c1 = (TextView) findViewById(R.id.c1);
        c2 = (TextView) findViewById(R.id.c2);

        floor1 = (Button) findViewById(R.id.floor1);
        floor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floor1.setTextColor(Color.rgb(220,20,60));
                floor2.setTextColor(Color.rgb(105,105,105));
                c1.setBackgroundColor(Color.rgb(255,000,000));
                c2.setBackgroundColor(Color.rgb(220,220,220));
                container.removeAllViews();
                container.addView(zoomView1);
                getData(url1, url2, url3);
            }
        });

        floor2 = (Button) findViewById(R.id.floor2);
        floor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floor2.setTextColor(Color.rgb(220,20,60));
                floor1.setTextColor(Color.rgb(105,105,105));
                c2.setBackgroundColor(Color.rgb(255,000,000));
                c1.setBackgroundColor(Color.rgb(220,220,220));
                container.removeAllViews();
                container.addView(zoomView2);
                getData(url1, url2, url3);
            }
        });


        /////////Window//////////////////////////////////////////////////////////////////////////////////////////
        window1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked == true) {
                        W1 = "1";
                        Toast.makeText(getApplicationContext(), "창문1을 열었습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        W1 = "0";
                        Toast.makeText(getApplicationContext(), "창문1을 닫았습니다", Toast.LENGTH_SHORT).show();
                    }
                    if (window2.isChecked() == true) W2 = "1";
                    else W2 = "0";
                    if (door1.isChecked() == true) D1 = "1";
                    else D1 = "0";
                    if (door2.isChecked() == true) D2 = "1";
                    else D2 = "0";

                    insertToDatabase(W1, W2, D1, D2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        window2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked == true) {
                        W2 = "1";
                        Toast.makeText(getApplicationContext(), "창문2을 열었습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        W2 = "0";
                        Toast.makeText(getApplicationContext(), "창문2을 닫았습니다", Toast.LENGTH_SHORT).show();
                    }
                    if (window1.isChecked() == true) W1 = "1";
                    else W1 = "0";
                    if (door1.isChecked() == true) D1 = "1";
                    else D1 = "0";
                    if (door2.isChecked() == true) D2 = "1";
                    else D2 = "0";

                    insertToDatabase(W1, W2, D1, D2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        door1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked == true) {
                        D1 = "1";
                        Toast.makeText(getApplicationContext(), "문1을 열었습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        D1 = "0";
                        Toast.makeText(getApplicationContext(), "문1을 닫았습니다", Toast.LENGTH_SHORT).show();
                    }
                    if (window1.isChecked() == true) W1 = "1";
                    else W1 = "0";
                    if (window2.isChecked() == true) W2 = "1";
                    else W2 = "0";
                    if (door2.isChecked() == true) D2 = "1";
                    else D2 = "0";

                    insertToDatabase(W1, W2, D1, D2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        door2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked == true) {
                        D2 = "1";
                        Toast.makeText(getApplicationContext(), "문2을 열었습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        D2 = "0";
                        Toast.makeText(getApplicationContext(), "문2을 닫았습니다", Toast.LENGTH_SHORT).show();
                    }
                    if (window1.isChecked() == true) W1 = "1";
                    else W1 = "0";
                    if (window2.isChecked() == true) W2 = "1";
                    else W2 = "0";
                    if (door1.isChecked() == true) D1 = "1";
                    else D1 = "0";

                    insertToDatabase(W1, W2, D1, D2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


//////////데이터 받고 정보 띄우기/////////////////////////////////////////////////////////////////////////////////////////
    protected void showpic() {
        ArrayList Rooms = new ArrayList();
        ArrayList Roomp = new ArrayList();
        /////////////////////////////////JSON1//////////////////////////////////////////////////////
        if (myJSON1 != null) {
            try {
                signals = new JSONArray(myJSON1);
                Log.d("Main", "JSON size1 : " + signals.length());

                Integer num;
                if (signals.length() > 20) {
                    num = signals.length() - 20;
                } else num = 0;

                for (int i = num; i < signals.length(); i++) {
                    JSONObject c = signals.getJSONObject(i);
                    Integer fire11 = c.getInt(TAG_fire11);
                    Integer dopp11 = c.getInt(TAG_doppler11);
                    String text11 = c.getString(TAG_time11);

                    if(i==signals.length()-1){
                        if(fire11==1)  createNotification("102호 화재 발생", "초록색 빛을 따라 신속히 대피하시길 바랍니다.", 1);
                    }

                    if (dopp11 == 0 && fire11 == 0) {
                        r11_red.setVisibility(View.INVISIBLE);
                        r11_green.setVisibility(View.INVISIBLE);
                        r11_purple.setVisibility(View.INVISIBLE);
                        if (Roomp.contains("Room1") == true) Roomp.remove("Room1");
                        if (Rooms.contains("Room1") == true) Rooms.remove("Room1");
                    } else if (dopp11 == 1 && fire11 == 0) {
                        r11_red.setVisibility(View.INVISIBLE);
                        r11_green.setVisibility(View.VISIBLE);
                        r11_purple.setVisibility(View.INVISIBLE);
                        if (Roomp.contains("Room1") == false) Roomp.add("Room1");
                        if (Rooms.contains("Room1") == true) Rooms.remove("Room1");
                    } else if (dopp11 == 0 && fire11 == 1) {
                        r11_red.setVisibility(View.VISIBLE);
                        r11_green.setVisibility(View.INVISIBLE);
                        r11_purple.setVisibility(View.INVISIBLE);
                        if (Roomp.contains("Room1") == true) Roomp.remove("Room1");
                        if (Rooms.contains("Room1") == false) Rooms.add("Room1");
                        if (time11.getText().length() < 5) {
                            time11.setText(text11);
                        }
                    } else if (dopp11 == 1 && fire11 == 1) {
                        r11_red.setVisibility(View.INVISIBLE);
                        r11_green.setVisibility(View.INVISIBLE);
                        r11_purple.setVisibility(View.VISIBLE);
                        if (Roomp.contains("Room1") == false) Roomp.add("Room1");
                        if (Rooms.contains("Room1") == false) Rooms.add("Room1");
                        if (time11.getText().length() < 5) {
                            time11.setText(text11);
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e("JSON1","Exception");
                e.printStackTrace();
            }
        }

        //////////////////////////////JSON2//////////////////////////////////////////////
        if (myJSON2 != null) {
            try {
                signals = new JSONArray(myJSON2);
                Log.d("Main", "JSON2 size : " + signals.length());

                for (int i = signals.length() - 1; i < signals.length(); i++) {
                    JSONObject c = signals.getJSONObject(i);
                    Integer win1 = c.getInt(TAG_window1);
                    Integer win2 = c.getInt(TAG_window2);
                    Integer doo1 = c.getInt(TAG_door1);
                    Integer doo2 = c.getInt(TAG_door2);

//                room1
                    if (win1 == 1) window1.setChecked(true);
                    else if (win1 == 0) window1.setChecked(false);
                    if (doo1 == 1) door1.setChecked(true);
                    else if (doo1 == 0) door1.setChecked(false);

//                room2
                    if (win2 == 1) window2.setChecked(true);
                    else if (win2 == 0) window2.setChecked(false);
                    if (doo2 == 1) door2.setChecked(true);
                    else if (doo2 == 0) door2.setChecked(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /////////////////////////////JSON3/////////////////////////////////////////////
        if (myJSON3 != null) {
            try {
                signals = new JSONArray(myJSON3);
                Log.d("Main", "JSON3 size : " + signals.length());

                for (int i = 0; i < signals.length(); i++) {
                    JSONObject c = signals.getJSONObject(i);
                    Integer fire21 = c.getInt(TAG_fire21);
                    Integer fire22 = c.getInt(TAG_fire22);
                    Integer fire23 = c.getInt(TAG_fire23);
                    Integer fire24 = c.getInt(TAG_fire24);
                    String text21 = c.getString(TAG_time21);
                    String text22 = c.getString(TAG_time22);
                    String text23 = c.getString(TAG_time23);
                    String text24 = c.getString(TAG_time24);

                    if(i==signals.length()-1){
                        if(fire21==1)    createNotification("201호 화재 발생", "초록색 빛을 따라 신속히 대피하시길 바랍니다.", 3);
                        if(fire22==1)    createNotification("202호 화재 발생", "초록색 빛을 따라 신속히 대피하시길 바랍니다.", 4);
                        if(fire23==1)    createNotification("203호 화재 발생", "초록색 빛을 따라 신속히 대피하시길 바랍니다.", 5);
                        if(fire24==1)    createNotification("204호 화재 발생", "초록색 빛을 따라 신속히 대피하시길 바랍니다.", 6);
                    }

                    if (fire21 == 1) {
                        r21.setVisibility(View.VISIBLE);
                        if(Rooms.contains("Room3")==false) Rooms.add("Room3");
                        if (time21.getText().length() < 5) {
                            time21.setText(text21);
                        }
                    } else if (fire21 == 0) {
                        r21.setVisibility(View.INVISIBLE);
                        if(Rooms.contains("Room3")==true) Rooms.remove("Room3");
                    }

                    if (fire22 == 1) {
                        r22.setVisibility(View.VISIBLE);
                        if(Rooms.contains("Room4")==false) Rooms.add("Room4");
                        if (time22.getText().length() < 5) {
                            time22.setText(text22);
                        }
                    } else if (fire22 == 0) {
                        r22.setVisibility(View.INVISIBLE);
                        if(Rooms.contains("Room4")==true) Rooms.remove("Room4");
                    }

                    if (fire23 == 1) {
                        r23.setVisibility(View.VISIBLE);
                        if(Rooms.contains("Room5")==false) Rooms.add("Room5");
                        if (time23.getText().length() < 5) {
                            time23.setText(text23);
                        }
                    } else if (fire23 == 0) {
                        r23.setVisibility(View.INVISIBLE);
                        if(Rooms.contains("Room5")==true) Rooms.remove("Room5");
                    }

                    if (fire24 == 1) {
                        r24.setVisibility(View.VISIBLE);
                        if(Rooms.contains("Room6")==false) Rooms.add("Room6");
                        if (time24.getText().length() < 5) {
                            time24.setText(text24);
                        }
                    } else if (fire24 == 0) {
                        r24.setVisibility(View.INVISIBLE);
                        if(Rooms.contains("Room6")==true) Rooms.remove("Room6");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(Rooms.isEmpty()==false && Roomp.isEmpty()==true){
            Snackbar snackbar = Snackbar.make(container, Rooms+" 화재 발생", Snackbar.LENGTH_INDEFINITE);
            View sb = snackbar.getView();
            sb.setBackgroundColor(Color.rgb(204, 000, 000));
            snackbar.show();
        }else if(Rooms.isEmpty()==true && Roomp.isEmpty()==false) {
            Snackbar snackbar = Snackbar.make(container,Roomp+" 사람 감지" , Snackbar.LENGTH_INDEFINITE);
            View sb = snackbar.getView();
            sb.setBackgroundColor(Color.rgb(000, 153, 000));
            snackbar.show();
        }else if(Rooms.isEmpty()==true && Roomp.isEmpty()==true) {
            Snackbar snackbar = Snackbar.make(container,  null, Snackbar.LENGTH_INDEFINITE);
            View sb = snackbar.getView();
            sb.setBackgroundColor(Color.TRANSPARENT);
            snackbar.show();
        }else if(Rooms.isEmpty()==false && Roomp.isEmpty()==false){
            Snackbar snackbar = Snackbar.make(container,  Roomp +" 사람 감지\n"+ Rooms + " 화재 발생", Snackbar.LENGTH_INDEFINITE);
            View sb = snackbar.getView();
            sb.setBackgroundColor(Color.rgb(051, 102, 255));
            snackbar.show();
        }
    }

    /////////////////////////WakeLock////////////////////////////////////////////////////////////////////////////////////
//    private void wakeLock() {
//        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); //진동기능. vibrator 객체 정의
//
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); //기본 알림음을 발생시키는 코드
//        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
//
//        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
//                CU와 화면을 밝게하며 키보드는 off 상태.
//                여기서 화면을 살짝 어둡게 켜기 위해서 PowerManager.DIM_WAKE_LOCK을 사용할 수 있다.
//                CPU만 On시키기 위해서는 PARTIAL_WAKE_LOCK을 사용한다.
//                PowerManager.ACQUIRE_CAUSES_WAKEUP |
//                WakeLock에게 조명이 켜지도록 한다.
//                PowerManager.ON_AFTER_RELEASE, "myapp:mywakelocktag");
//                WakeLock이 Relase되고 조명이 오래 유지되도록 한다.
//        vibrator.vibrate(1000); //1초간 진동
//        ringtone.play();    //기본 알림음 발생생
//        wakeLock.acquire(3000); //WakeLock 깨우기(해제하기 전까지 시간 제한)
//        wakeLock.release(); //WakeLock 해제
//    }

//////////////////////////Notification///////////////////////////////////////////////////////////////////////////////////
    private void createNotification(String title, String context, int id) {
        PendingIntent mPendingIntent = PendingIntent.getActivity(this,
                0,
                new Intent(getApplicationContext(),MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.lights)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.fire))
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)  //알람발생 시 진동과 사운드 설정
                .setContentTitle(title)
                .setContentText(context)
                .setAutoCancel(true)
                .setLights(Color.RED, 1000, 1000)
                .setContentIntent(mPendingIntent)
                .setFullScreenIntent(mPendingIntent,true);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("channel_id",
                    "channel_name",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(id, builder.build());        // id: 정의해야하는 각 알림의 고유한 int값
    }

////////////////////////////////getData/////////////////////////////////////////////////////////////////////////////////
    public void getData(String url1, String url2, String url3) {
        class GetDataJSON extends AsyncTask<String, Void, ArrayList<String>> {
            @Override
            protected ArrayList<String> doInBackground(String... params) {
                Log.d("Test", "doinbackground");
                ArrayList<String> returnlist = new ArrayList<String>();
                String uri1 = params[0];
                String uri2 = params[1];
                String uri3 = params[2];

                BufferedReader bufferedReader = null;
                try {
                    URL url1 = new URL(uri1);
                    URL url2 = new URL(uri2);
                    URL url3 = new URL(uri3);
                    String json1, json2, json3;

                    HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con1.getInputStream()));
                    StringBuilder sb1 = new StringBuilder();
                    while ((json1 = bufferedReader.readLine()) != null) {
                        sb1.append(json1 + "\n");
                    }
                    returnlist.add(sb1.toString().trim());

                    HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                    StringBuilder sb2 = new StringBuilder();
                    while ((json2 = bufferedReader.readLine()) != null) {
                        sb2.append(json2 + "\n");
                    }
                    returnlist.add(sb2.toString().trim());

                    HttpURLConnection con3 = (HttpURLConnection) url3.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con3.getInputStream()));
                    StringBuilder sb3 = new StringBuilder();
                    while ((json3 = bufferedReader.readLine()) != null) {
                        sb3.append(json3 + "\n");
                    }
                    returnlist.add(sb3.toString().trim());
                    return returnlist;
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ArrayList<String>();
                }
            }

            @Override
            protected void onPostExecute(ArrayList<String> result){
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
//                Log.d("MySql_result", result);
                myJSON1 = result.get(0);
                myJSON2 = result.get(1);
                myJSON3 = result.get(2);
                showpic();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url1, url2, url3);
    }
////////////////////////////////////send Data//////////////////////////////////////////////////////////
    private void insertToDatabase(String W1, String W2, String D1, String D2) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Loading...", null, true, true);
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String W1 = params[0];
                    String W2 = params[1];
                    String D1 = params[2];
                    String D2 = params[3];

                    String link = "http://155.230.184.64/DB_AW.php";
                    String data = URLEncoder.encode("W1", "UTF-8") + "=" + URLEncoder.encode(W1, "UTF-8");
                    data += "&" + URLEncoder.encode("W2", "UTF-8") + "=" + URLEncoder.encode(W2, "UTF-8");
                    data += "&" + URLEncoder.encode("D1", "UTF-8") + "=" + URLEncoder.encode(D1, "UTF-8");
                    data += "&" + URLEncoder.encode("D2", "UTF-8") + "=" + URLEncoder.encode(D2, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }

        }
        InsertData task = new InsertData();
        task.execute(W1, W2, D1, D2);
        }
}