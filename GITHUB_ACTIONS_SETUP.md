# GitHub Actions APK Build Setup

This guide explains how to automatically build Android APKs using GitHub Actions every time you push code.

## 🎯 What This Does

The GitHub Actions workflow (`.github/workflows/build-apk.yml`) will automatically:
- Build a debug APK whenever you push to the `main` branch
- Build an APK when you manually trigger it
- Build and attach APK to releases when you create a new release
- Store the APK for 30 days so you can download it

## 📋 Setup Steps

### 1. Push Your Code to GitHub

First, you need to get your code on GitHub. From Replit:

**Option A: Using Replit Git Pane**
1. Click "Tools" in the left sidebar
2. Select "Git" 
3. Stage all files
4. Commit with message: "Initial commit with GitHub Actions"
5. Push to GitHub

**Option B: Using Shell**
```bash
git add .
git commit -m "Initial commit with GitHub Actions"
git push
```

### 2. Verify the Workflow

Once you push, GitHub Actions will automatically start building your APK!

1. Go to your GitHub repository
2. Click the "Actions" tab at the top
3. You'll see the "Build Android APK" workflow running
4. Wait 5-10 minutes for it to complete

### 3. Download Your APK

After the build completes:

1. Click on the completed workflow run
2. Scroll down to "Artifacts"
3. Click "thermal-printer-test-debug" to download the APK
4. Transfer to your Android device and install

## 🔄 How to Build a New APK

You have three ways to trigger a new build:

### Method 1: Push to Main Branch (Automatic)
```bash
# Make your changes, then:
git add .
git commit -m "Your changes description"
git push
```
The APK will build automatically!

### Method 2: Manual Trigger
1. Go to GitHub → Actions tab
2. Click "Build Android APK" workflow
3. Click "Run workflow" button
4. Select branch and click "Run workflow"

### Method 3: Create a Release (For important versions)
1. Go to GitHub → Releases
2. Click "Create a new release"
3. Tag version (e.g., v1.0.0)
4. Click "Publish release"
5. APK will be built and attached to the release

## 📱 Installing the APK

1. Download the APK from GitHub Actions artifacts
2. Transfer to your Android device (via USB, email, cloud, etc.)
3. On Android: Enable "Install from unknown sources" in Settings
4. Tap the APK file to install
5. Open "Thermal Printer Test" app

## ⚠️ Important Notes

### Debug APK vs Release APK

The current workflow builds a **debug APK**:
- ✅ Easy to build (no signing needed)
- ✅ Quick to test
- ❌ Larger file size
- ❌ Not optimized for production
- ❌ Cannot publish to Google Play Store

### To Build Release APK (Advanced)

For a production release APK, you'll need to:
1. Generate a signing key
2. Add key to GitHub Secrets
3. Modify the workflow to sign the APK

See "RELEASE_APK_SETUP.md" (to be created) for details.

### USB Printing Reminder

Remember: This APK will run the app, but **USB printing is still in simulation mode**. You need to implement the native Capacitor plugin first (see IMPLEMENTATION_STATUS.md).

## 🐛 Troubleshooting

### Build Failed
1. Check the Actions tab for error messages
2. Click on the failed workflow
3. Expand the failed step to see details
4. Common issues:
   - Node/Java version mismatch
   - Missing dependencies
   - Capacitor configuration errors

### Can't Download APK
- Make sure the workflow completed successfully (green checkmark)
- Artifacts are only available for 30 days
- You need to be logged into GitHub to download artifacts

### APK Won't Install on Android
- Enable "Install from unknown sources"
- Make sure it's a debug build (release builds need signing)
- Check Android version compatibility (requires Android 5.0+)

## 📊 Workflow Details

The workflow does the following:
1. ✅ Checks out your code
2. ✅ Sets up Node.js 20
3. ✅ Installs npm dependencies
4. ✅ Installs Capacitor dependencies
5. ✅ Sets up Java 17
6. ✅ Sets up Android SDK
7. ✅ Initializes Capacitor project
8. ✅ Adds Android platform
9. ✅ Syncs files to Android project
10. ✅ Builds debug APK with Gradle
11. ✅ Uploads APK as artifact
12. ✅ (Optional) Attaches to release if tagged

## 🎉 Quick Reference

**To get a new APK:**
1. Make changes to your code
2. Push to GitHub: `git push`
3. Wait 5-10 minutes
4. Go to GitHub → Actions
5. Download artifact from completed run

**APK Location in GitHub:**
- Actions tab → Latest run → Artifacts section

**APK Name:**
- `thermal-printer-test-debug.apk`

## 💡 Tips

- **Build time**: Usually 5-10 minutes per build
- **Storage**: APKs stored for 30 days automatically
- **Cost**: GitHub Actions is free for public repos (2,000 minutes/month for private)
- **Testing**: Each push triggers a build, so test locally first to save build minutes

## 🔗 Next Steps

1. Push your code to GitHub
2. Wait for first build to complete
3. Download and test the APK
4. When ready for real USB printing, implement the native plugin
5. Update the workflow to build release APKs (optional)

---

**Need help?** Check the GitHub Actions logs for detailed error messages.
