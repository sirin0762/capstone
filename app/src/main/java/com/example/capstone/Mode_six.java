package com.example.capstone;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone.MyApplication.ConnectedBluetoothThread;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;
import static com.example.capstone.MyApplication.mBluetoothHandler;


public class Mode_six extends AppCompatActivity {

    private TextToSpeech tts;
    ImageButton six_pre_button;
    ImageButton six_next_button;
    TextView main_textview;
    
    ConnectedBluetoothThread mThreadConnectedBluetooth;

    String six = "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";
    String six_send = "각갂갃간갅갆갇갈갉갊갋갌갍갏감갑값갓갔강갖갗갘같갚갛";
    
    int index = -1;
    
    Date d1 = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_six);

        main_textview = findViewById(R.id.six_textview);
        six_pre_button = findViewById(R.id.six_pre_button);
        six_next_button = findViewById(R.id.six_next_button);


        try {
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(MyApplication.mBluetoothSocket);
            mThreadConnectedBluetooth.start();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }

        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        tts.setSpeechRate(0.75f);

        six_pre_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 0){
                    index -= 1;
                    viewButton(index);
                    tts.speak(String.valueOf(six.charAt(index)), TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(String.valueOf(six.charAt(index)));
                    main_textview.setTextSize(100);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write("3." + String.valueOf(six_send.charAt(index)));
                    }
                }
            }
        });

        six_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < six.length() - 1){
                    index += 1;
                    viewButton(index);
                    tts.speak(String.valueOf(six.charAt(index)), TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(String.valueOf(six.charAt(index)));
                    main_textview.setTextSize(100);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write("3." + String.valueOf(six_send.charAt(index)));
                    }
                }
            }
        });

        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MyApplication.BT_MESSAGE_READ){
                    String readMessage = null;
                    Date d2 = new Date();
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    char check = readMessage.replace("/r", "").charAt(0);
                    long sec = (d2.getTime() - d1.getTime()) / 1000;
                    d1 = d2;

                    if(sec > 0){
                        int check_integer = 100;
                        try{
                            check_integer = Integer.parseInt(String.valueOf(check));
                            Log.i("check_integer", String.valueOf(check_integer));
                        }
                        catch (Exception e){
                            //
                        }
                        if(check_integer  == 0){
                            Log.i("pre", "");
                            if(index > 0){
                                index -= 1;
                                viewButton(index);
                                tts.speak(String.valueOf(six.charAt(index)), TextToSpeech.QUEUE_FLUSH, null);
                                main_textview.setText(String.valueOf(six.charAt(index)));
                                main_textview.setTextSize(100);
                                if(mThreadConnectedBluetooth != null) {
                                    mThreadConnectedBluetooth.write("1." + String.valueOf(six_send.charAt(index)));
                                }
                            }
                        }
                        else if(check_integer == 1){
                            Log.i("next", "");
                            if(index < six.length() - 1){
                                index += 1;
                                viewButton(index);
                                tts.speak(String.valueOf(six.charAt(index)), TextToSpeech.QUEUE_FLUSH, null);
                                main_textview.setText(String.valueOf(six.charAt(index)));
                                main_textview.setTextSize(100);
                                if(mThreadConnectedBluetooth != null) {
                                    mThreadConnectedBluetooth.write("1." + String.valueOf(six_send.charAt(index)));
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private void viewButton(int index){
        if(index <= 0){
            six_pre_button.setVisibility(View.GONE);
        }
        else if(index > 0 && index < six.length() - 1){
            six_pre_button.setVisibility(View.VISIBLE);
            six_next_button.setVisibility(View.VISIBLE);
        }
        else if(index >= six.length() - 1){
            six_next_button.setVisibility(View.GONE);
        }
    }
}