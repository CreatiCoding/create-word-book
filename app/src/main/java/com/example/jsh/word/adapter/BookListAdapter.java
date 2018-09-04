package com.example.jsh.word.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jsh.word.R;
import com.example.jsh.word.activity.MainActivity;
import com.example.jsh.word.activity.WordCardActivty;
import com.example.jsh.word.util.CommonUtil;
import com.example.jsh.word.util.SqliteUtil;
import com.example.jsh.word.vo.BookVO;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jsh on 2017-09-22.
 */

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<BookVO> bookList;
    private TextView dialogEt;
    private AlertDialog.Builder dialogAd;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewGroup mParent;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;

        public ViewHolder(ViewGroup vg) {
            super(vg);
            mParent = vg;
            mTextView1 = (TextView) vg.getChildAt(0);
            mTextView2 = (TextView) vg.getChildAt(1);
            mTextView3 = (TextView) vg.getChildAt(2);
        }
    }

    public BookListAdapter(Context context, ArrayList<BookVO> bookList) {
        this.mContext = context;
        this.bookList = bookList;
    }

    @Override
    public BookListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_custom_view, parent, false);
        ViewHolder vh = new ViewHolder((ViewGroup)v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookVO vo = this.bookList.get(position);

        if(vo.getName() == ""){
            ViewGroup parent = (ViewGroup)holder.mTextView1.getParent();
            parent.removeView(holder.mTextView1);
            parent.removeView(holder.mTextView2);
            parent.removeView(holder.mTextView3);



            TextView tv = new TextView(holder.mTextView2.getContext()); //<-- this is the activity
            tv.setText("새로 만들기");
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    50));
            tv.setTextSize(20);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            parent.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDialog(view.getContext());
                }
            });
            return ;
        }

        holder.mTextView1.setText(String.valueOf(position + 1));
        holder.mTextView2.setText(vo.getName());
        System.out.println(vo.getCount()+"");
        holder.mTextView3.setText(vo.getCount()+"");

        holder.mTextView2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.isClickable()) {
                    String book_name = ((TextView)v).getText().toString();
                    Intent intent = new Intent(v.getContext(), WordCardActivty.class);
                    Bundle b = new Bundle();
                    b.putString("book_name", book_name); //Your id
                    //b.putInt("booK_last_page", SqliteUtil.getInstance(v.getContext(), null).findBookByName(book_name).getLast_page()); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    v.getContext().startActivity(intent);
                    //getActivity(v).finish();
                }
            }
        });

        holder.mTextView2.setOnLongClickListener(new BookLongClickListener(vo));
    }

    public class BookLongClickListener implements View.OnLongClickListener{

        private BookVO book;
        public BookLongClickListener(BookVO book){
            this.book = book;
        }

        @Override
        public boolean onLongClick(View view) {
            delDialog(this.book.getIdx());
            return true;
        }
    }

    private Activity getActivity(View v) {
            Context context = v.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return this.bookList.size();
    }


    public void delDialog(int idx){
        final int mIdx = idx;
        AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
        dialogAd = ad;
        dialogAd.setTitle("단어장 삭제하시겠습니까?");

        // 확인 버튼 설정
        dialogAd.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SqliteUtil sqliteUtil = SqliteUtil.getInstance(dialogAd.getContext(), null);
                sqliteUtil.removeBookByIdx(mIdx);
                dialog.dismiss();
                ((MainActivity)mContext).showList();
            }
        });
        // 취소 버튼 설정
        dialogAd.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = dialogAd.create();
        alert.show();
    }
    public void addDialog(Context context){
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        dialogAd = ad;

        ad.setTitle("단어장 파일 만들기");
        ad.setMessage("새로만들 단어장의 이름을 지어주세요");
        dialogEt = new EditText(context);
        ad.setView(dialogEt);

        // 확인 버튼 설정
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = dialogEt.getText().toString();
                SqliteUtil sqliteUtil = SqliteUtil.getInstance(dialogAd.getContext(), null);
                sqliteUtil.addBook(name);
                dialog.dismiss();
                ((MainActivity)mContext).showList();
            }
        });
        // 취소 버튼 설정
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }
}
