plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.chaquo.python")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.10"
}

android {
    namespace = "com.pyneon.academy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pyneon.academy"
        minSdk = 24
        targetSdk = 35
        versionCode = 10
        versionName = "0.3.4"
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    signingConfigs {
        // D1: 签名凭据改由环境变量注入；任一缺失则不创建 release 签名（不硬编码失败）
        val ksPath = System.getenv("KEYSTORE_PATH")
        val ksPass = System.getenv("KEYSTORE_PASSWORD")
        val keyPass = System.getenv("KEY_PASSWORD")
        val ksAlias = System.getenv("KEY_ALIAS")
        if (!ksPath.isNullOrBlank() && !ksPass.isNullOrBlank() && !keyPass.isNullOrBlank() && !ksAlias.isNullOrBlank()) {
            create("release") {
                storeFile = file(ksPath)
                storePassword = ksPass
                keyAlias = ksAlias
                keyPassword = keyPass
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // D1: 仅当环境变量齐备、release 签名已创建时才挂载，否则保持未签名
            signingConfigs.findByName("release")?.let { signingConfig = it }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources.excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    }
}

chaquopy {
    defaultConfig {
        version = "3.13"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    implementation(platform("androidx.compose:compose-bom:2026.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.navigation:navigation-compose:2.10.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // 缺失依赖补充：viewModelScope / asLiveData / liveData 来自 lifecycle-ktx
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    // LiveData → Compose State 桥接（observeAsState，来自 androidx.compose.runtime.livedata）
    implementation("androidx.compose.runtime:runtime-livedata:1.7.6")
    // kotlinx-serialization：BackupUtil 备份 JSON 序列化所需
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // QR code for certificate poster
    implementation("com.google.zxing:core:3.5.4")
}
