package com.ravi.bakingapp;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ravi.bakingapp.adapters.IngredientsAdapter;
import com.ravi.bakingapp.adapters.StepsAdapter;
import com.ravi.bakingapp.database.RecipesContract;
import com.ravi.bakingapp.model.Ingredients;
import com.ravi.bakingapp.model.Recipe;
import com.ravi.bakingapp.model.Steps;
import com.ravi.bakingapp.utils.JsonKeys;
import com.ravi.bakingapp.utils.OnItemClickHandler;
import com.ravi.bakingapp.utils.PreferencesUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailFragment extends Fragment implements OnItemClickHandler {

    @BindView(R.id.rv_ingredients_recycler)
    RecyclerView ingredientsRecycler;
    @BindView(R.id.rv_steps_recycler)
    RecyclerView stepsRecycler;

    ArrayList<Recipe> recipeList;
    int recipePosition;

    //    Recipe recipeItem;
    ArrayList<Ingredients> ingredientsList;
    ArrayList<Steps> stepsList;
    IngredientsAdapter adapter;
    StepsAdapter stepsAdapter;

    // Define a new interface OnStepSelectedListener that triggers a callback in the host activity
    OnStepSelectedListener mStepCallback;
    // Define a new interface OnActionClickedListener that triggers a callback in the host activity
//    OnActionClickedListener mActionCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        recipeList = getActivity().getIntent().getParcelableArrayListExtra(JsonKeys.DATA_KEY);
        if (savedInstanceState != null) {
            recipePosition = savedInstanceState.getInt(JsonKeys.POSITION_KEY);
//            Log.v(Constants.TAG, "fragment Saved Position " + recipePosition);
        }else {
            recipePosition = getActivity().getIntent().getIntExtra(JsonKeys.POSITION_KEY, -1);
//            Log.v(Constants.TAG, "fragment not saved Position " + recipePosition);
        }


        stepsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ViewCompat.setNestedScrollingEnabled(stepsRecycler, false);
        stepsRecycler.setHasFixedSize(true);

        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ViewCompat.setNestedScrollingEnabled(ingredientsRecycler, false);
        ingredientsRecycler.setHasFixedSize(true);

        setData(recipeList.get(recipePosition), recipePosition);
//        recipeItem = recipeList.get(recipePosition);
    }

    public void setData(Recipe recipe, int position) {
        recipePosition = position;
        //save name of recipe in shared preference in order for the widget to access it
        PreferencesUtils pref = new PreferencesUtils(getContext());
        pref.setData(JsonKeys.NAME_KEY, recipe.getName());
        if(ingredientsList != null)
            ingredientsList.clear();
        else
            ingredientsList = new ArrayList<>();
        if(stepsList != null)
            stepsList.clear();
        else
            stepsList = new ArrayList<>();

        ingredientsList.addAll(recipe.getIngredientsList());
        stepsList.addAll(recipe.getStepsList());
        if (adapter != null && stepsAdapter != null) {
            adapter.notifyDataSetChanged();
            stepsAdapter.notifyDataSetChanged();
        } else {
            adapter = new IngredientsAdapter(getContext(), ingredientsList);
            stepsAdapter = new StepsAdapter(getContext(), stepsList, this);
            ingredientsRecycler.setAdapter(adapter);
            stepsRecycler.setAdapter(stepsAdapter);
        }
        createDatabase();
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mStepCallback = (OnStepSelectedListener) context;
//            mActionCallback = (OnActionClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Interface");
        }
    }

    private void createDatabase() {
        // clear database first
        getActivity().getContentResolver().delete(RecipesContract.RecipeEntry.CONTENT_URI, null, null);
        // create new value set
        ContentValues[] contentValues = new ContentValues[ingredientsList.size()];
        int i = 0;
        for (Ingredients ingredients : ingredientsList) {
            ContentValues values = new ContentValues();
            values.put(RecipesContract.RecipeEntry.COLUMN_INGREDIENT, ingredients.getIngredientName());
            values.put(RecipesContract.RecipeEntry.COLUMN_MEASURE, ingredients.getMeasure());
            values.put(RecipesContract.RecipeEntry.COLUMN_QUANTITY, ingredients.getQuantity());
            contentValues[i++] = values;
        }
        // insert
        getActivity().getContentResolver().bulkInsert(RecipesContract.RecipeEntry.CONTENT_URI, contentValues);
        WidgetUpdateService.startActionUpdateRecipeWidget(getContext());
    }

    /*
    * This function is called when a step is selected
    */
    @Override
    public void onClick(int position) {
        mStepCallback.onStepSelected(position);
    }


    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(JsonKeys.POSITION_KEY, recipePosition);
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(JsonKeys.POSITION_KEY, recipePosition);
    }

    // OnStepSelectedListener interface, calls a method in the host activity named onStepSelected
    interface OnStepSelectedListener {
        void onStepSelected(int position);
    }

    /*interface OnActionClickedListener {
        void onActionClicked(int which, int position);
    }*/
}
