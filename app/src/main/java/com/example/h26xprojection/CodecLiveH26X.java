package com.example.h26xprojection;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.view.Surface;

import java.io.IOException;

/**
 * Created by Zach on 2021/6/10 21:47
 */
public class CodecLiveH26X extends Thread {

    private MediaCodec mediaCodec;
    private VirtualDisplay virtualDisplay;

    private MediaProjection mediaProjection;
    private SocketLive socketLive;

    private static final int width = 720;
    private static final int height = 1280;

    public CodecLiveH26X(SocketLive socketLive, MediaProjection mediaProjection) {
        this.socketLive = socketLive;
        this.mediaProjection = mediaProjection;
    }

    public void startLive() {
        try {
            MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, width, height);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE, width * height);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            mediaCodec = MediaCodec.createEncoderByType("video/hevc");   //h265
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface surface = mediaCodec.createInputSurface();
            virtualDisplay = mediaProjection.createVirtualDisplay("projection", width, height, 1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        super.run();
    }
}
