package com.smartcar.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;

import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static final String PREFERENCE_NAME = "HabitKickPrefs";
    public static final boolean IS_EMULATOR;

    static {
        boolean res =//
                Build.FINGERPRINT.startsWith("generic")//
                        || Build.FINGERPRINT.startsWith("unknown")//
                        || Build.MODEL.contains("google_sdk")//
                        || Build.MODEL.contains("Emulator")//
                        || Build.MODEL.contains("Android SDK built for x86")
                        || Build.MANUFACTURER.contains("Genymotion");

        res |= Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic");
        res |= "google_sdk".equals(Build.PRODUCT);

        IS_EMULATOR = res;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static <T> void putStore(Context context, String key, T value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();

        if (value == null) {
            editor.putString(key, "");
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }

        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getStore(Context context, String key, T def) {

        SharedPreferences editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        if (def == null) {
            String str = editor.getString(key, "");
            return str == null ? null : (T) str;
        } else if (def instanceof Integer) {
            Integer str = editor.getInt(key, (Integer) def);
            return (T) (str);
        } else if (def instanceof String) {
            String str = editor.getString(key, (String) def);
            return str == null ? null : (T) (str);
        } else if (def instanceof Boolean) {
            Boolean str = editor.getBoolean(key, (Boolean) def);
            return (T) (str);
        } else if (def instanceof Long) {
            Long str = editor.getLong(key, (Long) def);
            return (T) (str);
        }

        return null;
    }

    @SuppressWarnings("unused")
    public static int shiftHue(int color, float shift) {

        float[] hsv = new float[3];

        Color.RGBToHSV((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, hsv);
        hsv[0] = (hsv[0] + shift) % 360f;

        return Color.HSVToColor((color >> 24) & 0xFF, hsv);
    }

    @SuppressWarnings("unused")
    public static int contrast(int color, double contrast) {

        contrast = Math.pow((100 + contrast) / 100, 2);

        int A = (color >> 24) & 0xFF;
        int R = (color >> 16) & 0xFF;
        int G = (color >> 8) & 0xFF;
        int B = color & 0xFF;

        R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
        if (R < 0) {
            R = 0;
        } else if (R > 255) {
            R = 255;
        }

        G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
        if (G < 0) {
            G = 0;
        } else if (G > 255) {
            G = 255;
        }

        B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
        if (B < 0) {
            B = 0;
        } else if (B > 255) {
            B = 255;
        }

        return Color.argb(A, R, G, B);
    }

    public static Object setTimeout(Runnable runnable, long delay) {
        return new TimeoutEvent(runnable, delay);
    }

    public static void clearTimout(Object timeoutEvent) {
        if (timeoutEvent != null && timeoutEvent instanceof TimeoutEvent) {
            ((TimeoutEvent) timeoutEvent).cancelTimeout();
        }
    }

    private static class TimeoutEvent {
        private static Handler handler = new Handler();
        private volatile Runnable runnable;

        private TimeoutEvent(Runnable task, long delay) {
            runnable = task;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }, delay);
        }

        private void cancelTimeout() {
            runnable = null;
        }
    }

}
