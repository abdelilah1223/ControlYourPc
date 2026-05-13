package com.elbaroudi.pckeyboard

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import com.elbaroudi.pckeyboard.ui.ConnectedScreen
import com.elbaroudi.pckeyboard.ui.WelcomeScreen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private var gamepadService: PcHidService? = null

    private var isBound = false
    private val isConnectedState = mutableStateOf(false)
    private val isRegisteredState = mutableStateOf(false)
    private val logMessageState = mutableStateOf("No logs yet...")

    // Permission Launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            android.util.Log.d("MainActivity", "Permission result: $granted")
            permissions.entries.forEach { (permission, isGranted) ->
                android.util.Log.d("MainActivity", "  $permission: $isGranted")
            }
            if (granted) {
                android.util.Log.d("MainActivity", "✅ All permissions granted - Binding service")
                bindGamepadService()
            } else {
                android.util.Log.e("MainActivity", "❌ Some permissions denied - HID registration may fail")
            }
        }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as PcHidService.LocalBinder
            gamepadService = binder.getService()
            isBound = true
            
            // Set listener
            gamepadService?.setConnectionListener { connected ->
                runOnUiThread {
                    android.util.Log.e("MainActivity", "🔄 Connection state changed: $connected")
                    if (connected) {
                        android.util.Log.e("MainActivity", "✅✅✅ Device connected - Switching to GamepadUI")
                    } else {
                        android.util.Log.e("MainActivity", "❌ Device disconnected - Showing WelcomeScreen")
                    }
                    isConnectedState.value = connected
                }
            }
            gamepadService?.setRegistrationListener { registered ->
                runOnUiThread {
                    isRegisteredState.value = registered
                }
            }
            gamepadService?.setLogListener { message ->
                runOnUiThread {
                    logMessageState.value = message
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide System Bars
        hideSystemBars()

        setContent {
            val isConnected by remember { isConnectedState }
            val isRegistered by remember { isRegisteredState }
            
            if (isConnected) {
                ConnectedScreen(
                    service = if (isBound) gamepadService else null,
                    lastLog = remember { logMessageState }.value
                )
            } else {
                WelcomeScreen(
                    isRegistered = isRegistered,
                    lastLog = remember { logMessageState }.value,
                    onMakeDiscoverable = {
                        android.util.Log.e("MainActivity", "🔍 User tapped 'Make Visible' button")
                        try {
                            val discoverableIntent = Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                                putExtra(android.bluetooth.BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300) // 5 minutes instead of 2
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            android.util.Log.e("MainActivity", "📡 Starting discoverability request (300 seconds)")
                            startActivity(discoverableIntent)
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "❌ Error requesting discoverability", e)
                        }
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissionsToRequest = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            }
            
            if (permissionsToRequest.isNotEmpty()) {
                android.util.Log.d("MainActivity", "Requesting permissions: ${permissionsToRequest.joinToString()}")
                requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
            } else {
                // Verify all permissions are actually granted
                val connectGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                val advertiseGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED
                
                if (connectGranted && advertiseGranted) {
                    android.util.Log.d("MainActivity", "✅ All permissions granted - Binding service")
                    bindGamepadService()
                } else {
                    android.util.Log.e("MainActivity", "❌ Permissions not granted - Requesting again")
                    requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
                }
            }
        } else {
            bindGamepadService()
        }
    }

    private fun bindGamepadService() {
        if (!isBound) {
            android.util.Log.e("MainActivity", "🔌 Binding PcHidService...")
            Intent(this, PcHidService::class.java).also { intent ->
                // Start the service first to ensure it's running
                startService(intent)
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Don't unbind here - keep service running
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            try {
                unbindService(connection)
            } catch (e: Exception) {
                // Ignore if already unbound
            }
            isBound = false
        }
    }
    
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}