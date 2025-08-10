import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "org.proninyaroslav.opencomicvine"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.proninyaroslav.opencomicvine"
        minSdk = 21
        targetSdk = 35
        versionCode = 6
        versionName = "1.1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keyProperties = File("${rootProject.projectDir}/key.properties").let {
                if (it.exists) {
                    it.loadProperties()
                } else {
                    null
                }
            }

            storeFile = keyProperties?.getProperty("storeFile")?.let { file(it) }
            keyPassword = keyProperties?.getProperty("keyPassword")
            storePassword = keyProperties?.getProperty("storePassword")
            keyAlias = keyProperties?.getProperty("keyAlias")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            signingConfig = signingConfigs.getByName("debug")
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
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

dependencies {
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.room.testing)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.kotest.assertions.core)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ui.test.junit4)
   
    // Debug
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // AndroidX Core
    implementation(libs.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.core.splashscreen)
    implementation(libs.appcompat)

    // Android Material View
    implementation(libs.material)

    // Compose Core
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.ui.util)
    implementation(libs.material3)
    implementation(libs.material3.window.size)
    implementation(libs.androidx.ui.tooling.preview)
    // Fallback import for components that aren"t in Material 3
    implementation(libs.androidx.material)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Paging 3.0
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.room.paging)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Coil
    implementation(libs.coil.compose)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Accompanist
    implementation(libs.accompanist.webview)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    ksp(libs.moshi.kotlin.codegen)

    // moshi-sealed
    // TODO: https://github.com/ZacSweers/MoshiX/issues/530
    //noinspection GradleDependency
    implementation(libs.moshi.sealed.runtime)
    //noinspection GradleDependency
    ksp(libs.moshi.sealed.codegen)

    // ACRA
    implementation(libs.acra.mail)
    implementation(libs.acra.dialog)

    // Other
    implementation(libs.sandwich)
    implementation(libs.sandwich.retrofit)
    implementation(libs.compose.image)
    implementation(libs.compose.settings.ui.m3)
    implementation(libs.placeholder.material3)
}