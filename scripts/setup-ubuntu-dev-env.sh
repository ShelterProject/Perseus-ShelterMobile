#!/usr/bin/env bash
#
# All-in-one Android dev environment installer for Ubuntu 24.04 / Zorin OS 18
# (Zorin 18 is Ubuntu 24.04-based, so this script works on either unmodified).
#
# Installs: OpenJDK 17, Android SDK command-line tools, platform-tools,
# build-tools, platform 34, licenses accepted, env vars wired up, and
# (optionally) Android Studio + an emulator so app_v2 can be built and run.
#
# Usage:
#   chmod +x setup-ubuntu-dev-env.sh
#   ./setup-ubuntu-dev-env.sh                 # CLI SDK only (enough for ./gradlew assembleDebug)
#   ./setup-ubuntu-dev-env.sh --with-studio    # also installs Android Studio (snap) + one emulator
#
# Safe to re-run: every step checks whether it's already done first.

set -euo pipefail

WITH_STUDIO=false
WITH_EMULATOR=false
for arg in "$@"; do
  case "$arg" in
    --with-studio) WITH_STUDIO=true ;;
    --with-emulator) WITH_EMULATOR=true ;;
    -h|--help)
      grep '^#' "$0" | sed 's/^#//'
      exit 0
      ;;
    *)
      echo "Unknown option: $arg" >&2
      exit 1
      ;;
  esac
done

log() { echo -e "\n\033[1;34m==>\033[0m $*"; }
warn() { echo -e "\033[1;33m[warn]\033[0m $*" >&2; }

if [[ $EUID -eq 0 ]]; then
  SUDO=""
else
  SUDO="sudo"
fi

ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$HOME/Android/Sdk}"
CMDLINE_TOOLS_VERSION="11076708" # commandlinetools-linux; bump if https://developer.android.com/studio#command-tools lists a newer one
PLATFORM_VERSION="android-34"
BUILD_TOOLS_VERSION="34.0.0"

# ---------------------------------------------------------------------------
log "Updating apt and installing base packages (JDK 17, git, curl, unzip, KVM)"
# ---------------------------------------------------------------------------
$SUDO apt-get update -y
$SUDO apt-get install -y \
  openjdk-17-jdk-headless \
  git \
  curl \
  wget \
  unzip \
  ca-certificates \
  qemu-kvm \
  libvirt-daemon-system \
  libvirt-clients \
  bridge-utils

if getent group kvm >/dev/null 2>&1; then
  $SUDO usermod -aG kvm "$USER" || true
fi

JAVA_HOME_PATH="$(dirname "$(dirname "$(readlink -f "$(command -v javac)")")")"
log "JAVA_HOME resolved to: $JAVA_HOME_PATH"

# ---------------------------------------------------------------------------
log "Setting up Android SDK at $ANDROID_SDK_ROOT"
# ---------------------------------------------------------------------------
mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"

if [[ ! -x "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]]; then
  log "Downloading Android command-line tools"
  TMP_ZIP="$(mktemp --suffix=.zip)"
  wget -q --show-progress -O "$TMP_ZIP" \
    "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"
  TMP_EXTRACT="$(mktemp -d)"
  unzip -q "$TMP_ZIP" -d "$TMP_EXTRACT"
  rm -rf "$ANDROID_SDK_ROOT/cmdline-tools/latest"
  mv "$TMP_EXTRACT/cmdline-tools" "$ANDROID_SDK_ROOT/cmdline-tools/latest"
  rm -rf "$TMP_ZIP" "$TMP_EXTRACT"
else
  log "Command-line tools already present, skipping download"
fi

export ANDROID_SDK_ROOT
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator:$PATH"

SDKMANAGER="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"

log "Accepting Android SDK licenses"
yes | "$SDKMANAGER" --sdk_root="$ANDROID_SDK_ROOT" --licenses >/dev/null || true

log "Installing platform-tools, build-tools $BUILD_TOOLS_VERSION, $PLATFORM_VERSION"
"$SDKMANAGER" --sdk_root="$ANDROID_SDK_ROOT" \
  "platform-tools" \
  "platforms;$PLATFORM_VERSION" \
  "build-tools;$BUILD_TOOLS_VERSION"

if $WITH_EMULATOR || $WITH_STUDIO; then
  log "Installing emulator + system image for AVD"
  "$SDKMANAGER" --sdk_root="$ANDROID_SDK_ROOT" \
    "emulator" \
    "system-images;$PLATFORM_VERSION;google_apis;x86_64"

  if ! "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" list avd | grep -q "shelter_dev"; then
    log "Creating AVD 'shelter_dev'"
    echo "no" | "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" create avd \
      --name "shelter_dev" \
      --package "system-images;$PLATFORM_VERSION;google_apis;x86_64" \
      --device "pixel_6"
  fi
fi

# ---------------------------------------------------------------------------
if $WITH_STUDIO; then
  log "Installing Android Studio via snap"
  if ! command -v snap >/dev/null 2>&1; then
    $SUDO apt-get install -y snapd
  fi
  $SUDO snap install android-studio --classic
fi

# ---------------------------------------------------------------------------
log "Wiring up environment variables"
# ---------------------------------------------------------------------------
ENV_BLOCK_MARKER="# >>> shelter-android-dev-env >>>"
ENV_BLOCK_END="# <<< shelter-android-dev-env <<<"
ENV_BLOCK="$ENV_BLOCK_MARKER
export ANDROID_SDK_ROOT=\"$ANDROID_SDK_ROOT\"
export ANDROID_HOME=\"\$ANDROID_SDK_ROOT\"
export PATH=\"\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:\$ANDROID_SDK_ROOT/platform-tools:\$ANDROID_SDK_ROOT/emulator:\$PATH\"
$ENV_BLOCK_END"

for rc in "$HOME/.bashrc" "$HOME/.zshrc"; do
  [[ -f "$rc" ]] || continue
  if grep -qF "$ENV_BLOCK_MARKER" "$rc"; then
    # Replace the existing block so re-running the script keeps it up to date.
    sed -i "/$ENV_BLOCK_MARKER/,/$ENV_BLOCK_END/d" "$rc"
  fi
  printf '\n%s\n' "$ENV_BLOCK" >> "$rc"
  log "Updated $rc"
done

# ---------------------------------------------------------------------------
log "Verifying setup"
# ---------------------------------------------------------------------------
echo "java: $(java -version 2>&1 | head -1)"
echo "ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT"
"$SDKMANAGER" --sdk_root="$ANDROID_SDK_ROOT" --list_installed

log "Done. Open a NEW terminal (or 'source ~/.bashrc') so ANDROID_HOME/PATH take effect, then:"
echo "    cd app_v2 && ./gradlew :app:assembleDebug"
if $WITH_EMULATOR || $WITH_STUDIO; then
  echo "    emulator -avd shelter_dev &"
fi
