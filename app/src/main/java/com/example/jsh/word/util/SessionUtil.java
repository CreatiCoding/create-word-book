package com.example.jsh.word.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by jsh on 2017-12-07.
 */

public class SessionUtil {

    private String accessToken;
    private String id;
    private String profileJson;

    public String getProfileJson() {
        return profileJson;
    }

    public void setProfileJson(String profileJson) {
        this.profileJson = profileJson;
    }

    public SessionUtil(){
        accessToken = null;
        id = null;
    }

    @Override
    public String toString() {
        return "SessionUtil{" +
                "accessToken='" + accessToken + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    // 싱글톤 패턴을 위한 함수
    private static volatile SessionUtil singletonInstance = null;

    public static SessionUtil getInstance() {
        if (singletonInstance == null) {
            synchronized (SqliteUtil.class) {
                if (singletonInstance == null) {
                    singletonInstance = new SessionUtil();
                }
            }
        }
        return singletonInstance;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void toastSession(Context context){
        Toast.makeText(context, this.toString(), Toast.LENGTH_LONG).show();
    }
    public void setInfoFromProfile(String response) {
        try{
            JSONArray jsonArray = new JSONArray("["+response+"]");
            Log.e("response", response);
            //parsing json
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject childObj = jsonObject.getJSONObject("response");
                //String profile_image = childObj.getString("profile_image");
                String id = childObj.getString("email");
                this.setId(id);
                Log.e("id", id);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

}
