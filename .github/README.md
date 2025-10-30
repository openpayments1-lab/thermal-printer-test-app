# GitHub Actions Workflows

This directory contains automated workflows for the Thermal Printer Test app.

## Available Workflows

### 🏗️ Build Android APK
**File:** `build-apk.yml`

**Triggers:**
- Every push to `main` branch (automatic)
- Manual trigger from GitHub Actions tab
- When creating a new release

**Output:**
- Debug APK file
- Available in Actions → Artifacts for 30 days
- Automatically attached to releases

**Build Time:** ~5-10 minutes

## Quick Start

1. **Push your code to GitHub**
   ```bash
   git push
   ```

2. **Watch it build**
   - Go to GitHub repository
   - Click "Actions" tab
   - View build progress

3. **Download APK**
   - Click completed workflow
   - Scroll to "Artifacts"
   - Download APK

## Documentation

See `GITHUB_ACTIONS_SETUP.md` in the root directory for complete setup and usage instructions.
