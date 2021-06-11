package com.example.h26xprojection;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Zach on 2021/6/10 21:47
 */
public class CodecLiveH26X extends Thread {
    private static final String TAG = "CodecLiveH26X";

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

        start();
    }

    public void stopLive() {
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
        }
    }

    @Override
    public void run() {
        mediaCodec.start();

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (true) {

            try {
                int outputBufferId = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
                if (outputBufferId >= 0) {
                    ByteBuffer byteBuffer = mediaCodec.getOutputBuffer(outputBufferId);
                    dealFrame(byteBuffer, bufferInfo);

                    mediaCodec.releaseOutputBuffer(outputBufferId, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private static final int NAL_I = 19;    // I帧
    private static final int NAL_VPS = 32;  // VPS
    private byte[] vps_sps_pps_buf;

    private void dealFrame(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        int offset = 4;
        if (byteBuffer.get(2) == 0x01) {
            offset = 3;
        }

        int type = (byteBuffer.get(offset) & 0x7e) >> 1;
        if (type == NAL_VPS) {
            vps_sps_pps_buf = new byte[bufferInfo.size];
            byteBuffer.get(vps_sps_pps_buf);
        } else if (type == NAL_I) {
            byte[] iframe = new byte[bufferInfo.size];
            byteBuffer.get(iframe);

            Log.i(TAG, "I frame: " + ByteUtil.byte2hex(iframe));
            // 每个I帧前面添加一个vps_sps_pps
            byte[] buf = new byte[vps_sps_pps_buf.length + iframe.length];
            System.arraycopy(vps_sps_pps_buf, 0, buf, 0, vps_sps_pps_buf.length);
            System.arraycopy(iframe, 0, buf, vps_sps_pps_buf.length, iframe.length);
            socketLive.sendData(buf);
        } else {  // P/B帧
            byte[] bytes = new byte[bufferInfo.size];
            byteBuffer.get(bytes);
            socketLive.sendData(bytes);

            Log.i(TAG, "p/b frame: " + ByteUtil.byte2hex(bytes));
        }

    }


}
