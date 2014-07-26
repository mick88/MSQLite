package com.michaldabski.msqlite.utils;

import android.content.Context;

import com.michaldabski.msqlite.MSQLiteOpenHelper;

/**
 * Created by Michal on 27/07/2014.
 */
public class DatabaseHelper extends MSQLiteOpenHelper
{

    public static final String NAME = "test.db";
    public static final int VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context, NAME, null, VERSION);
    }
}
