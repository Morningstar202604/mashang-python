#!/bin/bash
# Upload APK to GitHub, Gitee, and GitCode releases
# Usage: ./tools/upload_release.sh [GITHUB_TOKEN] [GITEE_TOKEN] [GITCODE_TOKEN]

set -e

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
TAG="v0.3.3"
TITLE="v0.3.3 - Architecture Overhaul & Multilingual Support"
RELEASE_NOTES="RELEASE_NOTES_v0.3.3.md"

# Check if APK exists
if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK not found at $APK_PATH"
    echo "Please build first: gradle :app:assembleDebug"
    exit 1
fi

echo "📦 APK Size: $(du -h $APK_PATH | cut -f1)"
echo ""

# ============================================
# 1. GitHub Release
# ============================================
echo "🔵 Creating GitHub Release..."

if [ -n "$1" ]; then
    GITHUB_TOKEN="$1"
else
    # Try to get from environment or gh CLI config
    GITHUB_TOKEN=$(gh auth token 2>/dev/null || echo "")
fi

if [ -n "$GITHUB_TOKEN" ]; then
    # Create release via API
    RESPONSE=$(curl -s -X POST \
        -H "Authorization: token $GITHUB_TOKEN" \
        -H "Accept: application/vnd.github.v3+json" \
        https://api.github.com/repos/Morningstar202604/mashang-python/releases \
        -d "{
            \"tag_name\": \"$TAG\",
            \"name\": \"$TITLE\",
            \"body\": \"$(cat $RELEASE_NOTES | sed ':a;N;$!ba;s/\n/\\n/g')\",
            \"draft\": false,
            \"prerelease\": false
        }")

    UPLOAD_URL=$(echo "$RESPONSE" | grep -o '"upload_url":"[^"]*' | cut -d'"' -f4 | sed 's/{.*}//')

    if [ -n "$UPLOAD_URL" ]; then
        echo "✅ Release created, uploading APK..."
        curl -s -X POST \
            -H "Authorization: token $GITHUB_TOKEN" \
            -H "Content-Type: application/vnd.android.package-archive" \
            --data-binary @"$APK_PATH" \
            "$UPLOAD_URL?name=app-debug.apk&label=PY//NOW%20Debug%20APK"
        echo "✅ GitHub Release complete!"
    else
        echo "⚠️  Could not create GitHub release automatically"
        echo "Response: $RESPONSE"
        echo "Please create manually at: https://github.com/Morningstar202604/mashang-python/releases/new"
    fi
else
    echo "⚠️  No GitHub token found"
    echo "Please create release manually at: https://github.com/Morningstar202604/mashang-python/releases/new"
    echo "Or set GITHUB_TOKEN environment variable"
fi

echo ""

# ============================================
# 2. Gitee Release
# ============================================
echo "🟡 Creating Gitee Release..."

if [ -n "$2" ]; then
    GITEE_TOKEN="$2"
else
    GITEE_TOKEN="${GITEE_TOKEN:-}"
fi

if [ -n "$GITEE_TOKEN" ]; then
    # Create release on Gitee
    RESPONSE=$(curl -s -X POST \
        "https://gitee.com/api/v5/repos/badhope/mashang-python/releases" \
        -F "access_token=$GITEE_TOKEN" \
        -F "tag_name=$TAG" \
        -F "name=$TITLE" \
        -F "body=@$RELEASE_NOTES" \
        -F "target_commitish=main")

    RELEASE_ID=$(echo "$RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

    if [ -n "$RELEASE_ID" ]; then
        echo "✅ Gitee release created (ID: $RELEASE_ID), uploading APK..."
        curl -s -X POST \
            "https://gitee.com/api/v5/repos/badhope/mashang-python/releases/$RELEASE_ID/attach_files" \
            -F "access_token=$GITEE_TOKEN" \
            -F "file=@$APK_PATH"
        echo "✅ Gitee Release complete!"
    else
        echo "⚠️  Could not create Gitee release automatically"
        echo "Please create manually at: https://gitee.com/badhope/mashang-python/releases"
    fi
else
    echo "⚠️  No Gitee token found"
    echo "Please create release manually at: https://gitee.com/badhope/mashang-python/releases"
    echo "Or set GITEE_TOKEN environment variable"
fi

echo ""

# ============================================
# 3. GitCode Release
# ============================================
echo "🟣 Creating GitCode Release..."
echo "⚠️  GitCode API upload requires manual steps"
echo "Please visit: https://gitcode.com/badhope/mashang-python/releases"
echo "Create new release with tag: $TAG"
echo "Upload APK: $APK_PATH"

echo ""
echo "=========================================="
echo "📋 Summary"
echo "=========================================="
echo "APK Location: $APK_PATH"
echo "Tag: $TAG"
echo ""
echo "Manual Upload Links:"
echo "  GitHub:  https://github.com/Morningstar202604/mashang-python/releases/new"
echo "  Gitee:   https://gitee.com/badhope/mashang-python/releases"
echo "  GitCode: https://gitcode.com/badhope/mashang-python/releases"
echo ""
echo "Release notes saved to: $RELEASE_NOTES"
