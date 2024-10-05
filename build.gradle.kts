import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin.Companion.isUseK2

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.addzero"
version = "1.2"
configurations.all {
    resolutionStrategy.sortArtifacts(ResolutionStrategy.SortOrder.DEPENDENCY_FIRST)
}
repositories {
    mavenLocal()
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    maven { url = uri("https://mirrors.huaweicloud.com/repository/maven/") }
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://repo.spring.io/milestone") }
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {


    version.set("2022.2")
    type.set("IC") // Target IDE Platform
    plugins.set(
        listOf(
            "com.intellij.java",
            "org.jetbrains.kotlin"
        )
    )



//    localPath.set(ideahome)
    version.set("2023.2.6")
    type.set("IC") // Target IDE Platform
//    plugins.set(
//        listOf(
//    "com.intellij.java", "org.jetbrains.kotlin"

//        )
//    )
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.belerweb:pinyin4j:2.5.1")
    implementation("cn.hutool:hutool-all:5.8.25")
    implementation("com.alibaba:fastjson:2.0.52")
}


tasks {
// 将依赖打进jar包中
//    jar.configure {
//        duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.INCLUDE
//        from(configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) })
//    }

    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions{
            jvmTarget = "17"
            freeCompilerArgs += "-Xuse-k2" // 启用 K2 编译器
        }
    }

    patchPluginXml {
        sinceBuild.set("222.3345.118")
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}