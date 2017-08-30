package com.ravi.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ravi.bakingapp.model.Recipe;
import com.ravi.bakingapp.utils.JsonKeys;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MasterActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    boolean twoPane;
    Recipe recipeItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_master);

        ButterKnife.bind(this);

        recipeItem = getIntent().getParcelableExtra(JsonKeys.DATA_KEY);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(recipeItem.getName());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            twoPane = false;
        }else{
            twoPane = true;
            Bundle bundle = new Bundle();
            bundle.putParcelable(JsonKeys.DATA_KEY, recipeItem.getStepsList().get(0));
            Fragment fragment = new StepDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.fl_master_container, fragment).commit();
        }
    }

    @Override
    public void onStepSelected(int position) {
        if(twoPane){
            Bundle bundle = new Bundle();
            bundle.putParcelable(JsonKeys.DATA_KEY, recipeItem.getStepsList().get(position));
            Fragment fragment = new StepDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_master_container, fragment).commit();
        }else{
            startActivity(new Intent(this, StepActivity.class).putExtra(JsonKeys.DATA_KEY, recipeItem.getStepsList().get(position)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
