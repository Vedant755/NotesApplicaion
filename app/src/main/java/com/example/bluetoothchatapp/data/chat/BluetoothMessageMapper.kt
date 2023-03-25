package com.example.bluetoothchatapp.data.chat

import com.example.bluetoothchatapp.domain.chat.BluetoothMessage

fun String.tobluetoothMessage(isFromLocalUser: Boolean):BluetoothMessage{
    val name = substringBeforeLast("#")
    val message= substringAfter("#")
    return BluetoothMessage(
        message = message,
        senderName = name,
        isFromLocalUser = isFromLocalUser
    )
}

fun BluetoothMessage.toByteArray():ByteArray{
    return "$senderName#$message".encodeToByteArray()
}