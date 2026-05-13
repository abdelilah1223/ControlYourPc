package com.elbaroudi.pckeyboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    isRegistered: Boolean,
    lastLog: String,
    onMakeDiscoverable: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
             // Icon / Graphic
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(if (isRegistered) Color(0xFF4CAF50) else Color(0xFF212121)),
                contentAlignment = Alignment.Center
            ) {
                 CircularProgressIndicator(
                     color = if (isRegistered) Color.White else Color(0xFF6200EE),
                     modifier = Modifier.size(60.dp)
                 )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = if (isRegistered) "Ready to Connect!" else "Initializing HID Service...",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "HID Status: ${if (isRegistered) "Registered ✅" else "Not Registered ❌ restrat the app or wait until we turn on "}",
                color = if (isRegistered) Color.Green else Color.Red,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isRegistered) {
                    "✅ HID is registered!\n\n" +
                    "On your PC/Console:\n" +
                    "1. Open Bluetooth settings or scroll down and click in make me visible button \n" +
                    "3. Look for 'Simple Gamepad' or your phone name\n" +
                    "4.connect to your phone  from PC/Console side \n" +
                    "5. The connection will appear automatically here"
                } else {
                    "1. Wait for HID registration or just close and open the app ...\n" +
                    "2. Tap 'Make Visible' below\n" +
                    "3. try to connect to this phone from another device like android  PC/Console\n" +
                    "4. Pair from PC/Console side"
                },
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Always show the button - it's essential for device discovery
            androidx.compose.material3.Button(
                onClick = onMakeDiscoverable,
                enabled = true, // Always enabled
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (isRegistered) Color(0xFF6200EE) else Color(0xFF6200EE),
                    disabledContainerColor = Color(0xFF6200EE)
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = if (isRegistered) {
                        "Make Visible (5 min) - REQUIRED!"
                    } else {
                        "Make Visible (5 min)"
                    },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isRegistered) {
                    "⚠️ IMPORTANT: Tap 'Make Visible' to show device in Bluetooth scan!"
                } else {
                    "ℹ️ Tap 'Make Visible' when HID is registered to make device discoverable"
                },
                color = if (isRegistered) Color(0xFFFF9800) else Color(0xFF9E9E9E),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = if (isRegistered) FontWeight.Bold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Last Log: $lastLog",
                color = Color.Yellow,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
