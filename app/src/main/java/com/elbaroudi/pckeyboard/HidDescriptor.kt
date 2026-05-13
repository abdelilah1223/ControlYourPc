package com.elbaroudi.pckeyboard

object HidDescriptor {
    val KEYBOARD_MOUSE_DESCRIPTOR = byteArrayOf(
        // =======================
        // Keyboard (Report ID 1)
        // =======================
        0x05, 0x01,                   // USAGE_PAGE (Generic Desktop)
        0x09, 0x06,                   // USAGE (Keyboard)
        0xA1.toByte(), 0x01,          // COLLECTION (Application)
        0x85.toByte(), 0x01,          //   REPORT_ID (1)

        0x05, 0x07,                   //   USAGE_PAGE (Keyboard)
        0x19, 0xE0.toByte(),          //   USAGE_MINIMUM (Keyboard LeftControl)
        0x29, 0xE7.toByte(),          //   USAGE_MAXIMUM (Keyboard Right GUI)
        0x15, 0x00,                   //   LOGICAL_MINIMUM (0)
        0x25, 0x01,                   //   LOGICAL_MAXIMUM (1)
        0x75, 0x01,                   //   REPORT_SIZE (1)
        0x95.toByte(), 0x08,          //   REPORT_COUNT (8)
        0x81.toByte(), 0x02,          //   INPUT (Data,Var,Abs) ; Modifier byte

        0x95.toByte(), 0x01,          //   REPORT_COUNT (1)
        0x75, 0x08,                   //   REPORT_SIZE (8)
        0x81.toByte(), 0x01,          //   INPUT (Const,Array,Abs) ; Reserved byte

        0x95.toByte(), 0x06,          //   REPORT_COUNT (6)
        0x75, 0x08,                   //   REPORT_SIZE (8)
        0x15, 0x00,                   //   LOGICAL_MINIMUM (0)
        0x25, 0x65,                   //   LOGICAL_MAXIMUM (101)
        0x05, 0x07,                   //   USAGE_PAGE (Keyboard)
        0x19, 0x00,                   //   USAGE_MINIMUM (Reserved (no event indicated))
        0x29, 0x65,                   //   USAGE_MAXIMUM (Keyboard Application)
        0x81.toByte(), 0x00,          //   INPUT (Data,Array,Abs) ; Key arrays (6 bytes)
        0xC0.toByte(),                // END_COLLECTION

        // =======================
        // Mouse (Report ID 2)
        // =======================
        0x05, 0x01,                   // USAGE_PAGE (Generic Desktop)
        0x09, 0x02,                   // USAGE (Mouse)
        0xA1.toByte(), 0x01,          // COLLECTION (Application)
        0x85.toByte(), 0x02,          //   REPORT_ID (2)
        0x09, 0x01,                   //   USAGE (Pointer)
        0xA1.toByte(), 0x00,          //   COLLECTION (Physical)

        0x05, 0x09,                   //     USAGE_PAGE (Button)
        0x19, 0x01,                   //     USAGE_MINIMUM (Button 1)
        0x29, 0x03,                   //     USAGE_MAXIMUM (Button 3)
        0x15, 0x00,                   //     LOGICAL_MINIMUM (0)
        0x25, 0x01,                   //     LOGICAL_MAXIMUM (1)
        0x95.toByte(), 0x03,          //     REPORT_COUNT (3)
        0x75, 0x01,                   //     REPORT_SIZE (1)
        0x81.toByte(), 0x02,          //     INPUT (Data,Var,Abs)
        0x95.toByte(), 0x01,          //     REPORT_COUNT (1)
        0x75, 0x05,                   //     REPORT_SIZE (5)
        0x81.toByte(), 0x01,          //     INPUT (Const,Array,Abs)

        0x05, 0x01,                   //     USAGE_PAGE (Generic Desktop)
        0x09, 0x30,                   //     USAGE (X)
        0x09, 0x31,                   //     USAGE (Y)
        0x09, 0x38,                   //     USAGE (Wheel)
        0x15, 0x81.toByte(),          //     LOGICAL_MINIMUM (-127)
        0x25, 0x7F,                   //     LOGICAL_MAXIMUM (127)
        0x75, 0x08,                   //     REPORT_SIZE (8)
        0x95.toByte(), 0x03,          //     REPORT_COUNT (3)
        0x81.toByte(), 0x06,          //     INPUT (Data,Var,Rel)
        0xC0.toByte(),                //   END_COLLECTION
        0xC0.toByte(),                // END_COLLECTION

        // =======================
        // Consumer Control (Report ID 3)
        // =======================
        0x05, 0x0C,                   // USAGE_PAGE (Consumer)
        0x09, 0x01,                   // USAGE (Consumer Control)
        0xA1.toByte(), 0x01,          // COLLECTION (Application)
        0x85.toByte(), 0x03,          //   REPORT_ID (3)
        0x15, 0x00,                   //   LOGICAL_MINIMUM (0)
        0x26, 0xFF.toByte(), 0x03,    //   LOGICAL_MAXIMUM (0x03FF)
        0x19, 0x00,                   //   USAGE_MINIMUM (0)
        0x2A, 0xFF.toByte(), 0x03,    //   USAGE_MAXIMUM (0x03FF)
        0x75, 0x10,                   //   REPORT_SIZE (16)
        0x95.toByte(), 0x01,          //   REPORT_COUNT (1)
        0x81.toByte(), 0x00,          //   INPUT (Data,Array,Abs)
        0xC0.toByte()                 // END_COLLECTION
    )
}
