package com.elbaroudi.pckeyboard.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.elbaroudi.pckeyboard.HidKeyCodes
import com.elbaroudi.pckeyboard.PcHidService

private enum class KeyboardLanguage {
    EN,
    AR
}

@Composable
fun KeyboardUI(service: PcHidService?) {
    var textInput by remember { mutableStateOf("") }
    var currentTab by remember { mutableStateOf(0) } // 0: Keyboard, 1: Media
    var language by remember { mutableStateOf(KeyboardLanguage.EN) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {
        // Status Bar
        StatusHeader(service)

        // Text Input Preview
        TextPreview(textInput) { textInput = it }

        // Tabs
        Tabs(currentTab) { currentTab = it }

        // Content
        when (currentTab) {
            0 -> KeyboardTab(service, language) { language = it }
            1 -> MediaTab(service)
        }
    }
}

@Composable
private fun StatusHeader(
    service: PcHidService?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📱 Bluetooth Keyboard",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .background(
                        color = if (service != null) Color(0xFF00C853) else Color(0xFFD32F2F),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (service != null) "CONNECTED" else "DISCONNECTED",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TextPreview(text: String, onTextChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Black, RoundedCornerShape(12.dp))
            .border(2.dp, Color(0xFF333333), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 20.sp
            ),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxSize()) {
                    if (text.isEmpty()) {
                        Text(
                            text = "Type here or use keyboard below...\nText appears on connected PC",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun Tabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf("⌨️ Keyboard", "🎵 Media").forEachIndexed { index, title ->
            TabButton(
                title = title,
                isSelected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
private fun RowScope.TabButton(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF6200EE) else Color(0xFF2D2D2D))
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun KeyboardTab(
    service: PcHidService?,
    language: KeyboardLanguage,
    onLanguageChange: (KeyboardLanguage) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
        // Numbers row - Mobile keyboard style (with symbols)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").forEach { key ->
                KeyButton(
                    label = key,
                    modifier = Modifier.weight(1f),
                    onClick = { sendKeyForChar(key, language, service) }
                )
            }
            KeyButton(
                label = "⌫",
                modifier = Modifier.weight(1.2f),
                onClick = { service?.sendKey(0.toByte(), HidKeyCodes.KEY_BACKSPACE) }
            )
        }

        // First row of letters - Standard QWERTY for EN
        val row1 = if (language == KeyboardLanguage.EN) {
            // Standard QWERTY top row
            listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
        } else {
            // Standard Arabic phone keyboard first row
            listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "د")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row1.forEach { key ->
                KeyButton(
                    label = key,
                    modifier = Modifier.weight(1f),
                    onClick = { sendKeyForChar(key, language, service) }
                )
            }
        }

        // Second row of letters
        val row2 = if (language == KeyboardLanguage.EN) {
            // Standard QWERTY home row
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
        } else {
            // Standard Arabic phone keyboard second row
            listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row2.forEach { key ->
                KeyButton(
                    label = key,
                    modifier = Modifier.weight(1f),
                    onClick = { sendKeyForChar(key, language, service) }
                )
            }
            if (language == KeyboardLanguage.EN) {
                KeyButton(
                    label = "'",
                    modifier = Modifier.weight(1f),
                    onClick = { sendKeyForChar("'", language, service) }
                )
            }
        }

        // Third row of letters
        val row3 = if (language == KeyboardLanguage.EN) {
            // Standard QWERTY bottom row
            listOf("z", "x", "c", "v", "b", "n", "m")
        } else {
            // Standard Arabic phone keyboard third row
            listOf("ئ", "ء", "ؤ", "ر", "لا", "ى", "ة", "و", "ز", "ظ")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (language == KeyboardLanguage.EN) {
                KeyButton(
                    label = "↑",
                    modifier = Modifier.weight(1.2f),
                    onClick = {
                        // Shift functionality - for now just send uppercase letters
                        // You'll need to implement proper shift handling
                    }
                )
            } else {
                // For Arabic, we need shift-like key to access additional characters
                KeyButton(
                    label = "⇧",
                    modifier = Modifier.weight(1f),
                    onClick = { /* Shift functionality for Arabic */ }
                )
            }

            row3.forEach { key ->
                KeyButton(
                    label = key,
                    modifier = Modifier.weight(1f),
                    onClick = { sendKeyForChar(key, language, service) }
                )
            }

            if (language == KeyboardLanguage.EN) {
                KeyButton(
                    label = ",",
                    modifier = Modifier.weight(1f),
                    onClick = { sendKeyForChar(",", language, service) }
                )
                KeyButton(
                    label = ".",
                    modifier = Modifier.weight(1f),
                    onClick = { sendKeyForChar(".", language, service) }
                )
                KeyButton(
                    label = "↑",
                    modifier = Modifier.weight(1.2f),
                    onClick = { /* Shift functionality */ }
                )
            } else {
                KeyButton(
                    label = "،",
                    modifier = Modifier.weight(1f),
                    onClick = { sendKeyForChar("،", language, service) }
                )
                KeyButton(
                    label = "؟",
                    modifier = Modifier.weight(1f),
                    onClick = { sendKeyForChar("؟", language, service) }
                )
            }
        }
        
        // Special Characters Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(".", ",", "'", "?", "!", "@", "_", "-").forEach { key ->
                KeyButton(
                    label = key,
                    modifier = Modifier.weight(1f),
                    onClick = { sendKeyForChar(key, language, service) }
                )
            }
        }

        // Bottom row - Function keys
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyButton(
                label = if (language == KeyboardLanguage.EN) "123" else "١٢٣",
                modifier = Modifier.weight(1.2f),
                onClick = {
                    // Switch to numbers/symbols keyboard
                    // You can implement this by adding another keyboard state
                }
            )
            KeyButton(
                label = if (language == KeyboardLanguage.EN) "EN" else "العربية",
                modifier = Modifier.weight(1.5f),
                onClick = {
                    onLanguageChange(if (language == KeyboardLanguage.EN) KeyboardLanguage.AR else KeyboardLanguage.EN)
                }
            )
            KeyButton(
                label = "🌐",
                modifier = Modifier.weight(1f),
                onClick = { /* Emoji/Globe functionality */ }
            )
            KeyButton(
                label = "Space",
                modifier = Modifier.weight(3f),
                onClick = { service?.sendKey(0.toByte(), HidKeyCodes.KEY_SPACE) }
            )
            KeyButton(
                label = "↵",
                modifier = Modifier.weight(1.5f),
                onClick = { service?.sendKey(0.toByte(), HidKeyCodes.KEY_ENTER) }
            )
        }
    }
}
}

@Composable
private fun MediaTab(service: PcHidService?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Volume controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Volume",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MediaButton("🔇", "Mute", Color(0xFF757575)) {
                    // Mute implementation
                }
                MediaButton("🔉", "Vol-", Color(0xFF2196F3)) {
                    service?.consumerPress(PcHidService.CONSUMER_VOLUME_DOWN.toShort())
                }
                MediaButton("🔊", "Vol+", Color(0xFF2196F3)) {
                    service?.consumerPress(PcHidService.CONSUMER_VOLUME_UP.toShort())
                }
            }
        }

        // Playback controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Playback",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MediaButton("⏮️", "Prev", Color(0xFF4CAF50)) {
                    // Previous track
                }
                MediaButton("⏯️", "Play", Color(0xFF4CAF50)) {
                    // Play/Pause
                }
                MediaButton("⏭️", "Next", Color(0xFF4CAF50)) {
                    // Next track
                }
            }
        }

        // Brightness controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Brightness",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MediaButton("🔅", "Bright-", Color(0xFFFF9800)) {
                    service?.consumerPress(PcHidService.CONSUMER_BRIGHTNESS_DOWN.toShort())
                }
                MediaButton("🔆", "Bright+", Color(0xFFFF9800)) {
                    service?.consumerPress(PcHidService.CONSUMER_BRIGHTNESS_UP.toShort())
                }
            }
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(60.dp), // Increased height
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF2D2D2D),
        shadowElevation = 4.dp,
        tonalElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 18.sp, // Increased font size
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RowScope.MediaButton(icon: String, label: String, color: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = icon,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun sendKeyForChar(label: String, language: KeyboardLanguage, service: PcHidService?) {
    // For lowercase English letters, we need to handle them properly
    val englishKeyLabel = if (language == KeyboardLanguage.EN) {
        label.uppercase()
    } else {
        // Map Arabic characters to their corresponding Latin keyboard positions
        // This follows the standard Arabic phone keyboard layout
        when (label) {
            // First row (Q-W-E-R-T-Y-U-I-O-P)
            "ض" -> "Q"
            "ص" -> "W"
            "ث" -> "E"
            "ق" -> "R"
            "ف" -> "T"
            "غ" -> "Y"
            "ع" -> "U"
            "ه" -> "I"
            "خ" -> "O"
            "ح" -> "P"
            "ج" -> "["
            "د" -> "]"

            // Second row (A-S-D-F-G-H-J-K-L)
            "ش" -> "A"
            "س" -> "S"
            "ي" -> "D"
            "ب" -> "F"
            "ل" -> "G"
            "ا" -> "H"
            "ت" -> "J"
            "ن" -> "K"
            "م" -> "L"
            "ك" -> ";"
            "ط" -> "'"

            // Third row (Z-X-C-V-B-N-M)
            "ئ" -> "Z"
            "ء" -> "X"
            "ؤ" -> "C"
            "ر" -> "V"
            "لا" -> "B"
            "ى" -> "N"
            "ة" -> "M"
            "و" -> ","
            "ز" -> "."
            "ظ" -> "/"

            // Arabic punctuation
            "،" -> ","
            "؟" -> "/"

            else -> label.uppercase()
        }
    }

    val keyCode = when (englishKeyLabel) {
        "1" -> HidKeyCodes.KEY_1
        "2" -> HidKeyCodes.KEY_2
        "3" -> HidKeyCodes.KEY_3
        "4" -> HidKeyCodes.KEY_4
        "5" -> HidKeyCodes.KEY_5
        "6" -> HidKeyCodes.KEY_6
        "7" -> HidKeyCodes.KEY_7
        "8" -> HidKeyCodes.KEY_8
        "9" -> HidKeyCodes.KEY_9
        "0" -> HidKeyCodes.KEY_0
        "Q" -> HidKeyCodes.KEY_Q
        "W" -> HidKeyCodes.KEY_W
        "E" -> HidKeyCodes.KEY_E
        "R" -> HidKeyCodes.KEY_R
        "T" -> HidKeyCodes.KEY_T
        "Y" -> HidKeyCodes.KEY_Y
        "U" -> HidKeyCodes.KEY_U
        "I" -> HidKeyCodes.KEY_I
        "O" -> HidKeyCodes.KEY_O
        "P" -> HidKeyCodes.KEY_P
        "[" -> HidKeyCodes.KEY_LEFT_BRACKET
        "]" -> HidKeyCodes.KEY_RIGHT_BRACKET
        "A" -> HidKeyCodes.KEY_A
        "S" -> HidKeyCodes.KEY_S
        "D" -> HidKeyCodes.KEY_D
        "F" -> HidKeyCodes.KEY_F
        "G" -> HidKeyCodes.KEY_G
        "H" -> HidKeyCodes.KEY_H
        "J" -> HidKeyCodes.KEY_J
        "K" -> HidKeyCodes.KEY_K
        "L" -> HidKeyCodes.KEY_L
        ";" -> HidKeyCodes.KEY_SEMICOLON
        "'" -> HidKeyCodes.KEY_APOSTROPHE
        "Z" -> HidKeyCodes.KEY_Z
        "X" -> HidKeyCodes.KEY_X
        "C" -> HidKeyCodes.KEY_C
        "V" -> HidKeyCodes.KEY_V
        "B" -> HidKeyCodes.KEY_B
        "N" -> HidKeyCodes.KEY_N
        "M" -> HidKeyCodes.KEY_M
        "," -> HidKeyCodes.KEY_COMMA
        "." -> HidKeyCodes.KEY_DOT
        "/" -> HidKeyCodes.KEY_SLASH
        else -> return
    }

    service?.sendKey(0.toByte(), keyCode)
}