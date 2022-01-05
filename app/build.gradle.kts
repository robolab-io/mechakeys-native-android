plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("com.google.android.gms.oss-licenses-plugin")
    id("de.mannodermaus.android-junit5")
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"
    ndkVersion = "22.1.7171670"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf(
            "-Xallow-result-return-type",
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlin.contracts.ExperimentalContracts",
            "-Xjvm-default=compatibility",
        )
    }

    defaultConfig {
        applicationId = "dev.patrickgold.florisboard"
        minSdk = 23
        targetSdk = 31
        versionCode = 64
        versionName = "0.3.14"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    Pair("room.schemaLocation", "$projectDir/schemas"),
                    Pair("room.incremental", "true"),
                    Pair("room.expandProjection", "true")
                )
            }
        }

        externalNativeBuild {
            cmake {
                cFlags("-fvisibility=hidden", "-DU_STATIC_IMPLEMENTATION=1")
                cppFlags("-fvisibility=hidden", "-std=c++17", "-fexceptions", "-ffunction-sections", "-fdata-sections", "-DU_DISABLE_RENAMING=1", "-DU_STATIC_IMPLEMENTATION=1")
                arguments("-DANDROID_STL=c++_static")
            }
        }

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }

        sourceSets {
            maybeCreate("main").apply {
                assets {
                    srcDirs("src/main/assets", "src/main/icu4c/prebuilt/assets")
                }
                jniLibs {
                    srcDirs("src/main/icu4c/prebuilt/jniLibs")
                }
            }
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-rc01"
    }

    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
        }
    }

    buildTypes {
        named("debug").configure {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            isDebuggable = true
            isJniDebuggable = true

            ndk {
                // For running FlorisBoard on the emulator
                abiFilters += listOf("x86", "x86_64")
            }

            resValue("mipmap", "floris_app_icon", "@mipmap/ic_app_icon_debug")
            resValue("mipmap", "floris_app_icon_round", "@mipmap/ic_app_icon_debug_round")
            resValue("drawable", "floris_app_icon_foreground", "@drawable/ic_app_icon_debug_foreground")
            resValue("string", "floris_app_name", "FlorisBoard Debug")
        }

        create("beta") // Needed because by default the "beta" BuildType does not exist
        named("beta").configure {
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta08"
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))

            resValue("mipmap", "floris_app_icon", "@mipmap/ic_app_icon_beta")
            resValue("mipmap", "floris_app_icon_round", "@mipmap/ic_app_icon_beta_round")
            resValue("drawable", "floris_app_icon_foreground", "@drawable/ic_app_icon_beta_foreground")
            resValue("string", "floris_app_name", "FlorisBoard Beta")
        }

        named("release").configure {
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))

            resValue("mipmap", "floris_app_icon", "@mipmap/ic_app_icon_release")
            resValue("mipmap", "floris_app_icon_round", "@mipmap/ic_app_icon_release_round")
            resValue("drawable", "floris_app_icon_foreground", "@drawable/ic_app_icon_release_foreground")
            resValue("string", "floris_app_name", "@string/app_name")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    lint {
        isAbortOnError = false
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.autofill:autofill:1.1.0")
    implementation("androidx.collection:collection-ktx:1.2.0")
    implementation("androidx.compose.material:material:1.1.0-rc01")
    implementation("androidx.compose.runtime:runtime-livedata:1.1.0-rc01")
    implementation("androidx.compose.ui:ui:1.1.0-rc01")
    implementation("androidx.compose.ui:ui-tooling-preview:1.1.0-rc01")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.core:core-splashscreen:1.0.0-alpha02")
    implementation("androidx.navigation:navigation-compose:2.4.0-rc01")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.20.2")
    implementation("dev.patrickgold.jetpref:jetpref-datastore-model:0.1.0-beta02")
    implementation("dev.patrickgold.jetpref:jetpref-datastore-ui:0.1.0-beta02")
    implementation("dev.patrickgold.jetpref:jetpref-material-ui:0.1.0-beta02")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("androidx.room:room-runtime:2.4.0")
    kapt("androidx.room:room-compiler:2.4.0")

    testImplementation("io.kotest:kotest-runner-junit5:4.6.3")
    testImplementation("io.kotest:kotest-assertions-core:4.6.3")
    testImplementation("io.kotest:kotest-property:4.6.3")
    testImplementation("io.kotest.extensions:kotest-extensions-robolectric:0.4.0")

    androidTestImplementation("androidx.test.ext", "junit", "1.1.2")
    androidTestImplementation("androidx.test.espresso", "espresso-core", "3.3.0")
}
