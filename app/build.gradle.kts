plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.clovergrimoire"

    // Mantenha como estava originalmente para cobrir os requisitos das bibliotecas
    compileSdk = 36 // Ou use a sintaxe original: compileSdk { version = release(37) }

    defaultConfig {
        applicationId = "com.example.clovergrimoire"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    // --- DEPENDÊNCIAS DO ROOM ---
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    // --- DEPENDÊNCIA DO GSON ---
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}