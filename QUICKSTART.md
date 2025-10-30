# Quick Start Guide

Get your thermal printer test app running in 5 minutes!

## Option 1: Browser Testing (No Android Device Needed)

Perfect for testing the UI and receipt generation logic.

```bash
# Install dependencies
npm install

# Start the dev server
npm run dev
```

Open http://localhost:5000 in your browser.

**Note**: USB printing will be simulated. Deploy to Android for real hardware testing.

## Option 2: Android Device Testing (Real Printer)

For actual USB thermal printer testing.

### Step 1: Install Dependencies
```bash
npm install
npm run install-capacitor
```

### Step 2: Initialize Capacitor
```bash
npx cap init "Thermal Printer Test" "com.thermalprinter.test"
```

### Step 3: Add Android Platform
```bash
npm run add-android
```

### Step 4: Sync Files
```bash
npm run sync
```

### Step 5: Open in Android Studio
```bash
npm run open
```

### Step 6: Build & Deploy
1. Connect your Android device via USB
2. Enable Developer Mode and USB Debugging
3. In Android Studio, click the green "Run" button
4. Select your device
5. Wait for installation

### Step 7: Test with Printer
1. Connect USB thermal printer to Android device using OTG cable
2. Open the app
3. Click "Connect Printer"
4. Grant USB permissions
5. Click "Print Test Receipt"

## Quick Test Checklist

- [ ] App loads without errors
- [ ] Can add items to cart
- [ ] Cart totals calculate correctly
- [ ] Receipt preview shows properly
- [ ] Printer connects (green status)
- [ ] Test receipt prints successfully
- [ ] Customer display toggles on/off
- [ ] Activity log shows all actions

## Common Issues

**"npm: command not found"**
→ Install Node.js from nodejs.org

**"Cannot find module @capacitor/cli"**
→ Run `npm run install-capacitor`

**Printer won't connect**
→ Check USB cable, power, and Android USB permissions

**Nothing prints**
→ Ensure printer has paper and is in ready state

## Need Help?

Check the full README.md for detailed troubleshooting and documentation.
