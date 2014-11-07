/**
 *
 */
package com.imherolddev.dailylogkeeper.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import com.imherolddev.dailylogkeeper.R;

/**
 * @author imherolddev
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    EditTextPreference email;
    EditTextPreference username;

    SharedPreferences sharedPreferences;

    /**
     * No arg constructor
     */
    public SettingsFragment() {
        // Empty
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.fragment_settings);

        email = (EditTextPreference) findPreference("email");
        username = (EditTextPreference) findPreference("username");

        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        if (savedInstanceState != null) {

            email.setSummary(savedInstanceState.getString("email", ""));
            username.setSummary(savedInstanceState.getString("username", ""));

        } else {

            email.setSummary(sharedPreferences.getString("email", ""));
            username.setSummary(sharedPreferences.getString("username", ""));

        }

    }

    @Override
    public void onResume() {

        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {

        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle save) {

        EditTextPreference email = (EditTextPreference) findPreference("email");
        EditTextPreference username = (EditTextPreference) findPreference("username");

        save.putString("email", email.getText());
        save.putString("username", username.getText());

        super.onSaveInstanceState(save);

    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p/>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link android.content.SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference preference = findPreference(key);

        if (preference instanceof EditTextPreference) {
            EditTextPreference pref = (EditTextPreference) preference;
            preference.setSummary(pref.getText());
        }

    }
}
