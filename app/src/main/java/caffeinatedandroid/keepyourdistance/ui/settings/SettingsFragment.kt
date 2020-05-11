package caffeinatedandroid.keepyourdistance.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import caffeinatedandroid.keepyourdistance.R

/**
 * The root Settings [PreferenceFragmentCompat].
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    /**
     * Register preference change listener once [View] has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add preference change listener once `View` loaded
        PreferenceManager.getDefaultSharedPreferences(view.context.applicationContext)
            .registerOnSharedPreferenceChangeListener(this)
    }

    /**
     * [PreferenceFragmentCompat]'s `onResume()`. Enable Preference change listener.
     */
    override fun onResume() {
        super.onResume()

        // Enable preference change listener
        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)
    }

    /**
     * [PreferenceFragmentCompat]'s `onPause()`. Disable Preference change listener.
     */
    override fun onPause() {
        super.onPause()

        // Disable preference change listener
        PreferenceManager.getDefaultSharedPreferences(context)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * Actions to take in near realtime after a user has updated their preferences
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.pref_key_detection_enabled) -> {
                sharedPreferences?.let {
                    val isEnabled = it.getBoolean(key, false)
                    if (isEnabled) {
                        // TODO enable detection (start service)
                        // TODO ensure auto start/stop conditions also met before starting
                    } else {
                        // TODO disable detection (stop service)
                    }
                }
            }
            getString(R.string.pref_key_theme) -> {
                activity?.let {
                    (activity as MainActivity).loadTheme()
                }
            }
        }
    }
}
