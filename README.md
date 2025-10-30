# Thermal Printer Test App

A production-ready Android application with **native hardware support** for USB thermal printers and dual screen customer displays.

## ✅ Now with Real Hardware Support!

**Version 2.0 - Native Plugin Release**

This app now includes a **complete native Android Capacitor plugin** for real hardware connectivity:

- ✅ **Real USB printing** via Android USB Host API
- ✅ **Native thermal printer support** (VOLCORA & ESC/POS compatible)
- ✅ **Dual screen display** using Android Presentation API  
- ✅ **Production-ready** - Deploy and use with actual hardware!

**See IMPLEMENTATION_STATUS.md for complete technical details.**

## Overview

This app connects to VOLCORA thermal receipt printers and provides:
- Real USB connectivity via native Android USB Host API
- ESC/POS command generation and transmission
- Receipt building with cart management
- Dual customer-facing display support (requires secondary monitor)
- Complete print testing and preview functionality

## Printer Specifications

- **Model**: VOLCORA Thermal Receipt Printer
- **Print Method**: Direct thermal line
- **Print Speed**: 220mm/s
- **Interface**: USB (also supports Serial/Ethernet/Bluetooth)
- **Paper Width**: 80mm (72mm print width)
- **Commands**: ESC/POS compatible
- **Power**: DC 24V/2A

## Features

### ✅ USB Printer Connection (Native Hardware)
- Automatic USB device detection and enumeration
- Connect to VOLCORA and other ESC/POS thermal printers
- Real-time connection status
- USB permission handling
- Vendor/Product ID detection

### ✅ Real Thermal Printing
- Direct ESC/POS command transmission to printer
- Test receipt printing
- Custom receipt building and printing
- Printer self-test commands
- Real-time byte transfer feedback

### ✅ Receipt Builder
- Add items with name, price, and quantity
- Auto-calculate subtotal, tax (10%), and total
- Remove individual items
- Clear cart function
- Live preview before printing

### ✅ Dual Screen Customer Display (Native Hardware)
- Automatic secondary display detection
- Full HTML/CSS customer display rendering
- Product image slideshow
- Customer-facing messaging
- Fallback to in-app simulation if no display

### ✅ Activity Log
- Real-time logging of all operations
- Success/error status indicators
- Timestamp for each action
- Device information display

## Getting Started

### Prerequisites
- Android device with USB Host support (most phones/tablets)
- USB OTG cable/adapter
- VOLCORA thermal printer or compatible ESC/POS printer
- (Optional) Secondary display for dual screen feature

### Quick Start - Download Pre-built APK

The easiest way to get started:

1. **Download APK** from GitHub Actions:
   - Go to https://github.com/openpayments1-lab/thermal-printer-test-app/actions
   - Click the latest successful workflow run
   - Download the APK from "Artifacts" section

2. **Install on Android**:
   - Enable "Install from unknown sources" in Android settings
   - Transfer APK to your device
   - Install and open the app

3. **Connect Hardware**:
   - Connect USB thermal printer via OTG cable
   - (Optional) Connect secondary display via HDMI/USB-C
   - Power on printer

4. **Start Testing**:
   - Click "Connect Printer" in the app
   - Grant USB permission when prompted
   - Build and print receipts!

### Development Setup (For Modifying Code)

1. **Install dependencies**:
   ```bash
   npm install
   ```

2. **Test UI in browser** (hardware features won't work):
   ```bash
   npm run dev
   ```
   Open http://localhost:5000

3. **Sync Capacitor**:
   ```bash
   npx cap sync android
   ```

4. **Build APK** - Use GitHub Actions (automatic on each push) or build locally with Android Studio

## Usage Guide

### Connecting the Printer

1. **Connect printer** to Android device via USB OTG cable
2. **Power on** the thermal printer
3. **Open app** and click "Connect Printer"
4. **Grant permission** when Android asks "Allow USB device access?"
5. **Connection confirmed** - Status shows "Connected" with green indicator

**First time connection:** You'll need to grant USB permission. If connection fails, click "Connect Printer" again after granting permission.

### Android Device Testing

1. **Install APK** on your Android device
2. **Connect USB thermal printer** via OTG cable
3. **Open the app**
4. **Click "Connect Printer"**
5. **Grant USB permissions** when prompted
6. **Start printing!**

### Building and Printing a Receipt

1. **Add items** using the receipt builder:
   - Enter item name
   - Enter price
   - Set quantity (default: 1)
   - Click "Add Item"
2. **Review your cart** - Check items, prices, and total
3. **Preview** (optional) - Click "Preview Receipt" to see formatted output
4. **Print** - Click "Print Receipt" to send to printer

**The printer will print your receipt with:**
- Store header and timestamp
- All cart items with prices
- Subtotal, tax (10%), and total
- Thank you message
- Automatic paper cut

### Quick Test Print

1. **Connect printer** (see above)
2. **Click "Print Test Receipt"**
3. **Printer outputs** a test receipt with:
   - System information
   - Character set samples
   - Line quality patterns
   - Printer diagnostics

Perfect for verifying printer functionality!

### Using Dual Customer Display

1. **Connect secondary display** (HDMI, USB-C, or wireless)
2. **Click "Toggle Customer Display"**
3. **App detects** secondary screen automatically
4. **Customer sees** product images and welcome message on their display

**If no secondary display:**  
App falls back to in-app simulation for preview/testing.

**Note:** Slideshow shows one image on secondary display. For rotating images, this feature can be enhanced in future versions.

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

### "No USB devices found"
**Solution:**
- Verify printer is powered ON
- Check USB OTG cable is properly connected
- Try a different USB OTG cable (some are charge-only)
- Ensure printer uses USB interface (not Bluetooth/Network only)

### "USB permission requested. Please grant permission and try again"
**Solution:**
- Click "OK" on the Android permission dialog
- Click "Connect Printer" button again
- Permission is saved - only needed once per device

### Printer connected but not printing
**Solution:**
- Check printer has paper loaded
- Verify printer is not in error state (check status lights)
- Try disconnecting and reconnecting
- Check printer paper feed mechanism
- Restart printer and try again

### "No secondary display detected"
**Solution:**
- Connect a physical secondary display (HDMI, USB-C, wireless)
- Check display is enabled in Android settings
- App will use in-app simulation as fallback

### App issues or errors
**Solution:**
- Check the Activity Log in the app for detailed error messages
- Restart the app
- Reinstall the APK
- Check Android version (requires Android 5.0+)
- Verify Capacitor configuration is correct
- Ensure Android permissions are granted

### Browser Mode (UI Testing Only)
When running `npm run dev` in browser:
- USB and dual screen features are unavailable (requires Android device)
- Perfect for UI/UX testing and design
- ESC/POS command generation logic works
- Use for development and design iteration

**For real hardware:** Install the APK on Android device (see Quick Start above)

## Technical Details

### ESC/POS Commands

The app generates standard ESC/POS thermal printer commands:

- `ESC @` (0x1B 0x40) - Initialize/reset printer
- `GS V` (0x1D 0x56) - Cut paper (partial cut)
- Text formatting and line feeds
- Standard ASCII character encoding
- Compatible with VOLCORA and all ESC/POS printers

### Native Android Plugin Architecture

The app includes a custom Capacitor plugin with three components:

1. **ThermalPrinterPlugin.java** - Main plugin class exposing methods to JavaScript
2. **UsbPrinterManager.java** - Handles USB device enumeration, connection, and bulk data transfer
3. **DualScreenManager.java** - Manages secondary display detection and content rendering

### USB Communication Flow

1. User clicks "Connect Printer"
2. App enumerates USB devices using `UsbManager.getDeviceList()`
3. Finds thermal printer by device class/interface
4. Requests USB permission (system dialog)
5. Opens USB device connection
6. Claims printer interface (interface 0)
7. Finds bulk OUT endpoint
8. Sends ESC/POS commands via `bulkTransfer()`
9. Printer receives and executes commands

### Capacitor Integration

Uses Capacitor to bridge JavaScript to native Android:
- JavaScript calls `Capacitor.Plugins.ThermalPrinter.*` methods
- Plugin marshals calls to native Java code
- Native code interacts with Android USB Host API
- Results returned to JavaScript via callbacks

## Future Enhancements
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
