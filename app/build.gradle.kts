import com.github.megatronking.stringfog.plugin.StringFogPlugin

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

apply<StringFogPlugin>()

configure<com.github.megatronking.stringfog.plugin.StringFogExtension> {
    implementation = "com.github.megatronking.stringfog.xor.StringFogImpl"// 必要：加解密库的实现类路径，需和上面配置的加解密算法库一致。
    enable = true// 可选：加密开关，默认开启。
    fogPackages = arrayOf("com.example.strobf")// 可选：指定需加密的代码包路径，可配置多个，未指定将默认全部加密。
    kg = com.github.megatronking.stringfog.plugin.kg.RandomKeyGenerator()
    mode = com.github.megatronking.stringfog.plugin.StringFogMode.bytes//base64或者bytes
    packageName = "com.example.strobf"
}

android {
    namespace = "com.example.strobf"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.strobf"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        buildFeatures {
            buildConfig = true
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation(project(mapOf("path" to ":xor")))

}