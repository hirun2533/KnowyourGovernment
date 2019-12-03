package com.example.knowyourgovernment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "Main";
    private TextView currentLocation;
    private LocationManager locationManager;
    private RecyclerView recyclerView;
    private GovernmentAdapter rAdapter;
    private static int MY_LOCATION_REQUEST_CODE_ID = 329;
    private Criteria criteria;
    private ArrayList<Government> governmentArray = new ArrayList<>();
    private ArrayList<Government> offArr = new ArrayList<>();
    public static String saveLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        rAdapter = new GovernmentAdapter(governmentArray,this);
        recyclerView.setAdapter(rAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        currentLocation = findViewById(R.id.current);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if(NetworkCheck()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_LOCATION_REQUEST_CODE_ID);
            } else {
                if(saveLocal != null){
                    currentLocation.setText(saveLocal);
                    new GovernmentDownloader(MainActivity.this).execute(saveLocal);
                }
                else
                LocationSetup();
            }
        } else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
            AlertDialog dialog = builder.create();
            dialog.show();
            currentLocation.setText("No data for Location");
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_avtivity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem Item) {

        switch (Item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(this, aboutActivity.class);
                startActivity(intent);
                Toast.makeText(this, "You want to see the information", Toast.LENGTH_SHORT).show();

                break;
            case R.id.location:
                    OfficeDialogSelect();
                return true;

            default:
                return super.onOptionsItemSelected(Item);

        }
        return true;
    }


    public void OfficeDialogSelect() {

        if (!NetworkCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText text = new EditText(this);
            text.setInputType(InputType.TYPE_CLASS_TEXT);
            text.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(text);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String string = text.getText().toString();
                    Log.d(TAG, "onClick: input : " + string);
                    new GovernmentDownloader(MainActivity.this).execute(string);
                    saveLocal = string;

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "onClick: Cancel clicked, do nothing");


                }
            });

            builder.setMessage("Enter a City, State, or Zip Code:");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private boolean NetworkCheck(){

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            Log.d(TAG, "connected: Network is connected!!! ");

            return true;
        } else {
            Log.d(TAG, "connected: Network is not connected!!! ");
            return false;
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(MainActivity.this, GovernmentActivity.class);
        int pos = recyclerView.getChildLayoutPosition(v);
        Government Government = governmentArray.get(pos);
        intent.putExtra("currentLocation", currentLocation.getText().toString());

        Bundle bun = new Bundle();
        bun.putSerializable("official", Government);
        Log.d(TAG, "onClick: Government : " + Government);
        intent.putExtras(bun);
        startActivity(intent);

    }

    @Override
    public boolean onLongClick(View v){
        return false;
    }


    public void UpdateLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        Log.d(TAG, "address: Lat: " + latitude + ", Lon: " + longitude);

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);

            String zip =  addresses.get(0).getPostalCode();
            Log.d(TAG, "UpdateLocation: zip : "+ zip);

            new GovernmentDownloader(MainActivity.this).execute(zip);

//            String city =  addresses.get(0).getAddressLine(0);
//            Log.d(TAG, "UpdateLocation: city : "+ city);
//
//            new GovernmentDownloader(MainActivity.this).execute(city);

        } catch (IOException e) {
            Log.d(TAG, "doAddress: " + e.getMessage());
            Toast.makeText(MainActivity.this,"Address cannot be acquired from provided latitude/longitude",
                    Toast.LENGTH_SHORT).show();

        }


    }


    public void addOfficeGov(Object[] getCity){

        if(getCity != null){
            String getstring = getCity[0].toString();
            for (Government ignored : offArr = (ArrayList<Government>) getCity[1]) {
                currentLocation.setText(getstring);
                governmentArray.clear();
                int i =0;
                while(i<offArr.size()){
                    governmentArray.add( offArr.get(i));
                    i++;
                }
            }
        }
        else{

            currentLocation.setText("No Data For Location");
            governmentArray.clear();
        }
        rAdapter.notifyDataSetChanged();
    }


    @SuppressLint("MissingPermission")

    public void LocationSetup(){

       String bestProvider = locationManager.getBestProvider(criteria, true);

        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation != null) {


          double getLat =  currentLocation.getLatitude();
          double getLon =   currentLocation.getLongitude();

            UpdateLocation(getLat,getLon);
             
        }
        else {
            Log.d(TAG, "No location");
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults)
    {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                LocationSetup();
                return;
            }
        }

    }



}
