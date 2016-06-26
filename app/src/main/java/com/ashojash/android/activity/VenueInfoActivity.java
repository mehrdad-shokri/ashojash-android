package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.fragment.VenueInfoFragment;
import com.ashojash.android.fragment.VenueMapFragment;
/*
* Completed
* */
public class VenueInfoActivity extends BaseActivity {
    protected Toolbar toolbar;
    private String slug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slug = getIntent().getStringExtra("slug");
        if (slug == null) finish();
        setContentView(R.layout.activity_venue_info);
        setupViews();
        Fragment venueMapFragment = new VenueMapFragment();
        addFragment(R.id.fragmentMapContainer, venueMapFragment);
        Fragment venueLocationFragment=new VenueInfoFragment();
        addFragment(R.id.fragmentInfoContainer,venueLocationFragment);
    }

    /*private void setupNavigationDrawer() {
        if (AuthUtil.isUserLoggedIn()) {
            StructUser user = AuthUtil.getAuthUser();
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withSelectionListEnabledForSingleProfile(false)
                    .withProfileImagesClickable(false)
                    .addProfiles(
                            new ProfileDrawerItem().withName(user.getName()).withEmail(user.getEmail()).withIcon(GoogleMaterial.Icon.gmd_mail)
                    )
                    .withHeaderBackground(R.drawable.acount_header_background)
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                            return false;
                        }
                    })
                    .build();
            int reviewColor = getResources().getColor(R.color.colorNavigationDrawerItemAddReview);
            int photoColor = getResources().getColor(R.color.colorNavigationDrawerItemAddPhoto);
            int primaryDrawerItemColor = getResources().getColor(R.color.colorNavigationDrawerItemPrimary);
            new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withAccountHeader(headerResult)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName(R.string.add_review).withIcon(GoogleMaterial.Icon.gmd_star_border).withIconColor(reviewColor).withSelectedIconColor(reviewColor).withTextColor(reviewColor).withSelectedTextColor(reviewColor).withSelectedIconColor(reviewColor),
                            new PrimaryDrawerItem().withName(R.string.add_pic).withIcon(GoogleMaterial.Icon.gmd_photo_camera).withIconColor(photoColor).withSelectedIconColor(photoColor).withTextColor(photoColor).withSelectedTextColor(photoColor).withSelectedIconColor(photoColor),
                            new DividerDrawerItem(),
                            new PrimaryDrawerItem().withName(R.string.action_settings).withIcon(GoogleMaterial.Icon.gmd_settings).withTextColor(primaryDrawerItemColor).withIconColor(primaryDrawerItemColor).withSelectedIconColor(primaryDrawerItemColor).withSelectedTextColor(primaryDrawerItemColor)
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            // do something with the clicked item :D
//                            GoogleMaterial.Icon.gmd_photo_library
                            return false;
                        }
                    })
                    .build();
        }
    }*/

    private void setupViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        ((TextView) toolbar.findViewById(R.id.txtToolbarTitle)).setText(R.string.title_info);
    }
}
