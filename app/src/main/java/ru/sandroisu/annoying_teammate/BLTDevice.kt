package ru.sandroisu.annoying_teammate

import android.bluetooth.BluetoothDevice

class BLTDevice(device: BluetoothDevice, var rssi: Int) {
    var timeWaste = 0L
    var lastDetected = 0L
    var lastTimeVisible = System.currentTimeMillis()
    var starSelected = false
    var name: String? = device.name
    val macAddress: String = device.address

    fun addTimeWaste() {
        if (rssi > -80) {
            if (timeWaste == 0L) {
                lastDetected = System.currentTimeMillis() - 1000
            }
            timeWaste = timeWaste + System.currentTimeMillis() - lastDetected
            lastDetected = System.currentTimeMillis()
        }
    }

}