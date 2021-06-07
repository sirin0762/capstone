package com.example.capstone;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.example.capstone.MyApplication.ConnectedBluetoothThread;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import static com.example.capstone.MyApplication.mBluetoothHandler;


public class Mode_six extends AppCompatActivity {

    private TextToSpeech tts;
    ImageButton six_pre_button;
    ImageButton six_next_button;
    Button answer_button;
    TextView main_textview;
    Intent intent;
    SpeechRecognizer mRecognizer;
    ConnectedBluetoothThread mThreadConnectedBluetooth;

    String[] six = {"토끼", "거북이", "자라", "비버"};
    String word = "";
    String speak_word = "";
    
    int index = -1;
    
    Date d1 = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_six);

        main_textview = findViewById(R.id.six_textview);
        six_pre_button = findViewById(R.id.six_pre_button);
        six_next_button = findViewById(R.id.six_next_button);
        answer_button = findViewById(R.id.answer_button);
        final int PERMISSION = 1;

        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        if ( Build.VERSION.SDK_INT >= 23 ){ ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},PERMISSION); }

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

        answer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecognizer=SpeechRecognizer.createSpeechRecognizer(getBaseContext());
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(intent);

            }
        });
        six_pre_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 0){
                    index -= 1;
                    viewButton(index);
                    tts.speak(six[index], TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(six[index]);
                    main_textview.setTextSize(100);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write("4." + six[index]);
                    }
                }
            }
        });

        six_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < six.length - 1){
                    index += 1;
                    viewButton(index);
                    tts.speak(six[index], TextToSpeech.QUEUE_FLUSH, null);
                    main_textview.setText(six[index]);
                    main_textview.setTextSize(100);
                    if(mThreadConnectedBluetooth != null) {
                        mThreadConnectedBluetooth.write("4." + six[index]);
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
                                main_textview.setText(six[index]);
                                main_textview.setTextSize(100);
                                word = six[index];
                                if(mThreadConnectedBluetooth != null) {
                                    mThreadConnectedBluetooth.write("4." + six[index]);
                                }
                            }
                        }
                        else if(check_integer == 1){
                            Log.i("next", "");
                            if(index < six.length - 1){
                                index += 1;
                                viewButton(index);
                                main_textview.setText(six[index]);
                                main_textview.setTextSize(100);
                                word = six[index];
                                if(mThreadConnectedBluetooth != null) {
                                    mThreadConnectedBluetooth.write("4." + six[index]);
                                }
                            }
                        }
                        else if(check_integer == 2){
                            mRecognizer=SpeechRecognizer.createSpeechRecognizer(getBaseContext());
                            mRecognizer.setRecognitionListener(listener);
                            mRecognizer.startListening(intent);
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
        else if(index > 0 && index < six.length - 1){
            six_pre_button.setVisibility(View.VISIBLE);
            six_next_button.setVisibility(View.VISIBLE);
        }
        else if(index >= six.length - 1){
            six_next_button.setVisibility(View.GONE);
        }

        if (index < 0) {
            answer_button.setVisibility(View.GONE);
        }
        else{
            answer_button.setVisibility(View.VISIBLE);
        }
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "지금부터 말을 해주세요!", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {
            System.out.println("onRmsChanged.........................");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            System.out.println("onBufferReceived.........................");
        }

        @Override
        public void onEndOfSpeech() {
            System.out.println("onEndOfSpeech.........................");
        }

        @Override
        public void onError(int error) {
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default: message = "알 수 없는 오류임";
                break;
            } Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show(); }


        @Override
        public void onPartialResults(Bundle partialResults) {
            System.out.println("onPartialResults.........................");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            System.out.println("onEvent.........................");
        }

        @Override
        public void onResults(Bundle results) {
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            Toast.makeText(getApplicationContext(), rs[0], Toast.LENGTH_SHORT).show();
            speak_word = rs[0];
            
            try{
                Log.i("speak word", speak_word);
                Log.i("word", word);
            }catch (Exception e){

            }

            if(word.equals(speak_word)){
                tts.speak("정답입니다.", TextToSpeech.QUEUE_FLUSH, null);
            }
            else{
                tts.speak("오답입니다.", TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    };
}