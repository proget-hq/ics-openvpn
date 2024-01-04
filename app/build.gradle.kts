plugins {
    id("com.android.application")
    id("kotlin-android")
}

val buildVersionCode: Int = ((project.properties["versionCode"] as String?)?.toInt() ?: 999)
val buildVersionName: String = project.properties["versionName"] as String? ?: "develop"
val buildPackageNamePostfix: String = project.properties["packageNamePostfix"] as String? ?: ""

android {
    compileSdk = 34
    buildToolsVersion = "33.0.1"

    defaultConfig {
        applicationId = "pl.proget.openvpn"
        minSdk = 21
        targetSdk = 34
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

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("net.lingala.zip4j:zip4j:2.11.2")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
