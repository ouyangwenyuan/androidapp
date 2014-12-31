package com.kitty.utils;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * 音乐处理类
 */
public class MediaPlayerUtil {

    // 音乐播放器
    private MediaPlayer mp;
    // 当前音量
    private int streamVolume;
    // AudioManager
    private AudioManager mgr;
    // Context
    private Context context;
    // 要播放的音乐资源ＩＤ
    private int resid;
    private static MediaPlayerUtil instance;

    private MediaPlayerUtil(Context context) {
        this.context = context;
        mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // 获得声音设备和设备音量
        streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static MediaPlayerUtil init(Context context) {
        if (instance == null) {
            instance = new MediaPlayerUtil(context);
        }
        return instance;
    }

    public static MediaPlayerUtil getMediaPlayerUtil() {
        return instance;
    }

    public void setMediaRes(int resId) {
        this.resid = resId;
    }

    /**
     * 播放
     */
    public void play() {
        mp = MediaPlayer.create(context, resid);
        mp.setVolume(streamVolume, streamVolume);
        mp.setLooping(true);

        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
    }

    /**
     * 暂停
     */
    public void pause() {
        if (null != mp) {
            mp.pause();
        }
    }

    /**
	 * 
	 */
    public void resume() {
        if (mp == null) {
            return;
        }
        mp.start();
    }

    /**
     * 游戏停止
     */
    public void stop() {
        if (mp == null) {
            return;
        }
        mp.stop();
    }

}
