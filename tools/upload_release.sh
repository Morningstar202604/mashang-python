#!/bin/bash
# Create a GitCode release and upload the debug APK as attachment.
# Usage: ./tools/upload_release.sh <TAG> [RELEASE_NOTES_FILE]
#   Requires: GITCODE_TOKEN env var (GitCode 个人访问令牌，仓库有写权限)

set -euo pipefail

REPO_OWNER="badhope"
REPO_NAME="mashang-python"
API="https://api.gitcode.com/api/v5/repos/$REPO_OWNER/$REPO_NAME/releases"
TOKEN="${GITCODE_TOKEN:?please export GITCODE_TOKEN}"

TAG="${1:?usage: $0 <TAG> [RELEASE_NOTES_FILE]}"
NOTES_FILE="${2:-}"
APK_NAME="pynow-${TAG#v}.apk"

# 生产发布优先构建带签名+混淆的 release 包（需 KEYSTORE_* 环境变量）；
# 无签名凭据时退回 debug 包，仅用于内测预览。
if [ -n "${KEYSTORE_PATH:-}" ] && [ -n "${KEYSTORE_PASSWORD:-}" ] && [ -n "${KEY_PASSWORD:-}" ] && [ -n "${KEY_ALIAS:-}" ]; then
    echo "🔐 检测到签名凭据，构建 release APK …"
    ./gradlew :app:assembleRelease --no-daemon
    APK_PATH="app/build/outputs/apk/release/app-release.apk"
    BUILD_MODE="release"
else
    echo "⚠️ 未检测到签名凭据，退回 debug APK（仅内测，无法覆盖安装正式签名）"
    ./gradlew :app:assembleDebug --no-daemon
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    BUILD_MODE="debug"
fi

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK not found at $APK_PATH"
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

# 3) PUT 上传 APK（gitcode 附件走 OBS 签名直传，避免 shell 转义此处用 python3 完成）
echo "📤 uploading $APK_NAME ..."
GITCODE_API="$API" GITCODE_TOKEN="$TOKEN" GITCODE_TAG="$TAG" GITCODE_APK_NAME="$APK_NAME" GITCODE_APK_PATH="$APK_PATH" python3 - <<'PY'
import json, os, sys, urllib.error, urllib.request
api = os.environ["GITCODE_API"]
token = os.environ["GITCODE_TOKEN"]
tag = os.environ["GITCODE_TAG"]
apk_name = os.environ["GITCODE_APK_NAME"]
apk_path = os.environ["GITCODE_APK_PATH"]
with urllib.request.urlopen(f"{api}/{tag}/upload_url?access_token={token}&file_name={apk_name}") as r:
    info = json.load(r)
with open(apk_path, "rb") as f:
    data = f.read()
req = urllib.request.Request(info["url"], data=data, headers=info["headers"], method="PUT")
try:
    with urllib.request.urlopen(req, timeout=300) as r:
        print("upload status:", r.status)
except urllib.error.HTTPError as e:
    print("upload HTTPError:", e.code, e.read()[:200], file=sys.stderr)
    sys.exit(1)
PY

# GitCode 不支持 /releases/tag/<TAG> 与 /releases/download/... 直链，统一指向列表页
echo "✅ Release 列表页: https://gitcode.com/$REPO_OWNER/$REPO_NAME/releases"

# 4) 同步更新 catalog.json 的 app 段（应用内「检查更新」读取此段）
#    versionCode 从 app/build.gradle.kts 提取，与 APK 保持一致
VERSION_CODE=$(python3 -c "
import re
src = open('app/build.gradle.kts', encoding='utf-8').read()
m = re.search(r'versionCode\s*=\s*(\d+)', src)
print(m.group(1) if m else '0')
")
APK_SHA=$(sha256sum "$APK_PATH" | cut -d' ' -f1)
echo "🔁 update catalog.json: $TAG (code $VERSION_CODE) sha=$APK_SHA"
python3 tools/update_catalog.py \
    --version-name "${TAG#v}" \
    --version-code "$VERSION_CODE" \
    --apk-url "releases/$APK_NAME" \
    --apk-sha256 "$APK_SHA"

echo "ℹ️ 记得把 $APK_NAME 上传/提交到仓库 releases/ 目录（与 apk-url 对应），否则应用内更新无法下载。"