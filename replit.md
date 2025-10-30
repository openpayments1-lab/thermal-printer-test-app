# Thermal Printer Test App

## Project Overview

A standalone Android application built with Capacitor to test USB thermal printer connectivity and dual screen display functionality. Designed specifically for the VOLCORA thermal receipt printer but compatible with any ESC/POS printer.

## Purpose

This app serves as a testing and debugging tool to:
- Verify USB thermal printer hardware compatibility
- Test ESC/POS command sequences
- Validate receipt formatting and printing
- Test dual screen customer display functionality
- Isolate printer issues from main application code

## Current State

✅ **Completed**
- Full web interface with modern, clean UI
- USB printer connection management
- Test print functionality with ESC/POS commands
- Receipt builder with item management
- Auto-calculated totals (subtotal, 10% tax, total)
- Receipt preview functionality
- Dual screen customer display with rotating slideshow
- Activity logging system
- Comprehensive documentation

## Project Architecture

### Tech Stack
- **Frontend**: Vanilla JavaScript, HTML5, CSS3
- **Bridge**: Capacitor 5 for native Android integration
- **Communication**: WebUSB API for USB printer access
- **Commands**: ESC/POS thermal printer protocol
- **Server**: http-server for local development

### File Structure
```
.
├── src/
│   ├── index.html          # Main UI interface
│   ├── style.css           # Complete styling
│   └── app.js              # Application logic and printer control
├── package.json            # Dependencies and npm scripts
├── capacitor.config.ts     # Capacitor/Android configuration
├── README.md              # Full documentation
├── QUICKSTART.md          # Fast setup guide
├── START_HERE.md          # Getting started guide
├── DEPLOYMENT_CHECKLIST.md # Testing checklist
└── replit.md              # This file
```

## Key Features

1. **Printer Connection**
   - USB device detection and connection
   - Real-time status indicators
   - Automatic permission handling
   - Fallback simulation mode for browser testing

2. **Receipt Building**
   - Add/remove items dynamically
   - Price and quantity management
   - Automatic tax calculation (10%)
   - Live total updates
   - Preview before printing

3. **Print Functions**
   - Test receipt with comprehensive patterns
   - Printer self-test command
   - Custom receipt printing
   - ESC/POS command support

4. **Customer Display**
   - Toggle dual screen mode
   - Rotating image slideshow
   - 3-second interval transitions
   - Product showcase capability

5. **Activity Log**
   - Real-time operation logging
   - Success/error indicators
   - Timestamp tracking
   - Auto-scrolling display

## Printer Specifications

**VOLCORA Thermal Receipt Printer**
- Print Method: Direct thermal line
- Print Speed: 220mm/s
- Interface: USB, Serial, Ethernet, Bluetooth
- Paper Width: 80mm (72mm print area)
- Commands: ESC/POS compatible
- Power: DC 24V/2A

## Usage Flow

### Browser Testing (Development)
1. Run `npm run dev`
2. Test UI and logic at http://localhost:5000
3. USB operations run in simulation mode

### Android Deployment (Production)
1. Install dependencies: `npm install && npm run install-capacitor`
2. Initialize: `npx cap init`
3. Add platform: `npm run add-android`
4. Open Android Studio: `npm run open`
5. Build and deploy APK to device
6. Connect USB printer via OTG cable
7. Test with real hardware

## Recent Changes

**2025-10-30**: Initial project creation
- Created complete web interface
- Implemented USB printer connection logic
- Added receipt builder with calculations
- Created dual screen display system
- Added comprehensive documentation
- Set up development workflow

## Development Guidelines

### Testing
- Always test in browser first (simulation mode)
- Deploy to Android for USB hardware testing
- Use Activity Log to debug operations
- Check printer manual for specific commands

### Code Conventions
- Vanilla JavaScript (no framework dependencies)
- ES6 class-based architecture
- Async/await for USB operations
- Clear error handling and logging
- Mobile-first responsive design

### ESC/POS Commands
- `ESC @` - Initialize printer
- `GS V` - Cut paper
- Standard text encoding
- See printer manual for full command set

## Dependencies

**Runtime**
- @capacitor/core: ^5.5.1
- @capacitor/android: ^5.5.1

**Development**
- @capacitor/cli: ^5.5.1
- http-server: ^14.1.1

## Environment

- **Development**: Browser-based with http-server on port 5000
- **Production**: Android native app via Capacitor
- **Deployment**: Manual APK build through Android Studio

## Next Phase Enhancements

Potential features for future development:
- Barcode/QR code printing
- Cash drawer trigger commands
- Custom logo/header image upload
- Receipt template editor
- Print history with timestamps
- Network printer support (Ethernet/Bluetooth)
- Multiple printer profiles
- Advanced ESC/POS formatting (bold, underline, fonts)

## Troubleshooting

**Printer won't connect**
- Check USB OTG cable connection
- Verify printer is powered on
- Grant USB permissions on Android
- Check printer error LEDs

**Nothing prints**
- Ensure paper is loaded
- Check printer is in ready state
- Verify ESC/POS commands are correct
- Try self-test function

**Browser limitations**
- WebUSB API not available in standard browsers
- Use for UI testing only
- Deploy to Android for USB access

## Support Resources

- **App Documentation**: README.md, QUICKSTART.md
- **Printer Manual**: General_User_Manual.pdf (attached)
- **Capacitor Docs**: https://capacitorjs.com/
- **ESC/POS Reference**: Standard thermal printer commands

## Author Notes

This is a diagnostic tool designed to isolate thermal printer functionality from a larger application. Use it to:
1. Prove the printer hardware works with Android
2. Verify ESC/POS command compatibility
3. Test receipt formatting before implementing in main app
4. Document working configurations for development team
