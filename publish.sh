#!/bin/bash
# Maven Central 上传脚本 - 上传到Portal

set -e

export GPG_TTY=$(tty)

echo "🚀 开始上传到Maven Central Portal..."
echo ""

# 0. 确保GPG密码已缓存
echo "🔐 配置GPG签名..."
GPG_KEY_ID="F60455A7"
GPG_PASSWORD="T@feeling1211"

# 确保gpg-agent正在运行
if ! pgrep -x "gpg-agent" > /dev/null; then
    echo "启动gpg-agent..."
    gpg-agent --daemon > /dev/null 2>&1 || true
    sleep 1
fi

# 缓存GPG密码到gpg-agent（通过执行一次签名操作）
echo "正在缓存GPG密码..."
TEST_FILE="/tmp/gpg_test_$$.txt"
echo "test" > "$TEST_FILE"
echo "$GPG_PASSWORD" | gpg --batch --yes --pinentry-mode loopback --passphrase-fd 0 --default-key "$GPG_KEY_ID" --sign "$TEST_FILE" > /dev/null 2>&1

if [ $? -eq 0 ]; then
    echo "✅ GPG密码已缓存"
    rm -f "$TEST_FILE" "${TEST_FILE}.gpg" 2>/dev/null
else
    echo "⚠️  GPG密码缓存失败"
    echo "   尝试使用交互式方式..."
    # 如果批处理失败，提示用户手动输入
    echo "   请手动运行以下命令缓存密码："
    echo "   echo '$GPG_PASSWORD' | gpg --batch --yes --pinentry-mode loopback --passphrase-fd 0 --default-key $GPG_KEY_ID --sign <<< 'test'"
    rm -f "$TEST_FILE" "${TEST_FILE}.gpg" 2>/dev/null
fi

echo ""

# 1. 创建并上传部署包
echo "📦 创建部署包并上传..."
./gradlew clean createCentralBundle uploadToCentral

# 2. 获取部署ID
DEPLOYMENT_ID=$(cat build/central-bundle/deployment-id.txt 2>/dev/null || echo "")
if [ -z "$DEPLOYMENT_ID" ]; then
    echo "❌ 未找到部署ID，请检查上传是否成功"
    exit 1
fi

echo ""
echo "✅ 部署包已成功上传到Portal！"
echo ""
echo "📋 部署ID: $DEPLOYMENT_ID"
echo "🌐 Portal链接: https://central.sonatype.com/publishing/deployments"
echo ""
echo "📝 下一步："
echo "   1. 访问Portal查看部署状态和验证结果"
echo "   2. 验证通过后，在Portal UI中手动发布部署"
echo ""

