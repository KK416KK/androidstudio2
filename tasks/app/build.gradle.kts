plugins {
    id("com.android.application")
}

android {
    namespace = "com.websarva.wings.android.tasks"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.websarva.wings.android.tasks"
        minSdk = 29
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

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    //implementation'androidx.recycleview:recycleview:1.2.1'
    //implementation'androidx.recycleview:recycleview-selection:1.1.0'
    //implementation("androidx.appcompat:appcompat:1.7.0")
    //implementation("com.google.android.material:material:1.12.0")
    //implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //testImplementation("junit:junit:4.13.2")
    //androidTestImplementation("androidx.test.ext:junit:1.2.1")
    //androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}