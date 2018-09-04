package com.example.jsh.word.vo;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by jsh on 2017-09-27.
 */

public class WordVO {
    private	int idx;
    private	String name;
    private	String mean;
    private	int book_idx;
    private	String create_date;

    @Override
    public String toString() {
        return "WordVO{" +
                "idx=" + idx +
                ", name='" + name + '\'' +
                ", mean='" + mean + '\'' +
                ", book_idx=" + book_idx +
                ", create_date='" + create_date + '\'' +
                '}';
    }

    public WordVO(Cursor cursor){
        this.setIdx(cursor.getInt(cursor.getColumnIndex("idx")));
        this.setName(cursor.getString(cursor.getColumnIndex("name")));
        this.setMean(cursor.getString(cursor.getColumnIndex("mean")));
        this.setBook_idx(cursor.getInt(cursor.getColumnIndex("book_idx")));
        this.setCreate_date(cursor.getString(cursor.getColumnIndex("create_date")));
    }
    public WordVO(){

    }
    public WordVO(String name, String mean, int book_idx){
        this.setName(name);
        this.setMean(mean);
        this.setBook_idx(book_idx);
    }
    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public int getBook_idx() {
        return book_idx;
    }

    public void setBook_idx(int book_idx) {
        this.book_idx = book_idx;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public static WordVO findByNameInList(ArrayList<WordVO> arr, String name){
        int size = arr.size();
        for(int i=0;i<size;i++){
            if(name.equals(arr.get(i).getName())){
                return arr.get(i);
            }
        }
        return null;
    }
}
