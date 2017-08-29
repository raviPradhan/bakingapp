package com.ravi.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ravi.bakingapp.R;
import com.ravi.bakingapp.model.Ingredients;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    private Context mContext;
    private ArrayList<Ingredients> ingredientList;

    public IngredientsAdapter(Context mContext, ArrayList<Ingredients> list) {
        this.mContext = mContext;
        this.ingredientList = list;
    }

    // Inner class for creating ViewHolders
    class IngredientViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        @BindView(R.id.tv_ingredient_item_name)
        TextView ingredientName;
        @BindView(R.id.tv_ingredient_item_quantity)
        TextView ingredientQuantity;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.ingredient_item, parent, false);

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredients currentItem = ingredientList.get(position);
        holder.ingredientName.setText(currentItem.getIngredientName());
        holder.ingredientQuantity.setText(mContext.getString(R.string.dynamic_quantity, currentItem.getQuantity(), currentItem.getMeasure()));
    }

    @Override
    public int getItemCount() {
        return (ingredientList == null) ? 0 : ingredientList.size();
    }
}