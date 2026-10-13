# Rsporosh TV — Android Mobile + Android TV

## Features
- Opens `https://rs.porosh.com.bd/tv.m3u8` and plays live HLS on launch
- Android phones (landscape), Android TV and Android TV Box support
- Full-screen Media3 ExoPlayer, TV remote navigation, retry with exponential backoff
- Settings with reconnect and optional boot-start preference
- Uses secure HTTPS and no analytics or user accounts

## Build without coding
1. Install **Android Studio** (JDK 17 support). Download this project and extract it.
2. Android Studio > **Open** > select the `RsporoshTV` folder.
3. Allow Android Studio to download Gradle 8.9, Android Gradle Plugin 8.7.3 and SDK 35 (requires internet).
4. **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. APK will be at `app/build/outputs/apk/debug/app-debug.apk`.
6. Copy APK to Android phone/TV box; allow installation from your trusted source. On TV use an APK installer or ADB.

**Note:** This zip includes Gradle wrapper properties, but not `gradle-wrapper.jar` or launcher scripts. Android Studio can open/import the Gradle project directly. If CLI wrapper is needed, generate it with installed Gradle 8.9: `gradle wrapper --gradle-version 8.9`.

## Stream configuration
Change `STREAM_URL` in `app/src/main/java/bd/com/porosh/rsporoshtv/MainActivity.kt` if your server URL changes.

## TV / mobile behavior
- App appears in phone launcher and Android TV launcher.
- TV remote MENU opens Settings; BACK closes Settings.
- On phones the player is landscape by default. Modify `android:screenOrientation` if portrait support is desired.
- The `Request start after device boot` option is **not guaranteed** on modern Android. Android 10+ restricts background activity launches. For reliable boot-to-TV, deploy as a managed kiosk/device-owner solution or an OEM/default TV launcher on supported hardware. BootReceiver intentionally does not bypass platform restrictions.
- Many devices can resume the previously used HDMI/app after power restore, but this is device-specific.

## Production notes
- Test HLS segment playback on mobile data, Wi-Fi and TV boxes, not just HTTP 200.
- Ensure stream is properly encoded with H.264/AAC and compatible HLS segments.
- Protect publishing endpoints and use authenticated feeds for restricted distribution.
- TV banner is a placeholder shape; replace with a 320x180 branded PNG for polished TV launcher listing.
- Release distribution needs your own signing key; do not distribute debug APK as production.
- This source project has **not been compiled or device-tested** in the current environment.
