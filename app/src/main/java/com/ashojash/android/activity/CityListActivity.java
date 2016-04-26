package com.ashojash.android.activity;

import android.os.Bundle;
import com.ashojash.android.R;
import com.ashojash.android.fragment.CityListFragment;

public class CityListActivity extends ToolbarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        addFragment(R.id.fragmentCityListContainer, new CityListFragment());
    }
}
