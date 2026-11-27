import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Kotlin est intégré à AGP 9 : pas besoin du plugin org.jetbrains.kotlin.android
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// Signature release : key.properties à la racine du projet, hors du dépôt.
// En local on peut s'en passer, la CI le crée depuis les secrets au moment
// d'une release. Sans ce fichier, l'APK release est signé avec la clé de
// debug : il compile, mais il n'est pas publiable.
val keystoreProperties = Properties().apply {
    val file = rootProject.file("key.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "com.mpaitier.parcoeur"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.mpaitier.parcoeur"
        minSdk = 26
        targetSdk = 37
        // versionCode : +1 à chaque release publiée.
        // versionName : doit correspondre au tag (v0.1.0-alpha.1), vérifié par release.yml.
        versionCode = 1
        versionName = "0.1.0-alpha.1"
    }

    signingConfigs {
        create("release") {
            if (!keystoreProperties.isEmpty()) {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
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
            signingConfig = if (keystoreProperties.isEmpty()) {
                signingConfigs.getByName("debug")
            } else {
                signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    // Zéro avertissement : un warning de lint fait échouer le build.
    lint {
        warningsAsErrors = true
        abortOnError = true
        // Dependabot gère les mises à jour. Sans ça, chaque nouvelle version
        // d'une dépendance ferait échouer la CI du jour au lendemain.
        disable += setOf(
            "GradleDependency",
            "AndroidGradlePluginVersion",
            "NewerVersionAvailable",
            "OldTargetApi"
        )
        // TODO : à retirer quand l'icône de l'app sera ajoutée
        disable += "MissingApplicationIcon"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        // Même règle que lint : un warning du compilateur Kotlin est une erreur
        allWarningsAsErrors.set(true)
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
}
