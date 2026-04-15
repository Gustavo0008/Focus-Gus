package com.ciencias.focus_gus.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.ciencias.focus_gus.R;
public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Cargamos las preferencias desde el recurso XML
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}