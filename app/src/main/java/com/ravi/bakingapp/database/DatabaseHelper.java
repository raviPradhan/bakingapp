package com.ravi.bakingapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DatabaseHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "recipes.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;


    // Constructor
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + RecipesContract.RecipeEntry.TABLE_NAME + " (" +
                RecipesContract.RecipeEntry._ID                + " INTEGER PRIMARY KEY, " +
                RecipesContract.RecipeEntry.COLUMN_INGREDIENT    + " TEXT NOT NULL, " +
                RecipesContract.RecipeEntry.COLUMN_MEASURE + " TEXT NOT NULL, " +
                RecipesContract.RecipeEntry.COLUMN_QUANTITY + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipesContract.RecipeEntry.TABLE_NAME);
        onCreate(db);
    }
}
