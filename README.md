# Android PC Keyboard & Mouse via Bluetooth HID

Transform your Android device into a wireless Bluetooth keyboard, mouse, and media controller for your PC, Mac, or any Bluetooth-enabled device.

## Overview

This Android application leverages the **Bluetooth HID (Human Interface Device) Device profile** to emulate standard input devices. Once connected, your Android phone or tablet functions as a genuine wireless keyboard and mouse, allowing you to control computers, smart TVs, presentation systems, and more.

## Key Features

### Keyboard Support
- Complete QWERTY keyboard layout with all standard keys
- Modifier keys (Ctrl, Alt, Shift, GUI/Windows)
- Function keys (F1-F12)
- Navigation keys (Arrow keys, Home, End, Page Up/Down)
- Numpad support
- Special keys (Print Screen, Scroll Lock, Pause, Insert, Delete)
- Automatic shift detection for uppercase letters and symbols

### Mouse Control
- Relative mouse movement with smooth tracking
- Left and right click support
- Mouse scroll wheel
- Touch-based mouse pad interface

### Media & Consumer Controls
- Volume up/down/mute
- Play/Pause, Next/Previous track
- Brightness control
- Media playback controls

### Technical Highlights

#### Robust Bluetooth HID Implementation
- Proper HID descriptor supporting keyboard, mouse, and consumer control (3 report IDs)
- Automatic Bluetooth enabling and profile connection
- Service lifecycle management with background operation support
- Connection state polling for reliable detection

#### Smart Auto-Reconnection
- Remembers last connected host device
- Automatically reconnects to previously paired computers
- Prioritizes computer-class Bluetooth devices for reconnection
- Persists connection preferences using SharedPreferences

#### Permission Handling
- Runtime permission requests for Bluetooth Connect and Advertise (Android 12+)
- Graceful degradation for older Android versions
- Clear user feedback for permission states

#### Modern UI/UX
- Built with **Jetpack Compose** for a modern, declarative UI
- Material Design 3 theming
- Immersive fullscreen mode (hides system bars)
- Responsive layout adapting to connection states
- Real-time log messages for debugging

#### Device Compatibility Detection
- Detects HID Device profile support at runtime
- 5-second timeout for profile connection attempts
- Warnings for devices with limited HID support (e.g., some Xiaomi/MIUI devices)
- Detailed logging for troubleshooting

## Project Architecture

### Core Components

```
app/src/main/java/com/elbaroudi/pckeyboard/
├── MainActivity.kt           # Main entry point, UI state management, permission handling
├── PcHidService.kt           # Background service managing Bluetooth HID operations
├── HidDescriptor.kt          # HID report descriptor (keyboard + mouse + consumer)
├── HidKeyCodes.kt            # HID key code constants and character mapping utilities
└── HidReport.kt              # Data classes for HID report byte arrays
```

### UI Components

```
app/src/main/java/com/elbaroudi/pckeyboard/ui/
├── WelcomeScreen.kt          # Initial screen with discoverability and connection status
├── ConnectedScreen.kt        # Container for connected state UI
├── GamepadUI.kt              # Game controller interface with virtual joysticks
├── Joystick.kt               # Custom joystick composable component
├── KeyboardUI.kt             # Full virtual keyboard interface
├── MouseUI.kt                # Touch pad mouse interface
└── theme/                    # Material 3 theme configuration
```

### Technology Stack

- **Language**: Kotlin
- **Min SDK**: Android 9 (API 28) - Required for HID Device profile
- **Target SDK**: Android 15 (API 35)
- **UI Framework**: Jetpack Compose
- **Design System**: Material Design 3
- **Build System**: Gradle with Kotlin DSL
- **Bluetooth**: Android Bluetooth HID Device API

## Getting Started

### Prerequisites

- Android Studio (latest version recommended)
- Android device running **Android 9.0 (Pie)** or higher
- Device must support **Bluetooth HID Device profile**
- Bluetooth-enabled computer or device to connect to

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/gamecontroller.git
   cd gamecontroller
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build and Run**
   - Connect your Android device via USB
   - Enable USB debugging on your device
   - Click "Run" in Android Studio
   - Select your device

### Usage

1. **Launch the app** on your Android device
2. **Grant Bluetooth permissions** when prompted
3. **Make your Android device discoverable** by tapping the "Make Visible" button
4. **On your PC/Mac**:
   - Open Bluetooth settings
   - Scan for new devices
   - Look for "BT Keyboard & Mouse"
   - Pair and connect
5. **Start controlling** your computer using the keyboard, mouse, or media controls

## How It Works

### Bluetooth HID Protocol

The app implements the **Bluetooth HID Device profile** introduced in Android 9 (API 28). Here's the connection flow:

1. **Service Initialization**: `PcHidService` starts and obtains the Bluetooth HID Device proxy
2. **HID Registration**: Registers with the Bluetooth stack using a custom SDP (Service Discovery Protocol) descriptor
3. **Discoverability**: Android device becomes visible as a HID device (keyboard/mouse)
4. **Pairing**: User pairs the Android device with a host computer
5. **Connection**: HID profile connects, enabling input reports
6. **Input Reports**: The app sends HID reports (keyboard strokes, mouse movements) to the host

### HID Report Descriptor

The descriptor ([HidDescriptor.kt](file:///c:/android/gamecontroller/app/src/main/java/com/elbaroudi/pckeyboard/HidDescriptor.kt)) defines three report types:

- **Report ID 1**: Keyboard (8 modifier bits + 1 reserved + 6 key codes)
- **Report ID 2**: Mouse (3 buttons + X/Y movement + scroll wheel)
- **Report ID 3**: Consumer Control (16-bit usage for media keys)

### Key Code Mapping

[HidKeyCodes.kt](file:///c:/android/gamecontroller/app/src/main/java/com/elbaroudi/pckeyboard/HidKeyCodes.kt) provides comprehensive HID usage table mappings and helper functions like `keyForChar()` that automatically determine the correct key code and modifier for any character.

## Permissions

The app requires the following Bluetooth permissions:

```xml
<!-- For Android 11 and below -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />

<!-- For Android 12+ (API 31+) -->
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
```

## Device Compatibility

### Supported Devices
- Most Android devices running Android 9.0+
- Google Pixel series
- Samsung Galaxy series
- OnePlus devices
- Essential Phone

### Known Limitations
- **Xiaomi/MIUI devices**: Some models disable HID Device support at the manufacturer level
- **Custom ROMs**: May have incomplete Bluetooth HID implementations
- **Android < 9.0**: HID Device profile not available (requires API 28+)

The app includes runtime detection and will warn you if your device doesn't fully support the HID Device profile.

## Future Feature Ideas

Contributions are welcome. Below are suggested features that could be implemented:

### Input Enhancements
- [ ] Customizable keyboard layouts (AZERTY, QWERTZ, Dvorak, etc.)
- [ ] Macro recording and playback - Record key sequences and replay them
- [ ] Text-to-keyboard - Type text in the app and send it all at once
- [ ] Clipboard sync - Share clipboard between Android and PC
- [ ] Gesture support - Swipe gestures for special actions
- [ ] Multi-touch gestures - Pinch-to-zoom, three-finger swipe, etc.

### UI/UX Improvements
- [ ] Dark/Light theme toggle
- [ ] Customizable button layouts - Drag and drop UI elements
- [ ] Haptic feedback - Vibration on key presses
- [ ] Sound effects - Audio feedback for clicks and keys
- [ ] Multiple profiles - Save different layouts for different use cases
- [ ] Floating window mode - Use app while other apps are open

### Advanced Features
- [ ] Game controller mode - Full gamepad emulation with triggers and bumpers
- [ ] Presentation mode - Dedicated slide control interface
- [ ] Remote desktop integration - Work with VNC/RDP clients
- [ ] WiFi fallback - Use TCP/IP when Bluetooth is unavailable
- [ ] Multiple device support - Control multiple computers simultaneously
- [ ] Voice input - Speech-to-text keyboard input
- [ ] Barcode/QR scanner - Use camera as barcode scanner input
- [ ] Drawing tablet mode - Use touchscreen as graphics tablet

### Developer Features
- [ ] API for automation - Allow other apps to send keyboard/mouse events
- [ ] Intent support - Receive intents to trigger specific key combinations
- [ ] Plugin system - Third-party extensions and layouts
- [ ] Analytics - Optional usage statistics (privacy-focused)
- [ ] Testing framework - Automated UI tests for all input modes

### Performance & Reliability
- [ ] Connection quality monitoring - Show signal strength and latency
- [ ] Battery optimization - Reduce power consumption during idle
- [ ] Reconnection strategies - Smarter auto-reconnect logic
- [ ] Input buffering - Queue inputs during brief disconnections
- [ ] Diagnostic mode - Export logs for troubleshooting

## Contributing

Contributions are welcome. Follow these steps to contribute:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit your changes** (`git commit -m 'Add amazing feature'`)
4. **Push to the branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

### Development Guidelines
- Follow Kotlin coding conventions
- Use meaningful commit messages
- Test your changes on physical devices
- Add comments for complex logic
- Update documentation as needed

## Troubleshooting

### App shows "HID Registration FAILED"
- Ensure Bluetooth is enabled
- Grant all Bluetooth permissions
- Restart the app
- Check if your device supports HID Device profile

### PC does not discover the Android device
- Tap "Make Visible" in the app
- Ensure Android device is not already connected to another device
- Try turning Bluetooth off and on
- Check Android version (must be 9.0+)

### Connection drops frequently
- Keep the app in the foreground
- Disable battery optimization for the app
- Move devices closer together
- Check for Bluetooth interference

### Keys not working correctly
- Verify you are in the correct input mode (keyboard/mouse)
- Check connection state in the app
- Try reconnecting the Bluetooth connection
- Review logs in the app for error messages

## Technical Resources

- [Bluetooth HID Device Profile Specification](https://www.bluetooth.com/specifications/specs/hid-profile-1-1-1/)
- [HID Usage Tables](https://usb.org/sites/default/files/hut1_3_0.pdf)
- [Android Bluetooth HID Device API](https://developer.android.com/reference/android/bluetooth/BluetoothHidDevice)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)

## 📄 License

This project is open source. Please add your chosen license here.

## Acknowledgments

- Android Bluetooth framework team
- Jetpack Compose team for the modern UI toolkit
- HID specification contributors
- Community testers and contributors

## Support

If you encounter issues or have questions:
- Open an issue on GitHub
- Include detailed logs from the app
- Specify your Android version and device model
- Describe the steps to reproduce the problem

---

**Developed for the Android and open-source community**

*Transform your phone into a powerful input device*
