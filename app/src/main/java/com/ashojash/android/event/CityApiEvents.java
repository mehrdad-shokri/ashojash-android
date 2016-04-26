package com.ashojash.android.event;

import com.ashojash.android.model.City;

import java.util.List;

public final class CityApiEvents {
    private CityApiEvents() {
    }

    public static class OnAllCitiesAvailable {
        public List<City> cityList;

        public OnAllCitiesAvailable(List<City> cityList) {
            this.cityList = cityList;
        }
    }
}
