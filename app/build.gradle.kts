plugins {
    id("com.android.application")
    id("kotlin-android")
}

val buildVersionCode: Int = ((project.properties["versionCode"] as String?)?.toInt() ?: 999)
val buildVersionName: String = project.properties["versionName"] as String? ?: "develop"
val buildPackageNamePostfix: String = project.properties["packageNamePostfix"] as String? ?: ""

android {
    compileSdk = 35

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

    flavorDimensions += listOf("implementation", "ovpnimpl")

    productFlavors {
        create("staging") {
            dimension = "implementation"
            applicationId = "pl.proget.openvpndevelop"
            matchingFallbacks.add("skeleton")
        }
        create("prod") {
            dimension = "implementation"
            applicationId = "pl.proget.openvpn$buildPackageNamePostfix"
            matchingFallbacks.add("skeleton")
        }
        create("ovpn") {
            dimension = "ovpnimpl"
            matchingFallbacks.add("ovpn23")
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
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    namespace = "pl.proget.openvpn"
}

dependencies {
    implementation(project(":main"))

    implementation(libs.kotlin.stdlib.v1920)
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.androidx.appcompat.v161)
    implementation(libs.android.view.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.zip4j)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
