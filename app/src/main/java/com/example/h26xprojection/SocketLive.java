package com.example.h26xprojection;

import android.media.projection.MediaProjection;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Created by Zach on 2021/6/10 21:43
 */
public class SocketLive {

    private WebSocket webSocket;
    private int port;

    private CodecLiveH26X codecLiveH26X;

    public SocketLive(int port) {
        this.port = port;
    }

    public void start(MediaProjection mediaProjection) {
        webSocketServer.start();

        codecLiveH26X = new CodecLiveH26X(this, mediaProjection);
        codecLiveH26X.startLive();
    }

    public void close() {
    }

    private WebSocketServer webSocketServer = new WebSocketServer(new InetSocketAddress(port)) {
        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {

        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        }

        @Override
        public void onMessage(WebSocket conn, String message) {

        }

        @Override
        public void onError(WebSocket conn, Exception ex) {

        }

        @Override
        public void onStart() {

        }
    };
}
