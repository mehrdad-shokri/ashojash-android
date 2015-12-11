package com.ashojash.android.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

public class NavigationDrawerActivity extends BaseActivity {
    private Toolbar toolbarTop;
//    private Toolbar toolbarBottom;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupTopToolbar();
        setupNavigationDrawer2();
//        setupNavigationDrawer();
    }

    private void setupNavigationDrawer2() {
        String TAG = AppController.TAG;
//create the drawer and remember the `Drawer` result object
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                IconicsDrawable errorIcon = new IconicsDrawable(AppController.context).icon(GoogleMaterial.Icon.gmd_error).sizeDp(12).color(AppController.context.getResources().getColor(R.color.text_primary));
                Glide.with(imageView.getContext()).load(uri).centerCrop().placeholder(placeholder).diskCacheStrategy(DiskCacheStrategy.RESULT).error(errorIcon).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);

            }
        });
        /*if (AuthUtils.isUserLoggedIn()) {
            StructUser user = AuthUtils.getAuthUser();
            AccountHeader headerResult = new AccountHeaderBuilder()
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
                    .withToolbar(toolbarTop)
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
                            return false;
                        }
                    })
                    .build();
        }*/
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupTopToolbar() {
        toolbarTop = (Toolbar) findViewById(R.id.toolbarTop);
        setSupportActionBar(toolbarTop);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(false);

        } catch (NullPointerException e) {

        }
    }


    protected void setupNavigationDrawer() {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem();
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("تنظیمات").withIcon(GoogleMaterial.Icon.gmd_home);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withName(R.string.app_name).
                withIcon(GoogleMaterial.Icon.gmd_settings);


        AccountHeader accountHeader = setupProfile();
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbarTop)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new SecondaryDrawerItem().withName(R.string.app_name).withDescription("description , bitch")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        return false;
                    }
                })
                .withAccountHeader(accountHeader, true)
                .build();
    }

    private AccountHeader setupProfile() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(new IconicsDrawable(this)
                                .icon(GoogleMaterial.Icon.gmd_account_circle)
                                .color(Color.WHITE)
                                .sizeDp(24))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
        return headerResult;
    }
}
