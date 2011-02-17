/*
 * © Copyright 2011 Thibault Jouannic <thibault@jouannic.fr>. All Rights Reserved.
 *  This file is part of OpenQOTD.
 *
 *  OpenQOTD is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  OpenQOTD is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenQOTD. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tj.qotd;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class QOTDPreferences extends PreferenceActivity {

    private static final String PREF_LANG_KEY = "quote_lang";
    private static final String PREF_FREQ_KEY = "quote_update_frequency";

    // Send this result when preferences were updated
    public static final int RESULT_UPDATED = RESULT_OK + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Create layout
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Declare preference change listener
        OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setResult(RESULT_UPDATED);
                return true;
            }
        };

        // Update quote when lang change
        Preference pref = findPreference(PREF_LANG_KEY);
        pref.setOnPreferenceChangeListener(listener);

        pref = findPreference(PREF_FREQ_KEY);
        pref.setOnPreferenceChangeListener(listener);
    }
}
