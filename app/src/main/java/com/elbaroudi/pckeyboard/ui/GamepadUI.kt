package com.elbaroudi.pckeyboard.ui

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GamepadUI(
    onButtonEvent: (Int, Boolean) -> Unit,
    onLeftStick: (Float, Float) -> Unit,
    onRightStick: (Float, Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        // Main layout with adjusted spacing
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Left Side ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // L1/L2 Triggers - in the middle, closer to center
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = 0.dp, y = (-40).dp), // Moved closer to center (from -24 to 0)
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GameButton(
                        label = "L2",
                        size = 64.dp,
                        fontSize = 14.sp,
                        onPress = { onButtonEvent(6, it) }
                    )
                    GameButton(
                        label = "L1",
                        size = 64.dp,
                        fontSize = 14.sp,
                        onPress = { onButtonEvent(4, it) }
                    )
                }

                // Left Stick - kept in good position
                Joystick(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 8.dp, y = (-16).dp),
                    size = 110f,
                    onMoved = onLeftStick
                )
                
                // ABXY Cluster on LEFT side - swapped with D-Pad
                ABXYClusterNintendo(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = 24.dp, y = (-8).dp), // Moved down and right
                    onButtonEvent = onButtonEvent
                )
            }

            // --- Center (Select, Start) - moved down ---
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 48.dp) // Moved down more
                ) {
                    GameButton(
                        label = "SELECT", 
                        size = 56.dp, 
                        fontSize = 11.sp,
                        onPress = { onButtonEvent(8, it) }
                    )
                    GameButton(
                        label = "START", 
                        size = 56.dp, 
                        fontSize = 11.sp,
                        onPress = { onButtonEvent(9, it) }
                    )
                }
            }

            // --- Right Side ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // R1/R2 Triggers - in the middle, closer to center
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 0.dp, y = (-40).dp), // Moved closer to center (from 24 to 0)
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GameButton(
                        label = "R1",
                        size = 64.dp,
                        fontSize = 14.sp,
                        onPress = { onButtonEvent(5, it) }
                    )
                    GameButton(
                        label = "R2",
                        size = 64.dp,
                        fontSize = 14.sp,
                        onPress = { onButtonEvent(7, it) }
                    )
                }

                // Right Stick - kept in good position
                Joystick(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-16).dp),
                    size = 110f,
                    onMoved = onRightStick
                )

                // D-Pad on RIGHT side - swapped with ABXY
                DPad(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = (-24).dp, y = (-8).dp), // Moved down and left
                    onButtonEvent = onButtonEvent
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameButton(
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    color: Color = Color(0xFF2D2D2D),
    onPress: (Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = tween(durationMillis = 100)
    )
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 4f else 8f,
        animationSpec = tween(durationMillis = 100)
    )
    val pressedColor = Color(0xFF404040)
    
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = elevation.dp,
                shape = CircleShape,
                clip = true
            )
            .clip(CircleShape)
            .background(if (isPressed) pressedColor else color)
            .border(
                width = if (isPressed) 3.dp else 2.dp,
                color = if (isPressed) Color(0xFF64B5F6) else Color(0xFF555555),
                shape = CircleShape
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isPressed = true
                        onPress(true)
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isPressed = false
                        onPress(false)
                        true
                    }
                    else -> false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label, 
            color = if (isPressed) Color(0xFF64B5F6) else Color.White,
            fontSize = fontSize, 
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun DPad(
    modifier: Modifier,
    onButtonEvent: (Int, Boolean) -> Unit
) {
    Box(modifier = modifier.size(140.dp)) {
        GameButton(
            label = "↑",
            modifier = Modifier.align(Alignment.TopCenter),
            size = 54.dp,
            color = Color(0xFF3A3A3A),
            onPress = { onButtonEvent(12, it) }
        )
        GameButton(
            label = "↓",
            modifier = Modifier.align(Alignment.BottomCenter),
            size = 54.dp,
            color = Color(0xFF3A3A3A),
            onPress = { onButtonEvent(13, it) }
        )
        GameButton(
            label = "←",
            modifier = Modifier.align(Alignment.CenterStart),
            size = 54.dp,
            color = Color(0xFF3A3A3A),
            onPress = { onButtonEvent(14, it) }
        )
        GameButton(
            label = "→",
            modifier = Modifier.align(Alignment.CenterEnd),
            size = 54.dp,
            color = Color(0xFF3A3A3A),
            onPress = { onButtonEvent(15, it) }
        )
    }
}

@Composable
fun ABXYClusterNintendo(
    modifier: Modifier,
    onButtonEvent: (Int, Boolean) -> Unit
) {
    Box(modifier = modifier.size(140.dp)) {
        // Top: X
        GameButton(
            label = "X",
            modifier = Modifier.align(Alignment.TopCenter),
            size = 54.dp,
            color = Color(0xFFB39D00),
            onPress = { onButtonEvent(2, it) }
        )
        // Bottom: B
        GameButton(
            label = "B",
            modifier = Modifier.align(Alignment.BottomCenter),
            size = 54.dp,
            color = Color(0xFF388E3C),
            onPress = { onButtonEvent(1, it) }
        )
        // Left: Y
        GameButton(
            label = "Y",
            modifier = Modifier.align(Alignment.CenterStart),
            size = 54.dp,
            color = Color(0xFF1976D2),
            onPress = { onButtonEvent(3, it) }
        )
        // Right: A
        GameButton(
            label = "A",
            modifier = Modifier.align(Alignment.CenterEnd),
            size = 54.dp,
            color = Color(0xFFD32F2F),
            onPress = { onButtonEvent(0, it) }
        )
    }
}