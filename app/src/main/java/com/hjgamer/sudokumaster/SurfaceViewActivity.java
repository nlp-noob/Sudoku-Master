package com.hjgamer.sudokumaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SurfaceViewActivity extends AppCompatActivity {

    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        char difficulty = getIntent().getCharExtra("diff",'a');
        setContentView(new SudokuPanel(context, difficulty));
    }

    @Override
    public void finish() {
        Intent intent_music;
        intent_music = new Intent(context, BackgroundSoundService.class);
        String action = BackgroundSoundService.ACTION_MUSIC;
        intent_music.setAction(action);// 注册音乐服务
        this.stopService(intent_music);
        super.finish();
    }
}
