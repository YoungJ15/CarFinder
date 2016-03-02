package com.josermando.apps.carfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CarActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AdView mAdView;
    private String model;
    private String make;
    private String year;
    private ArrayAdapter<String> mCarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        setUpToolbar();
        populateListView();
        setUpAdView();

    }

    private void updateCarList(String make, String model, String year) {
        new FetchCarListTask().execute(make, model, year);
    }
    private void populateListView(){
        Intent intent = getIntent();
        make = intent.getStringExtra("MAKE");
        model = intent.getStringExtra("MODEL");
        year = intent.getStringExtra("YEAR");

        updateCarList(make, model, year);

        mCarAdapter = new ArrayAdapter<String>(this, R.layout.list_item_car, R.id.list_item_car_textview, new ArrayList<String>());
        List<String> carList = new ArrayList<>();
        mCarAdapter = new ArrayAdapter<>(this,
                R.layout.list_item_car,
                R.id.list_item_car_textview,
                new ArrayList<String>());

        ListView listView = (ListView) findViewById(R.id.listview_car);
        listView.setAdapter(mCarAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String car = mCarAdapter.getItem(position);
               // Toast.makeText(getApplicationContext(), "Details Clicked", Toast.LENGTH_SHORT).show();
                Intent detailIntent = new Intent(getApplicationContext(), CarDetails.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT,car);
                startActivity(detailIntent);
            }
        });
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Car Info");
        toolbar.inflateMenu(R.menu.menu_main);
    }

    private void setUpAdView(){
        mAdView = (AdView) findViewById(R.id.ad_view2);
        AdRequest adRequest = new AdRequest.Builder().setGender(AdRequest.GENDER_MALE).build();
        mAdView.loadAd(adRequest);
    }

        @Override
        public void onPause() {
            if (mAdView != null) {
            mAdView.pause();
            }
            super.onPause();
        }
        /** Called when returning to the activity */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
            mAdView.resume();
            }
            }
            /** Called before the activity is destroyed */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
            mAdView.destroy();
            }
            super.onDestroy();
            }

    public class FetchCarListTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchCarListTask.class.getSimpleName();

        private String[] getCarListFromJSON(String carListJSONString) throws JSONException {
            //JSON Objects Names to Extract
            final String EDP_STYLES = "styles";
            final String EDP_SUBMODEL = "submodel";

            JSONObject carListJSON = new JSONObject(carListJSONString);
            JSONArray carArray = carListJSON.getJSONArray(EDP_STYLES);
            Log.v(LOG_TAG + "Car Array Length: ", String.valueOf(carArray.length()));

            String [] resultString = new String[carArray.length()];
            for(int i=0;i<carArray.length();i++){

                String modelName;
                String idCar;
                String idModel;
                String year;

                //Get the JSON representing the car
                JSONObject Car = carArray.getJSONObject(i);

                idCar = carListJSON.getString("id");
                year = carListJSON.getString("year");
                idModel = carArray.getJSONObject(i).getString("id");
                JSONObject carObject = Car.getJSONObject(EDP_SUBMODEL);
                modelName = carObject.getString("modelName");

                resultString[i] = "Car ID: "+idModel+" - "+modelName + " - "+year;
                Log.v(LOG_TAG, "Result String: "+resultString[i]);

            }
            Log.v(LOG_TAG, "Resultado final: "+resultString);
            return resultString;
        }
        @Override
        protected String[] doInBackground(String... params) {
            Log.v(LOG_TAG, "Pararms count: " + params.length);
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            //This variable will contain the raw JSON Response
            String carJSONString = null;

            //Query Values
            String apiID = "y6hazeyr3t7tdhnpngjzy4rk";


            try {
                //Contructing the URL for the query and the other constant query parameterss
                final String BASE_URL = "http://api.edmunds.com/api/vehicle/v2/"+params[0]+"/"+params[1]+"/"+params[2];
                //final String BASE_URL = "http://api.edmunds.com/api/vehicle/v2/honda/crv/2000";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon().
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
                return getCarListFromJSON(carJSONString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if(strings != null){
                mCarAdapter.clear();
                for(String car : strings){
                    mCarAdapter.add(car);
                }
            }
        }
    }



}
