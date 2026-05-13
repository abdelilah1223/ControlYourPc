package com.elbaroudi.pckeyboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.concurrent.Executor

class PcHidService : Service() {

    private val binder = LocalBinder()
    private var inputHost: BluetoothDevice? = null
    private var hidDevice: BluetoothHidDevice? = null

    // HID Report State
    private val keyboardReport = KeyboardHidReport()
    private val mouseReport = MouseHidReport()
    private val consumerReport = ConsumerHidReport()

    // Listeners
    private var connectionListener: ((Boolean) -> Unit)? = null
    private var registrationListener: ((Boolean) -> Unit)? = null
    private var logListener: ((String) -> Unit)? = null

    // Timeout handler for HID profile connection
    private var profileConnectionTimeout: android.os.Handler? = null

    private val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var isAppRegistered: Boolean = false
    private var isRegistering: Boolean = false
    private var connectionPollRunnable: Runnable? = null

    private val prefs: SharedPreferences by lazy {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    fun setConnectionListener(listener: (Boolean) -> Unit) {
        connectionListener = listener
        // Immediately notify of current connection state
        val isConnected = inputHost != null
        Log.e(TAG, "📞 Connection listener set - Current state: ${if (isConnected) "CONNECTED" else "DISCONNECTED"}")
        if (isConnected) {
            Log.e(TAG, "  📱 Connected device: ${inputHost?.name ?: "Unknown"}")
        }
        // Update on main thread
        mainHandler.post {
            listener(isConnected)
        }

        // Also check for connected devices in case we missed any
        if (hidDevice != null) {
            checkConnectedDevices()
            tryAutoReconnectHost()
            startConnectionPolling()
        }
    }

    fun setRegistrationListener(listener: (Boolean) -> Unit) {
        registrationListener = listener
    }

    fun setLogListener(listener: (String) -> Unit) {
        logListener = listener
    }

    private fun log(message: String) {
        Log.d(TAG, message)
        logListener?.invoke(message)
    }

    private fun notifyConnectionState(connected: Boolean) {
        mainHandler.post {
            connectionListener?.invoke(connected)
        }
    }

    private val callback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            Log.e(TAG, "═══════════════════════════════════════")
            Log.e(TAG, "🎯 onAppStatusChanged CALLBACK RECEIVED")
            Log.e(TAG, "📱 Plugged Device: ${pluggedDevice?.name ?: "null"}")
            Log.e(TAG, "📱 Plugged Device Address: ${pluggedDevice?.address ?: "null"}")
            Log.e(TAG, "📝 Registered: $registered")
            Log.e(TAG, "═══════════════════════════════════════")

            if (registered) {
                isAppRegistered = true
                isRegistering = false
                Log.e(TAG, "✅✅✅ HID Registered = TRUE - Device will appear as a Keyboard/Mouse")
                Log.e(TAG, "✅✅✅ The device should now be visible in Bluetooth scan!")
                log("✅ HID Registered Successfully! Make device discoverable to connect.")

                // Check if a device is already connected
                if (pluggedDevice != null) {
                    Log.e(TAG, "🔌 Device already connected: ${pluggedDevice.name}")
                    inputHost = pluggedDevice
                    saveLastHost(pluggedDevice)
                    notifyConnectionState(true)
                    startConnectionPolling()
                } else {
                    // Check for connected devices
                    checkConnectedDevices()
                    // If none are connected yet, try to reconnect to the last known/bonded PC host.
                    tryAutoReconnectHost()
                    startConnectionPolling()
                }
            } else {
                isAppRegistered = false
                isRegistering = false
                Log.e(TAG, "❌❌❌ HID Registered = FALSE - Registration FAILED!")
                Log.e(TAG, "❌❌❌ Common causes:")
                Log.e(TAG, "❌❌❌   1. Device doesn't support HID Device profile (Xiaomi/MIUI)")
                Log.e(TAG, "❌❌❌   2. Missing BLUETOOTH_CONNECT permission")
                Log.e(TAG, "❌❌❌   3. Bluetooth not enabled")
                Log.e(TAG, "❌❌❌   4. Another app already registered as HID")
                log("❌ HID Registration FAILED - Check Bluetooth and permissions")
            }
            registrationListener?.invoke(registered)
        }

        override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
            val stateName = when (state) {
                BluetoothProfile.STATE_CONNECTED -> "CONNECTED"
                BluetoothProfile.STATE_CONNECTING -> "CONNECTING"
                BluetoothProfile.STATE_DISCONNECTED -> "DISCONNECTED"
                BluetoothProfile.STATE_DISCONNECTING -> "DISCONNECTING"
                else -> "UNKNOWN($state)"
            }
            Log.e(TAG, "═══════════════════════════════════════")
            Log.e(TAG, "🔌 onConnectionStateChanged CALLBACK")
            if (ActivityCompat.checkSelfPermission(
                    this@PcHidService,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            Log.e(TAG, "📱 Device: ${device?.name ?: "Unknown"} (${device?.address ?: "Unknown"})")
            Log.e(TAG, "📝 State: $stateName ($state)")
            Log.e(TAG, "═══════════════════════════════════════")
            log("Connection State: $stateName (${device?.name ?: "Unknown"})")

            if (state == BluetoothProfile.STATE_CONNECTED) {
                Log.e(TAG, "✅✅✅ Device CONNECTED - Keyboard/Mouse is ready!")
                inputHost = device
                device?.let { saveLastHost(it) }
                notifyConnectionState(true)
                startConnectionPolling()
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e(TAG, "❌ Device DISCONNECTED")
                inputHost = null
                notifyConnectionState(false)
            }
        }
    }

    private val serviceListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            // Cancel timeout since we connected
            profileConnectionTimeout?.removeCallbacksAndMessages(null)
            profileConnectionTimeout = null

            Log.e(TAG, "🔌 ServiceListener.onServiceConnected - Profile: $profile")
            if (profile == BluetoothProfile.HID_DEVICE) {
                Log.e(TAG, "✅✅✅ HID Profile connected successfully!")
                log("✅ HID Profile connected - Registering app...")
                hidDevice = proxy as BluetoothHidDevice

                // Verify Bluetooth is still enabled before registration
                val bluetoothManager = getSystemService(BluetoothManager::class.java)
                val adapter = bluetoothManager?.adapter

                if (adapter?.isEnabled == true) {
                    Log.e(TAG, "✅ Bluetooth enabled - Preparing HID registration...")
                    ensureRegistered()

                    // After registration attempt, check for already connected devices
                    mainHandler.postDelayed({
                        checkConnectedDevices()
                        tryAutoReconnectHost()
                        startConnectionPolling()
                    }, 1000)
                } else {
                    Log.e(TAG, "❌ Bluetooth disabled during registration attempt")
                    log("❌ Bluetooth was disabled - Cannot register HID")
                }
            } else {
                Log.e(TAG, "❌❌❌ Wrong profile connected: $profile (Expected: ${BluetoothProfile.HID_DEVICE})")
                log("❌ Wrong Bluetooth profile connected")
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            Log.e(TAG, "🔌 ServiceListener.onServiceDisconnected - Profile: $profile")
            if (profile == BluetoothProfile.HID_DEVICE) {
                log("⚠️ HID Profile disconnected")
                hidDevice = null
                isAppRegistered = false
                isRegistering = false
                stopConnectionPolling()
            }
        }
    }

    private fun ensureRegistered() {
        if (hidDevice == null) return
        if (isAppRegistered || isRegistering) {
            Log.e(TAG, "ℹ️ HID registration skipped (registered=$isAppRegistered registering=$isRegistering)")
            return
        }
        isRegistering = true
        registerApp()
    }

    @SuppressLint("MissingPermission")
    private fun tryAutoReconnectHost() {
        val device = hidDevice ?: return
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val adapter = bluetoothManager?.adapter ?: return

        if (!isAppRegistered) {
            Log.e(TAG, "ℹ️ Auto-reconnect skipped: HID not registered yet")
            return
        }
        if (inputHost != null) {
            Log.e(TAG, "ℹ️ Auto-reconnect skipped: already have inputHost")
            return
        }

        val lastHostAddress = prefs.getString(KEY_LAST_HOST_ADDRESS, null)
        val bonded = try {
            adapter.bondedDevices?.toList().orEmpty()
        } catch (e: Exception) {
            emptyList()
        }

        val candidates = buildList {
            if (lastHostAddress != null) {
                bonded.firstOrNull { it.address == lastHostAddress }?.let { add(it) }
            }
            bonded.filter {
                val major = it.bluetoothClass?.majorDeviceClass
                major == BluetoothClass.Device.Major.COMPUTER
            }.forEach { add(it) }
            bonded.forEach { add(it) }
        }.distinctBy { it.address }

        if (candidates.isEmpty()) {
            Log.e(TAG, "ℹ️ Auto-reconnect: no bonded devices found")
            return
        }

        // Try a few candidates; connect() will succeed only for the actual HID host.
        var attempted = 0
        for (host in candidates) {
            if (attempted >= 3) break
            attempted++
            try {
                Log.e(TAG, "🔁 Auto-reconnect attempt #$attempted -> ${host.name} (${host.address})")
                val ok = device.connect(host)
                Log.e(TAG, "🔁 hidDevice.connect() returned: $ok")
                if (ok == true) {
                    // Connection callback may come later; start polling to catch it quickly.
                    startConnectionPolling()
                    return
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Auto-reconnect failed for ${host.address}", e)
            }
        }
    }

    private fun saveLastHost(device: BluetoothDevice) {
        try {
            prefs.edit().putString(KEY_LAST_HOST_ADDRESS, device.address).apply()
        } catch (_: Exception) {
            // ignore
        }
    }

    private fun startConnectionPolling() {
        if (connectionPollRunnable != null) return
        connectionPollRunnable = Runnable {
            try {
                checkConnectedDevices()
            } finally {
                mainHandler.postDelayed(connectionPollRunnable!!, 2000)
            }
        }
        mainHandler.postDelayed(connectionPollRunnable!!, 2000)
    }

    private fun stopConnectionPolling() {
        connectionPollRunnable?.let { mainHandler.removeCallbacks(it) }
        connectionPollRunnable = null
    }

    override fun onCreate() {
        super.onCreate()
        // Use Log.e to ensure visibility in logcat
        Log.e(TAG, "═══════════════════════════════════════")
        Log.e(TAG, "🎮 PcHidService onCreate() STARTED")
        Log.e(TAG, "═══════════════════════════════════════")
        log("Service onCreate - Initializing HID...")

        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val adapter = bluetoothManager?.adapter

        if (adapter == null) {
            Log.e(TAG, "❌❌❌ BluetoothAdapter is NULL - Bluetooth not available!")
            log("❌ Bluetooth not available on this device")
            return
        }

        Log.e(TAG, "✅ BluetoothAdapter found")

        // Check if device supports multiple advertisements (indicator of HID support)
        try {
            val supportsMultipleAds = adapter.isMultipleAdvertisementSupported
            Log.e(TAG, "📱 Multiple Advertisements Supported: $supportsMultipleAds")
            log("Device supports multiple advertisements: $supportsMultipleAds")
            if (!supportsMultipleAds) {
                Log.e(TAG, "⚠️⚠️⚠️ WARNING: Device may not fully support HID Device profile")
                log("⚠️ Device may not fully support HID Device profile")
            }
        } catch (e: Exception) {
            Log.e(TAG, "⚠️ Could not check advertisement support", e)
        }

        // Check if HID Device profile is available (Android 9+)
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                // Try to get profile proxy - if it fails, HID is not supported
                Log.e(TAG, "🎮 Attempting to check HID Device profile support...")
                // We'll know if it's supported when onServiceConnected is called
            } else {
                Log.e(TAG, "⚠️ Android version ${android.os.Build.VERSION.SDK_INT} - HID Device requires Android 9+")
                log("⚠️ Android version too old - HID Device requires Android 9+")
            }
        } catch (e: Exception) {
            Log.e(TAG, "⚠️ Could not check HID profile support", e)
        }

        if (!adapter.isEnabled) {
            Log.e(TAG, "⚠️ Bluetooth is DISABLED - Enabling...")
            log("⚠️ Bluetooth is disabled - Attempting to enable...")
            try {
                adapter.enable()
                // Wait a bit for Bluetooth to enable, then try again
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    if (adapter.isEnabled) {
                        Log.e(TAG, "✅ Bluetooth enabled - Connecting to HID profile...")
                        log("✅ Bluetooth enabled - Connecting to HID profile...")
                        adapter.getProfileProxy(this, serviceListener, BluetoothProfile.HID_DEVICE)
                    } else {
                        Log.e(TAG, "❌ Failed to enable Bluetooth")
                        log("❌ Failed to enable Bluetooth - Please enable manually")
                    }
                }, 2000)
            } catch (e: SecurityException) {
                Log.e(TAG, "❌ Permission denied - Cannot enable Bluetooth", e)
                log("❌ Permission denied - Please enable Bluetooth manually")
            }
        } else {
            Log.e(TAG, "✅ Bluetooth is enabled - Connecting to HID profile...")
            log("✅ Bluetooth is enabled - Connecting to HID profile...")

            // Set a timeout - if HID profile doesn't connect in 5 seconds, it's likely not supported
            profileConnectionTimeout = android.os.Handler(android.os.Looper.getMainLooper())
            profileConnectionTimeout?.postDelayed({
                if (hidDevice == null) {
                    Log.e(TAG, "❌❌❌ TIMEOUT: HID Profile did not connect after 5 seconds!")
                    Log.e(TAG, "❌❌❌ This device likely does NOT support HID Device profile")
                    Log.e(TAG, "❌❌❌ Common on Xiaomi/MIUI devices - HID Device is disabled by manufacturer")
                    log("❌ TIMEOUT: HID Profile not available - Device may not support it")
                }
            }, 5000)

            adapter.getProfileProxy(this, serviceListener, BluetoothProfile.HID_DEVICE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopConnectionPolling()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        hidDevice?.unregisterApp()

        // Close Proxy to prevent leaks
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val adapter = bluetoothManager?.adapter
        if (hidDevice != null) {
            adapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hidDevice)
            hidDevice = null
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): PcHidService = this@PcHidService
    }

    @SuppressLint("MissingPermission")
    private fun registerApp() {
        Log.e(TAG, "📝 registerApp() called")

        if (hidDevice == null) {
            Log.e(TAG, "❌❌❌ HID Device proxy is NULL - Cannot register")
            log("❌ HID Device not available - Cannot register")
            return
        }

        Log.e(TAG, "📝 Registering HID app with name 'BT Keyboard & Mouse'...")
        log("Registering HID app with name 'BT Keyboard & Mouse'...")

        val sdp = BluetoothHidDeviceAppSdpSettings(
            "BT Keyboard & Mouse",
            "Android Bluetooth Keyboard/Mouse",
            "Antigravity",
            0xC0.toByte(),
            HidDescriptor.KEYBOARD_MOUSE_DESCRIPTOR
        )

        try {
            Log.e(TAG, "📝 Calling hidDevice.registerApp()...")
            val result = hidDevice?.registerApp(
                sdp,
                null,
                null,
                Executor { it.run() },
                callback
            )

            Log.e(TAG, "📝 registerApp() returned: $result")
            if (result == true) {
                Log.e(TAG, "✅ registerApp() called successfully - Waiting for onAppStatusChanged callback...")
                log("✅ registerApp() called successfully - Waiting for confirmation...")
            } else {
                Log.e(TAG, "❌❌❌ registerApp() returned FALSE - Registration will likely fail!")
                log("⚠️ registerApp() returned false - Check logs for details")
                isRegistering = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌❌❌ Exception during registerApp()", e)
            Log.e(TAG, "❌ Exception message: ${e.message}")
            Log.e(TAG, "❌ Exception stack trace:", e)
            log("❌ Error registering HID: ${e.message}")
            isRegistering = false
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkConnectedDevices() {
        if (hidDevice == null) {
            Log.e(TAG, "⚠️ Cannot check connected devices - HID device is null")
            return
        }

        try {
            val connectedDevices = hidDevice?.connectedDevices
            Log.e(TAG, "🔍 Checking connected devices... Found: ${connectedDevices?.size ?: 0}")
            if (connectedDevices != null && connectedDevices.isNotEmpty()) {
                connectedDevices.forEach { device ->
                    Log.e(TAG, "  ✅ Found connected device: ${device.name} (${device.address})")
                    inputHost = device
                    saveLastHost(device)
                    notifyConnectionState(true)
                }
            } else {
                Log.e(TAG, "  ℹ️ No devices currently connected")
                if (inputHost != null) {
                    inputHost = null
                    notifyConnectionState(false)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error checking connected devices", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendKeyboardReport() {
        val host = inputHost ?: return
        val device = hidDevice ?: return

        try {
            device.sendReport(host, REPORT_ID_KEYBOARD, keyboardReport.toByteArray())
        } catch (e: Exception) {
            Log.e(TAG, "Error sending keyboard report", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendMouseReport() {
        val host = inputHost ?: return
        val device = hidDevice ?: return

        try {
            device.sendReport(host, REPORT_ID_MOUSE, mouseReport.toByteArray())
        } catch (e: Exception) {
            Log.e(TAG, "Error sending mouse report", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendConsumerReport() {
        val host = inputHost ?: return
        val device = hidDevice ?: return

        try {
            device.sendReport(host, REPORT_ID_CONSUMER, consumerReport.toByteArray())
        } catch (e: Exception) {
            Log.e(TAG, "Error sending consumer report", e)
        }
    }

    private fun clampRel(value: Int): Byte {
        return when {
            value > 127 -> 127.toByte()
            value < -127 -> (-127).toByte()
            else -> value.toByte()
        }
    }

    fun sendKey(modifiers: Byte = 0, keyCode: Byte) {
        keyboardReport.modifiers = modifiers
        keyboardReport.keyCodes = byteArrayOf(keyCode, 0, 0, 0, 0, 0)
        sendKeyboardReport()

        keyboardReport.modifiers = 0
        keyboardReport.keyCodes = ByteArray(6)
        sendKeyboardReport()
    }

    fun mouseMove(dx: Int, dy: Int) {
        mouseReport.x = clampRel(dx)
        mouseReport.y = clampRel(dy)
        mouseReport.wheel = 0
        sendMouseReport()

        mouseReport.x = 0
        mouseReport.y = 0
        sendMouseReport()
    }

    fun mouseScroll(delta: Int) {
        mouseReport.x = 0
        mouseReport.y = 0
        mouseReport.wheel = clampRel(delta)
        sendMouseReport()

        mouseReport.wheel = 0
        sendMouseReport()
    }

    private fun setMouseButton(mask: Int, down: Boolean) {
        val current = mouseReport.buttons.toInt()
        mouseReport.buttons = if (down) {
            (current or mask).toByte()
        } else {
            (current and mask.inv()).toByte()
        }
        mouseReport.x = 0
        mouseReport.y = 0
        mouseReport.wheel = 0
        sendMouseReport()
    }

    fun leftDown() {
        setMouseButton(MOUSE_BTN_LEFT, true)
    }

    fun leftUp() {
        setMouseButton(MOUSE_BTN_LEFT, false)
    }

    fun rightDown() {
        setMouseButton(MOUSE_BTN_RIGHT, true)
    }

    fun rightUp() {
        setMouseButton(MOUSE_BTN_RIGHT, false)
    }

    fun leftClick() {
        mouseReport.buttons = (mouseReport.buttons.toInt() or MOUSE_BTN_LEFT).toByte()
        sendMouseReport()
        mouseReport.buttons = (mouseReport.buttons.toInt() and MOUSE_BTN_LEFT.inv()).toByte()
        sendMouseReport()
    }

    fun rightClick() {
        mouseReport.buttons = (mouseReport.buttons.toInt() or MOUSE_BTN_RIGHT).toByte()
        sendMouseReport()
        mouseReport.buttons = (mouseReport.buttons.toInt() and MOUSE_BTN_RIGHT.inv()).toByte()
        sendMouseReport()
    }

    fun consumerPress(usage: Short) {
        consumerReport.usage = usage
        sendConsumerReport()
        consumerReport.usage = 0
        sendConsumerReport()
    }

    companion object {
        private const val TAG = "PcHidService"

        private const val PREFS_NAME = "pc_hid_prefs"
        private const val KEY_LAST_HOST_ADDRESS = "last_host_address"

        const val REPORT_ID_KEYBOARD = 1
        const val REPORT_ID_MOUSE = 2
        const val REPORT_ID_CONSUMER = 3

        const val MOUSE_BTN_LEFT = 1
        const val MOUSE_BTN_RIGHT = 2
        const val MOUSE_BTN_MIDDLE = 4

        // Consumer usages (HID Usage Table)
        const val CONSUMER_VOLUME_UP: Short = 0x00E9.toShort()
        const val CONSUMER_VOLUME_DOWN: Short = 0x00EA.toShort()
        const val CONSUMER_MUTE: Short = 0x00E2.toShort()
        const val CONSUMER_BRIGHTNESS_UP: Short = 0x006F.toShort()
        const val CONSUMER_BRIGHTNESS_DOWN: Short = 0x0070.toShort()
        const val CONSUMER_PLAY_PAUSE: Short = 0x00CD.toShort()
        const val CONSUMER_NEXT_TRACK: Short = 0x00B5.toShort()
        const val CONSUMER_PREV_TRACK: Short = 0x00B6.toShort()
        const val CONSUMER_STOP: Short = 0x00B7.toShort()

        // HID Keyboard Modifier Keys
        const val MOD_LEFT_CTRL: Byte = 0x01
        const val MOD_LEFT_SHIFT: Byte = 0x02
        const val MOD_LEFT_ALT: Byte = 0x04
        const val MOD_LEFT_GUI: Byte = 0x08
        const val MOD_RIGHT_CTRL: Byte = 0x10
        const val MOD_RIGHT_SHIFT: Byte = 0x20
        const val MOD_RIGHT_ALT: Byte = 0x40
        const val MOD_RIGHT_GUI: Byte = 0x80.toByte()

        // HID Keyboard Key Codes (HID Usage Table)
        const val KEY_NONE: Byte = 0x00
        const val KEY_A: Byte = 0x04
        const val KEY_B: Byte = 0x05
        const val KEY_C: Byte = 0x06
        const val KEY_D: Byte = 0x07
        const val KEY_E: Byte = 0x08
        const val KEY_F: Byte = 0x09
        const val KEY_G: Byte = 0x0A
        const val KEY_H: Byte = 0x0B
        const val KEY_I: Byte = 0x0C
        const val KEY_J: Byte = 0x0D
        const val KEY_K: Byte = 0x0E
        const val KEY_L: Byte = 0x0F
        const val KEY_M: Byte = 0x10
        const val KEY_N: Byte = 0x11
        const val KEY_O: Byte = 0x12
        const val KEY_P: Byte = 0x13
        const val KEY_Q: Byte = 0x14
        const val KEY_R: Byte = 0x15
        const val KEY_S: Byte = 0x16
        const val KEY_T: Byte = 0x17
        const val KEY_U: Byte = 0x18
        const val KEY_V: Byte = 0x19
        const val KEY_W: Byte = 0x1A
        const val KEY_X: Byte = 0x1B
        const val KEY_Y: Byte = 0x1C
        const val KEY_Z: Byte = 0x1D

        const val KEY_1: Byte = 0x1E
        const val KEY_2: Byte = 0x1F
        const val KEY_3: Byte = 0x20
        const val KEY_4: Byte = 0x21
        const val KEY_5: Byte = 0x22
        const val KEY_6: Byte = 0x23
        const val KEY_7: Byte = 0x24
        const val KEY_8: Byte = 0x25
        const val KEY_9: Byte = 0x26
        const val KEY_0: Byte = 0x27

        const val KEY_ENTER: Byte = 0x28
        const val KEY_ESC: Byte = 0x29
        const val KEY_BACKSPACE: Byte = 0x2A
        const val KEY_TAB: Byte = 0x2B
        const val KEY_SPACE: Byte = 0x2C
        const val KEY_MINUS: Byte = 0x2D
        const val KEY_EQUAL: Byte = 0x2E
        const val KEY_LEFTBRACE: Byte = 0x2F
        const val KEY_RIGHTBRACE: Byte = 0x30
        const val KEY_BACKSLASH: Byte = 0x31
        const val KEY_HASH: Byte = 0x32
        const val KEY_SEMICOLON: Byte = 0x33
        const val KEY_APOSTROPHE: Byte = 0x34
        const val KEY_GRAVE: Byte = 0x35
        const val KEY_COMMA: Byte = 0x36
        const val KEY_DOT: Byte = 0x37
        const val KEY_SLASH: Byte = 0x38
        const val KEY_CAPSLOCK: Byte = 0x39

        const val KEY_F1: Byte = 0x3A
        const val KEY_F2: Byte = 0x3B
        const val KEY_F3: Byte = 0x3C
        const val KEY_F4: Byte = 0x3D
        const val KEY_F5: Byte = 0x3E
        const val KEY_F6: Byte = 0x3F
        const val KEY_F7: Byte = 0x40
        const val KEY_F8: Byte = 0x41
        const val KEY_F9: Byte = 0x42
        const val KEY_F10: Byte = 0x43
        const val KEY_F11: Byte = 0x44
        const val KEY_F12: Byte = 0x45

        const val KEY_PRINTSCREEN: Byte = 0x46
        const val KEY_SCROLLLOCK: Byte = 0x47
        const val KEY_PAUSE: Byte = 0x48
        const val KEY_INSERT: Byte = 0x49
        const val KEY_HOME: Byte = 0x4A
        const val KEY_PAGEUP: Byte = 0x4B
        const val KEY_DELETE: Byte = 0x4C
        const val KEY_END: Byte = 0x4D
        const val KEY_PAGEDOWN: Byte = 0x4E
        const val KEY_RIGHT: Byte = 0x4F
        const val KEY_LEFT: Byte = 0x50
        const val KEY_DOWN: Byte = 0x51
        const val KEY_UP: Byte = 0x52

        const val KEY_NUMLOCK: Byte = 0x53
        const val KEY_KP_SLASH: Byte = 0x54
        const val KEY_KP_ASTERISK: Byte = 0x55
        const val KEY_KP_MINUS: Byte = 0x56
        const val KEY_KP_PLUS: Byte = 0x57
        const val KEY_KP_ENTER: Byte = 0x58
        const val KEY_KP_1: Byte = 0x59
        const val KEY_KP_2: Byte = 0x5A
        const val KEY_KP_3: Byte = 0x5B
        const val KEY_KP_4: Byte = 0x5C
        const val KEY_KP_5: Byte = 0x5D
        const val KEY_KP_6: Byte = 0x5E
        const val KEY_KP_7: Byte = 0x5F
        const val KEY_KP_8: Byte = 0x60
        const val KEY_KP_9: Byte = 0x61
        const val KEY_KP_0: Byte = 0x62
        const val KEY_KP_DOT: Byte = 0x63

        const val KEY_MENU: Byte = 0x65
        const val KEY_POWER: Byte = 0x66

        // Media keys
        const val KEY_MEDIA_PLAY: Byte = 0xB0.toByte()
        const val KEY_MEDIA_STOP: Byte = 0xB7.toByte()
        const val KEY_MEDIA_PREV: Byte = 0xB6.toByte()
        const val KEY_MEDIA_NEXT: Byte = 0xB5.toByte()
        const val KEY_MEDIA_MUTE: Byte = 0xE2.toByte()
        const val KEY_VOLUME_UP: Byte = 0xE9.toByte()
        const val KEY_VOLUME_DOWN: Byte = 0xEA.toByte()
    }
}