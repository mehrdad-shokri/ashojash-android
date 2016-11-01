package com.ashojash.android.event;

import com.ashojash.android.model.Tag;
import java.util.List;

public final class TagApiEvents {
  private TagApiEvents() {
  }

  public static class OnTagsSuggestionsReady {
    public List<Tag> tags;

    public OnTagsSuggestionsReady(List<Tag> tags) {
      this.tags = tags;
    }
  }
}
