package com.example.bluetoothchatapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetoothchatapp.domain.chat.BluetoothDevice

@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onClick:(BluetoothDevice)->Unit,
    modifier: Modifier=Modifier
)
{
    LazyColumn(
        modifier = modifier
    ){
        item { 
            Text(
                text = "Paired Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(pairedDevices){devices->
            Text(
                text = devices.name?:"No name",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { devices }
                    .padding(16.dp)
            )
        }
        item {
            Text(
                text = "Scanned Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(scannedDevices){devices->
            Text(
                text = devices.name?:"No name",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { devices }
                    .padding(16.dp)
            )
        }
    }
}