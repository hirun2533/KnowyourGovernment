package com.example.knowyourgovernment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class photoDetailActivity extends AppCompatActivity {


    public static final String TAG = "PhotoActivity";

    public ImageView image;
    public ImageView partylogo;
    public TextView office;
    public TextView name;
    public Government official;
    public TextView party;
    public TextView current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);

        current = findViewById(R.id.current);
        office = findViewById(R.id.office);
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        partylogo = findViewById(R.id.partylogo);
        party = findViewById(R.id.party);

        Intent intent = this.getIntent();
        Bundle bun = intent.getExtras();
        official = (Government) bun.getSerializable("official");
        current.setText(intent.getStringExtra("currentLocation"));


        String string = intent.getStringExtra("currentLocation");
        current.setText(string.toString());

        office.setText(intent.getStringExtra("Government"));
        name.setText(intent.getStringExtra("name"));

        setBackground();

        if(NetworkCheck()) {
            LoadImage();

        } else{
            image.setImageResource(R.drawable.brokenimage);
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

    private void setBackground(){

        Intent intent = this.getIntent();
        String BG = intent.getStringExtra("BG");
        if (BG.equals("Rep")) {
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.Rep));
            partylogo.setImageResource(R.drawable.rep_logo);
        }
        if (BG.equals("Dem")) {
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.Dem));
            partylogo.setImageResource(R.drawable.dem_logo);
        }
        if(BG.equals("Nonpartisan")){
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.Nonpartisan));
            partylogo.setImageDrawable(null);
        }

    }

    private void LoadImage(){

        Intent intent = this.getIntent();
        final String photoUrl = intent.getStringExtra("photoUrl");
        Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                final String changedUrl = photoUrl.replace("http:", "https:");
                picasso.load(changedUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(image);

            }
        }).build();

        picasso.load(photoUrl)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(image);
    }

}
