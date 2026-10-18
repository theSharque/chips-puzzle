#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ -f "$SCRIPT_DIR/.env" ]; then
    set -a
    # shellcheck disable=SC1091
    . "$SCRIPT_DIR/.env"
    set +a
fi

VERSION_NAME=$(grep 'versionName' app/build.gradle | head -n1 | sed -E 's/.*"([^"]+)".*/\1/')
if [ -z "$VERSION_NAME" ]; then
    VERSION_NAME="unknown"
fi

APK_STORE_PASSWORD="${APK_STORE_PASSWORD:-$BALLMAZE_APK_STORE_PASSWORD}"
APK_KEY_PASSWORD="${APK_KEY_PASSWORD:-$BALLMAZE_APK_KEY_PASSWORD}"
APK_KEY_ALIAS="${APK_KEY_ALIAS:-ballmaze}"
KEYSTORE="${APK_KEYSTORE:-$SCRIPT_DIR/../Ballmaze/ballmaze-release-key.keystore}"
if [ "${KEYSTORE#/}" = "$KEYSTORE" ]; then
    KEYSTORE="$SCRIPT_DIR/$KEYSTORE"
fi
ALIAS="$APK_KEY_ALIAS"
FINAL_APK="chipspuzzle-release-v${VERSION_NAME}.apk"

if [ ! -f "$KEYSTORE" ]; then
    echo "APK keystore not found: $KEYSTORE"
    echo "Set APK_KEYSTORE in .env"
    exit 1
fi

SDK="${ANDROID_HOME:-$HOME/Android/Sdk}"
APKSIGNER=$(ls -1d "$SDK"/build-tools/*/apksigner 2>/dev/null | sort -V | tail -1)
if [ -z "$APKSIGNER" ] || [ ! -x "$APKSIGNER" ]; then
    echo "apksigner not found under $SDK/build-tools"
    exit 1
fi

echo "Building other-store APK v${VERSION_NAME}"
echo "  $KEYSTORE alias $ALIAS"

if [ -z "$APK_STORE_PASSWORD" ]; then
    echo "APK_STORE_PASSWORD is missing. Copy .env.example to .env"
    exit 1
fi
if [ -z "$APK_KEY_PASSWORD" ]; then
    APK_KEY_PASSWORD="$APK_STORE_PASSWORD"
fi
export APK_STORE_PASSWORD
export APK_KEY_PASSWORD
export APK_KEYSTORE="$KEYSTORE"
export APK_KEY_ALIAS="$ALIAS"

if ! keytool -list -keystore "$KEYSTORE" -alias "$ALIAS" -storepass:env APK_STORE_PASSWORD >/dev/null 2>&1; then
    echo "Wrong password for APK keystore"
    echo "This is not saturn7."
    exit 1
fi

./gradlew :app:assembleRelease -PreleaseSigner=apk

APK_PATH="app/build/outputs/apk/release/app-release.apk"
if [ ! -f "$APK_PATH" ]; then
    echo "Build failed - APK not found"
    exit 1
fi

cp "$APK_PATH" "$FINAL_APK"
"$APKSIGNER" verify --print-certs "$FINAL_APK" | grep -E 'Signer #1 certificate (DN|SHA-1)'

echo ""
echo "APK ready: $SCRIPT_DIR/$FINAL_APK  ($(du -h "$FINAL_APK" | cut -f1))"
echo "Sign: ballmaze / SHA1 41:34:E3:A2:42:1C:2E:93:72:36:E8:CF:AF:3F:8F:4E:F0:26:BD:87"
echo "Upload this file to the other store."
