plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
    `signing`
}

android {
    namespace = "com.joytalk.shuck.library"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

// 从gradle.properties读取配置
val groupId: String = project.findProperty("mavenCentral.groupId") as String? ?: "com.joytalk.shuck"
val artifactId: String = project.findProperty("mavenCentral.artifactId") as String? ?: "library"
val versionName: String = project.findProperty("mavenCentral.version") as String? ?: "1.0.0"
val projectName: String = project.findProperty("mavenCentral.name") as String? ?: "Shuck Library"
val projectDescription: String = project.findProperty("mavenCentral.description") as String? ?: "Shuck Library for Android"
val projectUrl: String = project.findProperty("mavenCentral.url") as String? ?: ""
val licenseName: String = project.findProperty("mavenCentral.license.name") as String? ?: "Apache-2.0"
val licenseUrl: String = project.findProperty("mavenCentral.license.url") as String? ?: "https://www.apache.org/licenses/LICENSE-2.0.txt"
val developerId: String = project.findProperty("mavenCentral.developer.id") as String? ?: ""
val developerName: String = project.findProperty("mavenCentral.developer.name") as String? ?: ""
val developerEmail: String = project.findProperty("mavenCentral.developer.email") as String? ?: ""
val scmUrl: String = project.findProperty("mavenCentral.scm.url") as String? ?: projectUrl

// GPG签名配置
val signingKeyId: String? = project.findProperty("mavenCentral.signing.keyId") as String?
val signingPassword: String? = project.findProperty("mavenCentral.signing.password") as String?
val signingSecretKeyRingFile: String? = project.findProperty("mavenCentral.signing.secretKeyRingFile") as String?

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    // 引用 libs 目录下的 jar 包
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

// GPG签名配置
// 注意：发布到Maven Central需要GPG签名
// 使用gpg命令进行签名，密码通过gpg-agent缓存
// 在运行构建前，请先缓存密码：echo "your-password" | gpg --batch --yes --pinentry-mode loopback --passphrase-fd 0 --clearsign --default-key F60455A7 <<< "test"
val signingEnabled = project.findProperty("mavenCentral.signing.enabled") as String? != "false"
if (signingKeyId != null && signingEnabled) {
    signing {
        useGpgCmd()
        sign(publishing.publications)
    }
} else if (!signingEnabled) {
    logger.warn("GPG签名已禁用。发布到Maven Central需要签名，请设置mavenCentral.signing.enabled=true")
} else {
    logger.warn("GPG签名未配置。发布到Maven Central需要签名，请在gradle.properties中设置mavenCentral.signing.keyId")
}

// Maven发布配置
mavenPublishing {
    coordinates(
        groupId = groupId,
        artifactId = artifactId,
        version = versionName
    )

    pom {
        name.set(projectName)
        description.set(projectDescription)
        url.set(projectUrl)
        
        licenses {
            license {
                name.set(licenseName)
                url.set(licenseUrl)
                distribution.set("repo")
            }
        }
        
        if (developerId.isNotEmpty() && developerName.isNotEmpty() && developerEmail.isNotEmpty()) {
            developers {
                developer {
                    id.set(developerId)
                    name.set(developerName)
                    email.set(developerEmail)
                }
            }
        }
        
        if (scmUrl.isNotEmpty()) {
            scm {
                url.set(scmUrl)
                connection.set("scm:git:${scmUrl.replace("https://", "https://").replace("http://", "git://")}.git")
                developerConnection.set("scm:git:${scmUrl.replace("https://", "ssh://git@").replace("http://", "ssh://git@")}.git")
            }
        }
    }
}

