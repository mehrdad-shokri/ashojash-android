package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ashojash.android.R;
import com.ashojash.android.adapter.TagSuggestionAdapter;
import com.ashojash.android.event.TagApiEvents;
import com.ashojash.android.helper.AppController;
import com.ashojash.android.model.Tag;
import com.ashojash.android.util.BusUtil;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;

public class TagsSuggestionFragment extends Fragment {
  private RecyclerView recyclerView;
  private List<Tag> tags;
  private TagSuggestionAdapter adapter;

  public TagsSuggestionFragment() {
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_tag_suggestion, container, false);
    return rootView;
  }

  @Override public void onResume() {
    super.onResume();
    setupViews();
  }

  @Override public void onStart() {
    super.onStart();
    BusUtil.getInstance().register(this);
  }

  @Override public void onStop() {
    super.onStop();
    BusUtil.getInstance().unregister(this);
  }

  @Subscribe public void onEvent(TagApiEvents.OnTagsSuggestionsReady e) {
    tags = e.tags;
    ((AVLoadingIndicatorView) getView().findViewById(R.id.progressbar)).smoothToHide();
    adapter = new TagSuggestionAdapter(tags);
    recyclerView.setAdapter(adapter);
    recyclerView.setVisibility(View.VISIBLE);
    recyclerView.setNestedScrollingEnabled(false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  private void setupViews() {
    recyclerView = (RecyclerView) getView().findViewById(R.id.tagSuggestionRecyclerView);
    RecyclerView.LayoutManager layoutManager =
        new LinearLayoutManager(AppController.context, LinearLayoutManager.HORIZONTAL, false);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(true);
  }
}
