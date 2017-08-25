package com.ravi.bakingapp.tasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.ravi.bakingapp.model.Recipe;
import com.ravi.bakingapp.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

public class GetRecipesLoader extends AsyncTaskLoader<ArrayList<Recipe>> {

    private ArrayList<Recipe> recipeList;
    private String url;

    public GetRecipesLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        if (recipeList != null) {
            // Delivers any previously loaded data immediately
            deliverResult(recipeList);
        } else {
            // Force a new load
            forceLoad();
        }
    }

    @Override
    public ArrayList<Recipe> loadInBackground() {
        try{
            String response = NetworkUtils.getJson(url);

        }catch (IOException ioEx){
            ioEx.printStackTrace();
        }
        return null;
    }

    // deliverResult sends the result of the load, a Cursor, to the registered listener
    public void deliverResult(ArrayList<Recipe> data) {
        recipeList.addAll(data);
        super.deliverResult(data);
    }
}
