package com.example.bluetoothchatapp.data.chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.example.bluetoothchatapp.domain.chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context: Context
): BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    // this below consists of all the functionalities that are passed such as scanned,paired and etc
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter//null check as the device won't support bluetooth
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDevice>>
        get() = _scannedDevices.asStateFlow()
//here we use first private as it prevents from change and then it gets accessed out loud in the public
    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>> (emptyList () )
    override val pairedDevices: StateFlow<List<BluetoothDevice>>
        get() = _pairedDevices.asStateFlow()

    private var dataTransferService: BluetoothDataTransferService?=null

    private val _isConnected= MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private var currentServerSocket:BluetoothServerSocket?=null
    private var currentClientSocket:BluetoothSocket?=null

    private val foundDeviceReceiver=FoundDeviceReceiver{device->
        _scannedDevices.update {devices->
                val newDevice= device.toBluetoothClassDomain()
                if (newDevice in devices){
                    devices
                }else{
                    devices+newDevice
                }
        }

    }

    private val bluetoothReceiver = BluetoothReceiver{isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice)==false){
            _isConnected.update {
                isConnected
            }
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Can't connect a non-paired device.")
            }
        }
    }

    init {
        updatePairedDevices()
        context.registerReceiver(
            bluetoothReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )
    }
    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
            return
        }

        context.registerReceiver(//here we call the broadcastreceiver we defined in foundeviceReceiver
            foundDeviceReceiver,
            IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
        )


        updatePairedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
            return
        }
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
                throw SecurityException("No Bluetooth Connect")
            }

        currentServerSocket=bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
            "chat_service",
            UUID.fromString(SERVER_UUID)//they are just the
        )
            var shouldLoop = true
            while (shouldLoop){
                currentClientSocket = try {//this will be just looping till we get the client, accepting
                    //the client and everything
                    currentServerSocket?.accept()
                }catch (e:IOException){

                    shouldLoop=false
                    null
                }
                emit(
                    ConnectionResult.ConnectionEstablished
                )
                currentClientSocket?.let {
                    currentServerSocket?.close()
                    val service= BluetoothDataTransferService(it)
                    dataTransferService=service

                    emitAll(service
                        .listenForIncomingMessage()
                        .map {
                            ConnectionResult.TransferSuceeded(it)
                        })
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {//the device is the device we actually clicked on in the UI
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
                throw SecurityException("No Bluetooth Connect")
            }
            val bluetoothDevice=bluetoothAdapter?.getRemoteDevice(device.address)



            currentClientSocket= bluetoothDevice
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVER_UUID)
                )
            stopDiscovery()


            currentClientSocket?.let { socket->
                try {
                    socket.connect()
                    emit(
                        ConnectionResult.ConnectionEstablished
                    )

                    BluetoothDataTransferService(socket).also {
                        dataTransferService=it
                        emitAll(
                            it.listenForIncomingMessage().map {
                                ConnectionResult.TransferSuceeded(it)
                            }
                        )
                    }
                }catch (e:IOException){
                    socket.close()
                    emit(
                        ConnectionResult.Error("Connection was Interrupted")
                    )
                }

            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun trySendMessage(message: String): BluetoothMessage? {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
            return null
        }
        if (dataTransferService==null){
            return null
        }
        val bluetoothMessage=BluetoothMessage(
            message=message,
            senderName = bluetoothAdapter?.name?:"Unknown",
            isFromLocalUser = true
        )
        dataTransferService?.sendMessage(bluetoothMessage.toByteArray())
        return bluetoothMessage
    }

    override fun closeConnection() {
        currentServerSocket?.close()
        currentClientSocket?.close()
        currentClientSocket=null
        currentServerSocket=null
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothReceiver)
        closeConnection()
    }


    private fun updatePairedDevices(){ //it just updates the paired devices present in mobile.
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
            return
        }
        bluetoothAdapter
            ?.bondedDevices
            ?.map {  it.toBluetoothClassDomain()  }
            ?.also {devices->
                _pairedDevices.update { devices } }
    }

    private fun hasPermission(permission: String): Boolean{
        return context.checkSelfPermission((permission))==PackageManager.PERMISSION_GRANTED
    }

    companion object{
        const val SERVER_UUID="60a7e883-56df-4f80-bbec-d6ca7011c839"
    }
}