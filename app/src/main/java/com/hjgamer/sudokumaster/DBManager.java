package com.hjgamer.sudokumaster;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DBManager {
    private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "puzzles_and_answers.db";
    public static final String PACKAGE_NAME = "com.hjgamer.sudokumaster"; // 包名
    // 数据库的绝对路径  (/data/data/com.*.*(package name))
    public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()+"/"+PACKAGE_NAME;
    private SQLiteDatabase db;
    private Context context;
    public DBManager(Context context){
        this.context = context;
    }
    // 对外提供打开数据库接口
    public void openDataBase(){
        this.db = this.openDataBase(DB_PATH+"/"+DB_NAME);
    }
    // 获取打开后的数据库
    public SQLiteDatabase getDb(){
        return this.db;
    }

    // 本地打开数据方法
    private SQLiteDatabase openDataBase(String filePath){
        try{
            File file = new File(filePath);
            Log.e("DBManager", "fileExists");
            if(!file.exists()){
                // 先判断文件是否存在
                // 通过输入流和输出流，把数据库拷贝到"filePath下"
                Log.e("DBManager", "fileNotExists");
                InputStream is = context.getResources().openRawResource(R.raw.puzzles_and_answers);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[BUFFER_SIZE];
                int readCount;
                while ((readCount = is.read(buffer))>0){
                    fos.write(buffer,0,readCount);
                }
                fos.close();
                is.close();
            }
            // 打开数据库
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(filePath, null);
            return db;
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    // 关闭数据库
    public void closeDataBase(){
        if(this.db!=null){
            db.close();
        }
    }
}
