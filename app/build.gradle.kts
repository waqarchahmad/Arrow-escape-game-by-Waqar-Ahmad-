plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.waqarahmad.arrowescape"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.waqarahmad.arrowescape"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.10.1")

    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.ui:ui-graphics:1.7.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")

    implementation("androidx.compose.foundation:foundation:1.7.8")
    implementation("androidx.compose.animation:animation:1.7.8")

    implementation("androidx.compose.material3:material3:1.3.1")

    debugImplementation("androidx.compose.ui:ui-tooling:1.7.8")
}    implementation("androidx.compose.ui:ui-tooling-preview")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
