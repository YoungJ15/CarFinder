package com.josermando.apps.carfinder;

import android.app.Fragment;
import android.app.ProgressDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by Josermando Peralta on 2/19/2016.
 */
public class CarListFragment extends Fragment {
    private ArrayAdapter<String> mCarAdapter;
    private AdView  mAdView;

    Button button;
    public EditText makeText;
    public EditText modelText;
    public EditText yearText;

    public CarListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCarAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_car, R.id.list_item_car_textview, new ArrayList<String>());
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
            updateCarList("honda","civic","2005");
            Toast.makeText(getActivity(),"Settings button clicked", Toast.LENGTH_LONG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateCarList(String make, String model, String year){
        FetchCarListTask carTask = new FetchCarListTask();
        carTask.execute(make, model, year);
    }

    /**   @Override
   public void onStart() {
        super.onStart();
        updateCarList();
    }
  **/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        button = (Button) rootView.findViewById(R.id.searchBtn);
        makeText = (EditText) rootView.findViewById(R.id.makeEt);
        modelText = (EditText) rootView.findViewById(R.id.modelEt);
        yearText = (EditText) rootView.findViewById(R.id.yearEt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String carMake = makeText.getText().toString();;
                String carModel = modelText.getText().toString();;
                String carYear = yearText.getText().toString();

                updateCarList(carMake, carModel, carYear);
                //getFragmentManager().beginTransaction().replace(R.id.container, new CarListFragment()).commit();
            }
        });
        //Ads info
             mAdView = (AdView) rootView.findViewById(R.id.ad_view2);
        //Create and Ad request
             AdRequest adRequest = new AdRequest.Builder().setGender(AdRequest.GENDER_MALE).build();
        //Start loading the ad in the background
           mAdView.loadAd(adRequest);

        List<String> carList = new ArrayList<>();
        mCarAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_car,
                R.id.list_item_car_textview,
                new ArrayList<String>());

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
