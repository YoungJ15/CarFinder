package com.josermando.apps.carfinder;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.Arrays;
import java.util.List;

/**
 * Created by Josermando Peralta on 2/19/2016.
 */
public class CarListFragment extends Fragment {
    private ArrayAdapter<String> mCarAdapter;

    public ArrayAdapter<String> getmCarAdapter() {
        return mCarAdapter;
    }

    public CarListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Fragment will handle menu events --- Basically receive callback for the methods onCreateOptionsMenu & onOptionsItemSelected
        inflater.inflate(R.menu.carfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //To handle action bar item clicks.
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            updateCarList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateCarList(){
        FetchCarListTask carTask = new FetchCarListTask();
        carTask.execute("toyota","corolla","2001");
    }

    @Override
    public void onStart() {
        super.onStart();
        updateCarList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        List<String> carList = new ArrayList<>();
        mCarAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_car,
                R.id.list_item_car_textview,
                carList);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_car);
        listView.setAdapter(mCarAdapter);
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/
        return rootView;
    }

    public class FetchCarListTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchCarListTask.class.getSimpleName();

        private String[] getCarListFromJSON(String carListJSONString) throws JSONException {
            //JSON Objects Names to Extract
            final String EDP_STYLES = "styles";
            final String EDP_SUBMODEL = "submodel";

            JSONObject carListJSON = new JSONObject(carListJSONString);
            JSONArray carArray = carListJSON.getJSONArray(EDP_STYLES);
            Log.v(LOG_TAG+"Car Array Length: ",String.valueOf(carArray.length()));

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
    //            Log.v(LOG_TAG,"ID Value: "+idCar);
                //modelName is in a child object called submodel
                JSONObject carObject = Car.getJSONObject(EDP_SUBMODEL);
      //          Log.v(LOG_TAG, carObject.toString());

                modelName = carObject.getString("modelName");
     //           Log.v(LOG_TAG, modelName);

                resultString[i] = "Car ID: "+idModel+" - "+modelName + " - "+year;
                Log.v(LOG_TAG, "Result String: "+resultString[i]);

            }
            Log.v(LOG_TAG, "Resultado final: "+resultString);
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


            try {
                //Contructing the URL for the query and the other constant query parameterss
                final String BASE_URL = "http://api.edmunds.com/api/vehicle/v2/"+params[0]+"/"+params[1]+"/"+params[2];
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
