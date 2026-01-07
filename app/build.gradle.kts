plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "de.andicodes.vergissnix"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.andicodes.vergissnix"
        minSdk = 26
        targetSdk = 34
        versionCode = 22
        versionName = "2.3.1"
        base.archivesName = "vergiss-nix-${versionName}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        managedDevices {
            localDevices {
                create("pixel6Api30") {
                    // Use device profiles you typically see in Android Studio.
                    device = "Pixel 6"
                    // Use only API levels 27 and higher.
                    apiLevel = 30
                    // -atd = Automated Test Device, optimized to reduce CPU and memory usage
                    systemImageSource = "aosp-atd"
                }
            }
        }

    }
    // don"t include dependencies signing block to allow F-Droid to reproduce builds
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.stdlib)

    // compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.androidx.room.testing)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.assertj.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.test.uiautomator)
}

// sort baseline profiles to enable reproducible builds for F-Droid
// see https://gist.github.com/obfusk/eb82a810ed6aad266dab19977b18cee6
apply(from = "fix-profm.gradle")