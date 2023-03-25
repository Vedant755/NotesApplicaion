package com.example.bluetoothchatapp.presentation

import com.example.bluetoothchatapp.domain.chat.BluetoothDevice
import com.example.bluetoothchatapp.domain.chat.BluetoothMessage

data class BluetoothUIState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean=false,
    val error: String?=null,
    val message: List<BluetoothMessage> = emptyList()

)
