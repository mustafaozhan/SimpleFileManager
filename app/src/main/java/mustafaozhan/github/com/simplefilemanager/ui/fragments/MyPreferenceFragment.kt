package mustafaozhan.github.com.simplefilemanager.ui.fragments

import android.R.xml
import android.os.Bundle
import android.preference.PreferenceFragment
import mustafaozhan.github.com.simplefilemanager.R
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.EditTextPreference
import android.view.animation.AnimationUtils


/**
Created by Mustafa Özhan on 7/18/17 at 8:50 PM on Linux.

 */
class MyPreferenceFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.fragment_preference)


        val defaultFolder = findPreference("defaultFolder") as EditTextPreference



        defaultFolder.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            val inputName = newValue as String

            defaultFolder.summary = inputName

            true
        }


    }

}