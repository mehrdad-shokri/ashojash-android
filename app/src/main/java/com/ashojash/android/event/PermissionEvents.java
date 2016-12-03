package com.ashojash.android.event;

import android.app.Activity;

public class PermissionEvents {
  private PermissionEvents() {
  }

  public static class OnPermissionGranted {
    public String permission;

    public OnPermissionGranted(String permission) {
      this.permission = permission;
    }
  }

  public static class OnPermissionDenied {
    public Activity activity;
    public String permission;

    public OnPermissionDenied(Activity activity, String permission) {
      this.activity = activity;
      this.permission = permission;
    }
  }
}
