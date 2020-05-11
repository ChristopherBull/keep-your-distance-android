package caffeinatedandroid.keepyourdistance

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import caffeinatedandroid.keepyourdistance.data.PreferencesHelper
import caffeinatedandroid.keepyourdistance.media.AppNotificationManager
import caffeinatedandroid.keepyourdistance.media.AudioManager
import caffeinatedandroid.keepyourdistance.service.ProximityDetection
import caffeinatedandroid.keepyourdistance.ui.main.MainFragment


class MainActivity : AppCompatActivity() {

    // TODO use `WorkManager` to schedule a ping to check health of service (i.e., check if the OS has killed it)
    // TODO if using `WorkManager` add/remove/start/stop it along with the service directly (avoid Service restarting after being stopped)

    // TODO start service at boot (with internal preference check that it should indeed be started).
    // TODO add a delay to starting service after device boot (60 seconds) - allow device to warm up faster; add advanced pref in user settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        // Load preferred theme
        loadTheme()

        // Set custom Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Start the detection service if user has enabled it
        if (PreferencesHelper.isDetectionEnabled(this)) {
            // TODO what happens if service already running?
            startProximityAlertService(null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * Handle [MenuItem] selection.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                // Open settings Fragment
                // TODO
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Sets the global theme (light/dark) based on preference setting.
     */
    fun loadTheme() {
        PreferenceManager.getDefaultSharedPreferences(applicationContext).apply {
            when (getString(
                resources.getString(R.string.pref_key_theme),
                resources.getString(R.string.pref_value_theme_system_default)
            )) {
                resources.getString(R.string.pref_value_theme_light) -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                resources.getString(R.string.pref_value_theme_dark) -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                else -> {
                    // Preference unspecified. Apply system default theme based on OS version:
                    // https://developer.android.com/guide/topics/ui/look-and-feel/darktheme
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                    }
                }
            }
        }
    }

    fun startProximityAlertService(view: View?) {
//        Log.d("Proximity", "Activity: Starting service")
//        Toast.makeText(applicationContext, "Test: start service", Toast.LENGTH_LONG).show()
        val intent = Intent(applicationContext, ProximityDetection::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    fun stopProximityAlertService(view: View?) {
//        Log.d("Proximity", "Activity: Stopping service")
//        Toast.makeText(applicationContext, "Test: stop service", Toast.LENGTH_LONG).show()
        stopService(Intent(applicationContext, ProximityDetection::class.java))
    }

    fun tempTestPlayAudio(view: View?) {
        AudioManager.playAudio(applicationContext, true)
    }
}
