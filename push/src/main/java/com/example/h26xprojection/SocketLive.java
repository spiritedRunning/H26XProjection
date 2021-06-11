package com.example.h26xprojection;

import android.media.projection.MediaProjection;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Zach on 2021/6/10 21:43
 */
public class SocketLive {
    private static final String TAG = "SocketLive";

    private WebSocket webSocket;
    private int port;

    private CodecLiveH26X codecLiveH26X;

    public SocketLive(int port) {
        this.port = port;
    }

    public void start(MediaProjection mediaProjection) {
        Log.i(TAG, "port: " + port);
        webSocketServer.start();


        codecLiveH26X = new CodecLiveH26X(this, mediaProjection);
        codecLiveH26X.startLive();
    }

    public void close() {
        codecLiveH26X.stopLive();

        try {
            if (webSocket != null && webSocketServer != null) {
                Log.i(TAG, "stop websocket");
                webSocket.close();
                webSocketServer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] bytes) {
        if (webSocket != null && webSocket.isOpen()) {
//            Log.e(TAG, "send data: " + DataUtil.byte2hex(bytes));
            webSocket.send(bytes);
        }
    }

    private WebSocketServer webSocketServer = new WebSocketServer(new InetSocketAddress(12005)) { // important! 这里直接写端口号client才连的上。 写port不行。 原因不明
        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            SocketLive.this.webSocket = conn;
            Log.i(TAG, "onOpen");
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            Log.i(TAG, "websocket close, reason: " + reason);
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            Log.d(TAG, "onMessage message: " + message);
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            Log.i(TAG, "websocket onError: " + ex.toString());
        }

        @Override
        public void onStart() {

        }
    };
}
