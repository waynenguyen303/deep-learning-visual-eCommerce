package com.example.waynage7.CS5542_lab3;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

//google custom search api key: AIzaSyAXVA5LjGNJBKqhekzKEfzFrMexz4xOjNs
//google search engine id: 010717275162581194031:craoix0bigm

//code from https://github.com/fanysoft/Android_Google_Custom_SearchDemo

public class product_search extends AppCompatActivity {

    EditText eText;
    Button btn;
    Button backbtn;
    TextView resultTextView;
    TextView resultTextView1;
    ProgressBar progressBar;

    private static final String TAG = "searchApp";
    static String result = null;
    Integer responseCode = null;
    String responseMessage = "";
    String s ="No Image Selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        Intent intent1 = getIntent();
        s = intent1.getStringExtra("ML_RESULTS");

        Log.d(TAG, "**** APP START ****");

        // GUI init
        eText = (EditText) findViewById(R.id.edittext);
        eText.setText(s);
        btn = (Button) findViewById(R.id.button);
        resultTextView = (TextView) findViewById(R.id.textView1);
        resultTextView1 = (TextView) findViewById(R.id.textView2);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // button onClick
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                final String searchString = eText.getText().toString();
                Log.d(TAG, "Searching for : " + searchString);
                resultTextView.setText("Searching for : " + searchString);

                // hide keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                // looking for
                String searchStringNoSpaces = searchString.replace(" ", "+");

                // Your API key
                // TODO replace with your value
                String key="AIzaSyAXVA5LjGNJBKqhekzKEfzFrMexz4xOjNs";

                // Your Search Engine ID
                // TODO replace with your value
                String cx = "010717275162581194031:craoix0bigm";

                String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchStringNoSpaces + "&key=" + key + "&cx=" + cx + "&alt=json";
                URL url = null;
                try {
                    url = new URL(urlString);
                } catch (MalformedURLException e) {
                    Log.e(TAG, "ERROR converting String to URL " + e.toString());
                }
                Log.d(TAG, "Url = "+  urlString);


                // start AsyncTask
                GoogleSearchAsyncTask searchTask = new GoogleSearchAsyncTask();
                searchTask.execute(url);

            }
        });

        //back button onclick
        backbtn = (Button) findViewById(R.id.backbutton);
        backbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMain();

            }

        });

    }

    public void openMain(){
        Intent intent = new Intent(this, MainActivity_addimage.class);
        startActivity(intent);
    }

    private class GoogleSearchAsyncTask extends AsyncTask<URL, Integer, String>{

        protected void onPreExecute(){
            Log.d(TAG, "AsyncTask - onPreExecute");
            // show progressbar
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(URL... urls) {

            URL url = urls[0];
            Log.d(TAG, "AsyncTask - doInBackground, url=" + url);

            // Http connection
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                Log.e(TAG, "Http connection ERROR " + e.toString());
            }


            try {
                responseCode = conn.getResponseCode();
                responseMessage = conn.getResponseMessage();
            } catch (IOException e) {
                Log.e(TAG, "Http getting response code ERROR " + e.toString());
            }

            Log.d(TAG, "Http response code =" + responseCode + " message=" + responseMessage);

            try {

                if(responseCode == 200) {

                    // response OK

                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = rd.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    rd.close();

                    conn.disconnect();

                    result = sb.toString();

                    String[] result1 = result.split("link\":");
                    String[] link = result1[1].split(",");
                    String[] url1 = link[0].split("\"");


                    Log.d(TAG, "result=" + result);

                    return url1[1];

                }else{

                    // response problem

                    String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                    Log.e(TAG, errorMsg);
                    result = errorMsg;
                    return  result;

                }
            } catch (IOException e) {
                Log.e(TAG, "Http Response ERROR " + e.toString());
            }


            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "AsyncTask - onProgressUpdate, progress=" + progress);

        }

        protected void onPostExecute(String result) {

            Log.d(TAG, "AsyncTask - onPostExecute, result=" + result);

            // hide progressbar
            progressBar.setVisibility(View.GONE);

            // make TextView scrollable
            resultTextView.setMovementMethod(new ScrollingMovementMethod());
            // show result
            resultTextView.setClickable(true);
            resultTextView.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='"+result+"'> Amazon hyperlink of "+s+" </a>";
            resultTextView1.setLinkTextColor(Color.BLACK);
            resultTextView.setText(Html.fromHtml(text));

            // make TextView scrollable
            resultTextView1.setMovementMethod(new ScrollingMovementMethod());
            // show result
            resultTextView1.setClickable(true);
            resultTextView1.setMovementMethod(LinkMovementMethod.getInstance());
            String text1 = "Google Search hyperlink of "+s;


            resultTextView1.setText(Html.fromHtml(text1));
            resultTextView1.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    intent.putExtra(SearchManager.QUERY, s);
                    startActivity(intent);
                }
            });

        }


    }




}
