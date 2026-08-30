plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "br.com.fiap.reciconecta"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "br.com.fiap.reciconecta"
        minSdk = 24
        targetSdk = 37
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
    buildFeatures {
        compose = true
    }

    // 💡 BLOCO ADICIONADO PARA CORRIGIR O ERRO DE 16 KB NO ANDROID 17
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    val bom = platform(libs.androidx.compose.bom)
    implementation(bom)
    androidTestImplementation(bom)
    debugImplementation(bom)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)

    //Recurso para capturar imagem de perfil
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation(libs.androidx.datastore.preferences)

    // Google Play Services Location (para FusedLocationProviderClient, Priority, etc)
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // CameraX (Para exibir a câmera e capturar os quadros)
    val cameraxVersion = "1.4.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // ML Kit Image Labeling (Para reconhecer os objetos)
    implementation("com.google.mlkit:image-labeling:17.0.9")

    // Permite usar o viewModel() direto no Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Bibliotecas oficiais do Google Maps para Jetpack Compose
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
}