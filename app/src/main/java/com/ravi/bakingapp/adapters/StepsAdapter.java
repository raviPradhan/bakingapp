package com.ravi.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ravi.bakingapp.R;
import com.ravi.bakingapp.model.Steps;
import com.ravi.bakingapp.utils.OnItemClickHandler;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {

    private Context mContext;
    private ArrayList<Steps> stepsList;
    private OnItemClickHandler itemClickCallback;

    public StepsAdapter(Context mContext, ArrayList<Steps> list, OnItemClickHandler itemClick) {
        this.mContext = mContext;
        this.stepsList = list;
        this.itemClickCallback = itemClick;
    }

    // Inner class for creating ViewHolders
    class StepsViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        @BindView(R.id.tv_steps_item_description)
        TextView stepTitle;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        StepsViewHolder(View itemView) {
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
    public StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.steps_item, parent, false);

        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, int position) {
        Steps currentItem = stepsList.get(position);
        final int adapterPosition = position;

        holder.stepTitle.setText(currentItem.getShortDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickCallback.onClick(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (stepsList == null) ? 0 : stepsList.size();
    }
}
