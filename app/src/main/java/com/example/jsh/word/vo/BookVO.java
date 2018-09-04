package com.example.jsh.word.vo;

import android.database.Cursor;

/**
 * Created by jsh on 2017-09-27.
 */

public class BookVO {
    private	int idx;
    private	String name;
    private	int count;
    private	int last_page;
    private	int sort;
    private	String create_date;

    public BookVO(Cursor cursor){
        this.setIdx(cursor.getInt(cursor.getColumnIndex("idx")));
        this.setName(cursor.getString(cursor.getColumnIndex("name")));
        this.setCount(cursor.getInt(cursor.getColumnIndex("count")));
        this.setLast_page(cursor.getInt(cursor.getColumnIndex("last_page")));
        this.setSort(cursor.getInt(cursor.getColumnIndex("sort")));
        this.setCreate_date(cursor.getString(cursor.getColumnIndex("create_date")));
    }
    public BookVO(String name){
        this.setName(name);
    }
    public BookVO( ){

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

}
