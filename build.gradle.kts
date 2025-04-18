fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.jetbrains.intellij") version "1.16.1"
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion")
    }
    
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            languageVersion = "1.8"
            apiVersion = "1.8"
        }
    }
    
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.release.set(17)
    }
    
    // 禁用不必要的任务
    withType<Test> {
        enabled = false
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

intellij {
    version.set("2024.1")
    type.set("IC") // Community Edition
    updateSinceUntilBuild.set(true)
}

allprojects {
    repositories {
        // 阿里云的 Maven 镜像仓库
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }

        // 清华大学的 Maven 镜像仓库
        maven { url = uri("https://mirrors.tuna.tsinghua.edu.cn/maven-central/") }
        maven { url = uri("https://mirrors.tuna.tsinghua.edu.cn/gradle-plugin/") }

        // 其他仓库
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.intellij")

    version = "2024.1." + (System.getenv("BUILD_NUMBER").takeIf { !it.isNullOrEmpty() } ?: "0")

    intellij {
        version.set("2024.1")
        type.set("IC") // Community Edition
        updateSinceUntilBuild.set(true)
    }

    tasks {
        withType<JavaCompile> {
            sourceCompatibility = "17"
            targetCompatibility = "17"
            options.release.set(17)
        }
        
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

        buildSearchableOptions {
            enabled = false
        }
        
        // 禁用不必要的任务
        withType<Test> {
            enabled = false
        }
    }
}
