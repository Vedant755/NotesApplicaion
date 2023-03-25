package com.example.bluetoothchatapp.domain.chat

sealed interface ConnectionResult{
    //here we will define different types of connection results.
    //connection establishment

    object ConnectionEstablished: ConnectionResult
    //error
    data class Error(val message: String): ConnectionResult

    //status
    data class TransferSuceeded(val message: BluetoothMessage):ConnectionResult
}