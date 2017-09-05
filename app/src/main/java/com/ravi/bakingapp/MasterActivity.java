package com.ravi.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ravi.bakingapp.model.Recipe;
import com.ravi.bakingapp.utils.Constants;
import com.ravi.bakingapp.utils.JsonKeys;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MasterActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepSelectedListener,
        /*RecipeDetailFragment.OnActionClickedListener,*/ View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bt_recycler_previous)
    Button previousButton;
    @BindView(R.id.bt_recycler_next)
    Button nextButton;

    RecipeDetailFragment fragment;

    boolean twoPane;
    ArrayList<Recipe> recipeList = new ArrayList<>();
    Recipe recipeItem;
    int recipePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_master);

        ButterKnife.bind(this);

        recipeList = getIntent().getParcelableArrayListExtra(JsonKeys.DATA_KEY);
        if (savedInstanceState != null) {
            recipePosition = savedInstanceState.getInt(JsonKeys.POSITION_KEY);
            Log.v(Constants.TAG, "Saved Position " + recipePosition);
        }else {
            recipePosition = getIntent().getIntExtra(JsonKeys.POSITION_KEY, -1);
            Log.v(Constants.TAG, "not saved Position " + recipePosition);
        }
        recipeItem = recipeList.get(recipePosition);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(recipeItem.getName());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            twoPane = false;
        } else {
            if (findViewById(R.id.fl_master_container) != null) {
                twoPane = true;
                if (savedInstanceState == null) {
                    Fragment fragment = new StepDetailFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.fl_master_container, fragment).commit();
                }
            }
        }
        fragment = (RecipeDetailFragment) getSupportFragmentManager().findFragmentById(R.id.master_detail_fragment);
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onStepSelected(int position) {
        if (twoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(JsonKeys.DATA_KEY, recipeItem.getStepsList().get(position));
            Fragment fragment = new StepDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_master_container, fragment).commit();
        } else {
            startActivity(new Intent(this, StepActivity.class).putExtra(JsonKeys.DATA_KEY, recipeItem.getStepsList().get(position)));
        }
    }

    /*
    * This function is called when previous or next button is clicked
    */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_recycler_previous:
                // check if its not the first recipe and update
                if (recipePosition > 0) {
                    --recipePosition;
                    recipeItem = recipeList.get(recipePosition);
                    Log.v(Constants.TAG, "POSITION: " + recipePosition + " recipe name: "
                            + recipeList.get(recipePosition).getName() + " ingredient size: "
                            + recipeList.get(recipePosition).getIngredientsList().size() + " NAME: "
                            + recipeList.get(recipePosition).getIngredientsList().get(0).getIngredientName());

                    Log.v(Constants.TAG, "SIZE: " + recipeList.get(0).getIngredientsList().size()
                            + "name: " + recipeList.get(0).getIngredientsList().get(0).getIngredientName());
                    fragment.setData(recipeItem, recipePosition);
                    /*ingredientsList.clear();
                    stepsList.clear();
                    ingredientsList.addAll(recipeItem.getIngredientsList());
                    stepsList.addAll(recipeItem.getStepsList());*/
                    /*Log.v(Constants.TAG, "" + recipePosition +
                            " Ingredient size: " + ingredientsList.size() +
                            " NAME: " + ingredientsList.get(0).getIngredientName());
                    adapter.notifyDataSetChanged();
                    stepsAdapter.notifyDataSetChanged();
                    mActionCallback.onActionClicked(0, recipePosition);*/
                } else {
                    Toast.makeText(this, getString(R.string.first_recipe), Toast.LENGTH_SHORT).show();
                }
                break;
            // check if its not the last recipe and update
            case R.id.bt_recycler_next:
                if (recipePosition != recipeList.size() - 1) {
                    ++recipePosition;
                    recipeItem = recipeList.get(recipePosition);
                    Log.v(Constants.TAG, "POSITION: " + recipePosition + " recipe name: "
                            + recipeList.get(recipePosition).getName() + " ingredient size: "
                            + recipeList.get(recipePosition).getIngredientsList().size() + " NAME: "
                            + recipeList.get(recipePosition).getIngredientsList().get(0).getIngredientName());
                    Log.v(Constants.TAG, "SIZE: " + recipeList.get(0).getIngredientsList().size()
                            + "name: " + recipeList.get(0).getIngredientsList().get(0).getIngredientName());
                    fragment.setData(recipeItem, recipePosition);
                    /*ingredientsList.clear();
                    ingredientsList.addAll(recipeItem.getIngredientsList());
                    stepsList.clear();
                    stepsList.addAll(recipeItem.getStepsList());
                    adapter.notifyDataSetChanged();
                    stepsAdapter.notifyDataSetChanged();
                    mActionCallback.onActionClicked(1, recipePosition);*/
                } else {
                    Toast.makeText(this, getString(R.string.last_recipe), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        getSupportActionBar().setTitle(recipeItem.getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(Constants.TAG, "SAVE POSITION " + recipePosition);
        outState.putInt(JsonKeys.POSITION_KEY, recipePosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
