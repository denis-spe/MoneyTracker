plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.moneytracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.moneytracker"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.example.moneytracker.HiltTestRunner"
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
    buildFeatures {
        compose = true
    }
}

// enable javac deprecation lint
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.testing)

    // Hilt (Dagger)
    implementation(libs.hilt.android.v257)
    implementation(libs.mockito.android)
    implementation(libs.mockito.bom)
    implementation(libs.googleid)
    implementation(libs.firebase.crashlytics.buildtools)
    ksp(libs.hilt.android.compiler.v257)

    // Androidx Hilt extension for compose (provide hiltViewModel)
    implementation(libs.androidx.hilt.navigation.compose.v100)
    ksp(libs.androidx.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Hilt testing
    androidTestImplementation(libs.hilt.android.testing) // Or your Hilt version
    kspAndroidTest(libs.hilt.compiler.v2572)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.kotlinx.serialization.json.v173)

    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.coil.compose)

}