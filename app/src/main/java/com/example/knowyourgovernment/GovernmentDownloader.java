package com.example.knowyourgovernment;

import android.os.AsyncTask;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GovernmentDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "GovernmentDownloader";
    private MainActivity mainActivity;
    private static final String quote = "&address=";
    private static final String key = "AIzaSyA3coZBcoBST0kxxIuY1KTucZg5xxjmDvw";
    private static final String OfficeURL = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    private  Object[] tempCity = new Object[2];
    private String city;
    private String state;
    private String zip;
    private String line1 = null;
    private String line2 = null;
    private Government governmentTemp = new Government();
    private ArrayList<Government> arrGovernments = new ArrayList<>();
    int[] TempArr;


    GovernmentDownloader(MainActivity mainActivity) {

        this.mainActivity = mainActivity;
    }


    @Override
    protected String doInBackground(String... params) {


        String apiEndPoint = params[0];
        String end = OfficeURL + key + quote + apiEndPoint;
        Uri.Builder buildURL = Uri.parse(end).buildUpon();

        String urlToUse = buildURL.build().toString();


        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            Log.d(TAG, "doInBackground: url : " + url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());
            Log.d(TAG, "doInBackground: " + urlToUse);

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        return sb.toString();

    }


    @Override
    protected void onPostExecute(String s) {


        if (s != null) {
            arrGovernments = parseJSON(s);
            tempCity = new Object[2];
            tempCity[0] = city + ", " + state + " " + zip;
            tempCity[1] = arrGovernments;
            mainActivity.addOfficeGov(tempCity);
            return;
        }

        if (s == null) {
            return;
        }



    }


    private ArrayList<Government> parseJSON(String s) {

        try {
            JSONObject JsonObj = new JSONObject(s);
            JSONArray ArrOffice = JsonObj.getJSONArray("offices");
            JSONArray ArrOfficial = JsonObj.getJSONArray("officials");

            int i = 0;
                while(i < ArrOffice.length()){
                JSONObject JFirm = ArrOffice.getJSONObject(i);
                String TempIndice = JFirm.getString("officialIndices");
                int len = TempIndice.length() - 1;
                String split = (String) TempIndice.subSequence(1, len);
                String[] ArrSplit = split.split(",");

                    TempArr = new int[ArrSplit.length];

                String TempOffice = JFirm.getString("name");

                int n = 0;
                while(n < ArrSplit.length){
                    TempArr[n] = Integer.parseInt(ArrSplit[n]);
                    n++;
                }
                    int m = 0;
                while(m < TempArr.length){
                    JSONObject JIndice;
                    JIndice = ArrOfficial.getJSONObject((TempArr[m]));

                    String name = TempNum(JIndice, "name");
                    boolean TempAdd = JIndice.has("address");
                    boolean tempParty = JIndice.has("party");
                    boolean tempPhone = JIndice.has("phones");
                    boolean tempWeb = JIndice.has("urls");
                    boolean tempEmail = JIndice.has("emails");
                    boolean tempimage = JIndice.has("photoUrl");
                    boolean tempChan = JIndice.has("channels");

                    JSONObject normalObj = JsonObj.getJSONObject("normalizedInput");
                    String address = "";

                    if (TempAdd) {

                        JSONArray ArrAdd = JIndice.getJSONArray("address");
                        JSONObject ObjAdd = ArrAdd .getJSONObject(0);
                        city = ObjAdd.getString("city")+ " ";
                        state = ObjAdd.getString("state") + ", ";
                        zip = ObjAdd.getString("zip");
                        line1 = ObjAdd.getString("line1");
                        line2 = ObjAdd.getString("line2");
                        boolean tempLine2 = ObjAdd.has("line2");
                        boolean tempCt = ObjAdd.has("city");
                        boolean tempSt = ObjAdd.has("state");
                        boolean tempZp = ObjAdd.has("zip");
                        address = address + line1 + "\n";

                        if (tempLine2) {
                            address = address + line2 + "\n";
                        }

                        if (tempCt) {
                            address = address + city;
                        }
                        if (tempSt) {
                            address = address + state;

                        }
                        if (tempZp) {
                            address = address +  zip;

                        }


                        Log.d(TAG, "address" + address);

                        city = normalObj.getString("city");
                        state = normalObj.getString("state");
                        zip = normalObj.getString("zip");
                    }

                    else {
                        address = "";

                    }

                    JSONArray Social;
                    if(tempChan) {
                        Social = JIndice.getJSONArray("channels");

                    }
                    else{
                        Social = null;
                    }

                    String party;

                    if (tempParty) {
                        party = JIndice.getString("party");
                    }
                    else {
                        party = "Unknown";
                    }

                    String phone;
                    if (tempPhone ) {
                        phone = JIndice.getJSONArray("phones").getString(0);
                    } else {
                        phone = "Unknown";
                    }

                    String website;
                    if (tempWeb) {
                        website = JIndice.getJSONArray("urls").getString(0);
                    } else {
                        website = "Unknown";
                    }

                    String email;
                    if (tempEmail) {
                        email = JIndice.getJSONArray("emails").getString(0);
                    } else {
                        email = "Unknown";
                    }
                    String image;
                    if (tempimage) {
                        image = JIndice.getString("photoUrl");
                    } else {
                        image = "Unknown";
                    }

                    String google;
                    if(Social != null){
                        google = Socialnetwork(Social, "GooglePlus");
                    }
                    else {
                        google = "";
                    }
                    String facebook;
                    if(Social != null){
                        facebook = Socialnetwork(Social, "Facebook");
                    }
                    else{
                        facebook = "";
                    }
                    String twitter;
                    if(Social != null){
                        twitter = Socialnetwork(Social, "Twitter");
                    }
                    else {
                        twitter = "";
                    }
                    String youtube;
                    if(Social != null){
                        youtube = Socialnetwork(Social, "YouTube");
                    }
                    else{
                        youtube = "";
                    }

                    governmentTemp = new Government(TempOffice, name, party, address, phone, email, website, image, facebook, twitter, youtube, google);
                    arrGovernments.add(governmentTemp);
                    m++;
                }
                i++;
            }

            return arrGovernments;

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }



    private String Socialnetwork(JSONArray channels, String name) throws JSONException {
        String ch = "";
        int k = 0;
            while(k < channels.length()){
            if(channels.getJSONObject(k).getString("type").equals(name))
                ch = channels.getJSONObject(k).getString("id");
                k++;
            }
        return ch;

    }

    private String TempNum(JSONObject JIndice, String name) throws JSONException {

        String nameTmp = "";
        int i = 0;
        while( i < JIndice.length()){
            nameTmp = JIndice.getString("name");
            i++;
        }
        return nameTmp;
    }

}
