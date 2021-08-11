package ru.sandroisu.annoying_teammate

import android.bluetooth.BluetoothDevice

class BLTDevice(device: BluetoothDevice) {
    val name = device.name
    val macAddress = device.address

}