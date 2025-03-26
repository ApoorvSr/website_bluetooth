package org.example.app.bluetooth

import org.w3c.dom.events.Event
import kotlin.js.Promise

// External declarations for Web Bluetooth API in Kotlin/JS
external class BluetoothDevice {
    val name: String?
    val gatt: BluetoothRemoteGATTServer?
}

external class BluetoothRemoteGATTServer {
    fun connect(): Promise<BluetoothRemoteGATTServer>
    fun getPrimaryService(uuid: String): Promise<BluetoothRemoteGATTService>
}

external class BluetoothRemoteGATTService {
    fun getCharacteristic(uuid: String): Promise<BluetoothRemoteGATTCharacteristic>
}

external class BluetoothRemoteGATTCharacteristic {
    val value: DataView?
    fun writeValue(value: Uint8Array): Promise<Unit>
    fun readValue(): Promise<DataView>
    fun startNotifications(): Promise<Unit>
    fun addEventListener(type: String, callback: (Event) -> Unit)
}

external class DataView {
    val buffer: ArrayBuffer
}

external class Uint8Array(buffer: ArrayBuffer)
external class ArrayBuffer
