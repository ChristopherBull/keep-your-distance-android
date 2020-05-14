package caffeinatedandroid.keepyourdistance.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import caffeinatedandroid.keepyourdistance.R

/**
 * Advanced Settings [PreferenceFragmentCompat].
 */
class AdvancedSettingsFragment : NoOptionsMenuPreferenceFragment() {

    /**
     * Called when preparing the `Preferences` to be displayed.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.advanced_preferences, rootKey)
    }

}
