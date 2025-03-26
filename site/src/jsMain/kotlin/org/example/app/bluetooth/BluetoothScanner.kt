package org.example.app.bluetooth

import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val SERVICE_UUID = "0000acf0-0000-1000-8000-00805f9b34fb"

fun connectToBluetoothDevice(
    onConnectionStatus: (String) -> Unit
) {
    val bluetooth: dynamic = window.navigator.asDynamic().bluetooth

    if (bluetooth == undefined) {
        onConnectionStatus("Error: Web Bluetooth API not supported.")
        console.error("Error: Web Bluetooth API is not supported in this browser.")
        return
    }

    val options = js("""
        {
            acceptAllDevices: false,
            filters: [{ services: ["0000acf0-0000-1000-8000-00805f9b34fb"] }]
        }
    """)

    bluetooth.requestDevice(options)
        .then({ device: dynamic ->
            val deviceName = device.name?.toString() ?: "Unnamed Device"
            onConnectionStatus("Connecting to $deviceName...")
            connectToGattServer(device, onConnectionStatus)
        }, { error: dynamic ->
            handleBluetoothError(error, onConnectionStatus)
        })
        .catch { error: dynamic ->
            handleBluetoothError(error, onConnectionStatus)
        }
}

private fun connectToGattServer(
    device: dynamic,
    onConnectionStatus: (String) -> Unit
) {
    if (device == null) {
        onConnectionStatus("Error: No device selected.")
        return
    }

    val gatt = device.gatt ?: run {
        onConnectionStatus("Error: GATT not available.")
        return
    }

    gatt.connect()
        .then({ gattServer: dynamic ->
            onConnectionStatus("Connected to ${device.name ?: "Unnamed Device"}")
            return@then gattServer.getPrimaryService(SERVICE_UUID)
        })
        .then({ service: dynamic ->
            console.log("Discovered service: ${service.uuid}")
            return@then service.getCharacteristics()
        })
        .then({ characteristics: Array<dynamic> ->
            console.log("Found ${characteristics.size} characteristics.")

            var writeCharacteristic: dynamic = null
            var readCharacteristic: dynamic = null

            characteristics.forEach { characteristic ->
                val uuid = characteristic.uuid
                val properties = characteristic.properties

                val isReadable = properties.read == true
                val isWritable = properties.write == true

                if (isWritable) {
                    writeCharacteristic = characteristic
                    console.log("✍️ Write Characteristic: $uuid")
                }
                if (isReadable) {
                    readCharacteristic = characteristic
                    console.log("📖 Read Characteristic: $uuid")
                }
            }

            if (writeCharacteristic != null) {
                writeToCharacteristic(writeCharacteristic)
            } else {
                console.error("❌ No writable characteristic found.")
            }

            if (readCharacteristic != null) {
                observeCharacteristic(readCharacteristic)
            } else {
                console.error("❌ No readable characteristic found.")
            }
        })
        .catch { error: dynamic ->
            handleBluetoothError(error, onConnectionStatus)
        }
}

// ✅ Write Data to the Write Characteristic
private fun writeToCharacteristic(characteristic: dynamic) {
    val encoder = js("new TextEncoder()")
    val data = encoder.encode("mode 9;")
    
    characteristic.writeValue(data)
        .then {
            console.log("✅ Successfully wrote 'mode 9;' to characteristic ${characteristic.uuid}")
        }
        .catch { error: dynamic ->
            console.error("❌ Failed to write to characteristic: ${error.message}")
        }
}

// ✅ Observe Notifications from Read Characteristic
public fun observeCharacteristic(characteristic: dynamic) {
    characteristic.startNotifications()
        .then {
            console.log("✅ Started notifications on ${characteristic.uuid}")
            val listener = { event: dynamic ->
                try {
                    val value = event.target.value
                    val decoder = js("new TextDecoder('utf-8')")
                    val dataString = decoder.decode(value)
                    console.log("📥 Received: $dataString")
                } catch (e: Exception) {
                    console.error("❌ Error decoding data: ${e.message}")
                }
            }
            characteristic.addEventListener("characteristicvaluechanged", listener)
        }
        .catch { error: dynamic ->
            console.error("❌ Failed to start notifications: ${error.message}")
        }
}

// 🛠️ Handle Bluetooth Errors
private fun handleBluetoothError(error: dynamic, onConnectionStatus: (String) -> Unit) {
    val errorMessage = error?.message?.toString() ?: "Unknown error"
    if (errorMessage.contains("NotFoundError")) {
        onConnectionStatus("Device selection cancelled.")
    } else {
        onConnectionStatus("Error: $errorMessage")
    }
}
