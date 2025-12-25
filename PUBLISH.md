# Maven Central 发布指南

## 最简单的发布步骤（上传到Portal）

### 1. 更新版本号
在 `gradle.properties` 中修改版本号：
```properties
mavenCentral.version=1.0.1  # 更新为您的新版本号
```

### 2. 执行上传命令（一键完成）
```bash
export GPG_TTY=$(tty)  # 确保GPG签名可用
./gradlew clean createCentralBundle uploadToCentral
```

完成！部署包已上传到 [Maven Central Portal](https://central.sonatype.com/publishing/deployments)

---

## 完整命令（复制粘贴即可）

```bash
# 更新版本号后，执行以下命令
export GPG_TTY=$(tty)
./gradlew clean createCentralBundle uploadToCentral
```

上传完成后，您可以：
- 访问 [Portal部署页面](https://central.sonatype.com/publishing/deployments) 查看部署状态
- 在Portal UI中手动验证和发布部署

---

## 注意事项

1. **GPG签名**：如果签名失败，先缓存密码：
   ```bash
   export GPG_TTY=$(tty)
   echo "T@feeling1211" | gpg --batch --yes --pinentry-mode loopback --passphrase-fd 0 --clearsign --default-key F60455A7 <<< "test"
   ```

2. **版本号**：每次发布必须使用新的版本号，不能重复使用已发布的版本

3. **发布类型**：当前配置为 `USER_MANAGED`，需要手动发布。如果改为 `AUTOMATIC`，验证通过后会自动发布

---

## 快速上传脚本

使用项目中的 `publish.sh` 脚本：

```bash
./publish.sh
```

脚本会自动：
- 创建部署包（包含校验和和签名）
- 上传到Maven Central Portal
- 显示部署ID和Portal链接

