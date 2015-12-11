package com.ashojash.android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.ashojash.android.R;
import com.ashojash.android.fragment.VenueInfoFragment;
import com.ashojash.android.fragment.VenueMapFragment;
import com.ashojash.android.struct.StructUser;
import com.ashojash.android.utils.AuthUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class VenueInfoActivity extends BaseActivity {
    private AccountHeader headerResult;
    protected Toolbar toolbar;
    private String slug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slug = getIntent().getStringExtra("slug");
        if (slug == null) finish();
        setContentView(R.layout.activity_venue_info);
        setupViews();
        setupNavigationDrawer();
        Fragment venueMapFragment = new VenueMapFragment();
        addFragment(R.id.fragmentMapContainer, venueMapFragment);
        Fragment venueLocationFragment=new VenueInfoFragment();
        addFragment(R.id.fragmentInfoContainer,venueLocationFragment);
    }

    private void setupNavigationDrawer() {
        if (AuthUtils.isUserLoggedIn()) {
            StructUser user = AuthUtils.getAuthUser();
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
    }

    private void setupViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbarTop);
        toolbar.setTitle("Venue name");
    }
}
