package caffeinatedandroid.keepyourdistance.service

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import caffeinatedandroid.keepyourdistance.media.AppNotificationManager


/**
 * A [Service] which detects nearby wireless devices by performing periodic scans.
 */
class ProximityDetection : Service() {

    companion object {
        private const val TAG = "Proximity"
    }

    private lateinit var appNotifications: AppNotificationManager

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var isBleAvailable = false

    // TODO First, make a periodic log/toast/ping, to see that service runs

    // TODO allow service to be cancelled during onCreate
    // TODO make this service cancellable

    // TODO ensure scans do not occur whilst previous scans still running (avoid tracking with simple bools, such as `isRunning`; use proper API functions
    // TODO encrypt SSID/name and BSSID/Mac-addr when persisting or caching on local device

    /**
     * Called when the [Service] is created. Prepares variables that only need initialising
     * once and whose state isn't effected by runtime changes (e.g., user turning off Bluetooth).
     */
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Creating Service")

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Check if Bluetooth Low Energy is available
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            isBleAvailable = true
        }

        // Register for broadcasts when a device is discovered
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(proximityDetectionBroadcastReceiver, filter)
    }

    /**
     * Called when the [Service] is being destroyed. Cleans up any resources, such as
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destroying service")

        // Stop starting new discovery tasks
        //alarmManager.cancel() // TODO
        // Do not handle anymore events (e.g., stop pending scans for BLE/WiFi)
        unregisterReceiver(proximityDetectionBroadcastReceiver)

        // Stop Bluetooth scanning
        bluetoothAdapter?.cancelDiscovery()
        /*if (isBleAvailable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bluetoothAdapter?.bluetoothLeScanner.stopScan()
                // TODO
            } else {
                bluetoothAdapter?.stopLeScan()
                // TODO
            }
        }*/

        // TODO cleanup WiFi

        Log.d(TAG, "Destroyed service")
    }

    /**
     * Called each time this [Service] starts.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Init variables that can be effected by external factors (e.g., user disables Bluetooth)
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "Starting service")

        appNotifications = AppNotificationManager.getInstance(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(
                AppNotificationManager.NOTIFICATION_ID_SERVICE_RUNNING,
                appNotifications.notificationServiceRunning(this)
            )
        }

        // Check device supports Bluetooth
        if (bluetoothAdapter != null) {
            // Check Bluetooth is enabled
            if (bluetoothAdapter?.isEnabled == false) {
                // Bluetooth not enabled
                requestBtEnabled()
            } else {
                // Bluetooth is enabled
                startBtDiscovery()
            }
        }

        Log.d(TAG, "Started service")
        return START_STICKY
    }

    /**
     * Binding to this [Service] is not enabled.
     */
    override fun onBind(p0: Intent?): IBinder? {
        // Do not bind
        return null
    }

    /**
     * [BroadcastReceiver] to handle Proximity and related hardware/system events.
     */
    private val proximityDetectionBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    Log.d(TAG, "BluetoothAdapter.ACTION_STATE_CHANGED")
                    // Bluetooth enabled/disabled
                    val intentExtra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1)
                    if (intentExtra == BluetoothAdapter.STATE_OFF) {
                        // Bluetooth not enabled
                        requestBtEnabled()
                    } else if (intentExtra == BluetoothAdapter.STATE_ON) {
                        // Bluetooth is enabled
                        startBtDiscovery()
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d(TAG, "BluetoothAdapter.ACTION_DISCOVERY_STARTED")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d(TAG, "BluetoothAdapter.ACTION_DISCOVERY_FINISHED")
                    // Bluetooth scan finished, now start Bluetooth LE scan (both cannot be done
                    // simultaneously)
                    startBleDiscovery()
                }
                BluetoothDevice.ACTION_FOUND -> {
                    deviceFoundBT(intent)
                }
            }
        }
    }

    private fun requestBtEnabled() {
        // TODO show notification to request user enables BT
        Log.d(TAG, "Notification to request BT enabled")

        // Request BT enabled
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }

    private fun requestWifiEnabled() {
        // TODO
    }

    private fun isDiscovering(): Boolean {
        //bluetoothAdapter?.isDiscovering
        //bluetoothAdapter?.bluetoothLeScanner.
        // TODO add wifi scanning check too
        // TODO Check if `bluetoothAdapter?.isDiscovering` covers BT & BLE, or just BT
        return bluetoothAdapter?.isDiscovering ?: false
    }

    private fun startBtDiscovery() {
        // TODO
        Log.d(TAG, "Start BT discovery")
    }

    private fun startBleDiscovery() {
        if (!isBleAvailable) {
            return
        }
        // TODO
        Log.d(TAG, "Start BLE discovery")
    }

    private fun startWifiDiscovery() {
        // TODO
    }

    /**
     * Notify user that a device has been detected if within the predefined radius.
     */
    private fun deviceFoundBT(intent: Intent) {
        // TODO
        Log.d(TAG, "BluetoothDevice.ACTION_FOUND")

        // Get details Bundle, or return if null
        val extras = intent.extras ?: return

        // Get Bluetooth device details
        val btDevice: BluetoothDevice? = extras.getParcelable(BluetoothDevice.EXTRA_DEVICE)
        if (btDevice == null) {
            Log.d(TAG,"No Bluetooth devices discovered")
            return
        }

        // Get RSSI signal strength
        val rssi = extras.getShort(BluetoothDevice.EXTRA_RSSI)

        Log.d(TAG, "Device found: name=${btDevice.name}, addr=${btDevice.address}, RSSI=$rssi")

        // Alert the user
        appNotifications.notificationDeviceDetected(applicationContext)
        // TODO play audio - can audio be played automatically as notification noise?
    }

    private fun deviceFoundWifi() {
        // TODO
    }

}