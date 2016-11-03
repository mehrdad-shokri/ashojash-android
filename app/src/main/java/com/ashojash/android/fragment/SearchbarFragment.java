package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.ashojash.android.R;

public class SearchBarFragment extends Fragment {
  private EditText edtTermSearch;
  private EditText edtLocationSearch;
  private OnTermChanged onTermChanged;

  public interface OnTermChanged {
    void onTermChanged(String term);
  }

  public void setOnTermChanged(OnTermChanged onTermChanged) {
    this.onTermChanged = onTermChanged;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_searchbar, container, false);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupViews();
  }

  private void setupViews() {
    edtTermSearch = (EditText) getView().findViewById(R.id.edtTermSearch);
    edtLocationSearch = (EditText) getView().findViewById(R.id.edtLocationSearch);
    edtTermSearch.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override public void afterTextChanged(Editable s) {
        String term = s.toString();
        if (onTermChanged != null) {
          onTermChanged.onTermChanged(term);
        }
      }
    });
  }
}
