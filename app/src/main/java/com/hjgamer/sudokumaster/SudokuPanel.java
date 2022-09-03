package com.hjgamer.sudokumaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SudokuPanel extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    Context context;
    // 难度选择
    char diff_chosen;
    private SurfaceHolder mySurfaceHolder;
    // 画布
    private Canvas myCanvas;
    // 子线程标志位
    private boolean isDrawing;
    // 按下的坐标
    private float x_index, y_index;
    // 背景图片
    private Bitmap backgroudPic;
    // 数模画板
    private Bitmap sudokuPanel;
    // 数字图片
    public Bitmap path_1;
    public Bitmap path_2;
    public Bitmap path_3;
    public Bitmap path_4;
    public Bitmap path_5;
    public Bitmap path_6;
    public Bitmap path_7;
    public Bitmap path_8;
    public Bitmap path_9;
    // 按下之后显示的背景
    public Bitmap press_background;
    // 错误提示背景
    public Bitmap error_backgroud;
    // 还没有按下的方块
    public Bitmap unPress_background;
    // 更新完单个背景之后需要把网格重新覆盖一遍，所以需要网格路径
    public Bitmap board_path;
    // 玩家填入的数字图片
    public Bitmap path_py1;
    public Bitmap path_py2;
    public Bitmap path_py3;
    public Bitmap path_py4;
    public Bitmap path_py5;
    public Bitmap path_py6;
    public Bitmap path_py7;
    public Bitmap path_py8;
    public Bitmap path_py9;

    // 返回按键图片
    public Bitmap back_button;
    protected float backButtonLeft;
    protected float backButtonTop;
    // 擦除按键图片
    public Bitmap clear_button;
    protected float clearButtonLeft;
    protected float clearButtonTop;
    // 背景音乐按键图片
    public Bitmap music_button;
    protected float musicButtonLeft;
    protected float musicButtonTop;
    // 屏幕长宽
    protected float screen_width;
    protected float screen_height;
    protected float scalex;
    protected float scaley;
    protected float bg_y;
    protected float bg_y2;
    protected float panelLeft;
    protected float panelTop;
    // 选择数字版
    protected float choosePanelLeft;
    protected float choosePanelTop;
    // 在绘图中，一个格子的宽度与高度
    protected float widthAUnit;
    protected float heightAUnit;
    // 当前题目的序号
    public int puzzleNo;
    // 当前题目的数组
    public int[][] current_pz =
                   {{0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},};
    // 当前题目答案
    public int[][] current_ans =
                   {{0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},};
    // 当前答题的矩阵
    public int[][] current_input =
                   {{0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},};
    // 相对应的表名与难度等级
    String puzzle_table;
    String answer_table;
    int diff_level;

    // 上次选择的填数坐标(-1代表尚未选择)
    int i_index = -1;
    int j_index = -1;
    // 填入数字具有明显不合法的坐标(横->竖->九宫格->填入数字)
    // 后面增加的3*7=21
    int illegal_index_i [] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    int illegal_index_j [] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    // 获取数据库对象
    DBManager manager;
    SQLiteDatabase db;

    // 数据加载完成的标志位
    int dbIsReady = 0;
    // 标志音乐正在播放
    boolean musicIsStarted = false;
    // 音乐播放的服务
    private Intent intent_music;

    public SudokuPanel(Context context, char diff_chosen){
        super(context);
        this.context = context;
        this.diff_chosen = diff_chosen;
        init();
    }

    // 初始化
    private void init(){
        mySurfaceHolder = getHolder(); // 得到SurfaceHolder对象
        mySurfaceHolder.addCallback(this); // 注册SurfaceHolder
        intent_music = new Intent(context, BackgroundSoundService.class);
        String action = BackgroundSoundService.ACTION_MUSIC;
        intent_music.setAction(action);// 注册音乐服务
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true); // 保持屏幕常亮
        initTableNameAndDiff();
        loadDB();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        isDrawing = true;
        screen_width = this.getWidth();
        screen_height = this.getHeight();
        // 初始化图片资源
        initBitmap();
        Log.e("surfaceCreated","--"+isDrawing);
        // 绘制线程
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        isDrawing = false;
        Log.e("surfaceDestroyed","--"+isDrawing);
    }

    @Override
    public void run() {
        Log.e("drawing","--"+111111);
        while (true){
            drawing();
        }

        //        while (isDrawing){
//            drawing();
//        }
    }
    private void drawing(){
        try{
            myCanvas = mySurfaceHolder.lockCanvas();
            myCanvas.drawColor(Color.BLACK); // 绘制背景色
            // 保存之前绘图的状态，防止这个scale操作影响后续的绘图
            myCanvas.save();
            // 计算背景图片与屏幕的比例
            myCanvas.scale(scalex, scaley,0,0);
            myCanvas.drawBitmap(backgroudPic,0,bg_y,null);
            myCanvas.restore();
//            drawInScale(sudokuPanel, (float) 0.05, (float) 0.05, sudokuPanel.getWidth(), sudokuPanel.getHeight());
            // 绘制答题板
            myCanvas.drawBitmap(sudokuPanel,panelLeft,panelTop,null);
            // 绘制选择数字面板
            myCanvas.drawBitmap(unPress_background,choosePanelLeft-4*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(path_1,choosePanelLeft-4*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(unPress_background,choosePanelLeft-3*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(path_2,choosePanelLeft-3*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(unPress_background,choosePanelLeft-2*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(path_3,choosePanelLeft-2*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(unPress_background,choosePanelLeft-1*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(path_4,choosePanelLeft-1*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(unPress_background,choosePanelLeft-0*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(path_5,choosePanelLeft-0*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(unPress_background,choosePanelLeft+1*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(path_6,choosePanelLeft+1*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(unPress_background,choosePanelLeft+2*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(path_7,choosePanelLeft+2*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(unPress_background,choosePanelLeft+3*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(path_8,choosePanelLeft+3*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(unPress_background,choosePanelLeft+4*unPress_background.getWidth(),choosePanelTop,null);
            myCanvas.drawBitmap(path_9,choosePanelLeft+4*unPress_background.getWidth(),choosePanelTop,null);
            // 返回按钮
            myCanvas.drawBitmap(back_button, backButtonLeft, backButtonTop,null);
            // 擦除按钮
            myCanvas.drawBitmap(clear_button, clearButtonLeft, clearButtonTop,null);
            // 音乐按钮
            myCanvas.drawBitmap(music_button, musicButtonLeft, musicButtonTop,null);
            // 相应点击事件 1 数板和选数板外部 2 数板内部 3 选数板内部 4 返回按键 5 擦除按键 6 音乐按键
            switch (clickArea(x_index,y_index)){
                case 1:
                    // 点到其他地方都需要把选择坐标更新为没有选择
                    i_index = -1;
                    j_index = -1;
                    break;
                case 2:
                    drawChooseState();
                    break;
                case 3:
                    fillInNumber();
                    // 绘制非法背景提示
                    drawIllegalSign();
                    // 判断是否过关
                    finishCheck();
                    break;
                case 4:
                    ((Activity)context).finish();
                    break;
                case 5:
                    clearFromClearButton();
                    break;
                case 6:
                    // 为了避免此处的重复执行应该把以下代码移植到点击事件检测的UP动作中，保证开启一次
//                    startOrStopMusic();
//                    musicIsStarted = !musicIsStarted;
                    break;
            }
            // 数字位于最上层，所以应该最后进行绘制
            if (dbIsReady==1){
                drawCurrentPuzzle();
                drawCurrentInput();
                // 清除与题目重叠的数字
                clearCurrentInputInPZ();
            }
            // 控制帧率
            Thread.sleep(33);
            // 在这里进行内容绘制
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (myCanvas != null){
                mySurfaceHolder.unlockCanvasAndPost(myCanvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x_index = x;
                y_index = y;
                break;
            case MotionEvent.ACTION_MOVE:
                x_index = x;
                y_index = y;
                break;
            case MotionEvent.ACTION_UP:
                // 防止快速点击，使用一个单独事件来标记才是合理的
                if (!ClickUtil.isFastClick()){
                    Log.e("ClickEvent","isFastClick");
                    return true;
                }
                Log.e("ClickEvent","isNotFastClick");
                if (clickArea(x,y)==6){
                    startOrStopMusic();
                    musicIsStarted = !musicIsStarted;
                }else {
                    x_index = x;
                    y_index = y;
                }

        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if(wSpecMode == MeasureSpec.AT_MOST && hSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(300, 300);
        } else if(wSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(300, hSpecSize);
        } else if(hSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(wSpecSize, 300);
        }
    }

    // 初始化图片资源以及计算相关屏幕比例
    public void initBitmap(){
        backgroudPic = BitmapFactory.decodeResource(getResources(), R.drawable.main_background);
        // 除了背景之外，别的图片都需要计算相应的合适屏幕的尺寸
        Bitmap oldSudokuPanel = BitmapFactory.decodeResource(getResources(), R.drawable.sudoku_board);
        Bitmap oldPath_1 = BitmapFactory.decodeResource(getResources(), R.drawable.path1);
        Bitmap oldPath_2 = BitmapFactory.decodeResource(getResources(), R.drawable.path2);
        Bitmap oldPath_3 = BitmapFactory.decodeResource(getResources(), R.drawable.path3);
        Bitmap oldPath_4 = BitmapFactory.decodeResource(getResources(), R.drawable.path4);
        Bitmap oldPath_5 = BitmapFactory.decodeResource(getResources(), R.drawable.path5);
        Bitmap oldPath_6 = BitmapFactory.decodeResource(getResources(), R.drawable.path6);
        Bitmap oldPath_7 = BitmapFactory.decodeResource(getResources(), R.drawable.path7);
        Bitmap oldPath_8 = BitmapFactory.decodeResource(getResources(), R.drawable.path8);
        Bitmap oldPath_9 = BitmapFactory.decodeResource(getResources(), R.drawable.path9);
        Bitmap oldPress_background = BitmapFactory.decodeResource(getResources(),R.drawable.press_backgroud);
        Bitmap oldError_background = BitmapFactory.decodeResource(getResources(),R.drawable.error_backgroud);
        Bitmap oldunPress_background = BitmapFactory.decodeResource(getResources(),R.drawable.unpress_backgroud);
        Bitmap oldboard_path = BitmapFactory.decodeResource(getResources(), R.drawable.board_path);
        Bitmap oldPath_py1 = BitmapFactory.decodeResource(getResources(), R.drawable.path1_py);
        Bitmap oldPath_py2 = BitmapFactory.decodeResource(getResources(), R.drawable.path2_py);
        Bitmap oldPath_py3 = BitmapFactory.decodeResource(getResources(), R.drawable.path3_py);
        Bitmap oldPath_py4 = BitmapFactory.decodeResource(getResources(), R.drawable.path4_py);
        Bitmap oldPath_py5 = BitmapFactory.decodeResource(getResources(), R.drawable.path5_py);
        Bitmap oldPath_py6 = BitmapFactory.decodeResource(getResources(), R.drawable.path6_py);
        Bitmap oldPath_py7 = BitmapFactory.decodeResource(getResources(), R.drawable.path7_py);
        Bitmap oldPath_py8 = BitmapFactory.decodeResource(getResources(), R.drawable.path8_py);
        Bitmap oldPath_py9 = BitmapFactory.decodeResource(getResources(), R.drawable.path9_py);
        Bitmap oldBack_btn = BitmapFactory.decodeResource(getResources(), R.drawable.back_button);
        Bitmap oldClear_btn = BitmapFactory.decodeResource(getResources(), R.drawable.clear_button);
        Bitmap oldMusic_btn = BitmapFactory.decodeResource(getResources(), R.drawable.music_button);
        // 按照百分比进行缩放
        float scale_btn = (float)(screen_height*0.1/oldBack_btn.getHeight());
        float scale_btn2 = (float)(screen_width*0.4/oldClear_btn.getWidth());
        Matrix btn_matrix = new Matrix();
        Matrix btn_matrix2 = new Matrix();
        btn_matrix.postScale(scale_btn,scale_btn);
        btn_matrix2.postScale(scale_btn2,scale_btn2);
        float scaleWidth = (float)(screen_width * 0.95 / oldSudokuPanel.getWidth());
        float scaleHeight = scaleWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        sudokuPanel = Bitmap.createBitmap(oldSudokuPanel, 0, 0,oldSudokuPanel.getWidth() ,oldSudokuPanel.getHeight() ,matrix, true);
        path_1 = Bitmap.createBitmap(oldPath_1, 0,0,oldPath_1.getWidth(),oldPath_1.getHeight(), matrix, true);
        path_2 = Bitmap.createBitmap(oldPath_2, 0,0,oldPath_2.getWidth(),oldPath_2.getHeight(), matrix, true);
        path_3 = Bitmap.createBitmap(oldPath_3, 0,0,oldPath_3.getWidth(),oldPath_3.getHeight(), matrix, true);
        path_4 = Bitmap.createBitmap(oldPath_4, 0,0,oldPath_4.getWidth(),oldPath_4.getHeight(), matrix, true);
        path_5 = Bitmap.createBitmap(oldPath_5, 0,0,oldPath_5.getWidth(),oldPath_5.getHeight(), matrix, true);
        path_6 = Bitmap.createBitmap(oldPath_6, 0,0,oldPath_6.getWidth(),oldPath_6.getHeight(), matrix, true);
        path_7 = Bitmap.createBitmap(oldPath_7, 0,0,oldPath_7.getWidth(),oldPath_7.getHeight(), matrix, true);
        path_8 = Bitmap.createBitmap(oldPath_8, 0,0,oldPath_8.getWidth(),oldPath_8.getHeight(), matrix, true);
        path_9 = Bitmap.createBitmap(oldPath_9, 0,0,oldPath_9.getWidth(),oldPath_9.getHeight(), matrix, true);
        press_background = Bitmap.createBitmap(oldPress_background, 0,0,oldPress_background.getWidth(),oldPress_background.getHeight(), matrix, true);
        error_backgroud = Bitmap.createBitmap(oldError_background, 0,0,oldError_background.getWidth(),oldError_background.getHeight(), matrix, true);
        unPress_background = Bitmap.createBitmap(oldunPress_background, 0,0,oldunPress_background.getWidth(),oldunPress_background.getHeight(), matrix, true);
        board_path = Bitmap.createBitmap(oldboard_path, 0,0,oldboard_path.getWidth(),oldboard_path.getHeight(), matrix, true);
        path_py1 = Bitmap.createBitmap(oldPath_py1, 0,0,oldPath_py1.getWidth(),oldPath_py1.getHeight(), matrix, true);
        path_py2 = Bitmap.createBitmap(oldPath_py2, 0,0,oldPath_py2.getWidth(),oldPath_py2.getHeight(), matrix, true);
        path_py3 = Bitmap.createBitmap(oldPath_py3, 0,0,oldPath_py3.getWidth(),oldPath_py3.getHeight(), matrix, true);
        path_py4 = Bitmap.createBitmap(oldPath_py4, 0,0,oldPath_py4.getWidth(),oldPath_py4.getHeight(), matrix, true);
        path_py5 = Bitmap.createBitmap(oldPath_py5, 0,0,oldPath_py5.getWidth(),oldPath_py5.getHeight(), matrix, true);
        path_py6 = Bitmap.createBitmap(oldPath_py6, 0,0,oldPath_py6.getWidth(),oldPath_py6.getHeight(), matrix, true);
        path_py7 = Bitmap.createBitmap(oldPath_py7, 0,0,oldPath_py7.getWidth(),oldPath_py7.getHeight(), matrix, true);
        path_py8 = Bitmap.createBitmap(oldPath_py8, 0,0,oldPath_py8.getWidth(),oldPath_py8.getHeight(), matrix, true);
        path_py9 = Bitmap.createBitmap(oldPath_py9, 0,0,oldPath_py9.getWidth(),oldPath_py9.getHeight(), matrix, true);
        back_button = Bitmap.createBitmap(oldBack_btn, 0, 0, oldBack_btn.getWidth(),oldBack_btn.getHeight(),btn_matrix,true);
        clear_button = Bitmap.createBitmap(oldClear_btn, 0, 0, oldClear_btn.getWidth(),oldClear_btn.getHeight(),btn_matrix2,true);
        music_button = Bitmap.createBitmap(oldMusic_btn, 0, 0, oldMusic_btn.getWidth(),oldMusic_btn.getHeight(),btn_matrix,true);
        scalex = screen_width / backgroudPic.getWidth();
        scaley = screen_height / backgroudPic.getHeight();
        bg_y = 0;
        bg_y2 = bg_y - screen_height;
        panelLeft = (screen_width- sudokuPanel.getWidth())/2;
        panelTop = (screen_height - sudokuPanel.getHeight())/5;
        choosePanelLeft = (screen_width - unPress_background.getWidth())/2;
        choosePanelTop = (panelTop+10*unPress_background.getHeight());
        widthAUnit = (screen_width-2*panelLeft)/9;
        heightAUnit = (screen_height-5*panelTop)/9;
        backButtonLeft = (screen_width - back_button.getWidth())/20;
        backButtonTop = screen_height-(backButtonLeft+back_button.getHeight());
        clearButtonLeft = (screen_width-clear_button.getWidth())/2;
        clearButtonTop = screen_height-(backButtonLeft+clear_button.getHeight());
        musicButtonLeft = screen_width-backButtonLeft-music_button.getWidth();
        musicButtonTop = backButtonTop;
    }

    public void initTableNameAndDiff(){
        switch (diff_chosen){
            case 'a':
                answer_table = "easy_answers";
                puzzle_table = "easy_puzzles";
                diff_level = 1;
                break;
            case 'b':
                answer_table = "common_answers";
                puzzle_table = "common_puzzles";
                diff_level = 2;
                break;
            case 'c':
                answer_table = "hard_answers";
                puzzle_table = "hard_puzzles";
                diff_level = 3;
                break;
            case 'd':
                answer_table = "expert_answers";
                puzzle_table = "expert_puzzles";
                diff_level = 4;
                break;
        }
    }

    public void loadDB(){
        manager = new DBManager(getContext());
        manager.openDataBase();
        db = manager.getDb();
        if(db==null){
            return;
        }
        // 获取之前保存的题目序号
        Cursor cursor_info = db.query("sudoku_player_info", null,null,null,null,null,null);
        if (cursor_info.moveToNext()){
            int diff_index;
            diff_index = cursor_info.getColumnIndex(""+diff_level);
            do {
                int current_level = cursor_info.getInt(diff_index);
                puzzleNo = current_level;
                Log.e("DBTest", ""+current_level);
            }while (cursor_info.moveToNext());
        }
        cursor_info.close();
        // 获取答案数据
        Cursor cursor_answers = db.query(answer_table,null,null,null,null,null,null);
        cursor_answers.moveToPosition(9*puzzleNo);
        int [] num_index = {0,0,0,0,0,0,0,0,0};
        for (int i = 0;i<9;i++){
            num_index[i] = cursor_answers.getColumnIndex(""+(i+1));
        }
        int counter = 0;
        do {
            for (int i = 0;i<9;i++){
                current_ans[counter][i] = cursor_answers.getInt(num_index[i]);
//                Log.e("ans",""+current_ans[counter][i]);
            }
            counter++;
            cursor_answers.moveToNext();
        }while (counter<9);
        cursor_answers.close();
        // 获取题目数据
        Cursor cursor_puzzles = db.query(puzzle_table,null,null,null,null,null,null);
        cursor_puzzles.moveToPosition(9*puzzleNo);
        for (int i = 0;i<9;i++){
            num_index[i] = cursor_puzzles.getColumnIndex(""+(i+1));
        }
        counter = 0;
        do {
            for (int i=0;i<9;i++){
                current_pz[counter][i] = cursor_puzzles.getInt(num_index[i]);
                Log.e("pz",""+current_pz[counter][i]);
            }
            counter++;
            cursor_puzzles.moveToNext();
        }while (counter<9);
        cursor_puzzles.close();
        manager.closeDataBase();
        dbIsReady=1;
    }

    // 绘制当前题目
    public void drawCurrentPuzzle(){
        for(int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                if (current_pz[i][j]!=0){
                    Bitmap bitmapToDraw = choosePZBitmap(current_pz[i][j]);
                    myCanvas.drawBitmap(bitmapToDraw,panelLeft+j*widthAUnit,panelTop+i*heightAUnit,null);
                }
            }
        }
        // 在这里进行内容绘制
    }
    // 绘制用户输入的数字
    public void drawCurrentInput(){
        for(int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                if (current_input[i][j]!=0){
                    Bitmap bitmapToDraw = choosePYBitmap(current_input[i][j]);
                    myCanvas.drawBitmap(bitmapToDraw,panelLeft+j*widthAUnit,panelTop+i*heightAUnit,null);
                }
            }
        }
    }

    public Bitmap choosePZBitmap(int num){
        switch (num){
            case 1:
                return path_1;
            case 2:
                return path_2;
            case 3:
                return path_3;
            case 4:
                return path_4;
            case 5:
                return path_5;
            case 6:
                return path_6;
            case 7:
                return path_7;
            case 8:
                return path_8;
            case 9:
                return path_9;
        }
        return null;
    }
    public Bitmap choosePYBitmap(int num){
        switch (num){
            case 1:
                return path_py1;
            case 2:
                return path_py2;
            case 3:
                return path_py3;
            case 4:
                return path_py4;
            case 5:
                return path_py5;
            case 6:
                return path_py6;
            case 7:
                return path_py7;
            case 8:
                return path_py8;
            case 9:
                return path_py9;
        }
        return null;
    }
    // 判断点击的区域 1：在数独题目板外面 2：在数独题目板里面 3：在选择数字板里面
    public int clickArea(float x, float y){
        if(x>panelLeft&&y>panelTop&&x<(panelLeft+9*widthAUnit)&&y<(panelTop+9*heightAUnit)){
            return 2;
        }else if(x>(choosePanelLeft-4*unPress_background.getWidth())&&y>choosePanelTop&&x<(choosePanelLeft+5*unPress_background.getWidth())&&y<(choosePanelTop+unPress_background.getHeight())){
            return 3;
        }else if(x>backButtonLeft&&y>backButtonTop&&x<(backButtonLeft+back_button.getWidth())&&y<(backButtonTop+back_button.getHeight())){
            return 4;
        }else if(x>clearButtonLeft&&y>clearButtonTop&&x<(clearButtonLeft+clear_button.getWidth())&&y<(clearButtonTop+clear_button.getHeight())){
            return 5;
        }else if(x>musicButtonLeft&&y>musicButtonTop&&x<(musicButtonLeft+music_button.getWidth())&&y<(musicButtonTop+music_button.getHeight())){
            return 6;
        }else {
            return 1;
        }
    }
    // 绘制数板的选择状态
    public void drawChooseState(){
        float b_x = (x_index-panelLeft)%widthAUnit;
        float b_y = (y_index-panelTop)%heightAUnit;
        myCanvas.drawBitmap(press_background,x_index-b_x,y_index-b_y,null);
        // 更新填数坐标
        int check_i = (int)((y_index-panelTop)/heightAUnit);
        int check_j = (int)((x_index-panelLeft)/widthAUnit);
        if (current_pz[check_i][check_j]==0){
            i_index = check_i;
            j_index = check_j;
        }else {
            i_index = -1;
            j_index = -1;
        }
    }
    // 填入数字
    public void fillInNumber(){
        if (i_index>=0&&j_index>=0){
            int numToFill = (int)((x_index-choosePanelLeft+4*unPress_background.getWidth())/(unPress_background.getWidth()))+1;
            current_input[i_index][j_index] = numToFill;
            Log.e("Fill",numToFill+"");
            updateIllegal(numToFill);
        }
    }
    // 判断填入数字是否明显不合法
    public void updateIllegal(int numToFill){
        clearIllegalSign();
        // 合法标志位
        boolean isLegal = true;
        int selfInputIllegalCNT = 0;
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if (j==j_index&&i==i_index){
                    continue;
                }
                // 同一行
                if (j==j_index&&numToFill==current_pz[i][j]){
                    illegal_index_i[0] = i;
                    illegal_index_j[0] = j;
                    isLegal = false;
                }
                // 同一列
                if (i==i_index&&numToFill==current_pz[i][j]){
                    illegal_index_i[1] = i;
                    illegal_index_j[1] = j;
                    isLegal = false;
                }
                // 同一九宫格
                int nigh_x = j_index-j_index%3;
                int nigh_y = i_index-i_index%3;
                if (i>=nigh_y&&j>=nigh_x&&i<(nigh_y+3)&&j<(nigh_x+3)&&numToFill==current_pz[i][j]){
                    illegal_index_i[2] = i;
                    illegal_index_j[2] = j;
                    isLegal = false;
                }
                // 与自己填写的数字具有非法关系加入数组的后面
                // 同一行
                if (j==j_index&&numToFill==current_input[i][j]){
                    illegal_index_i[selfInputIllegalCNT+4] = i;
                    illegal_index_j[selfInputIllegalCNT+4] = j;
                    isLegal = false;
                    selfInputIllegalCNT++;

                }
                // 同一列
                if (i==i_index&&numToFill==current_input[i][j]){
                    illegal_index_i[selfInputIllegalCNT+4] = i;
                    illegal_index_j[selfInputIllegalCNT+4] = j;
                    isLegal = false;
                    selfInputIllegalCNT++;
                }
                // 同一九宫格
                int nigh_xx = j_index-j_index%3;
                int nigh_yy = i_index-i_index%3;
                if (i>=nigh_yy&&j>=nigh_xx&&i<(nigh_yy+3)&&j<(nigh_xx+3)&&numToFill==current_input[i][j]){
                    illegal_index_i[selfInputIllegalCNT+4] = i;
                    illegal_index_j[selfInputIllegalCNT+4] = j;
                    isLegal = false;
                    selfInputIllegalCNT++;
                }
            }
        }
        if (isLegal){
            clearIllegalSign();
        }else {
            illegal_index_i[3] = i_index;
            illegal_index_j[3] = j_index;
        }
    }
    public void clearIllegalSign(){
        for (int i=0;i<illegal_index_j.length;i++){
            illegal_index_i[i]=-1;
            illegal_index_j[i]=-1;
        }
    }

    // 绘制出不合法填入的背景提示
    public void drawIllegalSign(){
        for (int i=0;i<illegal_index_j.length;i++){
            if (illegal_index_i[i]!=-1){
                myCanvas.drawBitmap(error_backgroud,panelLeft+illegal_index_j[i]*widthAUnit,panelTop+illegal_index_i[i]*heightAUnit,null);
            }
        }
    }
    // 相应清除按钮
    public void clearFromClearButton(){
        current_input[i_index][j_index] = 0;
    }
    // 判断是否过关
    public void finishCheck(){
        for(int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                // 表示还有未填写
                if (current_pz[i][j]==0&&current_input[i][j]==0){
                    return;
                }
                // 表示填写答案错误
                if (current_pz[i][j]==0&&current_input[i][j]!=current_ans[i][j]){
                    return;
                }
            }
        }
        Log.e("Good","成功通关");
        // 通关则更新所到达的关数以及进行恭喜过关动画并重新加载数据库
//        Toast.makeText(getContext(),"恭喜过关！！即将进入下一关！！",Toast.LENGTH_LONG).show();
        clearCurrentInput();
        updataDBWithWinning();
        loadDB();
        try {
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 通关后执行的关卡加一操作
    public void updataDBWithWinning(){
        // 关卡过完
        if (puzzleNo==999){
            Toast.makeText(getContext(),"您已经完成此难度的所有关卡",Toast.LENGTH_LONG).show();
            return;
        }
        manager = new DBManager(getContext());
        manager.openDataBase();
        db = manager.getDb();
        int updateNO = puzzleNo + 1;
        String sql = "UPDATE sudoku_player_info SET '"+diff_level+"' = "+updateNO+";";
        db.execSQL(sql);
        db.close();
    }
    // 清空输入矩阵
    public void clearCurrentInput(){
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                current_input[i][j] = 0;
            }
        }
    }
    // 清空与题目重叠的输入矩阵（题目切换的时候会有可能把上一个填写的数字写到题目数字上面）
    public void clearCurrentInputInPZ(){
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(current_pz[i][j]!=0&&current_input[i][j]!=0){
                    current_input[i][j]=0;
                }
            }
        }
    }
    public void startOrStopMusic(){
        if(musicIsStarted){
            context.stopService(intent_music);
        }else {
            context.startService(intent_music);
        }
    }
}
