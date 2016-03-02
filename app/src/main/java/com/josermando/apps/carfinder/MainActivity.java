package com.josermando.apps.carfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    //Ads
    private AdView  mAdView;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    private Button button;
    public EditText makeText;
    public EditText modelText;
    public EditText yearText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();

        button = (Button) findViewById(R.id.searchBtn);
        makeText = (EditText) findViewById(R.id.makeEt);
        modelText = (EditText) findViewById(R.id.modelEt);
        yearText = (EditText) findViewById(R.id.yearEt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String carMake = makeText.getText().toString();
                ;
                String carModel = modelText.getText().toString();
                ;
                String carYear = yearText.getText().toString();

                Intent intent = new Intent(getBaseContext(), CarActivity.class);
                intent.putExtra("MAKE",carMake);
                intent.putExtra("MODEL",carModel);
                intent.putExtra("YEAR",carYear);
                startActivity(intent);
            }
        });
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Car Info");
        toolbar.inflateMenu(R.menu.menu_main);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
