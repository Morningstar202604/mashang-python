#!/bin/bash
# Create a GitHub release and upload the APK.
# Usage: ./tools/upload_release.sh <TAG> [TITLE] [RELEASE_NOTES_FILE]
#   Requires: gh CLI authenticated (gh auth login) or GH_TOKEN env var.

set -euo pipefail

REPO="Morningstar202604/mashang-python"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

TAG="${1:?usage: $0 <TAG> [TITLE] [RELEASE_NOTES_FILE]}"
TITLE="${2:-$TAG}"
NOTES_FILE="${3:-}"

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK not found at $APK_PATH"
    echo "Please build first: gradle :app:assembleDebug"
    exit 1
fi

echo "📦 APK Size: $(du -h "$APK_PATH" | cut -f1)"
echo ""

# Latest commit on main
SHA=$(gh api "repos/$REPO/commits/main" --jq '.sha')

echo "🔵 Creating GitHub release $TAG (target: ${SHA:0:12})..."

ARGS=(gh release create "$TAG" "$APK_PATH" --repo "$REPO" --title "$TITLE" --target "$SHA")
if [ -n "$NOTES_FILE" ] && [ -f "$NOTES_FILE" ]; then
    ARGS+=(--notes-file "$NOTES_FILE")
fi

if ! "${ARGS[@]}" --latest; then
    echo "⚠️  Release might already exist; falling back to only uploading the asset..."
    gh release upload "$TAG" "$APK_PATH" --repo "$REPO" --clobber
fi

echo "✅ Release complete: https://github.com/$REPO/releases/tag/$TAG"