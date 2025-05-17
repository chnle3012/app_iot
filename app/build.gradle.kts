plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.btl_iot"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.btl_iot"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Nếu bạn bị giới hạn ABI, đảm bảo bao gồm cả arm64-v8a
        packagingOptions {
            jniLibs {
                // Đảm bảo pickFirst các .so cần thiết
                pickFirsts += "lib/**/libmediapipe_tasks_vision_jni.so"
                pickFirsts += "lib/**/libmediapipe_framework_jni.so"
            }
        }
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

    // PackagingOptions để đảm bảo các .so của MediaPipe được include
    packagingOptions {
        jniLibs {
            // Đảm bảo pickFirst các .so cần thiết
            pickFirsts += "lib/**/libmediapipe_tasks_vision_jni.so"
            pickFirsts += "lib/**/libmediapipe_framework_jni.so"
        }
    }
}


dependencies {
    // AndroidX
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.7.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0")
    
    // Navigation Component
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("com.google.mediapipe:tasks-vision:0.10.10")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}