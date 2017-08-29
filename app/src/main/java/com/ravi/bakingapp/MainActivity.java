package com.ravi.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    @BindView(R.id.rv_main_recipeList)
    RecyclerView recipeRecycler;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        recipeList = new ArrayList<>();
        setAdapter();
        if (NetworkUtils.isInternetConnected(this)) {
            getSupportLoaderManager().initLoader(RECIPES_LOADER_ID, null, this);
        } else {
            Toast.makeText(this, noInternet, Toast.LENGTH_SHORT).show();
        }

    }

    private void setAdapter() {
        messageText.setVisibility(View.GONE);
        recipeRecycler.setVisibility(View.VISIBLE);
        if (adapter == null) {
            adapter = new RecipeListAdapter(this, recipeList, this);
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
        recipeList.addAll(data);
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

    @Override
    public void onClick(int position) {
        startActivity(new Intent(this, RecipeDetailActivity.class).putExtra(JsonKeys.DATA_KEY, recipeList.get(position)));
    }
}
