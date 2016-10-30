package com.ashojash.android.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.ashojash.android.R;
import com.ashojash.android.event.OnApiResponseErrorEvent;
import com.ashojash.android.event.VenueCollectionEvents;
import com.ashojash.android.fragment.CollectionHeroFragment;
import com.ashojash.android.fragment.CollectionVerticalFragment;
import com.ashojash.android.fragment.CollectionsSlideShowFragment;
import com.ashojash.android.fragment.NavigationBarCoverFragment;
import com.ashojash.android.fragment.VenueHeroBigFragment;
import com.ashojash.android.fragment.VenueHeroNormalFragment;
import com.ashojash.android.fragment.VenueSlideShowFragment;
import com.ashojash.android.fragment.VenueVerticalFragment;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.VenueCollection;
import com.ashojash.android.ui.AshojashSnackbar;
import com.ashojash.android.util.BusUtil;
import com.ashojash.android.util.UiUtil;
import com.ashojash.android.webserver.VenueCollectionApi;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends BottomToolbarActivity {
  private static String CITY_SLUG = AppController.citySlug;
  private ArrayList<VenueCollection> collectionsSlideShow, collectionsVerticalNormal,
      collectionsHero;
  //    private ArrayList<Venue> venueSlideShow, venueHeroBig, venueHeroNormal;
  private ArrayList<VenueCollection> venueSlideShow, venueHeroBig, venueHeroNormal,
      venueVerticalNormal;
  private ViewGroup contentContainer;
  private Boolean isFirstGroupAdded = false;
  private AshojashSnackbar.AshojashSnackbarBuilder builder;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    contentContainer = (ViewGroup) findViewById(R.id.contentContainer);
    attachShy(this, savedInstanceState, (CoordinatorLayout) findViewById(R.id.rootView),
        findViewById(R.id.nestedScrollView));
    setupBottomNavBar();
    builder = new AshojashSnackbar.AshojashSnackbarBuilder(findViewById(R.id.rootView));
    VenueCollectionApi.collections(CITY_SLUG);
    //new AshojashSnackbar.AshojashSnackbarBuilder(this).message("Test snackbar").duration(Snackbar.LENGTH_INDEFINITE).build().show();
    //Snackbar.make(contentContainer, "test snackbar", Snackbar.LENGTH_INDEFINITE).show();
  }

  @Subscribe public void onEvent(VenueCollectionEvents.OnVenueCollectionsResponse response) {
    List<VenueCollection> collections = response.venueCollections;
    handleCollection(collections);
    addViewsToActivity();
    addBottomMarginToCollections();
    showMainProgressBar(false);
  }

  @Subscribe public void onEvent(OnApiResponseErrorEvent event) {
    builder.duration(Snackbar.LENGTH_INDEFINITE).message(R.string.error_retrieving_data);
    showMainProgressBar(false);
    builder.build().setAction(R.string.try_again, new View.OnClickListener() {
      @Override public void onClick(View view) {
        VenueCollectionApi.collections(CITY_SLUG);
        showMainProgressBar(true);
      }
    }).show();
  }

  private void addViewsToActivity() {
    final Gson gson = new Gson();

    if (venueHeroBig.size() > 0) {
      VenueHeroBigFragment fragment = new VenueHeroBigFragment();
      Bundle bundle = new Bundle();
      bundle.putString("collection", gson.toJson(venueHeroBig.get(0)));
      fragment.setArguments(bundle);
      FrameLayout view = new FrameLayout(this);
      view.setId(R.id.venueHeroBig);
      FrameLayout.LayoutParams params =
          new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
              FrameLayout.LayoutParams.WRAP_CONTENT);
      params.gravity = Gravity.TOP;
      handlePadding(view);
      //handleMargins(params);
      addFragment(R.id.venueHeroBig, fragment);
      contentContainer.addView(view, params);
    }

    if (venueHeroNormal.size() > 0) {
      VenueHeroNormalFragment fragment = new VenueHeroNormalFragment();
      Bundle bundle = new Bundle();
      bundle.putString("collection", gson.toJson(venueHeroNormal.get(0)));
      fragment.setArguments(bundle);
      FrameLayout view = new FrameLayout(this);
      view.setId(R.id.venueHeroNormal);
      FrameLayout.LayoutParams params =
          new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
              FrameLayout.LayoutParams.WRAP_CONTENT);
      handlePadding(view);
      //handleMargins(params);
      addFragment(R.id.venueHeroNormal, fragment);
      contentContainer.addView(view, params);
    }
    if (collectionsHero.size() > 0) {
      CollectionHeroFragment fragment = new CollectionHeroFragment();
      Bundle bundle = new Bundle();
      bundle.putString("collection", gson.toJson(collectionsHero.get(0)));
      fragment.setArguments(bundle);
      FrameLayout view = new FrameLayout(this);
      view.setId(R.id.collectionHero);
      FrameLayout.LayoutParams params =
          new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
              FrameLayout.LayoutParams.WRAP_CONTENT);
      handlePadding(view);
      //handleMargins(params);
      addFragment(R.id.collectionHero, fragment);
      contentContainer.addView(view, params);
    }
    if (venueSlideShow.size() > 0) {
      VenueSlideShowFragment fragment = new VenueSlideShowFragment();
      Bundle bundle = new Bundle();
      bundle.putString("collection", gson.toJson(venueSlideShow.get(0)));
      fragment.setArguments(bundle);
      FrameLayout view = new FrameLayout(this);
      view.setId(R.id.venuesSlideShow);
      addFragment(R.id.venuesSlideShow, fragment);
      FrameLayout.LayoutParams params =
          new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
              FrameLayout.LayoutParams.WRAP_CONTENT);
      handlePadding(view);
      //handleMargins(params);
      contentContainer.addView(view, params);
    }

    if (collectionsSlideShow.size() > 0) {
      CollectionsSlideShowFragment fragment = new CollectionsSlideShowFragment();
      Bundle bundle = new Bundle();
      bundle.putString("collections", gson.toJson(collectionsSlideShow));
      fragment.setArguments(bundle);
      FrameLayout view = new FrameLayout(this);
      addFragment(R.id.collectionsSlideshow, fragment);
      FrameLayout.LayoutParams params =
          new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
              FrameLayout.LayoutParams.WRAP_CONTENT);
      view.setId(R.id.collectionsSlideshow);
      handlePadding(view);
      //handleMargins(params);
      contentContainer.addView(view, params);
    }
    if (venueVerticalNormal.size() > 0) {
      VenueVerticalFragment fragment = new VenueVerticalFragment();
      Bundle bundle = new Bundle();
      bundle.putString("collection", gson.toJson(venueVerticalNormal.get(0)));
      fragment.setArguments(bundle);
      FrameLayout view = new FrameLayout(this);
      view.setId(R.id.venuesVerticalNormal);
      addFragment(R.id.venuesVerticalNormal, fragment);
      FrameLayout.LayoutParams params =
          new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
              FrameLayout.LayoutParams.WRAP_CONTENT);
      handlePadding(view);
      //handleMargins(params);
      contentContainer.addView(view, params);
    }
    if (collectionsVerticalNormal.size() > 0) {
      CollectionVerticalFragment fragment = new CollectionVerticalFragment();
      Bundle bundle = new Bundle();
      bundle.putString("collections", gson.toJson(collectionsVerticalNormal));
      fragment.setArguments(bundle);
      FrameLayout view = new FrameLayout(this);
      view.setId(R.id.collectionsVerticalNormal);
      FrameLayout.LayoutParams params =
          new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
              FrameLayout.LayoutParams.WRAP_CONTENT);
      handlePadding(view);
      //handleMargins(params);
      addFragment(R.id.collectionsVerticalNormal, fragment);
      contentContainer.addView(view, params);
    }
  }

  private void handlePadding(FrameLayout view) {
    if (isFirstGroupAdded) {
      view.setPadding(0, (int) UiUtil.dp2px(20), 0, 0);
    } else {
      isFirstGroupAdded = true;
    }
  }
  /*

  private void handleMargins(FrameLayout.LayoutParams view) {
    String TAG = "Ashojash";
    if (isFirstGroupAdded) {
      view.setMargins(0, (int) UiUtil.dp2px(15), 0, 0);
    } else {
      isFirstGroupAdded = true;
    }
  }
  */

  private void handleCollection(List<VenueCollection> collections) {
    venueSlideShow = new ArrayList<>();
    venueHeroBig = new ArrayList<>();
    venueHeroNormal = new ArrayList<>();
    venueVerticalNormal = new ArrayList<>();
    collectionsSlideShow = new ArrayList<>();
    collectionsVerticalNormal = new ArrayList<>();
    collectionsHero = new ArrayList<>();

    for (VenueCollection collection : collections) {
      switch (collection.type) {
        case 1:
          collectionsSlideShow.add(collection);
          break;
        case 2:
          collectionsVerticalNormal.add(collection);
          break;
        case 3:
          collectionsHero.add(collection);
          break;
        case 4:
          venueSlideShow.add(collection);
          break;
        case 5:
          venueHeroBig.add(collection);
          break;
        case 6:
          venueHeroNormal.add(collection);
          break;
                /*case 10:
                    venueHorizontalThumbnail.addAll(collection.venues);
                    break;*/
                /*case 11:
                    VenueVerticalThumbnail.addAll(collection.venues);
                    break;*/
        case 7:
          venueVerticalNormal.add(collection);
          break;
               /* case 13:
                    venueVerticalGridView.addAll(collection.venues);
                    break;*/
               /* case 14:
                    venueHorizontalNormal.addAll(collection.venues);
                    break;*/
      }
    }
  }

  private void setupBottomNavBar() {
    final int bottomNavigationBarHeight = UiUtil.getNavBarHeight();
    final int statusBarHeight = UiUtil.getStatusBarHeight();
    boolean hasMenuKey = ViewConfiguration.get(AppController.context).hasPermanentMenuKey();
    boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    View toolbarTop = findViewById(R.id.toolbarTop);
    CoordinatorLayout.LayoutParams toolbarTopLayoutParam =
        (CoordinatorLayout.LayoutParams) toolbarTop.getLayoutParams();
    toolbarTopLayoutParam.setMargins(0, statusBarHeight, 0, 0);
    toolbarTop.setLayoutParams(toolbarTopLayoutParam);

    View toolbarTopContainer = findViewById(R.id.toolbarTopContainer);
    LinearLayout.LayoutParams toolbarTopContainerLayoutParam =
        (LinearLayout.LayoutParams) toolbarTopContainer.getLayoutParams();
    toolbarTopContainerLayoutParam.setMargins(0, statusBarHeight, 0, 0);
    toolbarTopContainer.setLayoutParams(toolbarTopContainerLayoutParam);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && (!hasMenuKey && !hasBackKey)) {
      {
        final ViewGroup bottomBarOverload = (ViewGroup) findViewById(R.id.navigationBarBehind);
        bottomBarOverload.getLayoutParams().height = bottomNavigationBarHeight;
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        mBottomBar.setPadding(0, 0, 0, bottomNavigationBarHeight);
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        if (nestedScrollView != null) {
          nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

            boolean isFirstScroll = true;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX,
                int oldScrollY) {
              if (scrollY > oldScrollY) {
                bottomBarOverload.setVisibility(View.GONE);
                mBottomBar.setPadding(0, 0, 0, 0);
                if (isFirstScroll) {
                  isFirstScroll = false;
                  mBottomBar.setVisibility(View.GONE);
                }
              } else {
                bottomBarOverload.setVisibility(View.VISIBLE);
                mBottomBar.setVisibility(View.VISIBLE);
                mBottomBar.setPadding(0, 0, 0, bottomNavigationBarHeight);
              }
            }
          });
        }
      }
    }
  }

  public void addBottomMarginToCollections() {
    final int bottomNavigationBarHeight = UiUtil.getNavBarHeight();
    final boolean hasMenuKey = ViewConfiguration.get(AppController.context).hasPermanentMenuKey();
    final boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && (!hasMenuKey && !hasBackKey)) {
      NavigationBarCoverFragment fragment = new NavigationBarCoverFragment();
      FrameLayout view = new FrameLayout(this);
      view.setId(R.id.navigationBarCover);
      FrameLayout.LayoutParams params =
          new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
              bottomNavigationBarHeight);
      addFragment(R.id.navigationBarCover, fragment);
      contentContainer.addView(view, params);
    }
  }

  @Override protected void onStart() {
    super.onStart();
    BusUtil.getInstance().register(this);
  }

  @Override protected void onStop() {
    super.onStop();
    BusUtil.getInstance().unregister(this);
  }

  private void showMainProgressBar(boolean show) {
    findViewById(R.id.progressbar).setVisibility(show ? View.VISIBLE : View.GONE);
  }
}