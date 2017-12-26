package com.themparksdetermined.smartparkdisney.Controller;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.themparksdetermined.smartparkdisney.Model.UserDataContract;

/**
 * Created by Crist on 8/15/2017.
 */

public class UserDataDbHelper extends SQLiteOpenHelper {

    public UserDataDbHelper(Context context){
        super(context, UserDataContract.DATABASE_NAME, null, UserDataContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDataContract.Table1.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(UserDataContract.Table1.DELETE_TABLE);
        onCreate(db);
    }
}
