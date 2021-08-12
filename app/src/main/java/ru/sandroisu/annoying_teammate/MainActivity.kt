package ru.sandroisu.annoying_teammate

import DevicesAdapter
import android.Manifest
import android.bluetooth.BluetoothDevice
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
import android.bluetooth.BluetoothAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import java.util.*


class MainActivity : AppCompatActivity(), OnStarSelectedListener {
    private var requestLocationAccessPermissionLauncher: ActivityResultLauncher<String>? =
        null
    private var backGroundLocationPermissionLauncher: ActivityResultLauncher<String>? = null
    private val devices = arrayListOf<BLTDevice>()
    private val devicesAdapter = DevicesAdapter(devices, this)
    private lateinit var recyclerView: RecyclerView

    private var bleAdapter = BluetoothAdapter.getDefaultAdapter()
    var scanner: BluetoothLeScanner = bleAdapter.bluetoothLeScanner
    private val timerTask = object : TimerTask() {
        override fun run() {
            runOnUiThread {
                val iterator = devices.iterator()
                while (iterator.hasNext()) {
                    val device = iterator.next()
                    if (System.currentTimeMillis() - device.lastTimeVisible > 30000) {
                        val pos = devices.indexOf(device)
                        iterator.remove()
                        devicesAdapter.notifyItemRemoved(pos)
                    }
                }
            }
        }
    }
    private val timer = Timer()
    private var scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
        .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
        .setReportDelay(0L)
        .build()

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device: BluetoothDevice = result.device
            val rssi = result.rssi
            val bltDevice = BLTDevice(device, rssi)
            var toUpdate = -1
            devices.forEachIndexed { index, blt ->
                if (bltDevice.macAddress == blt.macAddress) {
                    blt.name = bltDevice.name
                    blt.rssi = bltDevice.rssi
                    blt.lastTimeVisible = System.currentTimeMillis()
                    toUpdate = index
                    return@forEachIndexed
                }
            }
            if (toUpdate >= 0) {
                devicesAdapter.notifyItemChanged(toUpdate)
            } else {
                devices.add(bltDevice)
                devicesAdapter.notifyItemInserted(devices.size)
            }
        }

        override fun onBatchScanResults(results: List<ScanResult?>?) {
        }

        override fun onScanFailed(errorCode: Int) {
        }
    }

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
        val buttonStopSearch = findViewById<MaterialButton>(R.id.main_stop_scan_for_devices)
        buttonStopSearch.setOnClickListener {
            scanner.stopScan(scanCallback)
            it.isEnabled = false
        }
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
                        scanner.startScan(null, scanSettings, scanCallback)
                        buttonStopSearch.isEnabled = true

                    } else {
                        showPermissionDialog(getString(R.string.location_permission))
                    }
                } else {
                    scanner.startScan(null, scanSettings, scanCallback)
                    buttonStopSearch.isEnabled = true
                }
            } else {
                showPermissionDialog(getString(R.string.location_permission))
            }
        }
        recyclerView = findViewById(R.id.main_devices_list)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = devicesAdapter
        recyclerView.itemAnimator = null
        timer.schedule(timerTask, 30000, 60000)

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

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onStarSelected(device: BLTDevice) {
        devices[devices.indexOf(device)].addTimeWaste()
    }

    override fun onStarUnselected(device: BLTDevice) {
    }
}