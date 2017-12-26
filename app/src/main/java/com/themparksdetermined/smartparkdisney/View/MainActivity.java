package com.themparksdetermined.smartparkdisney.View;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.themparksdetermined.smartparkdisney.Controller.UserDataDbHelper;
import com.themparksdetermined.smartparkdisney.Model.PlanItem;
import com.themparksdetermined.smartparkdisney.Model.UserDataContract;
import com.themparksdetermined.smartparkdisney.R;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {

    private int mSelectedItem;
    private BottomNavigationView nav;
    TextView customToolText;
    UserDataDbHelper      mDbHelper;
    SQLiteDatabase db;
    static List<PlanItem> cards;
    static String[] nameOfRides;
    static String[] ids;
    static int[]    imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Data for rides */
        Resources res = getResources();
        nameOfRides = res.getStringArray(R.array.arraryOfRides);
        ids         = res.getStringArray(R.array.ids);
        imgs        = new int[]{R.mipmap.astro,R.mipmap.autopia,R.mipmap.thunder,R.mipmap.buzz,
                R.mipmap.caseyjr,R.mipmap.chipndale,R.mipmap.davy,R.mipmap.monorail,
                R.mipmap.railroad,R.mipmap.donaldsboat,R.mipmap.dumbo,R.mipmap.tiki,
                R.mipmap.nemo,R.mipmap.fireengine,R.mipmap.shootin,R.mipmap.gocoaster,R.mipmap.goofys,
                R.mipmap.haunted,R.mipmap.horsedrawn,R.mipmap.horseless,R.mipmap.indianajones,
                R.mipmap.jungle,R.mipmap.kingarthur,R.mipmap.madtea,R.mipmap.cinema,
                R.mipmap.marktwain,R.mipmap.matterhorn,R.mipmap.mickey,R.mipmap.minnie,
                R.mipmap.toad,R.mipmap.omnibus,R.mipmap.peterpan,R.mipmap.pinocchio,
                R.mipmap.tomsawyer,R.mipmap.pirates,R.mipmap.cartoonspin,R.mipmap.columbia,
                R.mipmap.sleeping,R.mipmap.snowwhite,R.mipmap.ghostgalaxy,R.mipmap.splash,
                R.mipmap.startours,R.mipmap.canal,R.mipmap.tarzan,R.mipmap.winnie,
                R.mipmap.lincoln,R.mipmap.smallworld,R.mipmap.alice,R.mipmap.mainstreet,
                R.mipmap.pixie,R.mipmap.fantasmic,R.mipmap.gallery,R.mipmap.halloweenparty,
                R.mipmap.jungle,R.mipmap.smallworld,R.mipmap.launchbay,R.mipmap.spacemountain,
                R.mipmap.jedipath,R.mipmap.haunted,R.mipmap.spacemountain,R.mipmap.pumpkin,
                R.mipmap.holiday,R.mipmap.river};

        /* Initialize cards for smart plan */
        cards = new ArrayList<>();

        /* Get Custom Font */
        AssetManager manager   = getAssets();
        Typeface     mouseFont = Typeface.createFromAsset(manager,"font/mouse.ttf");

        /* Set Toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        customToolText  = (TextView) findViewById(R.id.customTool);
        setSupportActionBar(toolbar);
        customToolText.setTypeface(mouseFont);

        /* Set bottom navigation bar */
        nav = (BottomNavigationView) findViewById(R.id.navView);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

        /* Set default frament to list */
        MenuItem list = nav.getMenu().findItem(R.id.menu_list);
        selectFragment(list);
        list.setChecked(true);
    }

    /*
        Set reaction for back press
     */
    @Override
    public void onBackPressed() {
        MenuItem homeItem = nav.getMenu().getItem(1);
        if (mSelectedItem != homeItem.getItemId()) {
            selectFragment(homeItem);
            homeItem.setChecked(true);
        } else {
            super.onBackPressed();
        }
    }

    /*
        Method for selecting fragment
     */
    private void selectFragment(MenuItem item) {
        Fragment frag = null;
        /* init corresponding fragment */
        switch (item.getItemId()) {
            case R.id.menu_list:
                frag = ListFragment.newInstance();
                break;
            case R.id.menu_Info:
                frag = ParkInfoFragment.newInstance();
                break;
            case R.id.menu_plan:
                frag = SmartPlanFragment.newInstance();
                break;
        }

        /* update selected item */
        mSelectedItem = item.getItemId();

        /* un-check other items */
        for (int i = 0; i< nav.getMenu().size(); i++) {
            MenuItem menuItem = nav.getMenu().getItem(i);
            boolean check = menuItem.getItemId() == item.getItemId();
            menuItem.setChecked(check);
        }

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, frag, frag.getTag());
            ft.commit();
        }
    }

    /*
        delete database containing user data on destroy
     */
    @Override
    protected void onDestroy() {
        getApplicationContext().deleteDatabase(UserDataContract.DATABASE_NAME);
        super.onDestroy();
    }
}
