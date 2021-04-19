package com.example.capstone;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.UUID;

import static android.speech.tts.TextToSpeech.ERROR;
import static com.example.capstone.MainActivity.BT_CONNECTING_STATUS;
import static com.example.capstone.MainActivity.BT_MESSAGE_READ;

import com.example.capstone.MainActivity;
import com.example.capstone.MainActivity.ConnectedBluetoothThread;



public class Mode_one extends AppCompatActivity {

    private TextToSpeech tts;
    Button pre_button;
    Button next_button;
    TextView main_textview;
    TextView mTvSendData;


    static Handler mBluetoothHandler;

    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothSocket mBluetoothSocket;

    String jaum = "ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎ";
    String moum = "ㅏㅑㅓㅕㅗㅛㅜㅠㅡㅣ";

    int index = 0;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_one);

        mTvSendData =  (EditText) findViewById(R.id.tvSendData);

        main_textview = findViewById(R.id.main_textview);
        pre_button = findViewById(R.id.pre_button);
        next_button = findViewById(R.id.next_button);

        System.out.print("2 : ");
        System.out.println(MyApplication.device);

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
                        mThreadConnectedBluetooth.write(String.valueOf(jaum.charAt(index)));
                    }
                }
            }
        });
    }

    public class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}