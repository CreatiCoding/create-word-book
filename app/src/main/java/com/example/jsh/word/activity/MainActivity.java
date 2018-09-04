package com.example.jsh.word.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaCas;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.jsh.word.adapter.BookListAdapter;
import com.example.jsh.word.R;
import com.example.jsh.word.util.CommonUtil;
import com.example.jsh.word.util.FileDialog;
import com.example.jsh.word.util.SessionUtil;
import com.example.jsh.word.util.SqliteUtil;
import com.example.jsh.word.vo.BookVO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private EditText dialogEt;
    private AlertDialog.Builder dialogAd;
    private ArrayList<String> params;
    private SessionUtil mSessionUtil;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mContext = this;
        mSessionUtil=SessionUtil.getInstance();
        mSessionUtil.toastSession(this);
        params = new ArrayList<String>();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        showList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showList();
    }
    public void showList(){

        // specify an adapter (see also next example)
        SqliteUtil sqliteUtil = SqliteUtil.getInstance(this, null);
        ArrayList<BookVO> list = sqliteUtil.findBookList();
        list.add(new BookVO(""));
        mAdapter = new BookListAdapter(this, list);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void importDB(final Context context){

        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
        FileDialog fileDialog = new FileDialog((Activity)mContext, mPath, ".db");
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                SqliteUtil.getInstance(mContext, null).importDB(file, context);
                showList();
            }
        });
        fileDialog.showDialog();
    }


    public void addDialog(Context context){

        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
        FileDialog fileDialog = new FileDialog((Activity)mContext, mPath, ".csv");
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {

                String editTextStr = file.getAbsolutePath();    //dialogEt.getText().toString();
                CommonUtil commonUtil = CommonUtil.getInstance();
                SqliteUtil sqliteUtil = SqliteUtil.getInstance(mContext, null);
                String result = commonUtil.readFromFile(editTextStr);
                if(result == null){
                    Log.e("DEV", ((Boolean)(result==null)).toString());
                    Toast.makeText(mContext, "파일이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                HashMap<String, String> map = commonUtil.csv2arrList(result);
                String bookName = editTextStr.substring(editTextStr.lastIndexOf("/")+1, editTextStr.lastIndexOf("."));

                sqliteUtil.addBook(bookName);
                sqliteUtil.addWordList(bookName, map);
                sqliteUtil.printWord(bookName);
                showList();
                Log.d(getClass().getName(), "selected file " + file.toString()+"\nfull path "+file.getAbsolutePath());
            }
        });
        fileDialog.showDialog();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);

        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#273238")));


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.layout_actionbar, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);
        Toolbar.LayoutParams parms = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        actionbar.setLayoutParams(parms);


        if(mSessionUtil.getId() != null){
            menu.findItem(R.id.action_Menu1).setTitle(mSessionUtil.getId().split("@")[0]);
        }else{
            menu.findItem(R.id.action_Menu1).setTitle("로그인");
        }
        menu.findItem(R.id.action_Menu2).setTitle("전부 내보내기");
        menu.findItem(R.id.action_Menu3).setTitle("전부 불러오기");
        menu.findItem(R.id.action_Menu4).setTitle("단어장 불러오기");


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_Menu1) {

            if(mSessionUtil.getId() == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            return true;
        }

        if (id == R.id.action_Menu2) {
            if(SessionUtil.getInstance().getId() == null){
                Toast.makeText(this, "로그인 하세요!", Toast.LENGTH_LONG).show();
            }else {
                new Thread() {
                    public void run() {
                        File backup = SqliteUtil.getInstance(mContext, null).exportDB(mContext);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/html");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"papimon1203@naver.com"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "the subject");
                        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("the content"));
                        System.out.println("파일경로 : "+backup.getAbsolutePath());
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+backup.getAbsolutePath()));
                        startActivity(intent);
                    }
                }.start();
            }
            return true;
        }

        if (id == R.id.action_Menu3) {
            importDB(mContext);
            return true;
        }
        if (id == R.id.action_Menu4) {
            addDialog(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}

