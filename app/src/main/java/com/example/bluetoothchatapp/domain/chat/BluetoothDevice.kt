package com.example.bluetoothchatapp.domain.chat
typealias BluetoothDeviceDomain = BluetoothDevice

//this gives the information about the bluetooth devices which are interacting
data class BluetoothDevice(
    val name: String?,
    val address: String //mac address
)
