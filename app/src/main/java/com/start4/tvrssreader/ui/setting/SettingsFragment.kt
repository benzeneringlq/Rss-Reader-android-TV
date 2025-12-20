package com.start4.tvrssreader.ui.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.start4.tvrssreader.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}