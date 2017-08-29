package com.ravi.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ravi.bakingapp.adapters.IngredientsAdapter;
import com.ravi.bakingapp.adapters.StepsAdapter;
import com.ravi.bakingapp.model.Recipe;
import com.ravi.bakingapp.utils.JsonKeys;
import com.ravi.bakingapp.utils.OnItemClickHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements OnItemClickHandler {

    @BindView(R.id.tv_recipe_detail_name)
    TextView recipeName;
    @BindView(R.id.rv_recipe_detail_ingredients)
    RecyclerView ingredientsRecycler;
    @BindView(R.id.rv_recipe_detail_steps)
    RecyclerView stepsRecycler;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    IngredientsAdapter adapter;
    StepsAdapter stepsAdapter;
    Recipe recipeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ButterKnife.bind(this);

        recipeItem = getIntent().getParcelableExtra(JsonKeys.DATA_KEY);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(recipeItem.getName());

        recipeName = (TextView) findViewById(R.id.tv_recipe_detail_name);
        ingredientsRecycler = (RecyclerView) findViewById(R.id.rv_recipe_detail_ingredients);
        stepsRecycler = (RecyclerView) findViewById(R.id.rv_recipe_detail_steps);

        stepsRecycler.setLayoutManager(new LinearLayoutManager(this));
        ViewCompat.setNestedScrollingEnabled(stepsRecycler, false);
        stepsRecycler.setHasFixedSize(true);

        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(this));
        ViewCompat.setNestedScrollingEnabled(ingredientsRecycler, false);
        ingredientsRecycler.setHasFixedSize(true);

        recipeName.setText(recipeItem.getName());

        adapter = new IngredientsAdapter(this, recipeItem.getIngredientsList());
        ingredientsRecycler.setAdapter(adapter);

        stepsAdapter = new StepsAdapter(this, recipeItem.getStepsList(), this);
        stepsRecycler.setAdapter(stepsAdapter);
    }

    @Override
    public void onClick(int position) {
        startActivity(new Intent(this, StepDetailActivity.class).
                putExtra(JsonKeys.DATA_KEY, recipeItem.getStepsList().get(position)));
    }
}
