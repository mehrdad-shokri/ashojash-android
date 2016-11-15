package com.ashojash.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashojash.android.R;
import com.ashojash.android.helper.AppController;
import com.mypopsy.drawable.SearchCrossDrawable;
import com.mypopsy.drawable.ToggleDrawable;

public class SearchBarFragment extends Fragment {
  private EditText edtTermSearch;
  private EditText edtLocationSearch;
  private OnTermChanged onTermChanged;

  public void setTermText(String query) {
    edtTermSearch.setText(query);
  }

  public interface OnTermChanged {
    void onTermChanged(String term);

    void onTermFocusChanged(boolean hasFocus);

    void onLocationTermChanged(String term);

    void onLocationFocusChanged(boolean hasFocus, EditText edittext);

    void onSubmit(String term, String location);
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
    final ToggleDrawable drawable = new SearchCrossDrawable(getActivity());
    ImageView imgSearchCross = (ImageView) getView().findViewById(R.id.imgSearchCross);
    imgSearchCross.setImageDrawable(drawable);
    imgSearchCross.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (!edtTermSearch.getText().toString().isEmpty()) {
          edtTermSearch.getText().clear();
        }
      }
    });
    edtTermSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View view, boolean hasFocus) {
        onTermChanged.onTermFocusChanged(hasFocus);
      }
    });
    edtTermSearch.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
      boolean isCross = false;

      @Override public void afterTextChanged(Editable s) {
        String term = s.toString();
        if (term.isEmpty()) {
          if (isCross) {
            Thread thread = new Thread(new Runnable() {
              @Override public void run() {
                for (float i = 1; i >= 0; i -= .01) {
                  try {
                    Thread.sleep(1);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  final float finalI = i;
                  AppController.HANDLER.post(new Runnable() {
                    @Override public void run() {
                      drawable.setProgress(finalI);
                    }
                  });
                }
                AppController.HANDLER.post(new Runnable() {
                  @Override public void run() {
                    drawable.setProgress(0);
                  }
                });
              }
            });
            thread.start();
            isCross = false;
          }
        } else {
          if(!isCross)
          {
            Thread thread = new Thread(new Runnable() {
              @Override public void run() {
                for (float i = 0; i <= 1; i += .01) {
                  try {
                    Thread.sleep(1);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  final float finalI = i;
                  AppController.HANDLER.post(new Runnable() {
                    @Override public void run() {
                      drawable.setProgress(finalI);
                    }
                  });
                }
                AppController.HANDLER.post(new Runnable() {
                  @Override public void run() {
                    drawable.setProgress(1);
                  }
                });
              }
            });
            thread.start();
            isCross = true;
          }
        }
        if (onTermChanged != null) {
          onTermChanged.onTermChanged(term);
        }
      }
    });
    edtTermSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          String term = edtTermSearch.getText().toString();
          String location = edtLocationSearch.getText().toString();
          if (onTermChanged != null) onTermChanged.onSubmit(term, location);
          return true;
        }
        return false;
      }
    });

    edtLocationSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View view, boolean hasFocus) {
        onTermChanged.onLocationFocusChanged(hasFocus, (EditText) view);
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
          onTermChanged.onLocationTermChanged(term);
        }
      }
    });
    edtLocationSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          String term = edtTermSearch.getText().toString();
          String location = edtLocationSearch.getText().toString();
          Log.d("SearchActivity", "onEditorAction: " + term + " " + location);
          if (onTermChanged != null) onTermChanged.onSubmit(term, location);
          return true;
        }
        return false;
      }
    });
  }
}