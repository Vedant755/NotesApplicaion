package com.example.bluetoothchatapp.data.chat

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

//as soon as the device finds the device android will fire the broadcast the broadcast receiver will catch it
class BluetoothReceiver(
    private val onStateChange:(isConnected:Boolean,BluetoothDevice)->Unit
) :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        //here we send the name of bluetooth device
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE,
                BluetoothDevice::class.java
            )
        } else {
            intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }
        when(intent?.action){
            BluetoothDevice.ACTION_ACL_CONNECTED->{

                onStateChange(true,device?:return)
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED->{
                onStateChange(false,device?:return)
            }
        }
    }
}