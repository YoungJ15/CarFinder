package com.josermando.apps.carfinder;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

/**
 * Created by Josermando Peralta on 2/19/2016.
 */
public class CarListFragment extends Fragment {
    private ArrayAdapter<String> mCarAdapter;

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

    }
}
