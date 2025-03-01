package com.akylas.volumescroll;

import android.annotation.SuppressLint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressLint("PrivateApi")
public class SystemProperties {
    private SystemProperties() {}

    private static Class<?> PROPS = null;

    static {
        try {
            PROPS = Class.forName("android.os.SystemProperties");
        } catch (Throwable ignored) {}
    }

    public static String get(String key) {
        try {
            Method m = PROPS.getDeclaredMethod("get", String.class);
            return (String) m.invoke(null, key);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    public static String set(String key, String value) {
        try {
            Method m = PROPS.getDeclaredMethod("set", String.class, String.class);
            return (String) m.invoke(null, key, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
//        return (String) XposedHelpers.callStaticMethod(PROPS, "set", key, value);
    }

}