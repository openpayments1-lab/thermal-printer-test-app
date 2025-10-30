# Thermal Printer Test App

A standalone Android application built with Capacitor to test USB thermal printer connectivity and dual screen display functionality.

## Overview

This app is designed to test the VOLCORA thermal receipt printer with:
- USB connectivity via ESC/POS commands
- Receipt building and printing
- Customer-facing dual screen display
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

### ✅ USB Printer Connection
- Connect/disconnect button
- Real-time status indicator
- USB device detection (when deployed to Android)
- Simulation mode for browser testing

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

### ✅ Dual Screen Display
- Toggle customer-facing display
- Rotating photo slideshow
- 3-second interval between images
- Product showcase capability

### ✅ Activity Log
- Real-time logging of all operations
- Success/error status indicators
- Timestamp for each action

## Getting Started

### Prerequisites
- Node.js 18+ installed
- Android Studio (for building APK)
- USB thermal printer (VOLCORA or compatible)

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

### Android Device Testing (Real Hardware)

1. Build the APK using Android Studio
2. Install on your Android device
3. Connect USB thermal printer via OTG cable
4. Click "Connect Printer"
5. Grant USB permissions when prompted
6. Test printing functionality

### Printing a Receipt

1. Click "Connect Printer" and grant permissions
2. Add items using the receipt builder:
   - Enter item name
   - Enter price
   - Set quantity (default: 1)
   - Click "Add Item"
3. Review your cart
4. Click "Preview Receipt" to see formatted output
5. Click "Print Receipt" to send to printer

### Testing Print Quality

1. Click "Print Test Receipt" for a comprehensive test
2. The test receipt includes:
   - Header and timestamp
   - Printer information
   - Character set test
   - Line quality patterns
3. Click "Printer Self-Test" for hardware diagnostics

### Dual Screen Display

1. Click "Toggle Customer Display"
2. A slideshow will begin showing product images
3. Images rotate every 3 seconds
4. Click again to hide the display

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

### Printer Won't Connect
- Ensure USB OTG cable is properly connected
- Check that printer is powered on
- Grant USB permissions when prompted
- Try disconnecting and reconnecting

### Nothing Prints
- Verify printer has paper loaded
- Check printer error LEDs
- Ensure printer is in ready state (not paper out)
- Try the self-test function

### ESC/POS Commands Not Working
- Verify printer supports ESC/POS (VOLCORA does)
- Check printer documentation for specific commands
- Some printers require specific initialization sequences

### Browser Mode Limitations
- USB API not available in regular browsers
- Use for UI testing only
- Deploy to Android for actual USB printing

## Technical Details

### ESC/POS Commands Used

- `ESC @` - Initialize printer
- `GS V` - Cut paper
- Text formatting and line feeds
- Standard character encoding

### USB Communication

The app uses the WebUSB API (on Android via Capacitor) to communicate with the printer:

1. Request USB device access
2. Open device connection
3. Claim interface
4. Transfer data via bulk transfer
5. Close connection when done

### Capacitor Integration

Capacitor bridges web technologies to native Android:
- WebUSB API for USB communication
- Native permissions handling
- Hardware access through web standards

## Next Steps

### Potential Enhancements
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
