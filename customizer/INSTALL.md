# Installing the debug APK on a Redmi 9C

No root, unlocked bootloader, custom ROM or system-partition change is needed. The APK is an
ordinary app; Android only has to be allowed to install it.

## Before installing
1. Run `customizer/tools/check-device.sh` with the phone connected. Check `ro.build.version.sdk`:
   Lawnchair's declared `minSdk` is 26 (Android 8.0). Below that it will not install.
2. Quickstep/recents integration is documented for Android 15-16 only, so on older Android keep the
   default recents provider; expect gestures to come from MIUI.

## Install (either way)
- **adb:** `adb install -r Lawnchair.<version>.github.debug.apk`
- **On phone:** copy the APK, open it, allow "Install unknown apps" for that file manager when MIUI asks
  (MIUI may also ask to confirm via "Install via USB" in Developer options).

The debug build uses the package `app.lawnchair.debug`, so it installs beside any other launcher
and is removed with a normal uninstall.

## Make it the home app
Settings > Apps > Default apps > Home app > Lawnchair. Pick back the stock launcher there to revert.

## Use the customizer
Open Lawnchair settings > overflow menu > Customizer, pick a preset. Revert by choosing "Default".

## Recovery
If the launcher misbehaves: Settings > Apps > Default apps > Home app > stock launcher, then
uninstall Lawnchair. Nothing outside the app is modified.
