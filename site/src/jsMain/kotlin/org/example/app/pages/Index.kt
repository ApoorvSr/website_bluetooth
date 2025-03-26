package org.example.app.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.forms.Button
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.example.app.bluetooth.connectToBluetoothDevice
import org.example.app.bluetooth.observeCharacteristic

@Page
@Composable
fun Index() {
    var connectionStatus by remember { mutableStateOf("Click to pair with Bluetooth device") }
    var mode9Data by remember { mutableStateOf("Waiting for Mode 9 data...") }
    var isConnected by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var foundCharacteristics by remember { mutableStateOf<List<dynamic>>(emptyList()) }

    val scope = rememberCoroutineScope()

    Div {
        // ✅ Bluetooth Pairing Button
        Button(
            onClick = {
                if (!isConnected && !isScanning) {
                    isScanning = true
                    connectionStatus = "Scanning for Bluetooth device..."

                    connectToBluetoothDevice(
                        onConnectionStatus = { status ->
                            connectionStatus = status
                            if (status.startsWith("Connected")) {
                                isConnected = true
                            }
                            isScanning = false
                        },
                        
                    )
                }
            },
            enabled = !isConnected && !isScanning
        ) {
            Text(
                when {
                    isConnected -> "Connected ✅"
                    isScanning -> "Scanning..."
                    else -> "Pair Bluetooth Device"
                }
            )
        }

        // ✅ Connection Status
        P { Text("Status: $connectionStatus") }

        // ✅ Real-Time Mode 9 Data Display
        P { Text("Real-Time Mode 9 Data: $mode9Data") }

        // ✅ List Found Characteristics (For Debugging)
        if (foundCharacteristics.isNotEmpty()) {
            P { Text("Discovered Characteristics:") }
            foundCharacteristics.forEach { characteristic ->
                P {
                    Text("• ${characteristic.uuid} (Read: ${characteristic.properties.read}, Write: ${characteristic.properties.write})")
                }
            }
        }
    }
}
