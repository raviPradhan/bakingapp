package com.ravi.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    ArrayList<Steps> stepsList;
    Steps stepItem;
    int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step);

        ButterKnife.bind(this);
        stepsList = getIntent().getParcelableArrayListExtra(JsonKeys.DATA_KEY);
        if(savedInstanceState != null){
            position = savedInstanceState.getInt(JsonKeys.POSITION_KEY);
            stepItem = savedInstanceState.getParcelable(JsonKeys.DATA_KEY);
        }else {
            position = getIntent().getIntExtra(JsonKeys.POSITION_KEY, -1);
            stepItem = stepsList.get(position);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(stepItem.getShortDescription());

        if (savedInstanceState == null)
            replaceFragment();

        previous.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_step_previous:
                if (position == 0) {
                    Toast.makeText(this, getString(R.string.first_step), Toast.LENGTH_SHORT).show();
                } else {
                    position--;
                    stepItem = stepsList.get(position);
                    replaceFragment();
                    getSupportActionBar().setTitle(stepItem.getShortDescription());
                }
                break;

            case R.id.bt_step_next:
                if (position < stepsList.size() - 1) {
                    position++;
                    stepItem = stepsList.get(position);
                    replaceFragment();
                    getSupportActionBar().setTitle(stepItem.getShortDescription());
                } else {
                    Toast.makeText(this, getString(R.string.last_step), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void replaceFragment() {
        Fragment fragment = new StepDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(JsonKeys.DATA_KEY, stepItem);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_step_container, fragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(JsonKeys.POSITION_KEY, position);
        outState.putParcelable(JsonKeys.DATA_KEY, stepItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (android.R.id.home == item.getItemId())
            finish();

        return super.onOptionsItemSelected(item);
    }
}
