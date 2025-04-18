
# CVS插件 - IntelliJ IDEA 2024兼容性改造 Power by Cursor AI

本文档记录了为使CVS插件兼容IntelliJ IDEA 2024及更高版本所做的改动。

## 主要更改

1. **Java和Kotlin版本配置**
   - 使用Java 17作为编译目标
   - Kotlin版本降级到1.8.20，以确保与IDE兼容
   - Kotlin API和语言版本设置为1.8
   - 配置了Java工具链，确保使用Java 17

2. **构建工具更新**
   - Gradle版本更新到8.2
   - IntelliJ插件构建工具更新到1.16.1
   - 调整了sourceCompatibility和targetCompatibility设置

3. **插件兼容性范围**
   - 设置兼容性范围为241.14494（IDEA 2024.1）到243.*（IDEA 2024.3.x）
   - 在plugin.xml和构建配置中保持一致的版本设置

4. **依赖管理**
   - 移除了显式的依赖声明，改为使用IDE自带的库
   - 利用intellij插件自动解析所需依赖

5. **本地化**
   - 为中文用户更新了插件描述

6. **插件XML修正**
   - 修复了plugin.xml中的非法标签`<n>CVS</n>`，替换为标准的`<name>CVS</name>`
   - 解决了"Unknown element: n"错误，使插件能够被正确加载

## 构建说明

要构建插件，请使用以下命令：

```bash
# 清理并构建
gradlew.bat clean :cvs:build

# 仅编译Java代码
gradlew.bat :cvs:compileJava

# 构建插件JAR
gradlew.bat :cvs:buildPlugin
```

构建后的插件位于：`cvs/build/libs/cvs-2024.1.0.jar`

## 安装说明

1. 启动IntelliJ IDEA 2024
2. 进入 File > Settings > Plugins
3. 点击 ⚙️ 图标，选择 "Install Plugin from Disk..."
4. 选择生成的JAR文件
5. 重启IDE

## 故障排除

如果遇到构建问题：

1. 确保使用Java 17或更高版本
2. 检查Gradle版本是否为8.2
3. 尝试使用 `--refresh-dependencies` 参数刷新依赖
4. 检查镜像仓库是否可用

### 常见警告

1. **Java安装路径警告**
   
   如果看到以下警告：
   ```
   Directory 'C:\...\jdk-17.0.14+7' (Gradle property 'org.gradle.java.installations.paths') used for java installations does not exist
   ```
   
   这是由于gradle.properties中指定的Java安装路径不存在。解决方法：
   - 移除 `org.gradle.java.installations.paths` 属性
   - 或者修改为您系统上实际存在的JDK路径
   - 保留 `org.gradle.java.installations.auto-detect=true` 使Gradle自动检测JDK

2. **Kotlin版本警告**

   如果看到以下警告：
   ```
   The Kotlin plugin in version 1.8.20 used with the Gradle IntelliJ Plugin leads to the 'java.lang.OutOfMemoryError: Java heap space' exception
   ```
   
   这是由于Kotlin插件与Gradle IntelliJ插件一起使用时的已知内存问题。解决方法：
   - 这只是警告，通常不会影响构建
   - 如果确实出现内存问题，可以增加Gradle JVM内存 `-Xmx4g`
   - 或者尝试其他Kotlin版本（1.7.x或1.9.x）

3. **"version 63"错误**

   如果遇到 "Unsupported class file major version 63" 错误，说明尝试用Java 17运行Java 19编译的类文件。解决方法：
   - 确认您的JDK版本是Java 17
   - 所有Java编译选项都设置为Java 17兼容性
   - Kotlin编译选项也设置为适当的版本

4. **"Unknown element: n"错误**

   如果遇到以下错误：
   ```
   ERROR: Unknown element: n
   java.lang.Throwable: Unknown element: n
   ```
   
   这是由于plugin.xml文件中使用了无效的`<n>CVS</n>`标签。解决方法：
   - 在plugin.xml文件中，将`<n>CVS</n>`替换为`<name>CVS</name>`
   - 可以使用文本编辑器直接编辑文件
   - 重新构建项目

## 已知问题

CVS插件已经被官方标记为过时，仅按原样提供，未来可能不会有更多功能更新。 