package com.ravi.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ravi.bakingapp.database.RecipesContract;
import com.ravi.bakingapp.utils.Constants;

import static com.ravi.bakingapp.database.RecipesContract.RecipeEntry.CONTENT_URI;


class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    ListRemoteViewsFactory(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Log.v(Constants.TAG, "onDataSetChanged()");
        //Query to get the the ingredients stored
//        Uri INGREDIENT_URI = RecipesContract.BASE_CONTENT_URI.buildUpon().appendPath(RecipesContract.PATH_RECIPES).build();
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                CONTENT_URI,
                null,
                null,
                null,
                null);
        Log.v(Constants.TAG, "" + mCursor.getCount());
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;

        mCursor.moveToPosition(position);
        int ingredientIndex = mCursor.getColumnIndex(RecipesContract.RecipeEntry.COLUMN_INGREDIENT);
        int measureIndex = mCursor.getColumnIndex(RecipesContract.RecipeEntry.COLUMN_MEASURE);
        int quantityIndex = mCursor.getColumnIndex(RecipesContract.RecipeEntry.COLUMN_QUANTITY);

        String ingredient = mCursor.getString(ingredientIndex);
        String measure = mCursor.getString(measureIndex);
        String quantity = mCursor.getString(quantityIndex);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_item);

        views.setTextViewText(R.id.tv_ingredient_item_name, ingredient);
        views.setTextViewText(R.id.tv_ingredient_item_quantity, mContext.getString(R.string.dynamic_quantity, quantity, measure));

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
