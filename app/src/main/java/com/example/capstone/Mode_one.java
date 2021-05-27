package com.example.capstone;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.UUID;

import static android.speech.tts.TextToSpeech.ERROR;
import com.example.capstone.MyApplication.ConnectedBluetoothThread;



public class Mode_one extends AppCompatActivity {

    private TextToSpeech tts;
    ImageButton cho_pre_button;
    ImageButton cho_next_button;
    TextView main_textview;
    TextView mTvSendData;


    static Handler mBluetoothHandler;

    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothSocket mBluetoothSocket;

    String cho = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
    String cho_send = "가까나다따라마바빠사싸아자짜차카타파하";


    int index = -1;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private void viewButton(int index){
        if(index <= 0){
            cho_pre_button.setVisibility(View.GONE);
        }
        else if(index > 0 && index < cho.length() - 1){
            cho_pre_button.setVisibility(View.VISIBLE);
            cho_next_button.setVisibility(View.VISIBLE);
        }
        else if(index >= cho.length() - 1){
            cho_next_button.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_one);

        mTvSendData =  (EditText) findViewById(R.id.tvSendData);

        main_textview = findViewById(R.id.cho_textview);
        cho_pre_button = findViewById(R.id.cho_pre_button);
        cho_next_button = findViewById(R.id.cho_next_button);

        final Boolean[] isStart = {false};

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

        cho_pre_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 0){
                    index -= 1;
                    viewButton(index);
                    tts.speak(String.valueOf(cho.charAt(index)), TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(String.valueOf(cho.charAt(index)));
                    main_textview.setTextSize(100);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write("1." + String.valueOf(cho_send.charAt(index)));
                    }
                }
            }
        });

        cho_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < cho.length() - 1){
                    index += 1;
                    viewButton(index);
                    tts.speak(String.valueOf(cho.charAt(index)), TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(String.valueOf(cho.charAt(index)));
                    main_textview.setTextSize(100);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write("1." + String.valueOf(cho_send.charAt(index)));
                    }
                }
            }
        });
    }
}