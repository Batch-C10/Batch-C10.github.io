package com.micandmac.virtualtravelguide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class

Dashboard extends Activity {

    String rep_id = "";
    String sta = "";
    String vgh = "", vgh1 = "";
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    ArrayList<Tour_model> data = new ArrayList<>();
    int lenghtofjson, lenghtofjson1;
    private RecyclerView mRVFishPrice;
    PostSubAdapter mAdapter;
    LinearLayout restaurents;
    LocationManager locationManager;
    Double mlatt,mlongg;

    SearchView searchView;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_left_out_activity);
        setContentView(R.layout.dashboard);
        mRVFishPrice = (RecyclerView) findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        restaurents=(LinearLayout)findViewById(R.id.restaurents);

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);

            }
        });

        restaurents.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getApplicationContext(),Weather.class);
                startActivity(i);

            }
        });





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(Dashboard.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(Dashboard.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Dashboard.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(Dashboard.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(Dashboard.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            String stringLatitude = String.valueOf(gpsTracker.latitude);
            String stringLongitude = String.valueOf(gpsTracker.longitude);
            mlatt=gpsTracker.latitude;
            mlongg=gpsTracker.longitude;


        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
        new AsyncFetch().execute("");


    }

    
    private class AsyncFetch extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Dashboard.this);
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            /*pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();*/
            System.out.println("--1111-0-");

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                url = new URL("http://zimro.in/android_file/virtual_tourview.php");


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");


                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);

            } catch (IOException e1) {

                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {


                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {

                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            System.out.println("--1111-8-" + result);
            // pdLoading.dismiss();
            data = new ArrayList<>();

            // pdLoading.dismiss();
            try {

                JSONArray jArray = new JSONArray(result);



                // Extract data from json and store into ArrayList as class objects
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);

                    lenghtofjson = jArray.getJSONObject(i).length();
                    lenghtofjson1 = result.length();

                    Tour_model fishData = new Tour_model();

                    fishData.id = json_data.getString("id");
                    fishData.placename = json_data.getString("placename");
                    fishData.description = json_data.getString("description");
                    fishData.image = json_data.getString("image");
                    fishData.latt = json_data.getString("latti");
                    fishData.longgi = json_data.getString("longgi");
                    fishData.status = json_data.getString("status");


                    data.add(fishData);
                }


                if (lenghtofjson == 0) {

                    mRVFishPrice.setVisibility(View.GONE);
                } else {


                }

                mRVFishPrice.setLayoutManager(new LinearLayoutManager(Dashboard.this));
                mAdapter = new PostSubAdapter(Dashboard.this, data);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mRVFishPrice.setLayoutManager(mLayoutManager);
                mRVFishPrice.setItemAnimator(new DefaultItemAnimator());
                mRVFishPrice.setAdapter(mAdapter);


            } catch (JSONException e) {
                //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }

        }

    }

    public class PostSubAdapter extends RecyclerView.Adapter<PostSubAdapter.ViewHolder> {


        private Context context;
        private LayoutInflater inflater;
        String getposid, getposid1;
        ArrayList<Tour_model> data = new ArrayList<>();
        ArrayList<Tour_model> tempList = new ArrayList<>();
        Tour_model current;
        int currentPos = 0;
        String url;
        Typeface font, font1;
        String searchString = "";
        String ggg;

        public PostSubAdapter(Context context, ArrayList<Tour_model> data1) {

            this.tempList = data1;
            this.data = data1;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder mHolder, final int position) {
            final Tour_model current = tempList.get(position);

            //font = Typeface.createFromAsset(context.getAssets(), "Calibri.ttf");
            //font1 = Typeface.createFromAsset(context.getAssets(), "Smoolthan Bold.otf");

            mHolder.setIsRecyclable(true);
            mHolder.placename.setText(current.placename);

            Glide.with(getApplicationContext())
                    .load("http://zimro.in/virtualtourimages/"+current.image)
                    .override(300, 200)
                    .into(mHolder.imageview);

            mHolder.imageview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("tourismdetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("id", current.id);
                    editor.putString("place", current.placename);
                    editor.putString("description", current.description);
                    editor.putString("photo", current.image);
                    editor.putString("lat", current.latt);
                    editor.putString("long", current.longgi);
                    editor.commit();

                    Intent i=new Intent(getApplicationContext(),Viewfulldesc.class);
                    startActivity(i);

                }
            });





        }

        @Override
        public int getItemCount() {
            return tempList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            Context c;


            ImageView imageview;
            TextView placename;



            public ViewHolder(View itemView) {
                super(itemView);
                c = context;

                placename = (TextView) itemView.findViewById(R.id.placename);
                imageview = (ImageView) itemView.findViewById(R.id.imageview);




            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, (LocationListener) this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }





}