package com.themparksdetermined.smartparkdisney.Model;

import android.provider.BaseColumns;

/**
 * Created by Crist on 8/15/2017.
 */

public class UserDataContract {

    public static final  String DATABASE_NAME      = "database.db";
    public static final  int    DATABASE_VERSION   = 1;
    private static final String TEXT_TYPE          = " TEXT";
    private static final String COMMA_SEP          = ",";

    private UserDataContract(){}

    public static class Table1 implements BaseColumns{
        public static final String TABLE_NAME = "UserData";
        public static final String COLUMN_NAME = "Ride";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + TEXT_TYPE + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
