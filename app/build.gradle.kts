import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.2.21"
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

//extensions.configure<ApplicationExtension>
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

    // Move buildFeatures to be a direct child of 'android'
    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    // 🔑 This fixes the Java/Kotlin mismatch
    jvmToolchain(11)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)

        // ✅ Your opt-in stays
        optIn.add("kotlin.RequiresOptIn")
    }
}

// enable javac deprecation lint
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}

dependencies {
    implementation("androidx.compose.material:material-icons-extended")
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
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.compose.foundation.layout)
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

    // Vico chart
    // Core
    val vicoVersion = "2.3.6" // As of late 2025, please check for the latest version
    implementation("com.patrykandpatrick.vico:core:$vicoVersion")
    implementation("com.patrykandpatrick.vico:compose-m3:$vicoVersion")

    // Compose (new cartesian API)
    implementation("com.patrykandpatrick.vico:compose:$vicoVersion")

    // Material 3 (recommended)
    implementation("com.patrykandpatrick.vico:compose-m3:$vicoVersion")

    // Date time wheel
    implementation("network.chaintech:kmp-date-time-picker:1.1.1")
}