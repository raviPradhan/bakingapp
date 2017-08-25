package com.ravi.bakingapp;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ravi.bakingapp.adapters.RecipeListAdapter;
import com.ravi.bakingapp.model.Recipe;
import com.ravi.bakingapp.tasks.GetRecipesLoader;
import com.ravi.bakingapp.utils.Constants;
import com.ravi.bakingapp.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {

    @BindView(R.id.rv_main_recipeList)
    RecyclerView recipeRecycler;

    @BindView(R.id.tv_main_message)
    TextView messageText;

    @BindView(R.id.pb_main_progress)
    ProgressBar progress;

    @BindString(R.string.no_recipes)
    int noRecipes;
    @BindString(R.string.no_internet)
    int noInternet;

    ArrayList<Recipe> recipeList;
    RecipeListAdapter adapter;

    private static final int RECIPES_LOADER_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setAdapter();
        if (NetworkUtils.isInternetConnected(this)) {
            getSupportLoaderManager().initLoader(RECIPES_LOADER_ID, null, this);
        } else {
            Toast.makeText(this, getString(noInternet), Toast.LENGTH_SHORT).show();
        }

    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new RecipeListAdapter(this, recipeList);
            recipeRecycler.setLayoutManager(new LinearLayoutManager(this));
            recipeRecycler.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        if (NetworkUtils.isInternetConnected(this)) {
            return new GetRecipesLoader(this, Constants.GET_RECIPES_URL);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> data) {
        progress.setVisibility(View.GONE);
        recipeList = data;
        if (recipeList == null) {
            recipeRecycler.setVisibility(View.GONE);
            messageText.setText(noRecipes);
            messageText.setVisibility(View.VISIBLE);
        } else {
            setAdapter();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {
        if (recipeList != null)
            recipeList.clear();
    }
}
