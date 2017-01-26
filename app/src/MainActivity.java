package com.jamesdorfman.leaguegame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    final String[] type = {"CHAMPIONS", "ITEMS", "PEOPLE"};
    final String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    int strikes = 0;

    int locationInWord = 0;

    LinearLayout row1;
    LinearLayout row2;

    final int[] curPlaceFinal = new int[1];

    final String[] curWord = new String[1];

    String word;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        row1 = (LinearLayout) findViewById(R.id.lettersLinearLayout1);
        row2 = (LinearLayout) findViewById(R.id.lettersLinearLayout2);

        Random rand = new Random();
        String typeName = type[rand.nextInt(3)];

        ImageView picOne = (ImageView) findViewById(R.id.picOne);
        ImageView picTwo = (ImageView) findViewById(R.id.picTwo);
        ImageView picThree = (ImageView) findViewById(R.id.picThree);
        ImageView picFour = (ImageView) findViewById(R.id.picFour);

        ImageView[] listOfImageViews = {picOne, picTwo, picThree, picFour};


        AssetManager assetManager = getAssets();

        InputStream is = null;

        final LinearLayout[] rowList = {row1, row2};

        try {
            //GET LIST OF THINGS INSIDE FOLDER
            String[] fileList = assetManager.list("FourPicsCollection/" + typeName);

            word = fileList[rand.nextInt(fileList.length)];
            for (int i = 1; i <= 4; i++) {
                String x = "FourPicsCollection/" + typeName + "/" + word + "/" + word + i + ".jpg";
                System.out.println(x);
                is = assetManager.open(x);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                listOfImageViews[i - 1].setImageBitmap(bitmap);//i-1 because arrays start at 0 and i starts at 1
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //MAKE SQUARES THAT WILL GET FILLED IN WHEN CORRECT LETTERS ARE PICKED

        for (int i = 0; i < word.length(); i++) {
            final Button tv = (Button) getLayoutInflater().inflate(R.layout.letters_to_choose_tv, null);
            tv.setTextColor(getResources().getColor(R.color.black));
            //tv.setText("A");
            final String finalWord2 = word;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!((Button)view).getText().equals("")) {
                        System.out.println("CLICKED!");
                        int rowInt = 1;
                        if (((Button)row1.getChildAt(row1.getChildCount()-1)).getText().equals("") || row1.getChildCount()<7) {
                            int count = 6;
                            /*while(((Button)row1.getChildAt(count)).getText().equals("")){
                                row1.removeViewAt(count);
                                count = count-1;
                            }*/
                            System.out.println("space in row1!");
                            addButton(0, ((Button) view).getText().toString());
                            rowInt = 0;
                        } else {
                            System.out.println("space in row 2!");
                            addButton(1, ((Button) view).getText().toString());
                        }
                        LinearLayout parent = (LinearLayout) tv.getParent();

                        Button tvCopy = tv;
                        parent.removeView(tv);

                        tvCopy.setText("");
                        //parent.removeViewAt(parent.getChildCount()-1);
                        parent.addView(tvCopy);
                        System.out.println("ADDED CHILD: count:" + parent.getChildCount());
                        curPlaceFinal[0] = curPlaceFinal[0] - 1;
                        locationInWord = locationInWord-1;
                        curWord[0] = curWord[0].substring(0,curWord[0].length()-1);
                        System.out.println("Current word: " + curWord[0]);

                        LinearLayout toGuessLayout = (LinearLayout) findViewById(R.id.wordToGuessLinearLayout);
                        for(int j=0;j<toGuessLayout.getChildCount();j++) {
                            Button x = ((Button) toGuessLayout.getChildAt(j));
                            x.setBackground(getResources().getDrawable(R.drawable.border));
                        }
                        System.out.println("WORD IS: " + word);
                    }
                }
            });
            //tv.setText(Character.toString(word.charAt(i)));
            final LinearLayout wordToGuessLinearLayout = (LinearLayout) findViewById(R.id.wordToGuessLinearLayout);
            wordToGuessLinearLayout.addView(tv);
            LinearLayout.LayoutParams tvParams =
                    (LinearLayout.LayoutParams) tv.getLayoutParams();
            tvParams.gravity = Gravity.CENTER_HORIZONTAL;
            tvParams.height = dpToPx(35);
            tvParams.width = dpToPx(35);
            tvParams.setMargins(0,0,dpToPx(7),0);
            tv.setBackground(getResources().getDrawable(R.drawable.border));
            tv.setLayoutParams(tvParams);
        }
        //MAKE EMPTY WORD SQUARE THAT GET FILLED IN WHEN CORRECT LETTERS ARE GUESSED
        //GENERATE LIST OF 14 Letters (first, the letters not in the word, then add the letters in word)
        int numToGenerate = 14 - word.length();
        System.out.println("word: " + word + " numToGen length: " + numToGenerate);
        ArrayList<String> generatedLetters = new ArrayList<String>();
        ArrayList<String> alphabetCopy = new ArrayList<String>(Arrays.asList(alphabet));
        for (int i = 0; i < numToGenerate; i++) {
            int randNum = rand.nextInt(alphabetCopy.size());
            String letter = alphabetCopy.get(randNum);
            generatedLetters.add(alphabetCopy.get(randNum));
            alphabetCopy.remove(randNum);
            System.out.println("Cur word: " + word + " generating " + letter);
        }
        //add letters already in word, to array
        for (int i = 0; i < word.length(); i++) {
            generatedLetters.add(Character.toString(word.charAt(i)));
        }

        System.out.println("genArrayLen: " + generatedLetters.size());
        //CREATE BOTTOM TWO ROWS OF LETTERS (MAYBE MAKE 3?? IF THAT IS NECESSARY)


        System.out.println("generated letters: " + generatedLetters);
        curPlaceFinal[0] = 0;
        for (int i = 0; i < rowList.length; i++) {
            for (int x = 0; x < 7; x++) {
                int randInt = rand.nextInt(generatedLetters.size());
                System.out.println("This is I: " + i);
                addButton(i,generatedLetters.get(randInt));
                generatedLetters.remove(randInt);
            }
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int spToPx(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public void addButton(int row, String text){
        LinearLayout[] rowList = {row1,row2};
        for(int f=0;f<rowList[row].getChildCount();f++){
            Button b = (Button) rowList[row].getChildAt(f);
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) b.getLayoutParams();
            p.setMargins(0,0,dpToPx(7),0);
            b.setLayoutParams(p);
        }

            Button tv = (Button) getLayoutInflater().inflate(R.layout.letters_to_choose_tv, null);

        tv.setTextColor(getResources().getColor(R.color.black));
        final String finalWord = word;
        for(int f=0;f<rowList[row].getChildCount();f++){
            if(((Button)rowList[row].getChildAt(f)).getText()==""){
                rowList[row].removeViewAt(f);
                f = f-1;
                System.out.println("REMOVING!");
            }
        }
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationInWord!=finalWord.length()) {
                    Button b = (Button)view;
                    String currentChar = b.getText().toString();

                    if(curWord[0]!=null) {
                        curWord[0] = curWord[0] + currentChar;
                    }
                    else{
                        curWord[0] = currentChar;
                    }
                    ViewGroup parent = (ViewGroup) view.getParent();
                    Button viewCopy = (Button) view;
                    parent.removeView(view);

                    viewCopy.setText("");
                    viewCopy.setBackground(null);
                    viewCopy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Do nothing
                        }
                    });
                    parent.addView(viewCopy);
                    //int ind = adjustedw.indexOf(currentChar);

                    // if(ind!=-1){
                    System.out.println("loc:" + locationInWord);
                    System.out.println("this is place: " + curPlaceFinal[0]);
                    final LinearLayout wordToGuessLinearLayout = (LinearLayout) findViewById(R.id.wordToGuessLinearLayout);
                    System.out.println("Cur place final: " + curPlaceFinal[0]);
                    ((Button)wordToGuessLinearLayout.getChildAt(curPlaceFinal[0])).setText(currentChar);
                    curPlaceFinal[0] = curPlaceFinal[0] + 1;
                    locationInWord = locationInWord + 1;

                    if(word.length() == locationInWord){
                        LinearLayout toGuessLayout = (LinearLayout) findViewById(R.id.wordToGuessLinearLayout);
                        for(int j=0;j<toGuessLayout.getChildCount();j++) {
                            Button x = ((Button) toGuessLayout.getChildAt(j));
                            System.out.println(curWord[0] + " == " + word);
                            word = word.toLowerCase ();
                            curWord[0] = curWord[0].toLowerCase();
                            if (word.equals(curWord[0])) {
                                //x.setTextColor(getResources().getColor(R.color.red));
                                x.setBackground(getResources().getDrawable(R.drawable.green_border));
                                correctWord(word);
                            }
                            else{
                                x.setBackground(getResources().getDrawable(R.drawable.red_border));
                            }
                        }
                    }
                }
            }
        });

        tv.setText(text);

        rowList[row].addView(tv);

/*        for(int m=0;rowList[row].getChildCount()<7;m++){
            Button tvCopy = tv;
            tvCopy.setBackground(null);
            tvCopy.setText(null);
            ((ViewGroup)tvCopy.getParent()).removeView(tvCopy);
            rowList[row].addView(tvCopy);
        }*/
        LinearLayout.LayoutParams tvParams =
                (LinearLayout.LayoutParams) tv.getLayoutParams();
        tvParams.height = dpToPx(34);
        tvParams.width = dpToPx(34);
        if(rowList[row].getChildCount()<7 || ((Button)rowList[row].getChildAt(rowList[row].getChildCount()-1)).getText().equals("") ){
            System.out.println("<7!!");
            tvParams.setMargins(0, 0, dpToPx(7), 0);
           /* if(!(rowList[row].getChildCount()-1<1)) {
                ((Button) rowList[row].getChildAt(rowList[row].getChildCount()-1)).setText("Z");
                ((LinearLayout.LayoutParams) ((Button) rowList[row].getChildAt(rowList[row].getChildCount()-1)).getLayoutParams()).setMargins(0, 0, dpToPx(7), 0);
            }*/
        }
        tv.setBackground(getResources().getDrawable(R.drawable.border));
        //tv.setBackgroundColor(getResources().getColor(R.color.white));
        tv.setTextColor(getResources().getColor(R.color.black));
        tvParams.gravity = Gravity.CENTER_HORIZONTAL;
        tv.setLayoutParams(tvParams);

        for (int f=0;rowList[row].getChildCount()!=7;f++){
            Button tvCopy = (Button) getLayoutInflater().inflate(R.layout.letters_to_choose_tv, null);
            tvCopy.setText("");
            tvCopy.setBackground(null);
//            tvCopy.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
            int dpNum = dpToPx(7);
            if(rowList[row].getChildCount()==6){
                dpNum=0;
            }
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams( dpToPx(34), dpToPx(34) );
            p.setMargins(0,0,dpNum,0);
            tvCopy.setLayoutParams(p);
            Button previousButton = ((Button)rowList[row].getChildAt(rowList[row].getChildCount()-1));
            LinearLayout.LayoutParams prevParams = (LinearLayout.LayoutParams) previousButton.getLayoutParams();
            prevParams.setMargins(0,0,dpToPx(7),0);
            previousButton.setLayoutParams(prevParams);
            //previousButton.setText("Z");
            rowList[row].addView(tvCopy);
            //asd.setBackground(null);
        }
    }

    public void correctWord(String word){
        System.out.println("in the function");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Correct Word: \nYou have earned 50 lp")
                .setTitle("Success!")
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                        overridePendingTransition( R.anim.right_to_left, R.anim.stable );
                    }
                });
        builder.create();
        builder.show();
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
