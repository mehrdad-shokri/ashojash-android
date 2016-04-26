package com.ashojash.android.adapter;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.activity.VenueActivity;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

public class HeroPagerAdapter extends PagerAdapter {

    private List<Venue> venueList;

    public HeroPagerAdapter(List<Venue> venueList) {
        this.venueList = venueList;
    }


    @Override
    public int getCount() {
        return venueList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = AppController.layoutInflater.inflate(R.layout.viewpager_hero, null);
        final Venue venue = venueList.get(position);
        TextView txtHeroViewpager = (TextView) view.findViewById(R.id.txtHeroViewpager);
        ImageView imgHeroViewpager = (ImageView) view.findViewById(R.id.imgHeroViewpager);
        ViewGroup rootLayout = (ViewGroup) view.findViewById(R.id.layoutHeroViewPager);
        Glide.with(AppController.context).load(venue.photo.url).centerCrop().placeholder(R.drawable.city_list_loader).error(new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary))).diskCacheStrategy(DiskCacheStrategy.RESULT).into(imgHeroViewpager);
        txtHeroViewpager.setText(venue.name);
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppController.currentActivity, VenueActivity.class);
                intent.putExtra("slug", venue.slug);
                AppController.currentActivity.startActivity(intent);
            }
        });
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
