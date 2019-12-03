package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class GovernmentActivity extends AppCompatActivity {

    public static final String TAG = "GovernmentActivity";

    public TextView current, office, name, party, address, phone, email, website, add, phonenumber, emailaddress, web;
    public ImageView image, facebook, twitter, youtube, google, partylogo;
    public Government governmentGov;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.office_activity);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        youtube = findViewById(R.id.youtube);
        google = findViewById(R.id.google);
        facebook.setImageResource(R.drawable.facebook);
        twitter.setImageResource(R.drawable.twitter);
        youtube.setImageResource(R.drawable.youtube);
        google.setImageResource(R.drawable.googleplus);
        current = findViewById(R.id.current);
        office = findViewById(R.id.office);
        name = findViewById(R.id.name);
        party = findViewById(R.id.party);
        image = findViewById(R.id.image);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        website = findViewById(R.id.website);
        partylogo = findViewById(R.id.partylogo);
        add = findViewById(R.id.add);
        phonenumber = findViewById(R.id.phonenumber);
        emailaddress = findViewById(R.id.emailaddress);
        web = findViewById(R.id.web);



        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        governmentGov = (Government) bundle.getSerializable("official");
        current.setText(intent.getStringExtra("currentLocation"));
        phone.setText(governmentGov.getPhone());
        email.setText(governmentGov.getEmail());
        website.setText(governmentGov.getWebsite());
        address.setText(governmentGov.getAddress());
        Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
        Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(website, Linkify.WEB_URLS);


        if(NetworkCheck()) {
            LoadImage();
        }
        else {
            image.setImageResource(R.drawable.brokenimage);
        }


        if (governmentGov != null) {
            office.setText(governmentGov.getOffice());
            name.setText(governmentGov.getName());

        }

        SetBackground();

        if (governmentGov.getEmail().equals("Unknown")) {

            emailaddress.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }

        if (governmentGov.getAddress().equals("")) {
            address.setVisibility(View.GONE);

            add.setVisibility(View.GONE);
        }
        if (governmentGov.getWebsite().equals("Unknown")) {
            web.setVisibility(View.GONE);
            website.setVisibility(View.GONE);
        }
        if (governmentGov.getPhone().equals("Unknown")) {
            phone.setVisibility(View.GONE);
            phonenumber.setVisibility(View.GONE);
        }


        if (governmentGov.getYoutube().equals("")) {
            youtube.setVisibility(View.GONE);
        }
        if (governmentGov.getGoogle().equals("")) {
            google.setVisibility(View.GONE);
        }
        if (governmentGov.getTwitter().equals("")) {
            twitter.setVisibility(View.GONE);
        }
        if (governmentGov.getFacebook().equals("")) {
            facebook.setVisibility(View.GONE);

        }

    }




    private void LoadImage() {

        image.setImageResource(R.drawable.placeholder);

            if (governmentGov.getImage().equals("Unknown")) {
                image.setImageResource(R.drawable.missing);
            } else {
                final String photoUrl = governmentGov.getImage();

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




    private boolean NetworkCheck() {

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



    public void ActivatePhoto(View v){
        Intent intent = new Intent(GovernmentActivity.this, photoDetailActivity.class);
        intent.putExtra("currentLocation", current.getText().toString());
        intent.putExtra("Government", governmentGov.getOffice());
        intent.putExtra("name", governmentGov.getName());

        if(governmentGov.getParty().equals("Democratic Party")){
            intent.putExtra("BG","Dem");
        }
        else if(governmentGov.getParty().equals("Democratic")){
            intent.putExtra("BG","Dem");
        }
        if(governmentGov.getParty().equals("Republican Party")){
            intent.putExtra("BG","Rep");
        }
        if(governmentGov.getParty().equals("Nonpartisan")){
            intent.putExtra("BG", "Nonpartisan");
        }

        if(governmentGov.getImage().equals("Unknown")){
            return;
        }

        intent.putExtra("photoUrl", governmentGov.getImage());
        startActivity(intent);

    }





    public void twitterClicked(View v) {
        Intent intent = null;
        String name = governmentGov.getTwitter();
        try {

            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v){

        String FACEBOOK_URL = "https://www.facebook.com/" + governmentGov.getFacebook();
        String urlToUse;

        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + governmentGov.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }



    public void googlePlusClicked(View v) {
        String name = governmentGov.getGoogle();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }

    }

        public void youTubeClicked(View v) {
            String name = governmentGov.getYoutube();
            Intent intent = null;
            try {
                intent = new Intent(Intent.ACTION_VIEW); intent.setPackage("com.google.android.youtube");
                intent.setData(Uri.parse("https://www.youtube.com/" + name)); startActivity(intent);
            } catch (ActivityNotFoundException e) { startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
            }
        }


    private void SetBackground() {

        if(governmentGov != null){

            party.setText("(" + governmentGov.getParty() + ")");

            if (governmentGov.getParty().equals("Democratic Party") ) {
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.Dem));
            } else if (governmentGov.getParty().equals("Democratic")) {
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.Dem));
            } else if (governmentGov.getParty().equals("Republican Party")) {
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.Rep));
            }

            if (governmentGov.getParty().equals("Democratic Party")) {
                partylogo.setImageResource(R.drawable.dem_logo);
            } else if (governmentGov.getParty().equals("Democratic")) {
                partylogo.setImageResource(R.drawable.dem_logo);
            } else if (governmentGov.getParty().equals("Republican Party")) {
                partylogo.setImageResource(R.drawable.rep_logo);
            }

            if(governmentGov.getParty().equals("Nonpartisan")){
                party.setText(("(Nonpartisan)").toString());
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.Nonpartisan));
                partylogo.setImageDrawable(null);
            }
        }


    }

    public void partyClicked(View v) {

        String democrat = "https://democrats.org";
        Uri democ = Uri.parse(democrat);
        Intent intent = new Intent(Intent.ACTION_VIEW, democ);

        String repub = "https://www.gop.com";
        Uri rep = Uri.parse(repub);
        Intent intent2 = new Intent(Intent.ACTION_VIEW, rep);


        if (governmentGov.getParty().equals("Democratic Party")) {

            startActivity(intent);
        }
        else if(governmentGov.getParty().equals("Democratic")){
            startActivity(intent);
        }
        else {
            startActivity(intent2);
        }

    }

}
