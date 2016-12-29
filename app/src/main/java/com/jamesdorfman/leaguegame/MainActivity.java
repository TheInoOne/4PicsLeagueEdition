package com.jamesdorfman.leaguegame;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final String[] type = {"CHAMPIONS","ITEMS","PEOPLE"};
    final String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Random rand = new Random();
        String typeName = type[rand.nextInt(3)];

        ImageView picOne = (ImageView) findViewById(R.id.picOne);
        ImageView picTwo = (ImageView) findViewById(R.id.picTwo);
        ImageView picThree = (ImageView) findViewById(R.id.picThree);
        ImageView picFour = (ImageView) findViewById(R.id.picFour);

        ImageView[] listOfImageViews = {picOne,picTwo,picThree,picFour};


        AssetManager assetManager = getAssets();

        InputStream is = null;

        String word = "";
        try {
            //GET LIST OF THINGS INSIDE FOLDER
            String[] fileList = assetManager.list("FourPicsCollection/" + typeName);

            word = fileList[rand.nextInt(fileList.length)];
            for(int i=1;i<=4;i++) {
                String x = "FourPicsCollection/" + typeName + "/" + word + "/" + word + i + ".jpg";
                System.out.println(x);
                is = assetManager.open(x);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                listOfImageViews[i-1].setImageBitmap(bitmap);//i-1 because arrays start at 0 and i starts at 1
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //MAKE SQUARES THAT WILL GET FILLED IN WHEN CORRECT LETTERS ARE PICKED

        LinearLayout wordToGuessLinearLayout = (LinearLayout) findViewById(R.id.wordToGuessLinearLayout);
        for(int i=0;i<word.length();i++) {
            RelativeLayout relLay = new RelativeLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(30),
                    dpToPx(30)
            );
            params.setMargins(0, 0, dpToPx(3), 0);
            relLay.setLayoutParams(params);
            relLay.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            TextView tv = (TextView) getLayoutInflater().inflate(R.layout.letters_to_choose_tv, null);
            tv.setText(Character.toString(word.charAt(i)));
            tv.setTextSize(spToPx(9,this));
            tv.setTextColor(getResources().getColor(R.color.white));
            relLay.addView(tv);
            wordToGuessLinearLayout.addView(relLay);
            RelativeLayout.LayoutParams tvParams =
                    (RelativeLayout.LayoutParams)tv.getLayoutParams();
            tvParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            tv.setLayoutParams(tvParams);
        }
        //MAKE EMPTY WORD SQUARE THAT GET FILLED IN WHEN CORRECT LETTERS ARE GUESSED
        //GENERATE LIST OF 14 Letters (first, the letters not in the word, then add the letters in word)
        int numToGenerate = 14 - word.length();
        System.out.println("word: " + word + " numToGen length: " + numToGenerate);
        ArrayList<String> generatedLetters = new ArrayList<String>();
        ArrayList<String> alphabetCopy = new ArrayList<String>(Arrays.asList(alphabet));
        for(int i = 0;i<numToGenerate;i++){
            int randNum = rand.nextInt(alphabetCopy.size());
            generatedLetters.add(alphabetCopy.get(randNum));
            alphabetCopy.remove(randNum);
        }
        //add letters already in word, to array
        for(int i=0;i<word.length();i++){
            generatedLetters.add(Character.toString(word.charAt(i)));
        }

        System.out.println("genArrayLen: " + generatedLetters.size());
        //CREATE BOTTOM TWO ROWS OF LETTERS (MAYBE MAKE 3?? IF THAT IS NECESSARY)
        LinearLayout row1 = (LinearLayout) findViewById(R.id.lettersLinearLayout1);
        LinearLayout row2 = (LinearLayout) findViewById(R.id.lettersLinearLayout2);

        LinearLayout[] rowList = {row1,row2};
        for(int i=0;i<rowList.length;i++){
            for(int x=0;x<7;x++){
                RelativeLayout relLay = new RelativeLayout(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(50),
                    dpToPx(50)
                );
                params.setMargins(0,0,dpToPx(7),0);
                relLay.setLayoutParams(params);
                relLay.setBackgroundColor(getResources().getColor(R.color.black));

                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.letters_to_choose_tv, null);

                System.out.println("genSize: " + generatedLetters.size());
                int randInt = rand.nextInt(generatedLetters.size());
                tv.setText(generatedLetters.get(randInt));

                generatedLetters.remove(randInt);
                relLay.addView(tv);
                rowList[i].addView(relLay);

                RelativeLayout.LayoutParams tvParams =
                        (RelativeLayout.LayoutParams)tv.getLayoutParams();
                tvParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
                tv.setLayoutParams(tvParams);
            }
        }

    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int spToPx(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }

}
