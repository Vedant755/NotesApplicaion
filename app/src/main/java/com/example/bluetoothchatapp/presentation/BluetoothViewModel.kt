package com.example.bluetoothchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothchatapp.domain.chat.BluetoothController
import com.example.bluetoothchatapp.domain.chat.BluetoothDeviceDomain
import com.example.bluetoothchatapp.domain.chat.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
):ViewModel() {

    private val _state = MutableStateFlow(BluetoothUIState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ){ scannedDevices,pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices=pairedDevices,
            message = if (state.isConnected) state.message else emptyList()
        )
        //stateIn just converts the normal flow into stateflow and caches the latest value
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),_state.value)

    private var deviceCOntrollerJob: Job?=null

    init {
        bluetoothController.isConnected.onEach {isConnected->
            _state.update {
                it.copy(isConnected=isConnected)
            }
        }.launchIn(viewModelScope)
        bluetoothController.errors.onEach { errorMessage->
            _state.update {
                it.copy(
                    error=errorMessage
                )
            }
        }.launchIn(viewModelScope)
    }

    fun connectToDevice(device:BluetoothDeviceDomain){
        _state.update { it.copy(isConnecting = true) }
        deviceCOntrollerJob= bluetoothController.connectToDevice(device).listen()
    }

    fun waitingforIncomingConnection(){
        _state.update {
            it.copy(
                isConnecting = true
            )
        }
        deviceCOntrollerJob=bluetoothController
            .startBluetoothServer()
            .listen()
    }

    fun disconnectFromDevice(){
        deviceCOntrollerJob?.cancel()
        bluetoothController.closeConnection()
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false
            )
        }
    }

    fun sendMessage(message:String){
        viewModelScope.launch {
            val bluetoothMessage=bluetoothController.trySendMessage(message)
            if (bluetoothMessage!=null){
                _state.update {
                    it.copy(
                        message=it.message+bluetoothMessage
                    )
                }
            }
        }
    }


    fun startScan(){
        bluetoothController.startDiscovery()
    }
    fun stopScan(){
        bluetoothController.stopDiscovery()
    }

    private fun Flow<ConnectionResult>.listen():Job{
        return onEach {result->
            when(result){
                ConnectionResult.ConnectionEstablished -> {
                    _state.update {it.copy(
                        isConnected = true,
                        isConnecting = false,
                        error = null
                    )
                    }
                }
                is ConnectionResult.TransferSuceeded->{
                    _state.update {
                        it.copy(
                            message = it.message+result.message
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {it.copy(
                        isConnected = false,
                        isConnecting = false,
                        error = result.message
                    )
                }
            }

        }
    }
            .catch {throwable->
                bluetoothController.closeConnection()
                _state.update {
                    it.copy(
                        isConnected = false,
                        isConnecting = false,
                    )
                }

            }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }
}