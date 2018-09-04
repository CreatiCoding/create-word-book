package com.example.jsh.word.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.jsh.word.R;
import com.example.jsh.word.util.SqliteUtil;
import com.nhn.android.naverlogin.OAuthLogin;

public class SplashActivity extends AppCompatActivity {

    private SqliteUtil sqliteUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OAuthLogin mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                SplashActivity.this
                ,"6zTwkvHBs15oYYGh17BH"
                ,"i8GmpSo1Vp"
                ,"wordbook"
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );
        mOAuthLoginModule.logoutAndDeleteToken(this);

        //setContentView(R.layout.activity_main);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        sqliteUtil = SqliteUtil.getInstance(this, null);
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        // 처음 접속이라면 프로퍼티 로딩
        if(pref.getBoolean("ISFIRST", true)){
            Toast.makeText(this,"It is first", Toast.LENGTH_SHORT).show();
            PreferenceManager.setDefaultValues(this, R.xml.settings, false);
            sqliteUtil.checkTable(sqliteUtil.getWritableDatabase());
        }else{
            sqliteUtil.checkTable(sqliteUtil.getWritableDatabase());
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
