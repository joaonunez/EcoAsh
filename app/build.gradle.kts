plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Google services plugin
}

android {
    namespace = "com.example.ecoash"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ecoash"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // AndroidX dependencies
    implementation("androidx.appcompat:appcompat:1.6.1") // AndroidX AppCompat
    implementation("com.google.android.material:material:1.9.0") // Material Components
    implementation("androidx.activity:activity-ktx:1.7.2") // Activity KTX
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Constraint Layout
    implementation("androidx.viewpager2:viewpager2:1.0.0") // ViewPager2
    implementation("com.google.android.material:material:1.x.x")


    // Firebase BoM for managing Firebase dependencies versions
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // Firebase services
    implementation("com.google.firebase:firebase-analytics") // Firebase Analytics
    implementation("com.google.firebase:firebase-auth") // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore-ktx") // Firebase Firestore

    // Testing dependencies
    testImplementation("junit:junit:4.13.2") // JUnit for unit testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // AndroidX JUnit
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Espresso for UI tests
}
