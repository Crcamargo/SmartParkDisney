package com.themparksdetermined.smartparkdisney.View;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.themparksdetermined.smartparkdisney.Controller.PlanAdapter;
import com.themparksdetermined.smartparkdisney.Controller.UserDataDbHelper;
import com.themparksdetermined.smartparkdisney.Model.ListItem;
import com.themparksdetermined.smartparkdisney.Model.PlanDivider;
import com.themparksdetermined.smartparkdisney.Model.PlanItem;
import com.themparksdetermined.smartparkdisney.Model.UserDataContract;
import com.themparksdetermined.smartparkdisney.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Crist on 8/7/2017.
 */

public class SmartPlanFragment extends Fragment {

    PlanAdapter           adapter;
    List<String>          userData;
    static List<ListItem> data;
    FloatingActionButton  fab;
    UserDataDbHelper      mDbHelper;
    SQLiteDatabase        db;
    ProgressBar           progressBar;
    Context               context;

    /*
        Creates new Instance of this Fragment
     */
    public static Fragment newInstance(){
        Fragment frag = new SmartPlanFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    /*
        Method to create the fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Set Views */
        final View rootView = inflater.inflate(R.layout.fragment_smart_plan, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.loading);
        context = getContext();
        userData = new ArrayList<>();
        data = new ArrayList<>();
        rootView.setTag(TAG);

        /*Set local db*/
        mDbHelper = new UserDataDbHelper(getContext());
        db = mDbHelper.getWritableDatabase();

        String[] projection = {UserDataContract.Table1.COLUMN_NAME};
        Cursor cursor = db.query(UserDataContract.Table1.TABLE_NAME, projection,
                null, null, null, null, null);

        while(cursor.moveToNext()){
            String curr = cursor.getString(
                    cursor.getColumnIndexOrThrow(UserDataContract.Table1.COLUMN_NAME));
            Log.e("Database Read", curr);
            userData.add(curr);
        }

        /* Set Recycler view */
        RecyclerView recView = (RecyclerView) rootView.findViewById(R.id.rec_plan);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        recView.addItemDecoration(new PlanDivider(ContextCompat.getDrawable(getContext(),
                R.drawable.plan_decorator)));

        /* Set FAB */
        fab = (FloatingActionButton) rootView.findViewById(R.id.fragFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNetworkAvailable()){
                    Toast.makeText(getContext(),"Need Internet!",Toast.LENGTH_SHORT).show();
                    return;
                }
                PopupMenu pop = new PopupMenu(getContext(),view);
                MenuInflater inflater = pop.getMenuInflater();
                inflater.inflate(R.menu.smart_plan_menu,pop.getMenu());
                for(int i = 0; i < pop.getMenu().size(); i++){
                    MenuItem item = pop.getMenu().getItem(i);
                    ListItem listItem = data.get(getIndexOf((String) item.getTitle()));
                    if(!listItem.isOpen()) item.setVisible(false);
                }
                for(int i = 0; i < pop.getMenu().size(); i++){
                    MenuItem item = pop.getMenu().getItem(i);
                    if(userData.contains(item.getTitle())){
                        item.setChecked(true);
                    }
                }

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        item.setChecked(!item.isChecked());
                        if(item.isChecked()){
                            userData.add((String) item.getTitle());
                            ContentValues values = new ContentValues();
                            values.put(UserDataContract.Table1.COLUMN_NAME,(String) item.getTitle());
                            db.insert(UserDataContract.Table1.TABLE_NAME, null, values);
                        }
                        else{
                            userData.remove(item.getTitle());
                            for(int i = 0; i < MainActivity.cards.size(); i++){
                                if(MainActivity.cards.get(i).getTitle().equals(item.getTitle()))
                                    MainActivity.cards.remove(i);
                            }
                            String selection = UserDataContract.Table1.COLUMN_NAME + " Like ?";
                            String[] args = {(String)item.getTitle()};
                            db.delete(UserDataContract.Table1.TABLE_NAME, selection, args);
                        }

                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                        item.setActionView(new View(getContext()));
                        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                            @Override
                            public boolean onMenuItemActionExpand(MenuItem item) {
                                return false;
                            }

                            @Override
                            public boolean onMenuItemActionCollapse(MenuItem item) {
                                return false;
                            }
                        });
                        return false;
                    }
                });

                pop.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        MainActivity.cards.clear();
                        updatePlan(data, userData, true);

                        ImageView empty = (ImageView) rootView.findViewById(R.id.empty);
                        if(userData.size() == 0) empty .setVisibility(View.VISIBLE);
                        else empty.setVisibility(View.INVISIBLE);
                    }
                });
                pop.show();
            }
        });

        /* Variables for setting up data base */
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef    = database.getReference().child("Rides");


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
                String location;
                Long avg;
                boolean fastPass;
                data = new ArrayList<>();

                /* Parse Data */
                for (int i = 0; i < MainActivity.ids.length; i++) {
                    time     = Long.toString((long) dataBaseData.get(MainActivity.ids[i]).get("time"));
                    status   = (String) dataBaseData.get(MainActivity.ids[i]).get("status");
                    avg      = (long) dataBaseData.get(MainActivity.ids[i]).get("avg");
                    fastPass = (boolean) dataBaseData.get(MainActivity.ids[i]).get("fastPass");
                    location = (String) dataBaseData.get(MainActivity.ids[i]).get("location");
                    ListItem item = new ListItem();
                    if (status.equals("Operating")) {
                        item.setOpen(true);
                        status = "Open";
                    } else {
                        item.setOpen(false);
                    }
                    item.setFastPass(fastPass);
                    item.setWaitTime(time);
                    item.setStatus(status);
                    item.setAvg(avg);
                    item.setLocation(location);
                    item.setRideImg(MainActivity.imgs[i]);
                    item.setNameOfRide(MainActivity.nameOfRides[i]);
                    data.add(item);
                }

                /* Update list */
                updatePlan(data,userData,false);

                ImageView empty = (ImageView) rootView.findViewById(R.id.empty);
                if(adapter.getItemCount() == 0) empty .setVisibility(View.VISIBLE);
                else empty.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("Fail", "Failed to read value.");
            }
        });
        List<PlanItem> adapterList = new ArrayList<>();

        /* Finalize set up */
        adapter = new PlanAdapter(adapterList,getActivity());
        recView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String name = adapter.remove(viewHolder.getAdapterPosition());
                userData.remove(name);
                for(int i = 0; i < MainActivity.cards.size(); i++){
                    if(MainActivity.cards.get(i).getTitle().equals(name))
                        MainActivity.cards.remove(i);
                }
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                String selection = UserDataContract.Table1.COLUMN_NAME + " Like ?";
                String[] args = {name};
                db.delete(UserDataContract.Table1.TABLE_NAME, selection, args);
                ImageView empty = (ImageView) rootView.findViewById(R.id.empty);
                if(adapter.getItemCount() == 0){
                    empty.setVisibility(View.VISIBLE);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recView);
        return rootView;
    }

    /*
        Method to create the initial smart plan
     */
    public void newPlan(final List<ListItem> data, List<String> userData){

        if(userData.isEmpty()) return;

        /* Sort the userData by difference of wait time and average time */
        Collections.sort(userData, new Comparator<String>() {
            @Override
            public int compare(String first, String second) {

                ListItem firstItem  = data.get(getIndexOf(first));
                ListItem secondItem = data.get(getIndexOf(second));
                Long firstWaitTime  = Long.valueOf(firstItem.getWaitTime());
                Long firstAvgTime   = firstItem.getAvg();
                Long secondWaitTime = Long.valueOf(secondItem.getWaitTime());
                Long secondAvgTime  = secondItem.getAvg();

                Long firstDifference  = firstAvgTime - firstWaitTime;
                Long secondDifference = secondAvgTime - secondWaitTime;


                if(firstWaitTime == 0 && firstAvgTime==0){
                    return 1;
                }

                if(secondWaitTime == 0 && secondAvgTime == 0){
                    return -1;
                }

                if( firstDifference < secondDifference){
                    return 1;
                }else if( secondDifference < firstDifference){
                    return -1;
                } else{
                    return 0;
                }
            }
        });

        /* Find FastPass Ride */
        for(int fpIndex = userData.size() - 1; fpIndex >= 0; fpIndex--){
            String name   = userData.get(fpIndex);
            ListItem curr = data.get(getIndexOf(name));
            if(curr.isFastPass() && (Integer.valueOf(curr.getWaitTime()) >= 30|| userData.size() > 3) ){
                PlanItem item = new PlanItem();
                String description = "Fast pass " + name + "!\n";
                String time = "+" + "0 minutes";
                item.setTitle(name);
                item.setImg(data.get(getIndexOf(name)).getRideImg());
                item.setDescription(description);
                item.setTime(time);
                MainActivity.cards.add(item);
                userData.remove(fpIndex);
                userData.add(name);
                break;
            }
        }
        SmartPlan sp = new SmartPlan(MainActivity.cards);
        sp.execute();
    }


    /*
        Main method for creating/updating smart plan
     */
    public void updatePlan(final List<ListItem> data, List<String> userData, boolean isInitial){

        /* Check if network is available */
        if(!isNetworkAvailable()) return;

        /* Refresh plan or create new plan */
        if(isInitial){
            newPlan(data, userData);
        }
        else {          //adapter.update(MainActivity.cards);
            if(MainActivity.cards.isEmpty()) {
                for (int i = 0; i < userData.size(); i++) {
                    String name = userData.get(i);
                    ListItem curr = data.get(getIndexOf(name));
                    PlanItem item = new PlanItem();
                    String description = "Ride " + name + "!\n";
                    String time = "+" + curr.getWaitTime() + " minutes";
                    item.setDescription(description);
                    item.setTime(time);
                    item.setTitle(name);
                    item.setImg(curr.getRideImg());
                    MainActivity.cards.add(item);
                }
            }
            adapter.update(MainActivity.cards);
        }

    }

    /*
        Helper method to find and return index of specific ride in our data
        returns -1 if not found
     */
    public static int getIndexOf(String name){
        int index = -1;
        for(int i = 0; i < data.size(); i++){
            if(data.get(i).getNameOfRide().equals(name)){
                index = i;
                break;
            }
        }
        return index;
    }

    /*
        Helper method to check if network is available
     */
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
        Close the database if we exit fragment
     */
    @Override
    public void onDestroy() {
        //db.close();
        super.onDestroy();
    }

    /*
        SmartPlan is an AsyncTask class that does the optimizing using wait time and location
     */
    private class SmartPlan extends AsyncTask<String, String, Integer> {
        HttpURLConnection urlConnection;
        JSONObject        jsonObject;
        JSONArray         array;
        String            resp;
        List<PlanItem>    cards;

        public SmartPlan(List<PlanItem> cards){
            this.cards = cards;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Integer num) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Integer doInBackground(String... strings) {

            /* Add first ride */
            String name = userData.get(0);
            ListItem curr = data.get(getIndexOf(name));
            PlanItem item = new PlanItem();
            String description = "Ride " + name + "!\n" ;
            String time = "+" + curr.getWaitTime() + " minutes";
            item.setDescription(description);
            item.setTime(time);
            item.setTitle(name);
            item.setImg(curr.getRideImg());
            cards.add(item);



            if(userData.size() > 2) {
                int[] order = new int[userData.size() - 2];
                StringBuilder result = new StringBuilder();
                ListItem origin = data.get(getIndexOf(userData.get(0)));
                ListItem dest = data.get(getIndexOf(userData.get(userData.size() - 1)));
                String request = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + origin.getLocation() +
                        "&destination=" + dest.getLocation() +
                        "&waypoints=optimize:true|";

                for (int i = 1; i < userData.size() - 1; i++) {
                    curr = data.get(getIndexOf(userData.get(i)));
                    String currLocation = curr.getLocation();
                    int last = userData.size() - 2;
                    if (i != last) {
                        request = request + currLocation + "|";
                    } else {
                        request = request + currLocation;
                    }
                }
                request = request + "&key=" + getResources().getString(R.string.api_key);
                try {
                    URL url = new URL(request);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }

                resp = result.toString();
                try {
                    jsonObject = new JSONObject(resp);
                    if (jsonObject != null) {
                        array = (JSONArray) jsonObject.getJSONArray("routes");
                        jsonObject = (JSONObject) array.get(0);
                        array = jsonObject.getJSONArray("waypoint_order");
                    }

                    for (int i = 0; i < array.length(); i++) {
                        order[i] = array.optInt(i);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (order != null) {
                    for (int i = 0; i < order.length; i++) {
                        name = userData.get(order[i] + 1);
                        curr = data.get(getIndexOf(name));
                        item = new PlanItem();
                        description = "Ride " + name + "!\n";
                        time = "+" + curr.getWaitTime() + " minutes";
                        item.setDescription(description);
                        item.setTime(time);
                        item.setTitle(name);
                        item.setImg(curr.getRideImg());
                        cards.add(item);
                    }
                }
            }

            /* Add last */
            if(userData.size() > 1) {
                name = userData.get(userData.size() - 1);
                curr = data.get(getIndexOf(name));
                item = new PlanItem();
                description = "Ride " + name + "!\n";
                time = "+" + curr.getWaitTime() + " minutes";
                item.setDescription(description);
                item.setTime(time);
                item.setTitle(name);
                item.setImg(curr.getRideImg());
                cards.add(item);
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.update(cards);
                }
            });

            return 0;
        }
    }
}
