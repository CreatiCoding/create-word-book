package com.example.jsh.word.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.jsh.word.vo.BookVO;
import com.example.jsh.word.vo.WordVO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jsh on 2017-09-25.
 */

public class SqliteUtil extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wordlist.db";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;
    private SQLiteDatabase sQLiteDatabase;

    // 싱글톤 패턴을 위한 함수
    private static volatile SqliteUtil singletonInstance = null;

    public static SqliteUtil getInstance(Context context, SQLiteDatabase.CursorFactory factory) {
        if (singletonInstance == null) {
            synchronized (SqliteUtil.class) {
                if (singletonInstance == null) {
                    singletonInstance = new SqliteUtil(context, factory);
                }
            }
        }
        return singletonInstance;
    }

    public SqliteUtil(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        mContext = context;
        sQLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        craeteTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void printWord(String name) {
        Cursor cursor = sQLiteDatabase.rawQuery("SELECT * FROM word WHERE book_idx = " + this.findBookByName(name).getIdx() + " and activated = 1;", null);
        while (cursor.moveToNext()) {
            System.out.print("name : " + cursor.getString(cursor.getColumnIndex("name")));
            System.out.println("\tmean : " + cursor.getString(cursor.getColumnIndex("mean")));
        }
    }

    // Book
    // Create
    public void addBook(String pName) {
        String sql = "INSERT INTO book (name) VALUES ('" + pName + "');";
        sQLiteDatabase.execSQL(sql);
    }

    // Read
    public BookVO findBookByName(String name) {
        String sql = "select * from book where name = \"" + name + "\" and activated = 1;";
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
        cursor.moveToNext();
        return new BookVO(cursor);
    }

    // Update
    public void modifyBookPage(int idx, int mLast_page){
        String sql = "UPDATE book SET last_page = " +mLast_page + " where idx = "+idx+";";
        sQLiteDatabase.execSQL(sql);
    }
    public void modifyBookByIdx(int idx, String mName, int mLast_page, int mSort) {
        String sql = "UPDATE book SET ";
        if (mName == null && mLast_page == 0 && mSort == 0)
            return;
        if (mName != null)
            sql += "name='" + mName + "'";
        if ((mName != null) && (mLast_page != 0))
            sql += ", ";
        if (mLast_page != 0)
            sql += "last_page=" + mLast_page;
        if (((mLast_page != 0) && (mSort)!=0) || ((mName != null) && (mSort != 0)))
            sql += ", ";
        if (mSort != 0)
            sql += "sort=" + mSort;
        sql += " WHERE idx = " + idx;
        sQLiteDatabase.execSQL(sql);
    }
    public void modifyBookByName(String name, String mName, int mLast_page, int mSort) {
        String sql = "UPDATE book SET ";
        if (mName == null && mLast_page == 0 && mSort == 0)
            return;
        if (mName != null)
            sql += "name='" + mName + "'";
        if (mLast_page != 0)
            sql += ", last_page=" + mLast_page;
        if (mSort != 0)
            sql += ", sort=" + mSort;
        sql += " WHERE name = " + name;
        sQLiteDatabase.execSQL(sql);
    }

    // Delete
    public void removeBookByName(String name) {
        String sql = "UPDATE book SET activated = 0 where name = '" + name + "' and activated = 1;";
        sQLiteDatabase.execSQL(sql);
    }

    public void removeBookByIdx(int idx) {
        String sql = "UPDATE book SET activated = 0 where idx = " + idx + " and activated = 1;";
        sQLiteDatabase.execSQL(sql);
    }

    // Word
    // Create
    public void addWord(String name, String mean, int book_idx) {
        System.out.println(name);
        System.out.println(mean);
        if (name == null || mean == null || book_idx == 0)
            return;
        String sql = "INSERT INTO word (name, mean, book_idx) VALUES ('" + name + "', '" + mean + "', " + book_idx + " );";
        String sql2 = "UPDATE book SET count = count + 1 WHERE idx = " + book_idx;
        sQLiteDatabase.execSQL(sql);
        sQLiteDatabase.execSQL(sql2);
    }

    // Read
    public WordVO findWordByIdx(int idx) {
        String sql = "select * from word where idx = " + idx + "  and activated = 1;";
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
        cursor.moveToNext();
        return new WordVO(cursor);
    }

    // Update
    public void modifyWordByIdx(int idx, String mName, String mMean) {
        String sql = "UPDATE book SET ";
        if (mName == null && mName == null)
            return;
        if (mName != null)
            sql += "name='" + mName + "'";
        if (mMean != null)
            sql += ", mean='" + mMean + "'";
        sql += " WHERE idx = " + idx;
        sQLiteDatabase.execSQL(sql);
    }

    public void modifyWordByName(String name, String mName, String mMean) {
        String sql = "UPDATE book SET ";
        if (mName == null && mName == null)
            return;
        if (mName != null)
            sql += "name='" + mName + "'";
        if (mMean != null)
            sql += ", mean='" + mMean + "'";
        sql += " WHERE name = " + name;
        sQLiteDatabase.execSQL(sql);
    }

    // Delete
    public void removeWordByName(String name, int book_idx) {
        String sql = "UPDATE word SET activated = 0 where book_idx = "+book_idx+" and name = '" + name + "';";
        sQLiteDatabase.execSQL(sql);
        sql = "UPDATE book SET count = count - 1 where idx = "+book_idx+" ; ";
        sQLiteDatabase.execSQL(sql);
    }
    public void removeWordByIdx(int idx) {
        String sql = "UPDATE word SET activated = 0 where idx = " + idx + ";";
        sQLiteDatabase.execSQL(sql);
    }

    // word add list
    public void addWordList(String bookName, HashMap<String, String> map) {
        int idx = this.findBookByName(bookName).getIdx();
        for (int i = 0; i < map.keySet().size(); i++) {
            Object word_name = map.keySet().toArray()[i];
            String word_mean = map.get(word_name);
            this.addWord(word_name.toString(), word_mean, idx);
        }
    }

    // book find list
    public ArrayList<BookVO> findBookList(){
        String sql = "select * from book where activated = 1;";
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
        ArrayList<BookVO> result = new ArrayList<BookVO>();

        while(cursor.moveToNext()){
            result.add(new BookVO(cursor));
        }
        return result;
    }
    // word find list by page
    public ArrayList<WordVO> findWordList(int idx, int page){
        // page에 출력되는 단어의 갯수는 10개
        String sql = "select * from word where activated = 1 and book_idx = " + idx + " limit 10 offset "+page*10+";" ;
        Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
        ArrayList<WordVO> result = new ArrayList<>();
        while(cursor.moveToNext()){
            WordVO vo = new WordVO(cursor);
            result.add(vo);
        }
        return result;
    }

    public void checkTable(SQLiteDatabase db) {
        sQLiteDatabase.rawQuery("SELECT name FROM sqlite_master\n WHERE type='table' ORDER BY name;", null);
    }

    public void craeteTable(SQLiteDatabase db) {
        Toast.makeText(mContext, "DB Tables are created.", Toast.LENGTH_SHORT).show();
        String bookCreateSql = "CREATE TABLE book( " +
                "idx INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "count INTEGER NOT NULL DEFAULT 0, " +
                "last_page INTEGER DEFAULT 0, " +
                "sort INTEGER DEFAULT 0, " +
                "create_date DATE DEFAULT (datetime('now')), " +
                "activated INTEGER DEFAULT 1 " +
                ");";
        String wordCreateSql = "CREATE TABLE word( " +
                "idx INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "mean INTEGER NOT NULL, " +
                "book_idx INTEGER, " +
                "create_date DATE DEFAULT (datetime('now')), " +
                "activated INTEGER DEFAULT 1, " +
                "FOREIGN KEY(book_idx) REFERENCES book(idx) " +
                ");";
        db.execSQL(bookCreateSql);
        db.execSQL(wordCreateSql);
    }
    public void importDB(File backup, Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "com.example.jsh.word"
                        + "//databases//" + DATABASE_NAME;
                String backupDBPath  = "/BackupFolder/DatabaseName";
                File  backupDB= new File(data, currentDBPath);
                File currentDB  = backup;

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, backupDB.toString(),Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public File exportDB(Context context){

        String result = "";
        String token = SessionUtil.getInstance().getAccessToken();
        if(token == null){
            return null;
        }
        System.out.println(CommonUtil.getInstance().getId(token));
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.example.jsh.word" +"/databases/"+DATABASE_NAME;
        String backupDBPath = DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return backupDB;
    }
}
/*\
*
CREATE TABLE book(
	idx 		INTEGER     PRIMARY	KEY     NOT NULL,
	name		TEXT    NOT NULL,
	count		INTEGER     NOT NULL,
	last_page	INTEGER,
	sort		INTEGER,
	create_date	TEXT,
	activated	INTEGER
);
CREATE TABLE word(
	idx 		INTEGER     PRIMARY KEY     NOT NULL,
	word           	TEXT    NOT NULL,
	mean            INTEGER      NOT NULL,
	FOREIGN KEY(book_idx) REFERENCES book(idx),
	book_idx        INTEGER,
	create_date	TEXT,
	activated	INTEGER
);
* */