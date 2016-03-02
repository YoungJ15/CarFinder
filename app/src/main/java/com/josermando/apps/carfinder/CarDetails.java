package com.josermando.apps.carfinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CarDetails extends AppCompatActivity {

    private String mCarStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);
        Intent intent = getIntent();

        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            mCarStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            ((TextView) findViewById(R.id.detail_text)).setText(mCarStr);

        }

        String carID = mCarStr.trim().replaceAll(" ","").substring(6,15).replaceAll("%3A","");
        new FetchCarImagesTask().execute(carID);
    }
    public class FetchCarImagesTask extends AsyncTask<String, Void, Bitmap> {
        private final String LOG_TAG = FetchCarImagesTask.class.getSimpleName();

        private Bitmap getImageListFromJSON(String imageJSONString) {
            //JSON Objects Names to Extract
            final String AUTHOR = "authorNames";
            final String CAPTION_TRANSCRIPT = "captionTranscript";
            final String SUBTYPE = "subType";
            final String PHOTO_SRC = "photoSrcs";
            return null;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Log.v(LOG_TAG, "Pararms count: " + params.length);
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Variable that will contain the RAW JSON response
            String imageJSONString = null;
            //API Key
            String apiID = "y6hazeyr3t7tdhnpngjzy4rk";

            try{
                //Contructing the URL for the query and the other constant query parameterss
                final String BASE_URL = "http://api.edmunds.com/v1/api/vehiclephoto/service/findphotosbystyleid?";
                final String QUERY_PARM = "q";
                final String STYLEID = "styleId";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon().
                        appendQueryParameter(STYLEID,params[0] ).
                        appendQueryParameter(API_KEY, apiID).build();

                URL url = new URL(builtUri.toString().replaceAll("%2F","/"));
                Log.v(LOG_TAG,"Built Uri and URL: "+url);
                //Creating the Request and opening the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();//Reading the input into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    //Stream was empty, no need for parsing
                    return null;
                }
                imageJSONString = buffer.toString();
                Log.v(LOG_TAG, "Image JSON String: " + imageJSONString);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                //If no weather data was returned, there is no need for parsing
                return null;

            } finally {
                //Closing resources
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getImageListFromJSON(imageJSONString);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


    }
}
