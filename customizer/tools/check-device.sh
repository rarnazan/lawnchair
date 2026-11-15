#!/usr/bin/env bash
# Read-only device check for the Redmi 9C. Needs USB debugging enabled and `adb` on PATH.
# It only runs `getprop`; it does not root, unlock, flash or write anything.
set -euo pipefail
command -v adb >/dev/null || { echo "adb not found"; exit 1; }
adb get-state >/dev/null 2>&1 || { echo "no authorised device attached (check USB debugging prompt)"; exit 1; }

for k in ro.product.model ro.product.device ro.build.version.release ro.build.version.sdk \
         ro.miui.ui.version.name ro.miui.ui.version.code ro.build.version.incremental \
         ro.boot.flash.locked ro.boot.verifiedbootstate ro.boot.vbmeta.device_state; do
  printf '%-34s %s\n' "$k" "$(adb shell getprop "$k" | tr -d '\r')"
done
echo
echo "Expected for Redmi 9C: ro.product.device is one of angelica / angelican / angelicain"
echo "ro.boot.flash.locked: 1 = locked, 0 = unlocked. Empty values mean 'not reported', not 'fine'."
