plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Ensure Kotlin compiler JVM target matches Java (11)
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}
android {
    namespace = "com.dsronne.dewit"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dsronne.dewit"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packaging {
        resources {
            pickFirsts += "META-INF/LICENSE*"
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.mockk.core)
    testImplementation(libs.kotest.assertions.core.jvm)
    testImplementation(libs.kotest.runner.junit5.jvm)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.withType<org.gradle.api.tasks.testing.Test> {
    useJUnitPlatform()
}
