package com.example.apenadetect.net;

import android.util.Log;

import com.example.apenadetect.helper.Helper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ApenaDetectWsClient extends WebSocketClient {
    public ApenaDetectWsClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("WSCLIENTFSDF", "WS CLIENT CONNECTED TO SERVER");
    }

    @Override
    public void onMessage(String message) {
        Log.d("WSCLIENTFSDF", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("WSCLIENTFSDF", "WS CLOSE");
    }

    @Override
    public void onError(Exception ex) {
        Log.d("WSCLIENTFSDF", ex.getMessage());
    }
}
