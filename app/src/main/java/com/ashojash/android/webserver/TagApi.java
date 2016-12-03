package com.ashojash.android.webserver;

import com.ashojash.android.event.TagApiEvents;
import com.ashojash.android.model.Tag;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.POST;

public final class TagApi extends BaseApi {
  private final static Endpoints API;
  private static Call<List<Tag>> indexCall;

  private TagApi() {
  }

  static {
    API = RETROFIT.create(Endpoints.class);
  }

  public static void suggestions() {
    indexCall = API.suggestions();
    indexCall.enqueue(new ApiCallback<List<Tag>>() {
      @Override public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
        if (call.isCanceled()) return;
        handleResponse(response, new TagApiEvents.OnTagsSuggestionsReady(response.body()));
      }
    });
  }

  public static void canelIndex() {
    if (indexCall != null) indexCall.cancel();
  }

  private interface Endpoints {
    @POST("tag/suggestions") Call<List<Tag>> suggestions();
  }
}
