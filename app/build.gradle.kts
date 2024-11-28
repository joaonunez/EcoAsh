plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Plugin para Firebase
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

    buildFeatures {
        viewBinding = true // Activar ViewBinding
    }
}

dependencies {
    // AndroidX
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Firebase BoM para manejar versiones de Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // Servicios de Firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database")

    // CardView para layouts avanzados
    implementation("androidx.cardview:cardview:1.0.0")

    // Annotations para AndroidX
    implementation("androidx.annotation:annotation:1.6.0")

    // Retrofit para HTTP requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp y logging interceptor
    implementation("com.squareup.okhttp3:okhttp:4.11.0") // Cliente HTTP
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Logging

    // Gson para JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Dependencias para testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
