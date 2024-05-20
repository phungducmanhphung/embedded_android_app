plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.apenadetect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.apenadetect"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.java.websocket)
    implementation(libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor( "org.projectlombok:lombok:1.18.32")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}