package com.ashojash.android.util;

import android.content.Context;
import android.content.pm.PackageManager;
import com.ashojash.android.helper.AppController;

public class PermissionUtil {
    private static final Context CONTEXT = AppController.context;

    public static boolean hasPermission(String permission) {
        int res = CONTEXT.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean verfiyPermissions(int[] grantResults) {
        if (grantResults.length == 0)
            return false;
        for (int result : grantResults)
            if (result != PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }

}
