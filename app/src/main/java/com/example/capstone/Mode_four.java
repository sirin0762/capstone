package com.example.capstone;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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


public class Mode_four extends AppCompatActivity {

    private TextToSpeech tts;
    ImageButton four_pre_button;
    ImageButton four_next_button;
    TextView main_textview;
    TextView mTvSendData;

    ConnectedBluetoothThread mThreadConnectedBluetooth;

    String word = "길 강아지 가족 곰 군인 눈 냉면 나무 너구리 노래 다람쥐 덧니 도토리 도구 돌 라디오 로봇 라면 리코더 리본" +
            " 모래 마스크 모자 무지개 마차 비누 배 바지 번호 분홍 소시지 시계 시소 색종이 손가락 옷 양파 여우 우유 오이 자두 조개 저고리 지도 지우개" +
            " 차 초가 치마 치즈 체조 코끼리 키위 케이크 카메라 커피 타이어 토끼 타조 톱 태양 포도 피아노 피자 파도 파랑 하마 휴지 혀 해파리 호수";
    String[] words = word.split(" ");

    int length = words.length;

    String temp = "";

    int index = -1;
    int sub_index = -1;
    Date d1 = new Date();

    private String[][] split_words(String[] words){
        String[][] result = new String[length / 5][5];
        for(int i = 0; i < length / 5; i++){
            for(int j = 0 ; j < 5; j++){
                result[i][j] = words[i * 5 + j];
            }
        }

        return result;
    }

    private void viewButton(int index, int length){
        if(index <= 0){
            four_pre_button.setVisibility(View.GONE);
        }
        else if(index >= 0 && index < length - 1){
            four_pre_button.setVisibility(View.VISIBLE);
            four_next_button.setVisibility(View.VISIBLE);
        }
        else if(index >= length - 1){
            four_next_button.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_four);

        mTvSendData =  (EditText) findViewById(R.id.tvSendData);

        main_textview = findViewById(R.id.four_textview);
        four_pre_button = findViewById(R.id.four_pre_button);
        four_next_button = findViewById(R.id.four_next_button);

        final Boolean[] isStart = {false};

        int length = words.length;

        String[][] split_words = split_words(words);

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

        four_pre_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 0){
                    index -= 1;
                    int random = (int)(Math.random() * 5);
                    viewButton(index, split_words.length);
                    tts.speak(split_words[index][random], TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(split_words[index][random]);
                    main_textview.setTextSize(60);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write("0." + split_words[index][random]);
                    }
                }
            }
        });

        four_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < split_words.length - 1){
                    index += 1;
                    int random = (int)(Math.random() * 5);
                    viewButton(index, split_words.length);
                    tts.speak(split_words[index][random], TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(split_words[index][random]);
                    main_textview.setTextSize(60);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write("0." + split_words[index][random]);
                    }
                }
            }
        });

        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MyApplication.BT_MESSAGE_READ){
                    String readMessage = null;
                    String send_w = "";
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
                                int random = (int)(Math.random() * 5);
                                viewButton(index, split_words.length);
                                tts.speak(split_words[index][random], TextToSpeech.QUEUE_FLUSH, null);
                                main_textview.setText(split_words[index][random]);
                                main_textview.setTextSize(60);
                                temp = split_words[index][random];
                                sub_index = -1;
                            }
                        }
                        else if(check_integer == 1){
                            Log.i("next", "");
                            if(index < words.length - 1){
                                index += 1;
                                int random = (int)(Math.random() * 5);
                                viewButton(index, split_words.length);
                                tts.speak(split_words[index][random], TextToSpeech.QUEUE_FLUSH, null);
                                main_textview.setText(split_words[index][random]);
                                main_textview.setTextSize(60);
                                temp = split_words[index][random];
                                sub_index = -1;
                            }
                        }
                        else if(check_integer == 2){
                            Log.i("next_w", "");
                            if(sub_index < temp.length() - 1){
                                sub_index += 1;
                                send_w = String.valueOf(temp.charAt(sub_index));
                                tts.speak(send_w, TextToSpeech.QUEUE_FLUSH, null);
                                main_textview.setText(send_w);
                                main_textview.setTextSize(60);
                                if(mThreadConnectedBluetooth != null) {
                                    mThreadConnectedBluetooth.write("0." + send_w);
                                }
                            }
                        }
                        else if(check_integer == 3){
                            Log.i("pre_w", "");
                            if(sub_index > 0){
                                sub_index -= 1;
                                send_w = String.valueOf(temp.charAt(sub_index));
                                tts.speak(send_w, TextToSpeech.QUEUE_FLUSH, null);
                                main_textview.setText(send_w);
                                main_textview.setTextSize(60);
                                if(mThreadConnectedBluetooth != null) {
                                    mThreadConnectedBluetooth.write("0." + send_w);
                                }
                            }
                        }
                    }
                }
            }
        };
    }
}