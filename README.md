# Thermal Printer Test App

A standalone Android application built with Capacitor to test USB thermal printer connectivity and dual screen display functionality.

## ⚠️ Important Notice

**Current Status**: This app provides a **fully functional UI prototype and receipt builder** with ESC/POS command generation. However, USB printer communication requires a native Capacitor plugin to work on Android hardware.

- ✅ **Works Now**: Complete UI, receipt builder, preview, ESC/POS logic
- ⚠️ **Needs Plugin**: Real USB printing (simulation mode only in current state)

**See IMPLEMENTATION_STATUS.md for full technical details and implementation path.**

## Overview

This app is designed to test the VOLCORA thermal receipt printer with:
- USB connectivity via ESC/POS commands (requires native plugin for hardware)
- Receipt building and printing
- Customer-facing display simulation
- Print preview functionality

## Printer Specifications

- **Model**: VOLCORA Thermal Receipt Printer
- **Print Method**: Direct thermal line
- **Print Speed**: 220mm/s
- **Interface**: USB (also supports Serial/Ethernet/Bluetooth)
- **Paper Width**: 80mm (72mm print width)
- **Commands**: ESC/POS compatible
- **Power**: DC 24V/2A

## Features

### ⚠️ USB Printer Connection (Simulation Mode)
- Connect/disconnect button
- Real-time status indicator
- ESC/POS command generation
- **Note**: Requires native plugin for real USB hardware (see IMPLEMENTATION_STATUS.md)

### ✅ Test Print Functions
- Quick test receipt printing
- Printer self-test command
- Test pattern generation

### ✅ Receipt Builder
- Add items with name, price, and quantity
- Auto-calculate subtotal, tax (10%), and total
- Remove individual items
- Clear cart function
- Live preview before printing

### ✅ Customer Display Simulation
- Toggle in-app customer display
- Rotating photo slideshow
- 3-second interval between images
- Product showcase simulation (not hardware dual-display)

### ✅ Activity Log
- Real-time logging of all operations
- Success/error status indicators
- Timestamp for each action

## Getting Started

### Prerequisites
- Node.js 18+ installed
- Android Studio (for building APK - optional)
- **Note**: USB thermal printer support requires native plugin (not yet implemented)

### Installation

1. **Install dependencies**:
   ```bash
   npm install
   ```

2. **Test in browser** (simulation mode):
   ```bash
   npm run dev
   ```
   Open http://localhost:5000

3. **Install Capacitor** (for Android build):
   ```bash
   npm run install-capacitor
   ```

4. **Initialize Capacitor**:
   ```bash
   npx cap init "Thermal Printer Test" "com.thermalprinter.test"
   ```

5. **Add Android platform**:
   ```bash
   npm run add-android
   ```

6. **Open in Android Studio**:
   ```bash
   npm run open
   ```

7. **Build and deploy** to your Android device

## Usage Guide

### Browser Testing (Simulation Mode)

1. Run `npm run dev`
2. Test the UI and receipt generation
3. Note: USB connection will be simulated in browser

### Android Device Testing (Future - Requires Plugin)

**Current Status**: The app will build and run on Android, but USB printing is simulated only.

**To enable real USB printing**, you must first:
1. Implement a native Capacitor plugin for USB Host API (see IMPLEMENTATION_STATUS.md)
2. Replace WebUSB calls with native plugin calls
3. Test with real VOLCORA printer hardware

**Once plugin is implemented:**
1. Build the APK using Android Studio
2. Install on your Android device
3. Connect USB thermal printer via OTG cable
4. Click "Connect Printer"
5. Grant USB permissions when prompted
6. Test printing functionality

### Building and Previewing a Receipt

1. Add items using the receipt builder:
   - Enter item name
   - Enter price
   - Set quantity (default: 1)
   - Click "Add Item"
2. Review your cart
3. Click "Preview Receipt" to see formatted output
4. Click "Print Receipt" to see ESC/POS commands logged

**Note**: Actual printing requires native USB plugin (see IMPLEMENTATION_STATUS.md)

### Testing Receipt Generation

1. Click "Print Test Receipt" to generate a test receipt
2. The test receipt includes:
   - Header and timestamp
   - Printer information
   - Character set test
   - Line quality patterns
3. ESC/POS commands are generated and logged (simulation mode)

**Note**: For hardware diagnostics, requires native plugin integration

### Customer Display Simulation

1. Click "Toggle Customer Display"
2. An in-app slideshow will begin showing product images
3. Images rotate every 3 seconds
4. Click again to hide the display

**Note**: This is an in-app simulation, not hardware dual-display

## Project Structure

```
thermal-printer-test-app/
├── src/
│   ├── index.html          # Main UI
│   ├── style.css           # Styling
│   └── app.js              # Application logic
├── android/                # Native Android project (after setup)
├── capacitor.config.ts     # Capacitor configuration
├── package.json            # Dependencies and scripts
└── README.md              # This file
```

## Troubleshooting

### USB Printing Doesn't Work
**This is expected behavior** - Real USB printing requires a native Capacitor plugin that is not yet implemented. See IMPLEMENTATION_STATUS.md for implementation options.

**Current behavior:**
- "Connect Printer" runs in simulation mode
- Print commands are logged to console
- No data sent to physical hardware

### App Crashes on Android
- Check Android Studio logcat for errors
- Verify Capacitor configuration is correct
- Ensure Android permissions are granted

### Browser Mode
- USB simulation only
- Perfect for UI/UX testing
- ESC/POS commands logged to console
- No physical printer interaction possible

## Technical Details

### ESC/POS Commands Used

- `ESC @` - Initialize printer
- `GS V` - Cut paper
- Text formatting and line feeds
- Standard character encoding

### USB Communication (Requires Plugin)

**Current Implementation**: Uses WebUSB API (browser-only, not available in Capacitor WebView)

**Required for Hardware**: Native Capacitor plugin using Android USB Host API

The intended USB communication flow:
1. Request USB device access (via native plugin)
2. Enumerate devices and find printer
3. Open device connection
4. Claim appropriate interface
5. Transfer ESC/POS data via bulk transfer
6. Close connection when done

**See IMPLEMENTATION_STATUS.md** for detailed implementation requirements.

### Capacitor Integration

This app uses Capacitor to bridge web technologies to native Android.

**Currently working:**
- Web-based UI in Android WebView
- Standard JavaScript functionality

**Requires native plugin:**
- USB Host API access
- Hardware printer communication

## Next Steps

### Required for Production
1. **Implement USB Plugin** - Create or integrate Capacitor plugin for USB Host API (see IMPLEMENTATION_STATUS.md)
2. **Test with Hardware** - Validate with real VOLCORA printer
3. **Update USB Logic** - Replace WebUSB calls with native plugin

### Potential Future Enhancements
- Barcode printing (QR codes, UPC, EAN)
- Cash drawer trigger
- Custom logo/header images
- Receipt template editor
- Print history log
- Network printer support (Ethernet/Bluetooth)

## License

MIT

## Support

For issues with:
- **This app**: Check logs in the Activity Log section
- **VOLCORA printer**: Refer to the printer user manual
- **Capacitor**: See [Capacitor documentation](https://capacitorjs.com/)

## Author

Created for testing thermal printer integration in Android applications.
