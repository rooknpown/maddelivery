package com.example.stt02;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;

public class DeepLearning1 {
    private static final String MODEL_NAME = "noembed03.tflite";
    private Interpreter.Options options = new Interpreter.Options();
    private Interpreter mInterpreter;
    private final Komoran komoran;
    private HashMap<String, float[]> embedmap;
    public DeepLearning1(Activity activity, Komoran komo, HashMap<String, float[]> retmap) throws IOException {
        mInterpreter = new Interpreter(loadModelFile(activity), options);
        komoran = komo;
        embedmap = retmap;
    }
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {

        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String getAnswer(String input) {

        float[][] outputLocations = new float [1][1];
        List<Token> tokens = komoran.analyze(input).getTokenList();
        String temp;
        float[][][] embedlayer = new float[1][8][128];
        for(int i=0;i<8;i++){
            if(i<tokens.size()){
                temp = tokens.get(i).getMorph();
                if(embedmap.containsKey(temp)){
                    embedlayer[0][i] = embedmap.get(temp);
                }
                else{
                    embedlayer[0][i] = embedmap.get("byunghyungod");
                }
            }
            else {
                embedlayer[0][i] = embedmap.get("byunghyungod");
            }
        }


        mInterpreter.run(embedlayer, outputLocations);
        float val = outputLocations[0][0];
        Log.d("@@@@@@@@@@returnvalue  ", val + "asdfjkdafdslkafj;");
        if (val<0.65){
            return "그렇다면 어디에 맡겨둘까요?";
        }
        else if (val>0.73){
            return "그럼 3시에 뵙겠습니다";
        }
        else {
            return "네? 다시 말해주시겠어요?";
        }
    }
}
