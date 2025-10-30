# Deployment & Testing Checklist

Use this checklist to ensure your thermal printer test app is properly deployed and tested.

## Pre-Deployment

- [ ] Node.js 18+ installed
- [ ] npm dependencies installed (`npm install`)
- [ ] Browser test completed (`npm run dev`)
- [ ] UI displays correctly
- [ ] Receipt builder logic works
- [ ] All buttons functional

## Android Build Setup

- [ ] Android Studio installed
- [ ] Capacitor dependencies installed (`npm run install-capacitor`)
- [ ] Capacitor initialized (`npx cap init`)
- [ ] Android platform added (`npm run add-android`)
- [ ] Files synced (`npm run sync`)
- [ ] Project opens in Android Studio without errors

## Android Device Preparation

- [ ] Android device connected via USB
- [ ] Developer Mode enabled on device
- [ ] USB Debugging enabled
- [ ] Device appears in Android Studio
- [ ] Build configuration set to "Debug"

## Build & Install

- [ ] APK builds without errors
- [ ] App installs on device
- [ ] App launches successfully
- [ ] No crash on startup
- [ ] Permissions dialog appears (if applicable)

## Printer Hardware Setup

- [ ] Thermal printer powered on
- [ ] Paper loaded correctly
- [ ] No error LEDs on printer
- [ ] USB OTG cable connected
- [ ] Android device recognizes USB device

## App Functionality Tests

### Connection Test
- [ ] Click "Connect Printer" button
- [ ] USB permission dialog appears
- [ ] Grant permission
- [ ] Status changes to "Connected"
- [ ] Green indicator appears

### Test Print
- [ ] Click "Print Test Receipt"
- [ ] Printer responds (LED/motor activity)
- [ ] Paper feeds
- [ ] Test receipt prints clearly
- [ ] All text is legible
- [ ] Auto-cut works (if supported)

### Self-Test
- [ ] Click "Printer Self-Test"
- [ ] Command sent successfully
- [ ] Check printer response (or manually trigger self-test)

### Receipt Builder
- [ ] Add item with name and price
- [ ] Item appears in cart
- [ ] Totals calculate correctly (subtotal + 10% tax)
- [ ] Add multiple items
- [ ] Remove individual items
- [ ] Clear cart function works

### Receipt Preview
- [ ] Click "Preview Receipt"
- [ ] Modal opens with formatted receipt
- [ ] All cart items shown
- [ ] Totals correct
- [ ] Format looks good
- [ ] Close preview works

### Print Receipt
- [ ] Build a cart with items
- [ ] Click "Print Receipt"
- [ ] Receipt prints successfully
- [ ] Format matches preview
- [ ] Cart clears after print

### Dual Screen Display
- [ ] Click "Toggle Customer Display"
- [ ] Display area appears
- [ ] Images load and display
- [ ] Slideshow rotates (3-second intervals)
- [ ] Images are appropriate
- [ ] Hide display works

### Activity Log
- [ ] Log shows all actions
- [ ] Timestamps appear
- [ ] Success messages in green
- [ ] Error messages in red
- [ ] Log scrolls properly

## Print Quality Checks

- [ ] Text is clear and legible
- [ ] Lines are straight
- [ ] No missing characters
- [ ] Paper width correct (72mm print area)
- [ ] Top/bottom margins appropriate
- [ ] Cut position correct

## Error Handling Tests

### No Paper
- [ ] Remove paper from printer
- [ ] Try to print
- [ ] Check printer LED indicates error
- [ ] App handles gracefully

### Printer Disconnected
- [ ] Unplug printer during operation
- [ ] App shows disconnected state
- [ ] Reconnection works

### Permission Denied
- [ ] Deny USB permission
- [ ] App handles gracefully
- [ ] Can retry connection

## Performance Tests

- [ ] App starts quickly (<3 seconds)
- [ ] UI is responsive
- [ ] No lag when adding items
- [ ] Print commands send quickly
- [ ] Slideshow transitions smooth

## Documentation Check

- [ ] README.md is accurate
- [ ] QUICKSTART.md steps work
- [ ] All npm scripts functional
- [ ] Troubleshooting section helpful
- [ ] Printer specs match hardware

## Cleanup & Delivery

- [ ] Remove test/debug code
- [ ] Console logs appropriate level
- [ ] .gitignore configured
- [ ] Repository initialized
- [ ] Initial commit made
- [ ] Code pushed to GitHub
- [ ] README updated with repo URL

## Final Verification

- [ ] Fresh install on clean device works
- [ ] All features functional
- [ ] No crashes or errors
- [ ] Performance acceptable
- [ ] Documentation complete

## Post-Deployment

- [ ] Share repository URL
- [ ] Document any issues found
- [ ] Note printer compatibility
- [ ] Record test results
- [ ] Plan next features (if needed)

---

## Test Results Template

```
Date: _______________
Device: _______________
Android Version: _______________
Printer Model: _______________

Connection: ☐ Pass ☐ Fail
Test Print: ☐ Pass ☐ Fail
Receipt Builder: ☐ Pass ☐ Fail
Print Quality: ☐ Pass ☐ Fail
Dual Screen: ☐ Pass ☐ Fail

Notes:
_______________________________________
_______________________________________
_______________________________________

Overall: ☐ Success ☐ Needs Work
```
