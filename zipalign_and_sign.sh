#!/bin/sh

# --- 配置区域 ---
SDK_PATH="$HOME/Android/Sdk/build-tools/34.0.0"
KEYSTORE_PATH="$HOME/Android/mykey.jks"
ALIAS="key0"  # 请替换为你的 Key Alias

INPUT_APK="app-release.apk"
ALIGNED_APK="app-aligned.apk"
FINAL_APK="app-final.apk"

# --- 执行流程 ---

echo "1. 开始对齐 (zipalign)..."
# 如果输出文件已存在，先删除
rm -f $ALIGNED_APK
$SDK_PATH/zipalign -v -p 4 $INPUT_APK $ALIGNED_APK

if [ $? -eq 0 ]; then
    echo "✅ 对齐成功！"
else
    echo "❌ 对齐失败，请检查输入文件。"
    exit 1
fi

echo "--------------------------------------"
echo "2. 开始签名 (apksigner)..."
# 使用 apksigner 进行签名（会自动包含 V2/V3 签名）
$SDK_PATH/apksigner sign --ks $KEYSTORE_PATH \
    --ks-key-alias $ALIAS \
    --out $FINAL_APK \
    $ALIGNED_APK

if [ $? -eq 0 ]; then
    echo "✅ 签名成功！最终文件: $FINAL_APK"
    echo "--------------------------------------"
    echo "3. 验证签名与对齐..."
    $SDK_PATH/apksigner verify -v $FINAL_APK
    $SDK_PATH/zipalign -c -v -p 4 $FINAL_APK
else
    echo "❌ 签名失败，请检查密码或别名。"
    exit 1
fi
