package com.ravi.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ravi.bakingapp.model.Steps;
import com.ravi.bakingapp.utils.JsonKeys;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bt_step_previous)
    Button previous;
    @BindView(R.id.bt_step_next)
    Button next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step);

        ButterKnife.bind(this);
        ArrayList<Steps> stepsList = getIntent().getParcelableExtra(JsonKeys.DATA_KEY);
        int position = getIntent().getIntExtra(JsonKeys.POSITION_KEY, -1);
        Steps stepItem = stepsList.get(position);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(stepItem.getShortDescription());

        replaceFragment();

        previous.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    private void replaceFragment(){
        Fragment fragment = new StepDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(JsonKeys.DATA_KEY, getIntent().getParcelableExtra(JsonKeys.DATA_KEY));
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_step_container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(android.R.id.home == item.getItemId())
            finish();

        return super.onOptionsItemSelected(item);
    }
}
