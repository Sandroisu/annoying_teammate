package ru.sandroisu.annoying_teammate

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private var requestLocationAccessPermissionLauncher: ActivityResultLauncher<String>? =
        null
    private var backGroundLocationPermissionLauncher: ActivityResultLauncher<String>? = null
    private val devices = arrayListOf<BLTDevice>()
    private val devicesAdapter = DevicesAdapter(devices)
    private lateinit var recyclerView :RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestLocationAccessPermissionLauncher =
            registerForActivityResult(RequestPermission()) { isGranted ->
                if (isGranted) {
                    if (VERSION.SDK_INT >= VERSION_CODES.Q) {
                        requestBackgroundLocationPermission()
                    }
                } else {
                    showPermissionDialog(getString(R.string.location_permission))
                }
            }
        backGroundLocationPermissionLauncher =
            registerForActivityResult(RequestPermission()) { isGranted ->
                if (isGranted) {
                    Toast.makeText(this, "Необходимые разрешения предоставлены", Toast.LENGTH_LONG)
                        .show()
                } else {
                    showPermissionDialog(
                        getString(R.string.location_permission)
                    )
                }
            }
        requestLocationPermissionWithBackground()
        val buttonSearch = findViewById<MaterialButton>(R.id.main_scan_for_devices)
        buttonSearch.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (VERSION.SDK_INT >= VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                        registerReceiver(receiver, filter)
                    } else {
                        showPermissionDialog(getString(R.string.location_permission))
                    }
                } else {
                    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                    registerReceiver(receiver, filter)
                }
            } else {
                showPermissionDialog(getString(R.string.location_permission))
            }
        }
        recyclerView = findViewById(R.id.main_devices_list)
        recyclerView.adapter = devicesAdapter

    }

    private fun requestLocationPermissionWithBackground(): Boolean {
        return when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                requestBackgroundLocationPermission()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionDialog(getString(R.string.location_permission))
                false
            }
            else -> {
                requestLocationAccessPermissionLauncher!!.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                false
            }
        }
    }

    private fun requestBackgroundLocationPermission(): Boolean {
        if (VERSION.SDK_INT < VERSION_CODES.Q) {
            return true
        }
        return when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) -> {
                showPermissionDialog(
                    getString(R.string.location_permission)
                )
                false
            }
            else -> {
                backGroundLocationPermissionLauncher!!.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                false
            }
        }
    }

    private fun showPermissionDialog(message: String) {
        val permissionDialog = PermissionDialog()
        permissionDialog.isCancelable = false
        permissionDialog.setMessage(message)
        permissionDialog.show(supportFragmentManager, "dialog_permission")
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let { devices.add(BLTDevice(it)) }
                    devicesAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}