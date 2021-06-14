package com.micandmac.virtualtravelguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class Viewfulldesc extends AppCompatActivity {

    TextView placename,fulldesc,placename1;
    LinearLayout back,maps;
    String id,place,description,latx,longx,photo;
    TextToSpeech textToSpeech;
    ImageView imageview;
    LinearLayout floating_action_button,floating_action_button1,restaurents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewdesc);

        placename=(TextView)findViewById(R.id.placename);
        fulldesc=(TextView)findViewById(R.id.fulldesc);
        placename1=(TextView)findViewById(R.id.placename1);
        imageview=(ImageView)findViewById(R.id.imageview);

        back=(LinearLayout)findViewById(R.id.back);
        maps=(LinearLayout)findViewById(R.id.maps);
        restaurents=(LinearLayout)findViewById(R.id.restaurents);

        floating_action_button=(LinearLayout) findViewById(R.id.floating_action_button);
        floating_action_button1=(LinearLayout) findViewById(R.id.floating_action_button1);




        SharedPreferences pref = getApplicationContext().getSharedPreferences("tourismdetails", MODE_PRIVATE);
        id = pref.getString("id", "0");
        place = pref.getString("place", "0");
        description = pref.getString("description", "0");
        latx = pref.getString("lat", "0.0");
        longx = pref.getString("long", "0.0");
        photo = pref.getString("photo", "0.0");

        restaurents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("geo:"+latx+","+longx+"?q=restaurants");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }
        });


        placename.setText(place);
        placename1.setText(place);
        fulldesc.setText(description);

        Glide.with(getApplicationContext())
                .load("http://zimro.in/virtualtourimages/"+photo)
                .override(300, 200)
                .into(imageview);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(new Locale("en", "IND"));
                }
            }
        });

        floating_action_button.setVisibility(View.VISIBLE);
        floating_action_button1.setVisibility(View.GONE);

        floating_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textToSpeech.speak(description,TextToSpeech.QUEUE_FLUSH,null);



                floating_action_button.setVisibility(View.GONE);
                floating_action_button1.setVisibility(View.VISIBLE);

            }
        });

        floating_action_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textToSpeech.stop();

                floating_action_button.setVisibility(View.VISIBLE);
                floating_action_button1.setVisibility(View.GONE);

            }
        });




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latx +
                        "," + longx);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Google Map not found", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}