package com.ashojash.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.activeandroid.query.Select;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashojash.android.R;
import com.ashojash.android.customview.CostRatingOnRatingBarChangeListener;
import com.ashojash.android.customview.GenericOnRatingBarChangeListener;
import com.ashojash.android.customview.VenueScoreIndicator;
import com.ashojash.android.db.VenueDb;
import com.ashojash.android.fragment.*;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.struct.StructUser;
import com.ashojash.android.struct.StructVenue;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.utils.AuthUtils;
import com.ashojash.android.webserver.JsonParser;
import com.ashojash.android.webserver.WebServer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialize.util.UIUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VenueActivity extends BaseActivity {
    String TAG = AppController.TAG;
    private ImageView imgHeroCollapsingToolbarLayout;
    private AccountHeader headerResult;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton addPhotoFab;
    private FloatingActionButton addReviewFab;
    private VenueScoreIndicator venueScoreIndicator;
    private VenueBasicReviewFragment venueBasicReviewFragment;
    private VenueBasicInfoFragment venueBasicInfoFragment;
    private VenueBasicMenuFragment venueBasicMenuFragment;
    private VenueBasicPhotosFragment venueBasicPhotosFragment;
    private Venue venue;
    private final int REQUEST_REGISTER = 8000;
    private Tracker mTracker;

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Venue activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String slug = getIntent().getStringExtra("slug");
        setContentView(R.layout.activity_venue);
// Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        setupViews();
        if (slug != null) {
            venue = new Select().from(Venue.class).where("slug =?", slug).executeSingle();
            collapsingToolbarLayout.setTitle(venue.name);
            Glide.with(this).load(venue.imageUrl).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imgHeroCollapsingToolbarLayout);
            venueBasicInfoFragment = new VenueBasicInfoFragment();
            venueBasicReviewFragment = new VenueBasicReviewFragment();
            venueBasicMenuFragment = new VenueBasicMenuFragment();
            venueBasicPhotosFragment = new VenueBasicPhotosFragment();
            addFragment(R.id.venueActivityBasicInfoContainer, venueBasicInfoFragment);
            addFragment(R.id.venueActivityReviewsContainer, venueBasicReviewFragment);
            addFragment(R.id.venueActivityMenusContainer, venueBasicMenuFragment);
            addFragment(R.id.venueActivityPicsContainer, venueBasicPhotosFragment);
            syncVenueInfo(slug);
            findViewById(R.id.venueActivityMenusContainer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            findViewById(R.id.venueActivityPicsContainer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            findViewById(R.id.venueActivityReviewsContainer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        } else {
            finish();
        }
    }


    private void syncVenueInfo(final String slug) {
        JsonObjectRequest jsonObjectRequest = WebServer.getVenueBasicInfo(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    StructVenue structVenue = JsonParser.parseVenueJsonObject(data.getJSONObject("venue"));
                    VenueDb.createOrUpdate(structVenue);
                    int reviewsCount = data.getInt("reviews_count");
                    int menusCount = data.getInt("menus_count");
                    venueScoreIndicator.setScore(structVenue.getScore(), reviewsCount);
                    JSONArray menus = data.getJSONArray("menus");
                    JSONArray reviews = data.getJSONArray("reviews");
                    JSONArray photos = data.getJSONArray("photos");
                    venueBasicReviewFragment.onDataReceived(reviews, reviewsCount);
                    venueBasicMenuFragment.onDataReceived(menus, menusCount);
                    venueBasicPhotosFragment.onDataReceived(photos);
                } catch (JSONException e) {
                    e.printStackTrace();
                    venueBasicReviewFragment.onDataReceived(null, 0);
                    venueBasicMenuFragment.onDataReceived(null, 0);
                    venueBasicPhotosFragment.onDataReceived(null);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(R.id.venueActivityRootLayout), R.string.error_retrieving_data, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        syncVenueInfo(slug);
                    }
                }).show();
                venueBasicReviewFragment.onDataReceived(null, 0);
                venueBasicMenuFragment.onDataReceived(null, 0);
                venueBasicPhotosFragment.onDataReceived(null);
            }
        }, slug);
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "VENUE_UPDATE_INFO");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.getInstance().cancelPendingRequests("VENUE_UPDATE_INFO");
    }

    private void setupViews() {
        venueScoreIndicator = (VenueScoreIndicator) findViewById(R.id.venueScoreIndicator);
        imgHeroCollapsingToolbarLayout = (ImageView) findViewById(R.id.backdrop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addReviewFab = (FloatingActionButton) findViewById(R.id.fragmentCollapsingToolbarRateReviewFab);
        addPhotoFab = (FloatingActionButton) findViewById(R.id.fragmentCollapsingToolbarAddPhotoFab);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        addReviewFab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_rate_review).actionBar().color(Color.WHITE));
        addPhotoFab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add_a_photo).actionBar().color(Color.WHITE));
        CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
        lp.height = lp.height + UIUtils.getStatusBarHeight(this);
        toolbar.setLayoutParams(lp);
        setSupportActionBar(toolbar);
        setupNavigationDrawer();
        addPhotoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AuthUtils.isUserLoggedIn()) {
                    VenueUploadPhotoDialogFragment photoUploadDialogFragment = VenueUploadPhotoDialogFragment.newInstance();
                    photoUploadDialogFragment.venue = venue;
                    photoUploadDialogFragment.show(getSupportFragmentManager(), "VENUE_PHOTO_UPLOAD_FRAGMENT");
                } else {
                    showLoginForm();
                }
            }
        });
        addReviewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AuthUtils.isUserLoggedIn()) {
                    setupAddReview();
                } else {
                    showLoginForm();
                }
            }
        });
    }

    private void showLoginForm() {
        final AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(AppController.currentActivity, R.style.AppCompatAlertDialogStyle);
        dialogBuilder.setView(R.layout.dialog_login);
        dialogBuilder.setInverseBackgroundForced(true);
        final AlertDialog loginDialog = dialogBuilder.show();
        Button btnRegister = (Button) loginDialog.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VenueActivity.this, GuestProfileActivity.class);
                intent.putExtra("current_viewpager_tab", 1);
                startActivityForResult(intent, REQUEST_REGISTER);
                onPause();
            }
        });
    }



    /*private void setupAddPhoto() {
        final AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(AppController.currentActivity, R.style.AppCompatAlertDialogStyle);
        dialogBuilder.setView(R.layout.dialog_add_photo);
        final AlertDialog registerCompleteDialog = dialogBuilder.show();
        TextView txtVenueTitle = (TextView) registerCompleteDialog.findViewById(R.id.txtAddPhotoTitle);
        txtVenueTitle.setText(getString(R.string.add_venue_photo_title).replace("{{venueName}}", venue.name));

        btnUploadVenuePhotos = (Button) registerCompleteDialog.findViewById(R.id.btnUploadVenuePhotos);
        venuePhotosRecyclerView = (RecyclerView) registerCompleteDialog.findViewById(R.id.recyclerViewPhotosList);

        Button btnAddVenuePhotoCamera = (Button) registerCompleteDialog.findViewById(R.id.btnAddVenuePhotoCamera);
        Button btnAddVenuePhotoGallery = (Button) registerCompleteDialog.findViewById(R.id.btnAddVenuePhotoGallery);
        btnAddVenuePhotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) || getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))) {
                    Snackbar.make(findViewById(R.id.venueActivityRootLayout), R.string.camera_not_supported, Snackbar.LENGTH_LONG).show();
                    return;
                }
                dispatchTakePictureIntent();
            }
//            }
        });
    }*/

    private void setupAddReview() {
        final AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(AppController.currentActivity, R.style.AppCompatAlertDialogStyle);
        dialogBuilder.setView(R.layout.dialog_add_review);
        dialogBuilder.setInverseBackgroundForced(true);
        final AlertDialog registerCompleteDialog = dialogBuilder.show();
        final Button btnPublish = (Button) registerCompleteDialog.findViewById(R.id.btnAddReview);
        TextView txtReviewTitle = (TextView) registerCompleteDialog.findViewById(R.id.txtReviewTitle);
        final TextInputLayout reviewTextWrapper = (TextInputLayout) registerCompleteDialog.findViewById(R.id.reviewTextWrapper);
        final EditText edtReviewText = reviewTextWrapper.getEditText();
        reviewTextWrapper.setHintAnimationEnabled(false);
        assert edtReviewText != null;
        edtReviewText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    edtReviewText.setText("");
                    edtReviewText.setTextColor(Color.parseColor("#333333"));
                } else {
                    edtReviewText.setText(R.string.adding_review_hint);
                    edtReviewText.setTextColor(Color.parseColor("#aaaaaa"));
                }
            }
        });
        txtReviewTitle.setText(getResources().getString(R.string.review_venue_title).replace("{{venueName}}", venue.name));
        final TextView txtCostReviewIndicator = (TextView) registerCompleteDialog.findViewById(R.id.txtCostIndicator);
        final RatingBar costRatingBar = (RatingBar) registerCompleteDialog.findViewById(R.id.costRating);
        final RatingBar qualityRatingBar = (RatingBar) registerCompleteDialog.findViewById(R.id.scoreRating);
        final RatingBar decorRatingBar = (RatingBar) registerCompleteDialog.findViewById(R.id.decorRating);
        costRatingBar.setOnRatingBarChangeListener(new CostRatingOnRatingBarChangeListener(txtCostReviewIndicator));
        qualityRatingBar.setOnRatingBarChangeListener(new GenericOnRatingBarChangeListener());
        decorRatingBar.setOnRatingBarChangeListener(new GenericOnRatingBarChangeListener());
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reviewText = edtReviewText.getText().toString().trim();
                boolean isEditTextEmpty = reviewText.equals(getResources().getString(R.string.adding_review_hint)) || (reviewText.length() == 0);
                if (isEditTextEmpty) {
                    reviewTextWrapper.setError(getResources().getString(R.string.review_field_required));
                } else if (!UiUtils.isTextInPersian(reviewText)) {
                    reviewTextWrapper.setError(getResources().getString(R.string.error_enter_review_in_persian));

                } else if (reviewText.length() < 35) {
                    reviewTextWrapper.setError(getString(R.string.review_under_limit));
                } else {
                    reviewTextWrapper.setErrorEnabled(false);
                    final ProgressDialog dialog = ProgressDialog.show(AppController.currentActivity, null, getString(R.string.just_a_while), true, true);
//                    publish review
                    JsonObjectRequest request = WebServer.uploadVenueReview(venue.slug, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dialog.dismiss();
                            registerCompleteDialog.dismiss();
                            Snackbar.make(findViewById(R.id.venueActivityRootLayout), R.string.review_added_successfully, Snackbar.LENGTH_SHORT).show();
//                            AlertDialog dialog=new AlertDialog();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismiss();
                            Snackbar.make(findViewById(R.id.venueActivityRootLayout), R.string.error_connecting_to_server, Snackbar.LENGTH_SHORT).setAction(R.string.try_again, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    btnPublish.performClick();
                                }
                            }).show();
                        }
                    }, reviewText, qualityRatingBar.getRating(), costRatingBar.getRating(), decorRatingBar.getRating());
                    AppController.getInstance().addToRequestQueue(request);
                }
            }
//                registerCompleteDialog.dismiss();
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGISTER) {
        }
    }
}
