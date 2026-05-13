package com.elbaroudi.pckeyboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elbaroudi.pckeyboard.PcHidService

private enum class ControlMode {
    KEYBOARD,
    MOUSE
}

@Composable
fun ConnectedScreen(
    service: PcHidService?,
    lastLog: String
) {
    var mode by remember { mutableStateOf(ControlMode.KEYBOARD) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { mode = ControlMode.KEYBOARD },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mode == ControlMode.KEYBOARD) Color(0xFF6200EE) else Color(0xFF2D2D2D),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Keyboard", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.padding(horizontal = 6.dp))

            Button(
                onClick = { mode = ControlMode.MOUSE },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mode == ControlMode.MOUSE) Color(0xFF6200EE) else Color(0xFF2D2D2D),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Mouse", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            when (mode) {
                ControlMode.KEYBOARD -> KeyboardUI(service = service)
                ControlMode.MOUSE -> MouseUI(service = service)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Last Log: $lastLog",
            color = Color(0xFFFFD54F),
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
