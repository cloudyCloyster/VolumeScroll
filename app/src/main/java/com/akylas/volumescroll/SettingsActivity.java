package com.akylas.volumescroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {
    public static String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @SuppressLint("WorldReadableFiles")
        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.preferences);
            if (!ScrollAccessibilityService.isAccessibilityServiceEnabled(this.getActivity(), ScrollAccessibilityService.class)) {
                ScrollAccessibilityService.openAccessibilitySettings(this.getActivity());
            }

        }

    }
}
