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
import java.net.URL;

/**
 * Created by Josermando Peralta on 2/19/2016.
 */
public class FetchCarListTaskAparte extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchCarListTaskAparte.class.getSimpleName();

    private String[] getCarListFromJSON(String carListJSONString, int num) throws JSONException{
        //JSON Objects Names to Extract
        final String EDP_STYLES = "styles";
        final String EDP_MODELS = "models";
        final String EDP_NAME = "name";

        JSONObject carListJSON = new JSONObject(carListJSONString);
        JSONArray carArray = carListJSON.getJSONArray(EDP_STYLES);

        String [] resultString = new String[num];
        for(int i=0;i<carArray.length();i++){

            String make;
            String model;
            String modelName;

            //Get the JSON representing the car
            JSONObject Car = carArray.getJSONObject(i);
            
            String idCar = carListJSON.getString("id");
            Log.v(LOG_TAG,"ID Value: "+idCar);
            //modelName is in a child object called submodel
            Log.v(LOG_TAG, "Car Value "+Car.toString());
            //JSONObject carObject = Car.getJSONArray("submodel").getJSONObject(0);
            JSONObject carObject = Car.getJSONObject("submodel");
            Log.v(LOG_TAG, carObject.toString());
            modelName = carObject.getString("modelName");
            Log.v(LOG_TAG, modelName);

            resultString[0] = idCar+"- "+modelName;
            Log.v(LOG_TAG, "Result String: "+resultString[0]);

        }
        return resultString;
    }
    @Override
    protected String[] doInBackground(String... params) {
        Log.v(LOG_TAG, "Pararms count: "+params.length);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        //This variable will contain the raw JSON Response
        String carJSONString = null;

        //Query Values
        String format = "json";
        String state = "used";
        String view = "full";
        String divider = "%2F";
        String apiID = "y6hazeyr3t7tdhnpngjzy4rk";
        int num = 1;

        try {
            //Contructing the URL for the query and the other constant query parameters
            final String BASE_URL = "http://api.edmunds.com/api/vehicle/v2/honda/civic/2005";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon().
                   // appendQueryParameter(divider, params[0]).
                  //  appendQueryParameter(divider,params[1]).
                  //  appendQueryParameter(divider,params[2]).
                    appendQueryParameter(API_KEY, apiID).build();
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
            return getCarListFromJSON(carJSONString, num);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

  /**  @Override
    protected void onPostExecute(String[] strings) {
        if(strings != null){
            mCarAdapter.clear();
            for(String car : strings){
                mCarAdapter.add(car);
            }
        }
    }
  **/
}

