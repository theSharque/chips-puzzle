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

PLAY_STORE_PASSWORD="${PLAY_STORE_PASSWORD:-$BALLMAZE_STORE_PASSWORD}"
PLAY_KEY_PASSWORD="${PLAY_KEY_PASSWORD:-$BALLMAZE_KEY_PASSWORD}"
PLAY_KEY_ALIAS="${PLAY_KEY_ALIAS:-saturn}"
KEYSTORE="${PLAY_KEYSTORE:-$SCRIPT_DIR/../key/saturn7}"
if [ "${KEYSTORE#/}" = "$KEYSTORE" ]; then
    KEYSTORE="$SCRIPT_DIR/$KEYSTORE"
fi
ALIAS="$PLAY_KEY_ALIAS"
FINAL_AAB="chipspuzzle-release-v${VERSION_NAME}.aab"

if [ ! -f "$KEYSTORE" ]; then
    echo "AAB keystore not found: $KEYSTORE"
    exit 1
fi

echo "Building Play AAB v${VERSION_NAME}"
echo "  $KEYSTORE alias $ALIAS"

if [ -z "$PLAY_STORE_PASSWORD" ]; then
    read -rsp "Keystore password (saturn7): " PLAY_STORE_PASSWORD
    echo
fi
if [ -z "$PLAY_KEY_PASSWORD" ]; then
    if [ -t 0 ]; then
        read -rsp "Key password (Enter = same): " PLAY_KEY_PASSWORD
        echo
    fi
fi
if [ -z "$PLAY_KEY_PASSWORD" ]; then
    PLAY_KEY_PASSWORD="$PLAY_STORE_PASSWORD"
fi
export PLAY_STORE_PASSWORD
export PLAY_KEY_PASSWORD
export PLAY_KEYSTORE="$KEYSTORE"
export PLAY_KEY_ALIAS="$ALIAS"

if ! keytool -list -keystore "$KEYSTORE" -alias "$ALIAS" -storepass:env PLAY_STORE_PASSWORD >/dev/null 2>&1; then
    echo "Wrong password for saturn7"
    exit 1
fi

./gradlew :app:bundleRelease

AAB_PATH="app/build/outputs/bundle/release/app-release.aab"
if [ ! -f "$AAB_PATH" ]; then
    echo "Build failed - AAB not found"
    exit 1
fi

cp "$AAB_PATH" "$FINAL_AAB"

echo ""
echo "AAB ready: $SCRIPT_DIR/$FINAL_AAB  ($(du -h "$FINAL_AAB" | cut -f1))"
echo "Sign: saturn / SHA1 4F:D2:AF:3D:22:5E:97:80:98:0C:22:B9:39:3A:A2:07:04:4C:2C:14"
echo "Upload this file to Google Play."
