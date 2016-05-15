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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.ashojash.android.R;
import com.ashojash.android.customview.CostRatingOnRatingBarChangeListener;
import com.ashojash.android.customview.GenericOnRatingBarChangeListener;
import com.ashojash.android.customview.VenueScoreIndicator;
import com.ashojash.android.event.ErrorEvents;
import com.ashojash.android.event.OnApiRequestErrorEvent;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueApiEvents;
import com.ashojash.android.fragment.*;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Venue;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.ui.UiUtils;
import com.ashojash.android.utils.AuthUtils;
import com.ashojash.android.utils.BusProvider;
import com.ashojash.android.webserver.UserApi;
import com.ashojash.android.webserver.VenueApi;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import org.greenrobot.eventbus.Subscribe;

/*
* IN progress
* */
public class VenueActivity extends BaseActivity {
    private ImageView imgHeroCollapsingToolbarLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton addPhotoFab;
    private FloatingActionButton addReviewFab;
    private VenueScoreIndicator venueScoreIndicator;
    private VenueBasicReviewFragment venueBasicReviewFragment;
    private VenueBasicInfoFragment venueBasicInfoFragment;
    private VenueBasicMenuFragment venueBasicMenuFragment;
    private VenueBasicPhotosFragment venueBasicPhotosFragment;
    private final int REQUEST_REGISTER = 8000;
    private Tracker mTracker;
    private String slug;
    private Venue venue;
    private Button btnPublishReview;
    private ProgressDialog progressDialog;
    private AlertDialog registerCompleteDialog;
    private AshojashSnackbar.AshojashSnackbarBuilder builder;

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Venue activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private static final String TAG = "VenueActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slug = getIntent().getStringExtra("slug");
        Log.d(TAG, "onCreate: slug: " + slug);
        setContentView(R.layout.activity_venue);
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        setupViews();
        venue = AppController.gson.fromJson(getIntent().getStringExtra("venue"), Venue.class);
        collapsingToolbarLayout.setTitle(venue.name);
        Glide.with(this).load(venue.photo.url).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imgHeroCollapsingToolbarLayout);
        imgHeroCollapsingToolbarLayout.setColorFilter(Color.rgb(130, 130, 130), android.graphics.PorterDuff.Mode.MULTIPLY);
        venueBasicInfoFragment = new VenueBasicInfoFragment();
        venueBasicReviewFragment = new VenueBasicReviewFragment();
        venueBasicMenuFragment = new VenueBasicMenuFragment();
        venueBasicPhotosFragment = new VenueBasicPhotosFragment();
        addFragment(R.id.venueActivityBasicInfoContainer, venueBasicInfoFragment);
        addFragment(R.id.venueActivityReviewsContainer, venueBasicReviewFragment);
        addFragment(R.id.venueActivityMenusContainer, venueBasicMenuFragment);
        addFragment(R.id.venueActivityPicsContainer, venueBasicPhotosFragment);
        VenueApi.index(slug);
    }

    @Subscribe
    public void onEvent(VenueApiEvents.OnVenueIndexResultsReady event) {
        Venue venue = event.venue;

        venueScoreIndicator.setScore(venue.score, venue.reviewsCount);
//                    JSONObject data = response.getJSONObject("data");
//                    StructVenue structVenue = JsonParser.parseVenueJsonObject(data.getJSONObject("venue"));
//                    int reviewsCount = data.getInt("reviews_count");
//                    int menusCount = data.getInt("menus_count");
//                    JSONArray menus = data.getJSONArray("menus");
//                    JSONArray reviews = data.getJSONArray("reviews");
//                    JSONArray photos = data.getJSONArray("photos");
//                    venueBasicReviewFragment.onDataReceived(reviews, reviewsCount);
//                    venueBasicMenuFragment.onDataReceived(menus, menusCount);
//                    venueBasicPhotosFragment.onDataReceived(photos);
    }


    @Subscribe
    public void onEvent(final OnApiResponseErrorEvent event) {
        builder.message(R.string.error_retrieving_data).duration(Snackbar.LENGTH_INDEFINITE).build().setAction(R.string.try_again, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VenueApi.index(slug);
            }
        }).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }


    private void setupViews() {
        builder = new AshojashSnackbar.AshojashSnackbarBuilder(this);
        venueScoreIndicator = (VenueScoreIndicator) findViewById(R.id.venueScoreIndicator);
        imgHeroCollapsingToolbarLayout = (ImageView) findViewById(R.id.backdrop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addReviewFab = (FloatingActionButton) findViewById(R.id.fragmentCollapsingToolbarRateReviewFab);
        addPhotoFab = (FloatingActionButton) findViewById(R.id.fragmentCollapsingToolbarAddPhotoFab);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        addReviewFab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_rate_review).actionBar().color(Color.WHITE));
        addPhotoFab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add_a_photo).actionBar().color(Color.WHITE));
        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
        params.height = params.height + UiUtils.getStatusBarHeight(this);
        toolbar.setLayoutParams(params);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
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
        txtVenueTitle.setText(getString(R.string.add_venue_photo_title).replace("{{venueName}}", venueOrm.name));

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


    @Subscribe
    public void onEvent(VenueApiEvents.OnReviewAdded event) {
        if (progressDialog != null) progressDialog.dismiss();
        registerCompleteDialog.dismiss();
        builder.message(R.string.review_added_successfully).duration(Snackbar.LENGTH_LONG).build().show();

    }

    @Subscribe
    public void onEvent(ErrorEvents.OnReviewAddFailed event) {
        if (progressDialog != null) progressDialog.dismiss();
        String message = event.error.message;
        builder.message(message).duration(Snackbar.LENGTH_LONG).build().show();

    }

    @Subscribe
    public void onEvent(OnApiRequestErrorEvent event) {
        if (progressDialog != null) progressDialog.dismiss();
        builder.message(R.string.error_connecting_to_server).duration(Snackbar.LENGTH_LONG).build().show();

    }

    private void setupAddReview() {
        final AlertDialog.Builder dialogBuilder =
                new AlertDialog.Builder(AppController.currentActivity, R.style.AppCompatAlertDialogStyle);
        dialogBuilder.setView(R.layout.dialog_add_review);
        dialogBuilder.setInverseBackgroundForced(true);
        registerCompleteDialog = dialogBuilder.show();
        btnPublishReview = (Button) registerCompleteDialog.findViewById(R.id.btnAddReview);
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
        btnPublishReview.setOnClickListener(new View.OnClickListener() {
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
                    progressDialog = ProgressDialog.show(AppController.currentActivity, null, getString(R.string.just_a_while), true, false);
                    UserApi.addReview(slug, (int) qualityRatingBar.getRating(), (int) costRatingBar.getRating(), (int) decorRatingBar.getRating(), reviewText);
                }
            }
//                registerCompleteDialog.dismiss();
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGISTER) {
        }
    }
}
