package com.ravi.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ravi.bakingapp.model.Steps;
import com.ravi.bakingapp.utils.JsonKeys;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_step);

        ButterKnife.bind(this);

        Steps stepItem = getIntent().getParcelableExtra(JsonKeys.DATA_KEY);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(stepItem.getShortDescription());


        replaceFragment();
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
