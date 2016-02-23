package com.josermando.apps.carfinder;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Josermando Peralta on 2/19/2016.
 */
public class FetchCarListTask extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchCarListTask.class.getSimpleName();

    private String[] getCarListFromJSON(String carListJSONString, int num) throws JSONException{
        //JSON Objects Names to Extract
        final String EDP_MAKES = "makes";
        final String EDP_MODELS = "models";
        final String EDP_NAME = "name";

        JSONObject carListJSON = new JSONObject(carListJSONString);
        JSONArray carArray = carListJSON.getJSONArray(EDP_MAKES);

        String [] resultString = new String[num];
        for(int i=0;i<carArray.length();i++){

            String make;
            String model;
            String name;

            //Get the JSON representing the day
            JSONObject numCar = carArray.getJSONObject(i);
            //Description JSON Object
            JSONObject carObject = numCar.getJSONArray(EDP_MODELS).getJSONObject(0);
            name = carObject.getString(EDP_NAME);

            resultString[i] = " *-* "+name;

        }
        return resultString;
    }
    @Override
    protected String[] doInBackground(String... params) {
        if(params.length == 0){
            return  null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        //This variable will contain the raw JSON Response
        String carJSONString = null;

        //Query Values
        String format = "json";
        String state = "used";
        String view = "basic";
        String apiID = "y6hazeyr3t7tdhnpngjzy4rk";
        int num = 1;

        try {
            //Contructing the URL for the query and the other constant query parameters
            final String BASE_URL = "http://api.edmunds.com/api/vehicle/v2/";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon().
                    appendQueryParameter(API_KEY,apiID).build();
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG,"Built Uri: "+builtUri.toString());
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
            carJSONString = buffer.toString();
            Log.v(LOG_TAG, "Car JSON String: " + carJSONString);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            //If no car data was returned, there is no need for parsing
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
            return getCarListFromJSON(carJSONString, num);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

}

