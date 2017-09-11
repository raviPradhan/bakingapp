package com.ravi.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ravi.bakingapp.IdlingResource.SimpleIdlingResource;
import com.ravi.bakingapp.adapters.RecipeListAdapter;
import com.ravi.bakingapp.model.Recipe;
import com.ravi.bakingapp.tasks.GetRecipesLoader;
import com.ravi.bakingapp.utils.Constants;
import com.ravi.bakingapp.utils.JsonKeys;
import com.ravi.bakingapp.utils.NetworkUtils;
import com.ravi.bakingapp.utils.OnItemClickHandler;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>>, OnItemClickHandler {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_main_recipeList)
    RecyclerView recipeRecycler;

    @BindView(R.id.tv_main_message)
    TextView messageText;
    @BindView(R.id.pb_main_progress)
    ProgressBar progress;

    @BindString(R.string.no_recipes)
    String noRecipes;
    @BindString(R.string.no_internet)
    String noInternet;

    ArrayList<Recipe> recipeList;
    RecipeListAdapter adapter;

    private static final int RECIPES_LOADER_ID = 100;

//    Add a SimpleIdlingResource variable that will be null in production
    @Nullable
    private SimpleIdlingResource simpleIdlingResource;

    /*
     * DONE Create a method that returns the IdlingResource variable. It will
     * instantiate a new instance of SimpleIdlingResource if the IdlingResource is null.
     * This method will only be called from test.
     */
    /*
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public SimpleIdlingResource getIdlingResource() {
        if (simpleIdlingResource == null) {
            simpleIdlingResource = new SimpleIdlingResource();
        }
        return simpleIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if(savedInstanceState != null){ // load any saved data in the instance
            recipeList = savedInstanceState.getParcelableArrayList(JsonKeys.DATA_KEY);
            setAdapter();
        }else{ // make a network call if there is no data present
            recipeList = new ArrayList<>();
            setAdapter();
            if (NetworkUtils.isInternetConnected(this)) {
                getSupportLoaderManager().initLoader(RECIPES_LOADER_ID, null, this);
            } else {
                messageText.setText(noInternet);
                messageText.setVisibility(View.VISIBLE);
            }
        }

    }

    private void setAdapter() {
        messageText.setVisibility(View.GONE);
        recipeRecycler.setVisibility(View.VISIBLE);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recipeRecycler.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recipeRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        }
        if (adapter == null) {
            adapter = new RecipeListAdapter(this, recipeList, this);
            recipeRecycler.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
            if (simpleIdlingResource != null) {
                simpleIdlingResource.setIdleState(true);
            }
        }
    }

    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        if (NetworkUtils.isInternetConnected(this)) {
            return new GetRecipesLoader(this, Constants.GET_RECIPES_URL, getIdlingResource());
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> data) {
        progress.setVisibility(View.GONE);
        recipeList.addAll(data);
        if (recipeList.isEmpty()) {
            recipeRecycler.setVisibility(View.GONE);
            messageText.setText(noRecipes);
            messageText.setVisibility(View.VISIBLE);
        } else {
            setAdapter();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save instance to not make network calls in future if data already present
        outState.putParcelableArrayList(JsonKeys.DATA_KEY, recipeList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(int position) {
        startActivity(new Intent(this, MasterActivity.class)
                .putExtra(JsonKeys.POSITION_KEY, position)
                .putParcelableArrayListExtra(JsonKeys.DATA_KEY, recipeList));
    }
}
