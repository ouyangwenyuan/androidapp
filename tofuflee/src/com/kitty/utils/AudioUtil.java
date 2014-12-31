package com.kitty.utils;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.KeyEvent;

import com.kitty.tofuflee.R;

/**
 * 音效处理类
 * 
 * @author Administrator
 * 
 */
public class AudioUtil {
    // 音效：跳的更高
    public static final int SOUND_HIJUMP = 0;
    // 音效：踩断
    public static final int SOUND_CRUMBLE = 1;
    // 音效：在弹簧上跳
    public static final int SOUND_PICKUP = 2;
    // 音效：失败
    public static final int SOUND_LOSE = 3;
    // 音效：胜利
    public static final int SOUND_FANFARE = 4;
    // 声音资源ID
    private static final int[] soundIds = { R.raw.hijump, R.raw.crumble, R.raw.pickup, R.raw.lose, R.raw.fanfare };
    // 上下文环境
    private Context context;
    // AudioManager
    private AudioManager mgr;
    // 播放声音的SoundPool对象
    private SoundPool soundPool;
    // 声音资源集合
    private HashMap<Integer, Integer> soundPoolMap;
    // 当前音量
    private int streamVolume;
    private static AudioUtil instance;

    private AudioUtil(Context context) {
        this.context = context;
        mgr = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
        initSounds();
    }

    public static final AudioUtil init(Context context) {
        if (instance == null) {
            instance = new AudioUtil(context);
        }
        return instance;
    }

    public final static AudioUtil getAudioUtil() {
        return instance;
    }

    /**
     * 初始化音效
     */
    private void initSounds() {
        // 初始化soundPool对象,第一个参数是允许有多少个声音同时播放，第二个参数是声音类型,第三个是声音的品质
        soundPool = new SoundPool(soundIds.length, AudioManager.STREAM_MUSIC, 100);
        // 初始化ＨＡＳＨ表
        soundPoolMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < soundIds.length; i++) {
            loadSfx(soundIds[i], i);
        }
        // 获得声音设备和设备音量
        streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void loadSfx(int resId, int id) {
        // 把资源中的音效加载到指定的ID
        soundPoolMap.put(id, soundPool.load(context, resId, 1));
    }

    /**
     * 播放声音资源
     * 
     * @param id soundPool中的资源ID
     * @param loop 循环次数
     */
    public void play(int id, int loop) {
        soundPool.play(soundPoolMap.get(id), streamVolume, streamVolume, 1, loop, 1f);
    }

    /**
     * 根据音量键调节媒体音量大小
     * 
     * @param keyCode
     */
    public void dokeyDown(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                break;
            default:
        }
    }

}
