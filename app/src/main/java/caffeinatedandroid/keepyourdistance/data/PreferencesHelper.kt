package caffeinatedandroid.keepyourdistance.data

import android.content.Context
import androidx.preference.PreferenceManager
import caffeinatedandroid.keepyourdistance.R

object PreferencesHelper {

    /**
     * Retrieves user preference for whether detection is enabled.
     * @param context Required to interact with the SharedPreferences
     * @return whether detection is enabled (`true`), or not (`false`). Defaults to `false`.
     */
    fun isDetectionEnabled(context: Context): Boolean {
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext).apply {
            return getBoolean(context.getString(R.string.pref_key_detection_enabled), false)
        }
    }
}