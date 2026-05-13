package com.elbaroudi.pckeyboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elbaroudi.pckeyboard.HidKeyCodes
import com.elbaroudi.pckeyboard.PcHidService

@Composable
fun MouseUI(service: PcHidService?) {
    fun doubleLeftClick() {
        service?.leftClick()
        service?.leftClick()
    }

    fun copy() {
        service?.sendKey(HidKeyCodes.MOD_LEFT_CTRL, HidKeyCodes.KEY_C)
    }

    fun paste() {
        service?.sendKey(HidKeyCodes.MOD_LEFT_CTRL, HidKeyCodes.KEY_V)
    }

    var selectMode by remember { mutableStateOf(false) }
    var lastTapUptimeMs by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121212))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { selectMode = !selectMode },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectMode) Color(0xFF6200EE) else Color(0xFF2D2D2D),
                    contentColor = Color.White
                )
            ) {
                Text(if (selectMode) "Select ✓" else "Select", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(Color(0xFF1E1E1E))
                .pointerInput(selectMode) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val now = android.os.SystemClock.uptimeMillis()
                        val isDoubleTap = (now - lastTapUptimeMs) <= 300L
                        lastTapUptimeMs = now

                        var moved = false
                        var selecting = false

                        if (selectMode) {
                            selecting = true
                            service?.leftDown()
                        } else if (isDoubleTap) {
                            // Double click then hold left for drag selection.
                            doubleLeftClick()
                            selecting = true
                            service?.leftDown()
                        }

                        var lastPos = down.position
                        val pointerId = down.id

                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                            if (!change.pressed) break

                            val delta = change.position - lastPos
                            if (delta.x != 0f || delta.y != 0f) {
                                moved = true
                                val dx = (delta.x / 2f).toInt()
                                val dy = (delta.y / 2f).toInt()
                                if (dx != 0 || dy != 0) {
                                    service?.mouseMove(dx, dy)
                                }
                            }

                            lastPos = change.position
                            change.consumePositionChange()
                        }

                        if (selecting) {
                            service?.leftUp()
                        } else {
                            // No drag-selection: tap / double-tap.
                            if (!moved) {
                                if (isDoubleTap) doubleLeftClick() else service?.leftClick()
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Touchpad\nDrag = move\nDouble tap + drag = select\nSelect mode = drag to select",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { service?.leftClick() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Left", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Button(
                onClick = { service?.rightClick() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Right", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Button(
                onClick = { doubleLeftClick() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Double", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { service?.mouseScroll(20) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Scroll ↑", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Button(
                onClick = { service?.mouseScroll(-20) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Scroll ↓", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Button(
                onClick = { copy() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Copy", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { paste() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Paste", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.weight(2f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { service?.consumerPress(PcHidService.CONSUMER_VOLUME_DOWN) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Vol -", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Button(
                onClick = { service?.consumerPress(PcHidService.CONSUMER_VOLUME_UP) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Vol +", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { service?.consumerPress(PcHidService.CONSUMER_BRIGHTNESS_DOWN) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Bri -", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Button(
                onClick = { service?.consumerPress(PcHidService.CONSUMER_BRIGHTNESS_UP) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D2D), contentColor = Color.White)
            ) {
                Text("Bri +", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
