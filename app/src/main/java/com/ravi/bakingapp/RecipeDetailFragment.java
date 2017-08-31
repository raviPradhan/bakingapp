package com.ravi.bakingapp;

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
import com.ravi.bakingapp.model.Recipe;
import com.ravi.bakingapp.utils.JsonKeys;
import com.ravi.bakingapp.utils.OnItemClickHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailFragment extends Fragment implements OnItemClickHandler {

    @BindView(R.id.rv_ingredients_recycler)
    RecyclerView ingredientsRecycler;
    @BindView(R.id.rv_steps_recycler)
    RecyclerView stepsRecycler;

    IngredientsAdapter adapter;
    StepsAdapter stepsAdapter;

    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    OnStepSelectedListener mCallback;

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        stepsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ViewCompat.setNestedScrollingEnabled(stepsRecycler, false);
        stepsRecycler.setHasFixedSize(true);

        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ViewCompat.setNestedScrollingEnabled(ingredientsRecycler, false);
        ingredientsRecycler.setHasFixedSize(true);

        Recipe recipeItem = getActivity().getIntent().getParcelableExtra(JsonKeys.DATA_KEY);

        adapter = new IngredientsAdapter(getContext(), recipeItem.getIngredientsList());
        ingredientsRecycler.setAdapter(adapter);

        stepsAdapter = new StepsAdapter(getContext(), recipeItem.getStepsList(), this);
        stepsRecycler.setAdapter(stepsAdapter);
    }

    @Override
    public void onClick(int position) {
        mCallback.onStepSelected(position);
    }

    // OnStepSelectedListener interface, calls a method in the host activity named onStepSelected
    interface OnStepSelectedListener {
        void onStepSelected(int position);
    }
}