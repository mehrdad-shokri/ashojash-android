package com.ashojash.android.utils;

import android.content.Context;

import java.io.FileOutputStream;

public class Util {
    public static void writeConfiguration(Context context) {
        try (FileOutputStream openFileOutput = context.openFileOutput("config.txt", Context.MODE_PRIVATE)) {
            openFileOutput.write("This is a test1.".getBytes());
            openFileOutput.write("This is a test2.".getBytes());
        } catch (Exception e) {
            // not handled
        }
    }
}
