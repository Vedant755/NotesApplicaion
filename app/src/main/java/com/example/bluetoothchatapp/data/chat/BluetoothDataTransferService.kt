package com.example.bluetoothchatapp.data.chat

import android.bluetooth.BluetoothSocket
import com.example.bluetoothchatapp.domain.chat.BluetoothMessage
import com.example.bluetoothchatapp.domain.chat.ConnectionResult
import com.example.bluetoothchatapp.domain.chat.TransferFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    fun listenForIncomingMessage():Flow<BluetoothMessage>{
        return flow {
            if (!socket.isConnected){
                return@flow
            }
            val buffer=ByteArray(1024)
            while (true){
                val byteCount = try {
                    socket.inputStream.read(buffer)//this returns the amount of byte that are read
                }catch (e:IOException){
                    throw TransferFailedException()
                }
                emit(
                     buffer.decodeToString(
                            endIndex = byteCount
                        ).tobluetoothMessage(
                            isFromLocalUser = false
                        )
                    )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(byteArray: ByteArray):Boolean{
        return withContext(Dispatchers.IO){
            try {
                socket.outputStream.write(byteArray)
            }catch (e:IOException){
                e.printStackTrace()
                return@withContext false
            }
            true
        }
    }
}