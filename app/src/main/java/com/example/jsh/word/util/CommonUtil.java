package com.example.jsh.word.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class CommonUtil {

    // 싱글톤 패턴을 위한 함수
    private static volatile CommonUtil singletonInstance = null;

    public static CommonUtil getInstance() {
        if (singletonInstance == null) {
            synchronized (SqliteUtil.class) {
                if (singletonInstance == null) {
                    singletonInstance = new CommonUtil();
                }
            }
        }
        return singletonInstance;
    }

    public HashMap<String, String> csv2arrList(String csvStr)
    {
        HashMap<String, String> result = new HashMap<String, String>();
        String factor = ((csvStr.indexOf("\t")!=-1)?"\t":",");
        String[] arr = csvStr.replaceAll("\"", "").split("\\n");
        for (int i=0;i < arr.length;i++)
        {
            int pos = arr[i].indexOf(factor);
            result.put(arr[i].substring(0,pos ), arr[i].substring(pos + 1));
        }
        return result;
    }
    public String readFromFile(String fileName){
        String body = "";
        StringBuffer bodytext = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(new File(fileName));
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while ((body = bufferReader.readLine()) != null) {
                bodytext.append(body).append(System.getProperty("line.separator") );
            }
            return bodytext.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public String getId(String accessToken) {
        String header = "Bearer " + accessToken; // Bearer 다음에 공백 추가

        try {
            String apiURL = "https://openapi.naver.com/v1/nid/me";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", header);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return (response.toString());
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            Log.e("what",e.toString());
        }
        return null;
    }
}