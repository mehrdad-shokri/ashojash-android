package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.ashojash.android.R;

public class SearchBarFragment extends Fragment {
  private EditText edtTermSearch;
  private EditText edtLocationSearch;
  private OnTermChanged onTermChanged;

  public interface OnTermChanged {
    void onTermChanged(String term);

    void onTermFocusChanged(boolean hasFocus);

    void onLocationChanged(String term);

    void onLocationFocusChanged(boolean hasFocus);

    void onTermSubmitted(String term);

    void onLocationSubmitted(String term);
  }

  public void setOnTermChanged(OnTermChanged onTermChanged) {
    this.onTermChanged = onTermChanged;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_searchbar, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setupViews();
  }

  private void setupViews() {
    edtTermSearch = (EditText) getView().findViewById(R.id.edtTermSearch);
    edtLocationSearch = (EditText) getView().findViewById(R.id.edtLocationSearch);
    edtTermSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
          onTermChanged.onTermFocusChanged(hasFocus);
        }
      }
    });
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
    edtTermSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          String term = v.getText().toString();
          if (onTermChanged != null && !term.isEmpty()) onTermChanged.onTermSubmitted(term);
          return true;
        }
        return false;
      }
    });

    edtLocationSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
          onTermChanged.onLocationFocusChanged(hasFocus);
        }
      }
    });
    edtLocationSearch.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override public void afterTextChanged(Editable s) {
        String term = s.toString();
        if (onTermChanged != null) {
          onTermChanged.onLocationChanged(term);
        }
      }
    });
    edtLocationSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          String term = v.getText().toString();
          if (onTermChanged != null && !term.isEmpty()) onTermChanged.onLocationSubmitted(term);
          return true;
        }
        return false;
      }
    });
  }
}
