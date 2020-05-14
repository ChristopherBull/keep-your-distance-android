package caffeinatedandroid.keepyourdistance.ui.settings

import android.os.Bundle
import android.view.Menu
import androidx.preference.PreferenceFragmentCompat

/**
 * Abstract [PreferenceFragmentCompat] which removes the Options [Menu].
 */
abstract class NoOptionsMenuPreferenceFragment : PreferenceFragmentCompat() {

    /**
     * Called when creating the [PreferenceFragmentCompat].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide Options Menu - used in combination with `menu.clear()` in `onPrepareOptionsMenu`
        setHasOptionsMenu(true)
    }

    /**
     * Hide the Options [Menu] whilst viewing the Settings Fragment.
     */
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // Hide Options Menu - used in combination with `setHasOptionsMenu(true)` in `onCreate`
        menu.clear()
    }
}
