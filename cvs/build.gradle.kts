import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.16.1"
    id("org.jetbrains.kotlin.jvm") version "1.8.20"  // 降级到1.8.20
}

// 添加编译选项以忽略已弃用警告
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:none")  // 禁用所有警告
    options.release.set(17)  // 使用Java 17以支持2024版本
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
        jvmTarget = "17"  // 使用Java 17
        languageVersion = "1.8"  // 降级到1.8
        apiVersion = "1.8"  // 降级到1.8
    }
}

sourceSets {
    main {
        java.srcDirs(listOf("cvs-core/src", "cvs-plugin/src", "javacvs-src", "smartcvs-src"))
        resources.srcDirs( listOf("cvs-core/resources", "cvs-plugin/resources", "javacvs-src", "smartcvs-src"))
    }

//    test {
//        java.srcDirs("testSource")
//    }
}

dependencies {
    implementation(files("lib/trilead-ssh2-build213.jar"))
    // 使用intellij插件提供的依赖，而不是直接指定版本
    // 删除这些显式的依赖
    // compileOnly("com.jetbrains.intellij.platform:core-api:241.14494.158")
    // compileOnly("com.jetbrains.intellij.platform:vcs-api:241.14494.158")
}

version = "2024.1.0"

intellij {
    version.set("2024.1")  // 使用2024.1版本
    type.set("IC") // Community Edition
    plugins.set(listOf()) // 清空插件依赖
    updateSinceUntilBuild.set(true)
}

// 配置Java 17兼容性
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// 添加插件发布配置
tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            languageVersion = "1.8"
            apiVersion = "1.8"
        }
    }
    
    patchPluginXml {
        sinceBuild.set("241.14494")  // 2024.1版本
        untilBuild.set("243.*")      // 兼容到2024.3.x
    }
    
    publishPlugin {
        token.set(System.getenv("PUBLISH_PLUGIN_TOKEN"))
    }
    
    // 禁用不必要的任务
    withType<Test> {
        enabled = false
    }
    
    // 添加更多额外配置以防止编译问题
    compileKotlin {
        kotlinOptions.suppressWarnings = true
    }
}
