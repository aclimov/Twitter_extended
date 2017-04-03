package com.codepath.apps.TwitterClientR3.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {

    public static final String NAME = "TwitterRestClientDatabase";

    public static final int VERSION = 1;
}


