package com.themparksdetermined.smartparkdisney.View;


import android.content.Intent;
import android.content.res.AssetManager;

import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.themparksdetermined.smartparkdisney.R;

import java.util.HashMap;

/**
 * Created by Crist on 8/4/2017.
 */

public class ParkInfoFragment extends Fragment {

    /* Member variables */
    TextView titleView;
    TextView dateView;
    TextView hoursView;
    TextView info;
    TextView mapTitle;
    TextView smartPark;
    TextView version;
    ImageButton map;
    String date;
    String openingTime;
    String closingTime;

    /*
        Creates new Instance of this Fragment
     */
    public static Fragment newInstance(){
        Fragment frag = new ParkInfoFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Initialize views */
        View rootView = inflater.inflate(R.layout.fragment_park_info,container,false);
        titleView = (TextView) rootView.findViewById(R.id.parkTitle);
        smartPark = (TextView) rootView.findViewById(R.id.smartPark);
        dateView  = (TextView) rootView.findViewById(R.id.parkDate);
        hoursView = (TextView) rootView.findViewById(R.id.parkHours);
        info      = (TextView) rootView.findViewById(R.id.infoTitle);
        mapTitle  = (TextView) rootView.findViewById(R.id.mapTitle);
        version   = (TextView) rootView.findViewById(R.id.version);
        map       = (ImageButton)   rootView.findViewById(R.id.mapBut);

        /* Get Custom fonts*/
        AssetManager manager = getContext().getAssets();
        Typeface mouse = Typeface.createFromAsset(manager,"font/mouse.ttf");
        Typeface rale = Typeface.createFromAsset(manager, "font/raleway.ttf");


        /* Set fonts */
        titleView.setTypeface(rale);
        smartPark.setTypeface(rale);
        hoursView.setTypeface(mouse);
        mapTitle.setTypeface(mouse);
        dateView.setTypeface(mouse);
        version.setTypeface(rale);
        info.setTypeface(mouse);

        /* Launch map activity */
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getContext(),MapsActivity.class);
                getContext().startActivity(myIntent);
            }
        });


        /* Variables for Firbase database */
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference      myRef    = database.getReference().child("parkValues");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* DataBase returns data as HashMap with String String key value pairs*/
                HashMap<String,String> dataBaseParkValues =
                        (HashMap<String, String>) dataSnapshot.getValue();

                date        = dataBaseParkValues.get("date");
                openingTime = dataBaseParkValues.get("openingTime");
                closingTime = dataBaseParkValues.get("closingTime");
                String dateFinal  = "Park Date:   " + formatDate(date);
                String hoursFinal = "Park Hours: "  + formatHours(openingTime,closingTime);
                dateView.setText(dateFinal);
                hoursView.setText(hoursFinal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("Fail", "Failed to read value.");
            }
        });
        return rootView;
    }

    /*
        Used to format specific date string from database
     */
    public String formatDate(String date){
        /* Parse Date */
        char[] formattedDate = new char[10];

        date.getChars(5,7,formattedDate,0); //getMonth
        formattedDate[2] = '/';
        date.getChars(8,10,formattedDate,3); //getDay
        formattedDate[5] = '/';
        date.getChars(0,4,formattedDate,6); //getYear

        return String.valueOf(formattedDate);
    }

    /*
        Used to format specific park hours strings from database
     */
    public String formatHours(String openingTime, String closingTime){
        Boolean afternoon = false;

        /* Parse Opening Time */
        char[] formattedOpen = new char[2];
        openingTime.getChars(11,13,formattedOpen,0); //getHour
        if(formattedOpen[0] == '0'){
            char temp = formattedOpen[1];
            formattedOpen = new char[1];
            formattedOpen[0] = temp;
        }
        String openHourString = (String.copyValueOf(formattedOpen)).trim();
        int openHourInt = Integer.parseInt(openHourString);
        if(openHourInt > 12){
            afternoon = true;
            openHourInt -= 12;
            openHourString = Integer.toString(openHourInt);
        }
        formattedOpen = new char[2];
        openingTime.getChars(14,16,formattedOpen,0); //getMinute
        String finalOpen = openHourString + ":" + String.copyValueOf(formattedOpen);
        if(afternoon) {
            finalOpen += " pm";
        }else{
            finalOpen += " am";
        }

        /* Parse Closing Time */
        String finalClose;
        char[] formattedClose = new char[8];
        closingTime.getChars(11,13,formattedClose,0); //getHour
        String closeHourString = (String.copyValueOf(formattedClose)).trim();
        int closeHourInt = Integer.parseInt(closeHourString);
        if(closeHourInt > 12){
            closeHourInt -= 12;
            closeHourString = Integer.toString(closeHourInt);
            closingTime.getChars(14,16,formattedClose,0);
            finalClose = closeHourString + ":" + String.valueOf(formattedClose) + " pm";
        }
        else if(closeHourInt == 0){
            finalClose = "Midnight";
        }else{

            finalClose = closeHourString + ":" + String.valueOf(formattedClose) + " am";
        }
        return  finalOpen + " to " + finalClose;
    }
}
