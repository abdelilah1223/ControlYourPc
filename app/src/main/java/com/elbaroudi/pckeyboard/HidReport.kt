package com.elbaroudi.pckeyboard

import java.nio.ByteBuffer

data class KeyboardHidReport(
    var modifiers: Byte = 0,
    var keyCodes: ByteArray = ByteArray(6)
) {
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(8)
        buffer.put(modifiers)
        buffer.put(0)
        buffer.put(keyCodes.copyOf(6))
        return buffer.array()
    }
}

data class MouseHidReport(
    var buttons: Byte = 0,
    var x: Byte = 0,
    var y: Byte = 0,
    var wheel: Byte = 0
) {
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(4)
        buffer.put(buttons)
        buffer.put(x)
        buffer.put(y)
        buffer.put(wheel)
        return buffer.array()
    }
}

data class ConsumerHidReport(
    var usage: Short = 0
) {
    fun toByteArray(): ByteArray {
        val buffer = ByteBuffer.allocate(2)
        buffer.put((usage.toInt() and 0xFF).toByte())
        buffer.put(((usage.toInt() shr 8) and 0xFF).toByte())
        return buffer.array()
    }
}
