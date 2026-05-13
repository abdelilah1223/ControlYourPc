package com.elbaroudi.pckeyboard

object HidKeyCodes {
    // Modifier keys
    const val MOD_LEFT_CTRL: Byte = 0x01
    const val MOD_LEFT_SHIFT: Byte = 0x02
    const val MOD_LEFT_ALT: Byte = 0x04
    const val MOD_LEFT_GUI: Byte = 0x08
    const val MOD_RIGHT_CTRL: Byte = 0x10
    const val MOD_RIGHT_SHIFT: Byte = 0x20
    const val MOD_RIGHT_ALT: Byte = 0x40
    const val MOD_RIGHT_GUI: Byte = 0x80.toByte()

    // Alphabet keys
    const val KEY_A: Byte = 0x04
    const val KEY_B: Byte = 0x05
    const val KEY_C: Byte = 0x06
    const val KEY_D: Byte = 0x07
    const val KEY_E: Byte = 0x08
    const val KEY_F: Byte = 0x09
    const val KEY_G: Byte = 0x0A
    const val KEY_H: Byte = 0x0B
    const val KEY_I: Byte = 0x0C
    const val KEY_J: Byte = 0x0D
    const val KEY_K: Byte = 0x0E
    const val KEY_L: Byte = 0x0F
    const val KEY_M: Byte = 0x10
    const val KEY_N: Byte = 0x11
    const val KEY_O: Byte = 0x12
    const val KEY_P: Byte = 0x13
    const val KEY_Q: Byte = 0x14
    const val KEY_R: Byte = 0x15
    const val KEY_S: Byte = 0x16
    const val KEY_T: Byte = 0x17
    const val KEY_U: Byte = 0x18
    const val KEY_V: Byte = 0x19
    const val KEY_W: Byte = 0x1A
    const val KEY_X: Byte = 0x1B
    const val KEY_Y: Byte = 0x1C
    const val KEY_Z: Byte = 0x1D

    // Number keys
    const val KEY_1: Byte = 0x1E
    const val KEY_2: Byte = 0x1F
    const val KEY_3: Byte = 0x20
    const val KEY_4: Byte = 0x21
    const val KEY_5: Byte = 0x22
    const val KEY_6: Byte = 0x23
    const val KEY_7: Byte = 0x24
    const val KEY_8: Byte = 0x25
    const val KEY_9: Byte = 0x26
    const val KEY_0: Byte = 0x27

    // Basic keys
    const val KEY_ENTER: Byte = 0x28
    const val KEY_ESC: Byte = 0x29
    const val KEY_BACKSPACE: Byte = 0x2A
    const val KEY_TAB: Byte = 0x2B
    const val KEY_SPACE: Byte = 0x2C

    // Punctuation and symbols
    const val KEY_MINUS: Byte = 0x2D
    const val KEY_EQUAL: Byte = 0x2E
    const val KEY_LEFT_BRACKET: Byte = 0x2F
    const val KEY_RIGHT_BRACKET: Byte = 0x30
    const val KEY_BACKSLASH: Byte = 0x31
    const val KEY_HASH: Byte = 0x32          // # (for UK keyboards)
    const val KEY_SEMICOLON: Byte = 0x33
    const val KEY_APOSTROPHE: Byte = 0x34
    const val KEY_GRAVE: Byte = 0x35         // `
    const val KEY_COMMA: Byte = 0x36
    const val KEY_DOT: Byte = 0x37
    const val KEY_SLASH: Byte = 0x38

    // Lock keys
    const val KEY_CAPS_LOCK: Byte = 0x39
    const val KEY_NUM_LOCK: Byte = 0x53
    const val KEY_SCROLL_LOCK: Byte = 0x47

    // Function keys
    const val KEY_F1: Byte = 0x3A
    const val KEY_F2: Byte = 0x3B
    const val KEY_F3: Byte = 0x3C
    const val KEY_F4: Byte = 0x3D
    const val KEY_F5: Byte = 0x3E
    const val KEY_F6: Byte = 0x3F
    const val KEY_F7: Byte = 0x40
    const val KEY_F8: Byte = 0x41
    const val KEY_F9: Byte = 0x42
    const val KEY_F10: Byte = 0x43
    const val KEY_F11: Byte = 0x44
    const val KEY_F12: Byte = 0x45

    // System keys
    const val KEY_PRINT_SCREEN: Byte = 0x46
    const val KEY_PAUSE: Byte = 0x48
    const val KEY_INSERT: Byte = 0x49
    const val KEY_HOME: Byte = 0x4A
    const val KEY_PAGE_UP: Byte = 0x4B
    const val KEY_DELETE: Byte = 0x4C
    const val KEY_END: Byte = 0x4D
    const val KEY_PAGE_DOWN: Byte = 0x4E

    // Arrow keys
    const val KEY_RIGHT: Byte = 0x4F
    const val KEY_LEFT: Byte = 0x50
    const val KEY_DOWN: Byte = 0x51
    const val KEY_UP: Byte = 0x52

    // Numpad keys
    const val KEY_KP_SLASH: Byte = 0x54      // Numpad /
    const val KEY_KP_ASTERISK: Byte = 0x55   // Numpad *
    const val KEY_KP_MINUS: Byte = 0x56      // Numpad -
    const val KEY_KP_PLUS: Byte = 0x57       // Numpad +
    const val KEY_KP_ENTER: Byte = 0x58      // Numpad Enter
    const val KEY_KP_1: Byte = 0x59          // Numpad 1
    const val KEY_KP_2: Byte = 0x5A          // Numpad 2
    const val KEY_KP_3: Byte = 0x5B          // Numpad 3
    const val KEY_KP_4: Byte = 0x5C          // Numpad 4
    const val KEY_KP_5: Byte = 0x5D          // Numpad 5
    const val KEY_KP_6: Byte = 0x5E          // Numpad 6
    const val KEY_KP_7: Byte = 0x5F          // Numpad 7
    const val KEY_KP_8: Byte = 0x60          // Numpad 8
    const val KEY_KP_9: Byte = 0x61          // Numpad 9
    const val KEY_KP_0: Byte = 0x62          // Numpad 0
    const val KEY_KP_DOT: Byte = 0x63        // Numpad .

    // Multimedia keys
    const val KEY_MENU: Byte = 0x65          // Right-click menu key
    const val KEY_POWER: Byte = 0x66         // Power key

    // Media keys (Consumer Page)
    const val KEY_MEDIA_PLAY: Byte = 0xB0.toByte()
    const val KEY_MEDIA_PAUSE: Byte = 0xB1.toByte()
    const val KEY_MEDIA_RECORD: Byte = 0xB2.toByte()
    const val KEY_MEDIA_FAST_FORWARD: Byte = 0xB3.toByte()
    const val KEY_MEDIA_REWIND: Byte = 0xB4.toByte()
    const val KEY_MEDIA_NEXT: Byte = 0xB5.toByte()
    const val KEY_MEDIA_PREV: Byte = 0xB6.toByte()
    const val KEY_MEDIA_STOP: Byte = 0xB7.toByte()
    const val KEY_MEDIA_EJECT: Byte = 0xB8.toByte()
    const val KEY_MEDIA_RANDOM_PLAY: Byte = 0xB9.toByte()
    const val KEY_MEDIA_PLAY_PAUSE: Byte = 0xCD.toByte()
    const val KEY_MEDIA_MUTE: Byte = 0xE2.toByte()
    const val KEY_VOLUME_UP: Byte = 0xE9.toByte()
    const val KEY_VOLUME_DOWN: Byte = 0xEA.toByte()

    // System control keys
    const val KEY_SYSTEM_POWER: Byte = 0x81.toByte()
    const val KEY_SYSTEM_SLEEP: Byte = 0x82.toByte()
    const val KEY_SYSTEM_WAKE: Byte = 0x83.toByte()

    // International keys
    const val KEY_NON_US_BACKSLASH: Byte = 0x64  // Non-US \ and |
    const val KEY_APPLICATION: Byte = 0x65       // Application/Menu key

    // Language keys
    const val KEY_INTERNATIONAL1: Byte = 0x87.toByte()  // Japanese keyboard
    const val KEY_INTERNATIONAL2: Byte = 0x88.toByte()
    const val KEY_INTERNATIONAL3: Byte = 0x89.toByte()
    const val KEY_INTERNATIONAL4: Byte = 0x8A.toByte()
    const val KEY_INTERNATIONAL5: Byte = 0x8B.toByte()
    const val KEY_INTERNATIONAL6: Byte = 0x8C.toByte()
    const val KEY_INTERNATIONAL7: Byte = 0x8D.toByte()
    const val KEY_INTERNATIONAL8: Byte = 0x8E.toByte()
    const val KEY_INTERNATIONAL9: Byte = 0x8F.toByte()

    // Korean keyboard keys
    const val KEY_LANG1: Byte = 0x90.toByte()    // Korean Hangul/English
    const val KEY_LANG2: Byte = 0x91.toByte()    // Hanja conversion
    const val KEY_LANG3: Byte = 0x92.toByte()    // Katakana (Japanese)
    const val KEY_LANG4: Byte = 0x93.toByte()    // Hiragana (Japanese)
    const val KEY_LANG5: Byte = 0x94.toByte()    // Zenkaku/Hankaku (Japanese)

    // Enhanced keyboard keys
    const val KEY_KP_COMMA: Byte = 0x85.toByte() // Numpad comma (Brazilian)
    const val KEY_KP_EQUAL: Byte = 0x86.toByte() // Numpad = (AS/400)

    // Windows specific keys
    const val KEY_LEFT_WINDOWS: Byte = MOD_LEFT_GUI
    const val KEY_RIGHT_WINDOWS: Byte = MOD_RIGHT_GUI
    const val KEY_CONTEXT_MENU: Byte = KEY_MENU

    // Browser keys
    const val KEY_BROWSER_BACK: Byte = 0x9A.toByte()
    const val KEY_BROWSER_FORWARD: Byte = 0x9B.toByte()
    const val KEY_BROWSER_REFRESH: Byte = 0x9C.toByte()
    const val KEY_BROWSER_STOP: Byte = 0x9D.toByte()
    const val KEY_BROWSER_SEARCH: Byte = 0x9E.toByte()
    const val KEY_BROWSER_FAVORITES: Byte = 0x9F.toByte()
    const val KEY_BROWSER_HOME: Byte = 0xA0.toByte()

    // Audio keys
    const val KEY_VOLUME_MUTE: Byte = KEY_MEDIA_MUTE
    const val KEY_VOLUME_INCREMENT: Byte = KEY_VOLUME_UP
    const val KEY_VOLUME_DECREMENT: Byte = KEY_VOLUME_DOWN

    // Calculator keys
    const val KEY_CALCULATOR: Byte = 0xA1.toByte()
    const val KEY_MY_COMPUTER: Byte = 0xA2.toByte()

    // Email keys
    const val KEY_EMAIL: Byte = 0xA3.toByte()
    const val KEY_MAIL: Byte = KEY_EMAIL

    // Media select keys
    const val KEY_MEDIA_SELECT: Byte = 0xA4.toByte()

    // Helper function to get key code for character
    fun keyForChar(c: Char): Pair<Byte?, Byte> {
        val noMod: Byte = 0
        return when (c) {
            'a', 'A' -> Pair(KEY_A, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'b', 'B' -> Pair(KEY_B, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'c', 'C' -> Pair(KEY_C, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'd', 'D' -> Pair(KEY_D, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'e', 'E' -> Pair(KEY_E, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'f', 'F' -> Pair(KEY_F, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'g', 'G' -> Pair(KEY_G, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'h', 'H' -> Pair(KEY_H, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'i', 'I' -> Pair(KEY_I, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'j', 'J' -> Pair(KEY_J, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'k', 'K' -> Pair(KEY_K, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'l', 'L' -> Pair(KEY_L, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'm', 'M' -> Pair(KEY_M, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'n', 'N' -> Pair(KEY_N, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'o', 'O' -> Pair(KEY_O, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'p', 'P' -> Pair(KEY_P, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'q', 'Q' -> Pair(KEY_Q, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'r', 'R' -> Pair(KEY_R, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            's', 'S' -> Pair(KEY_S, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            't', 'T' -> Pair(KEY_T, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'u', 'U' -> Pair(KEY_U, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'v', 'V' -> Pair(KEY_V, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'w', 'W' -> Pair(KEY_W, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'x', 'X' -> Pair(KEY_X, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'y', 'Y' -> Pair(KEY_Y, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            'z', 'Z' -> Pair(KEY_Z, if (c.isUpperCase()) MOD_LEFT_SHIFT else noMod)
            '1' -> Pair(KEY_1, noMod)
            '2' -> Pair(KEY_2, noMod)
            '3' -> Pair(KEY_3, noMod)
            '4' -> Pair(KEY_4, noMod)
            '5' -> Pair(KEY_5, noMod)
            '6' -> Pair(KEY_6, noMod)
            '7' -> Pair(KEY_7, noMod)
            '8' -> Pair(KEY_8, noMod)
            '9' -> Pair(KEY_9, noMod)
            '0' -> Pair(KEY_0, noMod)
            '!' -> Pair(KEY_1, MOD_LEFT_SHIFT)
            '@' -> Pair(KEY_2, MOD_LEFT_SHIFT)
            '#' -> Pair(KEY_3, MOD_LEFT_SHIFT)
            '$' -> Pair(KEY_4, MOD_LEFT_SHIFT)
            '%' -> Pair(KEY_5, MOD_LEFT_SHIFT)
            '^' -> Pair(KEY_6, MOD_LEFT_SHIFT)
            '&' -> Pair(KEY_7, MOD_LEFT_SHIFT)
            '*' -> Pair(KEY_8, MOD_LEFT_SHIFT)
            '(' -> Pair(KEY_9, MOD_LEFT_SHIFT)
            ')' -> Pair(KEY_0, MOD_LEFT_SHIFT)
            '-' -> Pair(KEY_MINUS, noMod)
            '_' -> Pair(KEY_MINUS, MOD_LEFT_SHIFT)
            '=' -> Pair(KEY_EQUAL, noMod)
            '+' -> Pair(KEY_EQUAL, MOD_LEFT_SHIFT)
            '[' -> Pair(KEY_LEFT_BRACKET, noMod)
            '{' -> Pair(KEY_LEFT_BRACKET, MOD_LEFT_SHIFT)
            ']' -> Pair(KEY_RIGHT_BRACKET, noMod)
            '}' -> Pair(KEY_RIGHT_BRACKET, MOD_LEFT_SHIFT)
            '\\' -> Pair(KEY_BACKSLASH, noMod)
            '|' -> Pair(KEY_BACKSLASH, MOD_LEFT_SHIFT)
            ';' -> Pair(KEY_SEMICOLON, noMod)
            ':' -> Pair(KEY_SEMICOLON, MOD_LEFT_SHIFT)
            '\'' -> Pair(KEY_APOSTROPHE, noMod)
            '"' -> Pair(KEY_APOSTROPHE, MOD_LEFT_SHIFT)
            '`' -> Pair(KEY_GRAVE, noMod)
            '~' -> Pair(KEY_GRAVE, MOD_LEFT_SHIFT)
            ',' -> Pair(KEY_COMMA, noMod)
            '<' -> Pair(KEY_COMMA, MOD_LEFT_SHIFT)
            '.' -> Pair(KEY_DOT, noMod)
            '>' -> Pair(KEY_DOT, MOD_LEFT_SHIFT)
            '/' -> Pair(KEY_SLASH, noMod)
            '?' -> Pair(KEY_SLASH, MOD_LEFT_SHIFT)
            ' ' -> Pair(KEY_SPACE, noMod)
            '\n' -> Pair(KEY_ENTER, noMod)
            '\t' -> Pair(KEY_TAB, noMod)
            else -> Pair(null, noMod)
        }
    }

    // Helper function for alpha keys only (keeps your existing function)
    fun alphaKeyForChar(c: Char): Byte? {
        return when (c.lowercaseChar()) {
            'a' -> KEY_A
            'b' -> KEY_B
            'c' -> KEY_C
            'd' -> KEY_D
            'e' -> KEY_E
            'f' -> KEY_F
            'g' -> KEY_G
            'h' -> KEY_H
            'i' -> KEY_I
            'j' -> KEY_J
            'k' -> KEY_K
            'l' -> KEY_L
            'm' -> KEY_M
            'n' -> KEY_N
            'o' -> KEY_O
            'p' -> KEY_P
            'q' -> KEY_Q
            'r' -> KEY_R
            's' -> KEY_S
            't' -> KEY_T
            'u' -> KEY_U
            'v' -> KEY_V
            'w' -> KEY_W
            'x' -> KEY_X
            'y' -> KEY_Y
            'z' -> KEY_Z
            else -> null
        }
    }

    // Helper to get key name for display
    fun getKeyName(keyCode: Byte): String {
        return when (keyCode) {
            KEY_A -> "A"
            KEY_B -> "B"
            KEY_C -> "C"
            KEY_D -> "D"
            KEY_E -> "E"
            KEY_F -> "F"
            KEY_G -> "G"
            KEY_H -> "H"
            KEY_I -> "I"
            KEY_J -> "J"
            KEY_K -> "K"
            KEY_L -> "L"
            KEY_M -> "M"
            KEY_N -> "N"
            KEY_O -> "O"
            KEY_P -> "P"
            KEY_Q -> "Q"
            KEY_R -> "R"
            KEY_S -> "S"
            KEY_T -> "T"
            KEY_U -> "U"
            KEY_V -> "V"
            KEY_W -> "W"
            KEY_X -> "X"
            KEY_Y -> "Y"
            KEY_Z -> "Z"
            KEY_1 -> "1"
            KEY_2 -> "2"
            KEY_3 -> "3"
            KEY_4 -> "4"
            KEY_5 -> "5"
            KEY_6 -> "6"
            KEY_7 -> "7"
            KEY_8 -> "8"
            KEY_9 -> "9"
            KEY_0 -> "0"
            KEY_ENTER -> "Enter"
            KEY_ESC -> "Esc"
            KEY_BACKSPACE -> "⌫"
            KEY_TAB -> "Tab"
            KEY_SPACE -> "Space"
            KEY_MINUS -> "-"
            KEY_EQUAL -> "="
            KEY_LEFT_BRACKET -> "["
            KEY_RIGHT_BRACKET -> "]"
            KEY_BACKSLASH -> "\\"
            KEY_SEMICOLON -> ";"
            KEY_APOSTROPHE -> "'"
            KEY_GRAVE -> "`"
            KEY_COMMA -> ","
            KEY_DOT -> "."
            KEY_SLASH -> "/"
            KEY_CAPS_LOCK -> "Caps"
            KEY_NUM_LOCK -> "Num"
            KEY_SCROLL_LOCK -> "Scroll"
            KEY_F1 -> "F1"
            KEY_F2 -> "F2"
            KEY_F3 -> "F3"
            KEY_F4 -> "F4"
            KEY_F5 -> "F5"
            KEY_F6 -> "F6"
            KEY_F7 -> "F7"
            KEY_F8 -> "F8"
            KEY_F9 -> "F9"
            KEY_F10 -> "F10"
            KEY_F11 -> "F11"
            KEY_F12 -> "F12"
            KEY_PRINT_SCREEN -> "PrtSc"
            KEY_PAUSE -> "Pause"
            KEY_INSERT -> "Ins"
            KEY_HOME -> "Home"
            KEY_PAGE_UP -> "PgUp"
            KEY_DELETE -> "Del"
            KEY_END -> "End"
            KEY_PAGE_DOWN -> "PgDn"
            KEY_RIGHT -> "→"
            KEY_LEFT -> "←"
            KEY_DOWN -> "↓"
            KEY_UP -> "↑"
            KEY_MENU -> "Menu"
            else -> "?"
        }
    }
}