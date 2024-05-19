package com.example.apenadetect.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.apenadetect.ApenaApplication;
import com.example.apenadetect.data.Esp32Response;
import com.example.apenadetect.helper.Helper;
import com.example.apenadetect.ui.home.HomeActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WSClientService extends Service {
    private ApenaDetectWsClient apenaDetectWsClient;
    private Intent homeIntent;
    @Override
    public void onCreate() {
//        Log.d("WSCSJDFSD", "BEGIN ON CREATE");
        super.onCreate();
        try {
            String wsHost = ApenaApplication.WS_HOST;
            int wsPort = ApenaApplication.WS_PORT;
            String url = "ws://" + wsHost + ":" + Integer.toString(wsPort) + "/ws";
            apenaDetectWsClient = new ApenaDetectWsClient(new URI(url));
            apenaDetectWsClient.connect();

            homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);

        } catch (URISyntaxException e) {
            Toast.makeText(this, "KET NOI THAT BAI", Toast.LENGTH_SHORT).show();
//            throw new RuntimeException(e);
        }
//        Log.d("WSCSJDFSD", "FINISH ON CREATE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void playSound(){
        startService(new Intent(this, SoundService.class));

        CountDownTimer countDownTimer;
        long duration = 5000;

        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                stopService(new Intent(WSClientService.this, SoundService.class));

            }
        };

        countDownTimer.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ApenaDetectWsClient extends WebSocketClient{
        public ApenaDetectWsClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d("WSCLIENTFSDF", "WS CLIENT CONNECTED TO SERVER");
        }

        @Override
        public void onMessage(String message) {
            Esp32Response response = Helper.ConvertJson(message, Esp32Response.class);
            Log.d("WSCLIENTFSDF", response.toString());

            Intent intent = new Intent("SEND_NHIP_THO");
            intent.putExtra("nhipTho", response.getNhipTho());
            sendBroadcast(intent);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Toast.makeText(WSClientService.this, "WS CLOSE", Toast.LENGTH_SHORT).show();
            Log.d("WSCLIENTFSDF", reason);

            Intent intent = new Intent("SEND_CLOSE_WS");
            sendBroadcast(intent);
        }

        @Override
        public void onError(Exception ex) {
            Log.d("WSCLIENTFSDF", ex.getMessage());
        }
    }

}
