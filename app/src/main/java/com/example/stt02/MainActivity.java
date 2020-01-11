package com.example.stt02;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;


public class MainActivity extends Activity {
    Button sttbtn;
    Button ttsbtn;
    Button inputbtn;
    Button startbtn;
    EditText editbtn;
    TextView txtbtn;
    public static Komoran komoran;
    String json;
    public static HashMap<String, float[]>  retMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
        komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        loadJSONFromAsset();
        retMap = new Gson().fromJson(
                json, new TypeToken<HashMap<String, float[]>>() {}.getType()
        );
        setContentView(R.layout.activity_main);
        startbtn = findViewById(R.id.start);
        sttbtn = findViewById(R.id.btn_stt);
        inputbtn = findViewById(R.id.btn_ml);
        editbtn = findViewById(R.id.inputtext);
        txtbtn = findViewById(R.id.outtext);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CallingActivity.class);
                intent.putExtra("talk1", "안녕하세요, 택배입니다. 3시에 집에 계시나요?");
                startActivity(intent);
            }
        });

        sttbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), STTActivity.class);
                startActivity(intent);
            }
        });

        inputbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String text = editbtn.getText().toString();
                    Log.e("@@@", text + "The input text is");
                    String result = new DeepLearning1(MainActivity.this, komoran, retMap).getAnswer(text);
                    txtbtn.setText(result);
                }catch (IOException e) {
                    Toast.makeText(MainActivity.this,"Failed to create Classifier", Toast.LENGTH_LONG).show();
                    Log.e("@@@", "Failed to create Classifier", e);
                }

            }
        });
    }

    public void loadJSONFromAsset() {
        try {
            InputStream is = this.getAssets().open("embedding03.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        return;
    }
}

