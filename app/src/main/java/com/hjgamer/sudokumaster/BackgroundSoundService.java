package com.hjgamer.sudokumaster;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.Random;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BackgroundSoundService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.hjgamer.sudokumaster.action.FOO";
    private static final String ACTION_BAZ = "com.hjgamer.sudokumaster.action.BAZ";
    // action声明
    public static final String ACTION_MUSIC = "com.hjgamer.sudokumaster.action.music";
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.hjgamer.sudokumaster.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.hjgamer.sudokumaster.extra.PARAM2";

    // 背景音乐列表
    private final int [] raw_id_list = {R.raw.bgm_waltz_in_a_minor, R.raw.bgm_dripdripdrip, R.raw.bgm_morzart, R.raw.bgm_canon, R.raw.bgm_floating_in_the_city, R.raw.bgm_raindrop};
    // 背景音乐所一一对应的时间长度列表
    private final int [] raw_time_lenth = {140000, 402000, 157000, 323000, 474000, 289000};
    private Context context = this;
    // 声明MediaPlayer对象
    private MediaPlayer mediaPlayer;
    // 要结束播放的标志位
    private boolean readyToStop = false;

    public BackgroundSoundService() {
        super("BackgroundSoundService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BackgroundSoundService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BackgroundSoundService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
            // 根据intent设置的action来执行对应服务的操作
            if(ACTION_MUSIC.equals(action)){
                handleActionMusic();
            }
        }
    }

    private void handleActionMusic(){
        if (mediaPlayer == null){
            while (true){
                if (readyToStop){
                    break;
                }
                int rand_msc = getRandomNumberInRange(0, raw_time_lenth.length-1);
                mediaPlayer = MediaPlayer.create(this, raw_id_list[rand_msc]);
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
                sleepingPlayer(raw_time_lenth[rand_msc]);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void sleepingPlayer(int timeLenth){
        try{
            Thread.sleep(timeLenth);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 产生含参数中两个数的随机整数
    private static int getRandomNumberInRange(int min, int max){
        Random r = new Random();
        return r.nextInt((max - min)+1) + min;
    }

    @Override
    public void onDestroy() {
        Log.e("Service","OnDestroy");
        readyToStop = true;
        mediaPlayer.stop();
        stopSelf();
        super.onDestroy();
    }
}
