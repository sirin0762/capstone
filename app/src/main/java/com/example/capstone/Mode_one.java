package com.example.capstone;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import static android.speech.tts.TextToSpeech.ERROR;
import static com.example.capstone.MainActivity.BT_CONNECTING_STATUS;

import com.example.capstone.MainActivity;
import com.example.capstone.MainActivity.ConnectedBluetoothThread;



public class Mode_one extends AppCompatActivity {

    private TextToSpeech tts;
    Button pre_button;
    Button next_button;
    TextView main_textview;

    ConnectedBluetoothThread mThreadConnectedBluetooth;

    String jaum = "ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎ";
    String moum = "ㅏㅑㅓㅕㅗㅛㅜㅠㅡㅣ";

    int index = 0;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_one);


        main_textview = findViewById(R.id.main_textview);
        pre_button = findViewById(R.id.pre_button);
        next_button = findViewById(R.id.next_button);

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

        pre_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 0){
                    index -= 1;
                    tts.speak(String.valueOf(jaum.charAt(index)), TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(String.valueOf(jaum.charAt(index)));
                    main_textview.setTextSize(300);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write(String.valueOf(jaum.charAt(index)));
                    }
                }
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < jaum.length() - 1){
                    index += 1;
                    tts.speak(String.valueOf(jaum.charAt(index)), TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(String.valueOf(jaum.charAt(index)));
                    main_textview.setTextSize(300);
                    if(mThreadConnectedBluetooth != null) {
                        System.out.println("Success");
                        mThreadConnectedBluetooth.write(String.valueOf(jaum.charAt(index)));
                    }else{
                        System.out.println("Fail");
                    }
                }
            }
        });
    }
}