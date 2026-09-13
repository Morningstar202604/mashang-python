#!/bin/bash
# Create a GitCode release and upload the debug APK as attachment.
# Usage: ./tools/upload_release.sh <TAG> [RELEASE_NOTES_FILE]
#   Requires: GITCODE_TOKEN env var (GitCode 个人访问令牌，仓库有写权限)

set -euo pipefail

REPO_OWNER="badhope"
REPO_NAME="mashang-python"
API="https://api.gitcode.com/api/v5/repos/$REPO_OWNER/$REPO_NAME/releases"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
TOKEN="${GITCODE_TOKEN:?please export GITCODE_TOKEN}"

TAG="${1:?usage: $0 <TAG> [RELEASE_NOTES_FILE]}"
NOTES_FILE="${2:-}"
APK_NAME="pynow-${TAG#v}.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK not found at $APK_PATH"
    echo "Please build first: ./gradlew :app:assembleDebug"
    exit 1
fi

echo "📦 APK: $APK_PATH ($(du -h "$APK_PATH" | cut -f1)) → $APK_NAME"

if [ -n "$NOTES_FILE" ] && [ -f "$NOTES_FILE" ]; then
    BODY=$(cat "$NOTES_FILE")
else
    BODY="Release $TAG"
fi

# 1) Create release（已存在则忽略错误）
PAYLOAD=$(python3 -c "import json,sys; print(json.dumps({'tag_name': sys.argv[1], 'name': sys.argv[1], 'body': sys.argv[2]}))" "$TAG" "$BODY")
HTTP=$(curl -sS -o /tmp/gitcode_release.json -w "%{http_code}" \
    -X POST "$API?access_token=$TOKEN" \
    -H "Content-Type: application/json;charset=UTF-8" \
    -d "$PAYLOAD")
echo "🔵 create release HTTP $HTTP"

# 2) 获取附件上传地址（官方 upload_url 接口）
UP=$(curl -sS "$API/$TAG/upload_url?access_token=$TOKEN&file_name=$APK_NAME")
URL=$(echo "$UP" | python3 -c "import json,sys; print(json.load(sys.stdin)['url'])")
HEADERS=$(echo "$UP" | python3 -c "import json,sys; print(' '.join('-H \"%s: %s\"' % (k,v) for k,v in json.load(sys.stdin)['headers'].items()))")

# 3) PUT 上传 APK（gitcode 附件走 OBS 签名直传）
echo "📤 uploading $APK_NAME ..."
eval "curl -sS -o /dev/null -w 'upload HTTP %{http_code}\\n' -X PUT \"$URL\" $HEADERS --data-binary @\"$APK_PATH\""

echo "✅ Release: https://gitcode.com/$REPO_OWNER/$REPO_NAME/releases/tag/$TAG"