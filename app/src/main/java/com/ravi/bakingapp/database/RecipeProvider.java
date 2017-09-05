package com.ravi.bakingapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.ravi.bakingapp.database.RecipesContract.RecipeEntry.CONTENT_URI;
import static com.ravi.bakingapp.database.RecipesContract.RecipeEntry.TABLE_NAME;

public class RecipeProvider extends ContentProvider {

    public static final int RECIPE = 100;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DatabaseHelper favoritesHelper;

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the FAVORITES directory and a single item by ID.
         */
        uriMatcher.addURI(RecipesContract.AUTHORITY, RecipesContract.PATH_RECIPES, RECIPE);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        favoritesHelper = new DatabaseHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = favoritesHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the ingredients directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case RECIPE:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = favoritesHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned
        switch (match) {
            case RECIPE:
                // Inserting values into recipe table
                long id = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("The recipe is already selected");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = favoritesHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match){
            case RECIPE:
                int rowCount = 0;
                db.beginTransaction();
                try {
                    for(ContentValues value : values){
                        long id = db.insert(TABLE_NAME, null, value);
                        if(id > 0)
                            rowCount++;
                    }
                    db.setTransactionSuccessful();
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
//                Log.v(Constants.TAG, "Rows inserted " + rowCount);
                return  rowCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = favoritesHelper.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, null, null);
//        Log.v(Constants.TAG, "deleting database " + rowsDeleted);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
