package com.elbaroudi.pckeyboard.ui

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    size: Float = 200f,
    dotSize: Float = 50f,
    onMoved: (x: Float, y: Float) -> Unit
) {
    var knobPosition by remember { mutableStateOf(Offset.Zero) }
    var center by remember { mutableStateOf(Offset.Zero) }
    val radius = size / 2
    
    // Convert dp to px for internal calculation if needed, but drawing is largely relative
    
    Box(
        modifier = modifier
            .size(size.dp)
            .onGloballyPositioned { coordinates ->
                val localSize = coordinates.size
                center = Offset(localSize.width / 2f, localSize.height / 2f)
            }
            .pointerInput(Unit) {
               detectDragGestures(
                   onDragEnd = {
                       knobPosition = Offset.Zero
                       onMoved(0f, 0f)
                   },
                   onDragCancel = {
                       knobPosition = Offset.Zero
                       onMoved(0f, 0f)
                   }
               ) { change, dragAmount ->
                   change.consume()
                   
                   val currentPos = if (knobPosition == Offset.Zero) center else knobPosition + center
                   val newPos = currentPos + dragAmount
                   
                   val deltaX = newPos.x - center.x
                   val deltaY = newPos.y - center.y
                   val distance = hypot(deltaX, deltaY)
                   
                   val maxDistance = (size.dp.toPx() / 2) - (dotSize.dp.toPx() / 2) // Approximate bounds
                   
                   val actualX: Float
                   val actualY: Float
                   
                   if (distance <= maxDistance) {
                       actualX = deltaX
                       actualY = deltaY
                   } else {
                       val angle = atan2(deltaY, deltaX)
                       actualX = cos(angle) * maxDistance
                       actualY = sin(angle) * maxDistance
                   }
                   
                   knobPosition = Offset(actualX, actualY)
                   
                   // Normalize to -1.0 .. 1.0
                   val normalizedX = actualX / maxDistance
                   val normalizedY = actualY / maxDistance
                   onMoved(normalizedX, normalizedY)
               }
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Draw Base
            drawCircle(
                color = Color.DarkGray.copy(alpha = 0.5f),
                radius = size.dp.toPx() / 2
            )
            
            // Draw Knob
            val localCenter = Offset(size.dp.toPx() / 2, size.dp.toPx() / 2)
            drawCircle(
                color = Color(0xFF6200EE), // Example Primary Color
                radius = dotSize.dp.toPx() / 2,
                center = localCenter + knobPosition
            )
        }
    }
}
