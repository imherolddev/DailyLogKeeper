/**
 *
 */
package com.imherolddev.dailylogkeeper.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.imherolddev.dailylogkeeper.R;

/**
 * @author imherolddev
 */
public class SettingsFragment extends PreferenceFragment {

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

    }

}
