/**
 *
 */
package com.imherolddev.dailylogkeeper.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import com.imherolddev.dailylogkeeper.R;
import com.imherolddev.dailylogkeeper.persistance.PersistenceHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author imherolddev
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    EditTextPreference email;
    EditTextPreference username;
    ListPreference dateFormat;

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

        email = (EditTextPreference) findPreference(PersistenceHelper.KEY_EMAIL);
        username = (EditTextPreference) findPreference(PersistenceHelper.KEY_USERNAME);
        dateFormat = (ListPreference) findPreference(PersistenceHelper.KEY_DATE_FORMAT);

        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        if (savedInstanceState != null) {

            email.setSummary(savedInstanceState.getString(PersistenceHelper.KEY_EMAIL, ""));
            username.setSummary(savedInstanceState.getString(PersistenceHelper.KEY_USERNAME, ""));
            dateFormat.setSummary(
                    new SimpleDateFormat(
                            savedInstanceState.getString(PersistenceHelper.KEY_DATE_FORMAT,
                                    "MMM d, yyyy")).format(new Date())
            );

        } else {

            email.setSummary(sharedPreferences.getString(PersistenceHelper.KEY_EMAIL, ""));
            username.setSummary(sharedPreferences.getString(PersistenceHelper.KEY_USERNAME, ""));
            dateFormat.setSummary(
                    new SimpleDateFormat(
                            sharedPreferences.getString(PersistenceHelper.KEY_DATE_FORMAT,
                                    "MMM d, yyyy")).format(new Date()));

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

        EditTextPreference email = (EditTextPreference) findPreference(PersistenceHelper.KEY_EMAIL);
        EditTextPreference username = (EditTextPreference) findPreference(PersistenceHelper.KEY_USERNAME);

        save.putString(PersistenceHelper.KEY_EMAIL, email.getText());
        save.putString(PersistenceHelper.KEY_USERNAME, username.getText());

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
        } else if (preference instanceof ListPreference) {
            ListPreference pref = (ListPreference) preference;
            preference.setSummary(pref.getEntry()
            );
        }

    }
}
