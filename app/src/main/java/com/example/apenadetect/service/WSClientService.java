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
import com.example.apenadetect.data.BreathDatabase;
import com.example.apenadetect.data.Breathing;
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
    BreathDatabase breathDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        // Init database
        breathDatabase = new BreathDatabase(this);
        //Start ws client
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
        }
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
            /*
            * save breathing to database
            * */
            Breathing breathing = Breathing.builder()
                    .breathRate(response.getNhipTho())
                    .timestamp(System.currentTimeMillis() / 1000)
                    .build();
            WSClientService.this.breathDatabase.insertBreathData(breathing);
            /*
            * send breathing data to all activity
            * */
            Intent intent = new Intent("SEND_NHIP_THO");
            intent.putExtra("nhipTho", breathing);
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
            Toast.makeText(WSClientService.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
