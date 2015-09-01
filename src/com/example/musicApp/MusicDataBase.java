package com.example.musicApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Nick_dnepr on 22.06.2015.
 */
public class MusicDataBase extends SQLiteOpenHelper{


    private SQLiteDatabase musicBase;
    private int idCollumIndex;
    private int authorCollumIndex;
    private int nameCollumIndex;
    private int likesCountCollumIndex;
    private int downloadUrlCollumIndex;
    private int streamUrlCollumIndex;
    private int durationCollumIndex;
    private int pathToFileCollumIndex;
    private Cursor cursor;

    public MusicDataBase(Context context) {

        super(context, "myDB", null, 1);
        Log.i("info", "created database");
        musicBase = getWritableDatabase();
        cursor = musicBase.query("mytable", null, null, null, null, null, null);
        Log.i("info", "dataBase online");
    }

    public void addInfo(Info addedInfo) {
        Log.i("info", "called method addInfo");

        initColumns();

        Log.i("id", idCollumIndex + "");
        Log.i("id", authorCollumIndex+"");
        Log.i("id", nameCollumIndex+"");
        Log.i("id", likesCountCollumIndex+"");
        Log.i("id", downloadUrlCollumIndex+"");
        Log.i("id", streamUrlCollumIndex+"");
        Log.i("id", durationCollumIndex+"");
        Log.i("id", pathToFileCollumIndex+"");
        cursor = musicBase.query("mytable", null, null, null, null, null, null);


        if (cursor.moveToFirst()) {
            Log.i("info", "cursor moved to first");

            do {
                Info tempInfo = new Info(cursor.getString(nameCollumIndex), cursor.getInt(durationCollumIndex), new User(cursor.getString(authorCollumIndex)), cursor.getInt(likesCountCollumIndex), cursor.getString(streamUrlCollumIndex), cursor.getString(downloadUrlCollumIndex), cursor.getString(pathToFileCollumIndex));
                Log.i("info", "addedInfo________" + addedInfo.getStream_url());
                Log.i("info", "tempInfo________" + tempInfo.getStream_url());

                Log.i("list", tempInfo.toString());
                Log.i("list", addedInfo.toString());
                if (addedInfo.getStream_url().equals(tempInfo.getStream_url())) {
                    Log.i("info", "we have this music");
                    return;
                }

            } while (cursor.moveToNext());
        }


        Calendar c = Calendar.getInstance();
        String seconds = c.get(Calendar.YEAR) + "" + c.get(Calendar.MONTH) + "" + c.get(Calendar.DAY_OF_MONTH) + "" + c.get(Calendar.HOUR_OF_DAY) + "" + c.get(Calendar.MINUTE) + "" + c.get(Calendar.SECOND);
        Log.i("time", seconds);
        String name = addedInfo.getTitle();
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File myFile = new File(dir, name + ".mp3");


        try {
            myFile.createNewFile();
            Log.i("info", "file created");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("info", e.toString());
        }


        if (myFile.exists()) {

            Log.i("info", "started downloading");
            DownloadService.downloadFile(addedInfo.getStream_url()+"?client_id=b45b1aa10f1ac2941910a7f0d10f8e28", myFile);

            addedInfo.setPath_to_file(myFile.getAbsolutePath());
            Log.i("info", "file created");
        } else {
            Log.i("info", "file not found");
            return;
        }


        Log.i("FileInfo", myFile.getAbsolutePath());


        ContentValues values = new ContentValues();
        values.put("author", addedInfo.getUser().getUsername());
        values.put("name", addedInfo.getTitle());
        values.put("likesCount", addedInfo.getLikes_count());
        values.put("duration", addedInfo.getDuration());
        values.put("downloadUrl", addedInfo.getDownload_url());
        values.put("pathToFile", addedInfo.getPath_to_file());
        values.put("streamUrl", addedInfo.getStream_url());

        long insertResult = musicBase.insert("mytable", null, values);
        Log.i("asdfasdf", "insertResult: " + insertResult);

        Log.i("insert", addedInfo.getUser().getUsername());
        Log.i("insert", addedInfo.getTitle());
        Log.i("insert", addedInfo.getLikes_count()+"");
        Log.i("insert", addedInfo.getDuration()+"");


        Log.i("info", "info inserted");
    }

    private void initColumns() {
        idCollumIndex = cursor.getColumnIndex("id");
        authorCollumIndex = cursor.getColumnIndex("author");
        nameCollumIndex = cursor.getColumnIndex("name");
        likesCountCollumIndex = cursor.getColumnIndex("likesCount");
        downloadUrlCollumIndex = cursor.getColumnIndex("downloadUrl");
        streamUrlCollumIndex = cursor.getColumnIndex("streamUrl");
        durationCollumIndex = cursor.getColumnIndex("duration");
        pathToFileCollumIndex = cursor.getColumnIndex("pathToFile");
    }

    public ArrayList<Info> getInfo() {
        cursor = musicBase.query("mytable", null, null, null, null, null, null);

        initColumns();

        ArrayList<Info> list = new ArrayList<>();
        if (cursor.moveToFirst()) {


            do {
                Info addedInfo = new Info(cursor.getString(nameCollumIndex), cursor.getInt(durationCollumIndex), new User(cursor.getString(authorCollumIndex)), cursor.getInt(likesCountCollumIndex), cursor.getString(streamUrlCollumIndex), cursor.getString(downloadUrlCollumIndex), cursor.getString(pathToFileCollumIndex));
                list.add(addedInfo);
                Log.i("list", addedInfo.toString());
                Log.i("list", "----------------------------------------------");
            } while (cursor.moveToNext());

        }
        return list;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table mytable ("
                + "id integer primary key autoincrement,"
                + "author text,"
                + "name text,"
                + "likesCount integer,"
                + "duration integer,"
                + "downloadUrl text,"
                + "pathToFile text,"
                + "streamUrl text " + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteInfo(Info info){
        cursor = musicBase.query("mytable", null, null, null, null, null, null);
        getWritableDatabase().delete("mytable", "streamUrl = ?", new String[]{String.valueOf(info.getStream_url())});
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File myFile = new File(dir, info.getTitle() + ".mp3");
        Log.i("delete",myFile.delete()+"");
    }


}

