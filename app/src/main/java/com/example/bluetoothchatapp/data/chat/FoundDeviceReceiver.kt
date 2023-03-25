package com.example.bluetoothchatapp.data.chat

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

//as soon as the device finds the device android will fire the broadcast the broadcast receiver will catch it
class FoundDeviceReceiver(
    private val onDeviceFound:(BluetoothDevice)->Unit
) :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        //here we send the name of bluetooth device
        when(intent?.action){
            BluetoothDevice.ACTION_FOUND->{
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )
                } else {
                    intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                device?.let(onDeviceFound)
            }
        }

    }


}