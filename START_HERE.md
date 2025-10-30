# 🚀 START HERE

Welcome to your Thermal Printer Test App!

## What This App Does

This is a standalone Android application designed to test your VOLCORA USB thermal receipt printer. It helps you:

1. **Verify Hardware** - Confirm your printer works with Android
2. **Test USB Connection** - Check USB OTG connectivity
3. **Print Receipts** - Build and print formatted receipts
4. **Dual Screen** - Test customer-facing display functionality

## Quick Decision Tree

### I want to test the UI only
→ Run `npm install && npm run dev`
→ Open http://localhost:5000

### I want to test with a real printer
→ See QUICKSTART.md for Android build instructions
→ You'll need Android Studio installed

### I just want to understand the code
→ Check out:
- `src/index.html` - User interface
- `src/style.css` - Styling
- `src/app.js` - Application logic
- `README.md` - Full documentation

## First Time Setup

```bash
# 1. Install dependencies
npm install

# 2. Test in browser (simulation mode)
npm run dev

# 3. For Android deployment, see QUICKSTART.md
```

## App Features at a Glance

✅ **Printer Connection**
- USB device detection
- Status indicators
- Connect/disconnect controls

✅ **Receipt Builder**
- Add items with price/quantity
- Auto-calculate tax and totals
- Print preview

✅ **Test Functions**
- Quick test print
- Printer self-test
- ESC/POS command testing

✅ **Customer Display**
- Toggle dual screen
- Rotating slideshow
- 3-second intervals

## Printer Compatibility

This app is designed for:
- **Primary**: VOLCORA Thermal Receipt Printer
- **Compatible**: Any ESC/POS thermal printer
- **Interface**: USB (also works with Serial/Ethernet/Bluetooth models)
- **Paper**: 80mm thermal paper

## Next Steps

1. **Browser Test**: Run `npm run dev` to test the interface
2. **Read Docs**: Check README.md for full details
3. **Android Build**: Follow QUICKSTART.md for APK deployment
4. **Hardware Test**: Connect real printer and verify functionality

## File Guide

| File | Purpose |
|------|---------|
| `START_HERE.md` | You are here! |
| `QUICKSTART.md` | Fast setup instructions |
| `README.md` | Complete documentation |
| `src/index.html` | User interface |
| `src/app.js` | Application logic |
| `package.json` | Dependencies & scripts |
| `capacitor.config.ts` | Android configuration |

## Support

- UI/Logic Issues → Check Activity Log in the app
- Printer Issues → See printer manual (attached PDF)
- Build Issues → Check QUICKSTART.md troubleshooting

---

**Ready to begin?** Run `npm install && npm run dev` now!
