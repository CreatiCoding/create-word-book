package com.example.jsh.word.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jsh.word.R;
import com.example.jsh.word.util.SqliteUtil;
import com.example.jsh.word.vo.BookVO;
import com.example.jsh.word.vo.WordVO;

import java.util.ArrayList;
import java.util.Arrays;

public class WordCardActivty extends AppCompatActivity {

    private Context mContext;
    private AlertDialog.Builder dialogAd;
    private BookVO curBook;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_card_activty);
        mContext = this;
        SqliteUtil sqliteUtil = SqliteUtil.getInstance(this, null);
        //this.current_page = getIntent().getExtras().getInt("book_last_page");
        curBook = sqliteUtil.findBookByName(getIntent().getExtras().getString("book_name"));

        // UI 작업부터 합니다.
        setWordCards(sqliteUtil.findWordList(curBook.getIdx(), curBook.getLast_page()));

        // 오른쪽 왼쪽 적용
        Button leftBtn = (Button)findViewById(R.id.left_btn);
        Button rightBtn = (Button)findViewById(R.id.right_btn);
        leftBtn.setOnClickListener(new movePageClickListener(this.curBook));
        rightBtn.setOnClickListener(new movePageClickListener(this.curBook));
        // 아래 버튼 적용
        Button reverseBtn = (Button)findViewById(R.id.reverse_btn);

        reverseBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ArrayList<TextView> wordTextViews = new ArrayList<>();
                ArrayList<WordVO> list = SqliteUtil.getInstance(view.getContext(), null).findWordList(curBook.getIdx(), curBook.getLast_page());
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                for(int i=0;i<list.size();i++) {
                    TextView t = ((TextView)findViewById(getResources().getIdentifier("word" + (i + 1), "id", getPackageName())));
                    t.setText(list.get(i).getMean());
                    t.setTag("mean");
                }
                break;
                case MotionEvent.ACTION_UP:
                for(int i=0;i<list.size();i++) {
                    TextView t = ((TextView)findViewById(getResources().getIdentifier("word" + (i + 1), "id", getPackageName())));
                    t.setText(list.get(i).getName());
                    t.setTag("name");
                }
                break;
            }
                return false;
            }
        });

    }
    public class movePageClickListener implements View.OnClickListener{
        private BookVO curBook;
        public movePageClickListener(BookVO mCurBook){
            this.curBook = mCurBook;
        }
        @Override
        public void onClick(View view) {
            SqliteUtil sqliteUtil = SqliteUtil.getInstance(view.getContext(), null);
            int curPage = this.curBook.getLast_page();
            if(view.getTag().equals("left_btn")) {
                if(curPage == 0) {
                    Toast.makeText(view.getContext(), "It is first page.", Toast.LENGTH_SHORT).show();
                    return ;
                }
                curPage = curPage - 1;
            } else if(view.getTag().equals("right_btn")){
                if(curPage >= (((this.curBook.getCount()/10)-1) + ((this.curBook.getCount()%10 == 0)?0:1))) {
                    Toast.makeText(view.getContext(), "It is last page.", Toast.LENGTH_SHORT).show();
                    return ;
                }
                curPage = curPage + 1;
            }
            this.curBook.setLast_page(curPage);
            sqliteUtil.modifyBookPage(this.curBook.getIdx(), this.curBook.getLast_page());
            //sqliteUtil.modifyBookByIdx(this.curBook.getIdx(),null, this.curBook.getLast_page(), 0);
            setWordCards(sqliteUtil.findWordList(this.curBook.getIdx(), this.curBook.getLast_page()));
        }
    }
    public void setWordCards(ArrayList<WordVO> list) {
        if (list.size() == 0){
            Log.e("DEV]", "word's size is 0.");
            return;
        }
        Log.e("DEV]", "word's size is 10.");
        for(int i=0;i<10;i++) {
            int word_resID = getResources().getIdentifier("word" + (i+1), "id", getPackageName());
            int idx_resID = getResources().getIdentifier("word_idx" + (i+1), "id", getPackageName());
            TextView word = (TextView)findViewById(word_resID);
            TextView idx = (TextView)findViewById(idx_resID);
            word.setText("");
            idx.setText("");
        }
        for(int i=0;i<list.size();i++){
            int word_resID = getResources().getIdentifier("word" + (i+1), "id", getPackageName());
            int idx_resID = getResources().getIdentifier("word_idx" + (i+1), "id", getPackageName());
            TextView word = (TextView)findViewById(word_resID);
            TextView idx = (TextView)findViewById(idx_resID);
            WordVO vo = list.get(i);
            word.setText(vo.getName());
            idx.setText(String.valueOf(curBook.getLast_page()*10+i+1));
            word.setTag("name");
            word.setOnClickListener(new wordClickListener(vo));
            word.setOnLongClickListener(new wordLongClickListener(vo));
        }
    }
    public class wordLongClickListener implements View.OnLongClickListener{

        private WordVO word;
        public wordLongClickListener(WordVO word){
            this.word = word;
        }

        @Override
        public boolean onLongClick(View view) {
            delDialog(this.word.getName());
            return true;
        }
    }


    public class wordClickListener implements View.OnClickListener{
        private WordVO word;
        public wordClickListener(WordVO word){
            this.word = word;
        }
        @Override
        public void onClick(View view) {
            TextView tv = ((TextView)view);
            if (tv.isClickable()) {
                if((tv.getTag()).equals("mean")) {
                    tv.setText(word.getName());
                    tv.setTag("name");
                }else {
                    tv.setText(word.getMean());
                    tv.setTag("mean");
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // getMenuInflater().inflate(R.menu.option_menu, menu);
        ActionBar actionBar = getSupportActionBar();
        ImageButton btnAdd ;
        ImageButton btnBack ;
        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#273238")));

        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater2 = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater2.inflate(R.layout.layout_actionbar, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);
        Toolbar.LayoutParams parms = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        actionbar.setLayoutParams(parms);

        btnBack = (ImageButton)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnAdd = (ImageButton)findViewById(R.id.btnAdd);
        btnAdd.setVisibility(View.VISIBLE);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void delDialog(String name){
        final String mName = name;
        AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
        dialogAd = ad;
        dialogAd.setTitle("단어 삭제하시겠습니까?");

        // 확인 버튼 설정
        dialogAd.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SqliteUtil sqliteUtil = SqliteUtil.getInstance(dialogAd.getContext(), null);
                sqliteUtil.removeWordByName(mName,curBook.getIdx());
                dialog.dismiss();

                setWordCards(sqliteUtil.findWordList(curBook.getIdx(), curBook.getLast_page()));
                //((WordCardActivty)mContext).showList();
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
    public void addDialog(){
        AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
        dialogAd = ad;
        dialogAd.setTitle("단어 추가");
        final EditText inputName = new EditText(mContext);
        inputName.setHint("단어");
        final EditText inputValue = new EditText(mContext);
        inputValue.setHint("뜻");

        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        inputValue.setInputType(InputType.TYPE_CLASS_TEXT);

        LinearLayout ll=new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(inputName);
        ll.addView(inputValue);
        dialogAd.setView(ll);

        // 확인 버튼 설정
        dialogAd.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = inputName.getText().toString();
                String value = inputValue.getText().toString();
                SqliteUtil sqliteUtil = SqliteUtil.getInstance(dialogAd.getContext(), null);
                sqliteUtil.addWord(name, value, curBook.getIdx());
                dialog.dismiss();

                setWordCards(sqliteUtil.findWordList(curBook.getIdx(), curBook.getLast_page()));
                //((WordCardActivty)mContext).showList();
            }
        });
        // 취소 버튼 설정
        dialogAd.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = dialogAd.create();
        alert.show();
    }
}
