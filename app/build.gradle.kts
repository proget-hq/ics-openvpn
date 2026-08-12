plugins {
    id("com.android.application")
    id("kotlin-android")
    alias(libs.plugins.kotlin.compose)
}

val buildVersionCode: Int = ((project.properties["versionCode"] as String?)?.toInt() ?: 999)
val buildVersionName: String = project.properties["versionName"] as String? ?: "develop"
val buildPackageNamePostfix: String = project.properties["packageNamePostfix"] as String? ?: ""

android {
    compileSdk = 35
    namespace = "pl.proget.openvpn"

    defaultConfig {
        applicationId = "pl.proget.openvpn"
        minSdk = 21
        targetSdk = 35
        versionCode = buildVersionCode
        versionName = buildVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    /**
     * Prevents IOException during connection (process start failure).
     * Only occurs when installing from .aab.
     * @note Ensure the code below is present in main/build.gradle during updates.
     */
    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    sourceSets {
        create("staging")
        create("prod")
    }

    flavorDimensions.add("implementation")

    productFlavors {
        create("staging") {
            dimension = "implementation"
            applicationId = "pl.proget.openvpndevelop"
            matchingFallbacks.add("skeleton")
        }
        create("prod") {
            dimension = "implementation"
            matchingFallbacks.add("skeleton")
            applicationId = "pl.proget.openvpn$buildPackageNamePostfix"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        aidl = true
        compose = true
    }
    namespace = "pl.proget.openvpn"
}

dependencies {
    implementation(project(":main"))
    implementation(libs.androidx.compose.material3.window.size.class1)

    implementation(libs.kotlin.stdlib.v1621)
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.view.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.zip4j)
    implementation(libs.androidx.datastore.preferences)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.android)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
