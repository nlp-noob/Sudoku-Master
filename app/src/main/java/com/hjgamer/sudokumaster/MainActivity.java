package com.hjgamer.sudokumaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_begin;
    Button btn_exit;
    Button btn_diff_back;
    Button btn_diff_easy;
    Button btn_diff_medium;
    Button btn_diff_hard;
    Button btn_diff_expert;
    Context context = this;
    Intent startIntent;
    int inMainView = 1;
    char difficulty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startIntent = new Intent(getApplicationContext(),SurfaceViewActivity.class);
        initMainView();
    }

    public void initMainView(){
        btn_begin = (Button)findViewById(R.id.begin_btn);
        btn_exit = (Button)findViewById(R.id.exit_btn);
        View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.exit_btn:
                        finish();
                        break;
                    case R.id.begin_btn:
                        setContentView(R.layout.choose_difficulty);
                        initDiffView();
                        inMainView=0;
                        break;
                }
            }
        };
        btn_begin.setOnClickListener(myOnClickListener);
        btn_exit.setOnClickListener(myOnClickListener);
    }

    public void initDiffView(){
        btn_diff_back = (Button)findViewById(R.id.difficulty_back);
        btn_diff_easy = (Button)findViewById(R.id.difficulty_easy);
        btn_diff_medium = (Button)findViewById(R.id.difficulty_medium);
        btn_diff_hard = (Button)findViewById(R.id.difficulty_hard);
        btn_diff_expert = (Button)findViewById(R.id.difficulty_expert);
        View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.difficulty_back:
                        setContentView(R.layout.activity_main);
                        initMainView();
                        inMainView = 1;
                        break;
                    case R.id.difficulty_easy:
                        difficulty = 'a';
                        startIntent.putExtra("diff",difficulty);
//                        setContentView(new SudokuPanel(context, difficulty));
                        startActivity(startIntent);
                        break;
                    case R.id.difficulty_medium:
                        difficulty = 'b';
                        startIntent.putExtra("diff",difficulty);
//                        setContentView(new SudokuPanel(context, difficulty));
                        startActivity(startIntent);
                        break;
                    case R.id.difficulty_hard:
                        difficulty = 'c';
                        startIntent.putExtra("diff",difficulty);
//                        setContentView(new SudokuPanel(context, difficulty));
                        startActivity(startIntent);
                        break;
                    case R.id.difficulty_expert:
                        difficulty = 'd';
                        startIntent.putExtra("diff",difficulty);
//                        setContentView(new SudokuPanel(context, difficulty));
                        startActivity(startIntent);
                        break;
                }
            }
        };
        btn_diff_back.setOnClickListener(myOnClickListener);
        btn_diff_easy.setOnClickListener(myOnClickListener);
        btn_diff_medium.setOnClickListener(myOnClickListener);
        btn_diff_hard.setOnClickListener(myOnClickListener);
        btn_diff_expert.setOnClickListener(myOnClickListener);
    }


    public void backClickInSurfaceView(){
        setContentView(R.layout.choose_difficulty);
    }
}
