package com.example.capstone;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone.MyApplication.ConnectedBluetoothThread;

import java.util.Locale;
import java.util.UUID;

import static android.speech.tts.TextToSpeech.ERROR;


public class Mode_three extends AppCompatActivity {

    private TextToSpeech tts;
    ImageButton jong_pre_button;
    ImageButton jong_next_button;
    TextView main_textview;



    ConnectedBluetoothThread mThreadConnectedBluetooth;

    String jong = "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";
    String jong_send = "각갂갃간갅갆갇갈갉갊갋갌갍갏감갑값갓갔강갖갗갘같갚갛";
    
    int index = -1;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_three);

        main_textview = findViewById(R.id.jong_textview);
        jong_pre_button = findViewById(R.id.jong_pre_button);
        jong_next_button = findViewById(R.id.jong_next_button);


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

        jong_pre_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 0){
                    index -= 1;
                    viewButton(index);
                    tts.speak(String.valueOf(jong.charAt(index)), TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(String.valueOf(jong.charAt(index)));
                    main_textview.setTextSize(100);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write("3." + String.valueOf(jong_send.charAt(index)));
                    }
                }
            }
        });

        jong_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < jong.length() - 1){
                    index += 1;
                    viewButton(index);
                    tts.speak(String.valueOf(jong.charAt(index)), TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(String.valueOf(jong.charAt(index)));
                    main_textview.setTextSize(100);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write("3." + String.valueOf(jong_send.charAt(index)));
                    }
                }
            }
        });
    }

    private void viewButton(int index){
        if(index <= 0){
            jong_pre_button.setVisibility(View.GONE);
        }
        else if(index > 0 && index < jong.length() - 1){
            jong_pre_button.setVisibility(View.VISIBLE);
            jong_next_button.setVisibility(View.VISIBLE);
        }
        else if(index >= jong.length() - 1){
            jong_next_button.setVisibility(View.GONE);
        }
    }
}