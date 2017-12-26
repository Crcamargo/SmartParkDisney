package com.themparksdetermined.smartparkdisney.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.themparksdetermined.smartparkdisney.Controller.Adapter;
import com.themparksdetermined.smartparkdisney.Model.DividerItemDecoration;
import com.themparksdetermined.smartparkdisney.Model.ListItem;
import com.themparksdetermined.smartparkdisney.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Crist on 8/4/2017.
 */

public class ListFragment extends Fragment {

    /* Member variables */
    SharedPreferences sharedPref;
    String[]       waitTimes  = new String[100];
    String[]       statuses   = new String[100];
    List<ListItem> data       = new ArrayList<>();
    String         sortBy     = "Name";
    Boolean        showClosed = false;
    Boolean        ascending  = true;
    Menu           filterMenu;
    Adapter        adapter;

    /*
        Creates new Instance of this Fragment
     */
    public static Fragment newInstance(){
        Fragment frag = new ListFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    /*
        On Create view for Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Set root view */
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        rootView.setTag(TAG);

        /* Set recycler view */
        RecyclerView recView = (RecyclerView) rootView.findViewById(R.id.rec_list);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        recView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getContext(),
                R.drawable.item_decorator)));
        setHasOptionsMenu(true);

         /* Variables for setting up data base */
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference      myRef    = database.getReference().child("Rides");
        Resources              res      = getResources();


        /* Set event listener for data changes of database */
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* DataBase returns data as HashMap with each value being a hashmap */
                HashMap<String, HashMap<String, Object>> dataBaseData =
                        (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();

                /* Setup variables for parsing database data */
                String time;
                String status;
                boolean fastPass;
                String fastOpen;
                String name;
                String fastClose;
                data = new ArrayList<>();

                /* Parse Data */
                for (int i = 0; i < MainActivity.ids.length; i++) {
                    String currId = MainActivity.ids[i];
                    name     = (String)             dataBaseData.get(currId).get("name");
                    time     = Long.toString((long) dataBaseData.get(currId).get("time"));
                    status   = (String)             dataBaseData.get(currId).get("status");
                    fastPass = (Boolean)            dataBaseData.get(currId).get("fastPass");

                    /* Format Data */
                    waitTimes[i]  = time;
                    ListItem item = new ListItem();
                    if (status.equals("Operating")) {
                        item.setOpen(true);
                        status = "Open";
                    } else {
                        item.setOpen(false);
                    }

                    /* Set up List item data and add */
                    item.setNameOfRide(name);
                    item.setWaitTime(waitTimes[i]);
                    item.setRideImg(MainActivity.imgs[i]);
                    item.setStatus(status);
                    item.setActive(false);
                    item.setFastPass(fastPass);
                    data.add(item);
                }
                /* update list */
                adapter.update(data, showClosed, sortBy, ascending);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("Fail", "Failed to read value.");
            }
        });

        /* Finalize set up */
        adapter = new Adapter(data,getActivity());
        recView.setAdapter(adapter);

        return rootView;
    }

    /*
        Inflate menus for filtering and sorting rec view
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        filterMenu = menu;

        /* Read in settings */
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        sortBy     = sharedPref.getString("sort", sortBy);
        ascending  = Boolean.valueOf(sharedPref.getString("ascending", String.valueOf(ascending)));
        showClosed = Boolean.valueOf(sharedPref.getString("closed", String.valueOf(showClosed)));

        if(sortBy.equals("Name")) menu.findItem(R.id.filterBy).setIcon(R.drawable.clock);
        else menu.findItem(R.id.filterBy).setIcon(R.drawable.abc);

        if(ascending) menu.findItem(R.id.filterOrder).setIcon(R.drawable.ascending);
        else menu.findItem(R.id.filterOrder).setIcon(R.drawable.descending);

        if(showClosed) menu.findItem(R.id.showClosed).setChecked(true);

        adapter.update(data, showClosed, sortBy, ascending);
        super.onCreateOptionsMenu(menu,inflater);
    }

    /*
        Set reactions to option selections
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = sharedPref.edit();

        switch (item.getItemId()){
            case R.id.filterBy:
                if(sortBy.equals("Name")){
                    sortBy = "Wait";
                    item.setIcon(R.drawable.abc);
                } else{
                    sortBy = "Name";
                    item.setIcon(R.drawable.clock);
                }
                editor.putString("sort", sortBy);
                editor.commit();
                break;
            case R.id.filterOrder:
                ascending = !ascending;
                if(ascending) item.setIcon(R.drawable.ascending);
                else item.setIcon(R.drawable.descending);
                editor.putString("ascending", String.valueOf(ascending));
                editor.commit();
                break;
            case R.id.showClosed:
                item.setChecked(!item.isChecked());
                showClosed = item.isChecked();
                editor.putString("closed", String.valueOf(showClosed));
                editor.commit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        adapter.update(data, showClosed, sortBy, ascending);
        return super.onOptionsItemSelected(item);

    }
}
