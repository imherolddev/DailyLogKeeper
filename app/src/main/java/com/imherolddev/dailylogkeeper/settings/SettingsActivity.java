/**
 *
 */
package com.imherolddev.dailylogkeeper.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author imherolddev
 */
public class SettingsActivity extends PreferenceActivity {

    /**
     * No arg constructor
     */
    public SettingsActivity() {
        // Empty
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();

    }

}
