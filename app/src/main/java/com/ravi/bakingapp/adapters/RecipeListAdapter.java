package com.ravi.bakingapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ravi.bakingapp.R;
import com.ravi.bakingapp.model.Recipe;
import com.ravi.bakingapp.utils.OnItemClickHandler;

import java.util.ArrayList;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    private Context mContext;
    private ArrayList<Recipe> recipeList;
    private OnItemClickHandler clickCallback;

    public RecipeListAdapter(Context mContext, ArrayList<Recipe> list, OnItemClickHandler callback) {
        this.mContext = mContext;
        this.recipeList = list;
        this.clickCallback = callback;
    }

    // Inner class for creating ViewHolders
    class RecipeViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        @BindView(R.id.tv_recipe_item_name)
        TextView recipeName;
        @BindView(R.id.iv_recipe_item_image)
        ImageView recipeImage;
        @BindDrawable(R.drawable.ic_recipe_placehoder)
        Drawable placeHolder;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public RecipeViewHolder(View itemView) {
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
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recipe_item, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe currentItem = recipeList.get(position);
        final int adapterPosition = position;
        holder.recipeName.setText(currentItem.getName());
        Glide.with(mContext).load(currentItem.getImgPath())
                .placeholder(holder.placeHolder)
                .error(holder.placeHolder).
                into(holder.recipeImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCallback.onClick(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (recipeList == null)? 0 : recipeList.size();
    }
}
