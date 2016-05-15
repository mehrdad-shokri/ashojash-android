package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.fragment.CityListFragment;

public class CityListActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        setupView();
        addFragment(R.id.fragmentCityListContainer, new CityListFragment());
    }

    private static final String TAG = "CityListActivity";

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        ((TextView) toolbar.findViewById(R.id.txtToolbarTitle)).setText(R.string.title_city_select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
