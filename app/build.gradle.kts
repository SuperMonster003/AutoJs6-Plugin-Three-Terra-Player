import com.android.build.api.variant.FilterConfiguration
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Property
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

plugins {
    id("io.github.supermonster003.autojs6-native-alignment")
    id("org.autojs.build.utils")
    id("org.autojs.build.versions")
    id("org.autojs.build.signs")
    id("org.autojs.build.jvm-convention")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val sourceNamespace = "io.github.supermonster003.autojs6.plugin.threeterraplayer"

// Keep the published application ID stable so existing installations, AutoJs6's official
// catalog entry and per-package authorization upgrade in place after the product rename.
val globalApplicationId = "io.github.supermonster003.autojs6.plugin.audioplayer"

val buildTypeDebug = "debug"
val buildTypeRelease = "release"

android {
    namespace = sourceNamespace
    compileSdk = versions.sdkVersionCompile

    defaultConfig {
        applicationId = globalApplicationId
        minSdk = versions.sdkVersionMin
        targetSdk = versions.sdkVersionTarget
        versionCode = versions.appVersionCode
        versionName = versions.appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "plugin_author", "SuperMonster003")
        resValue("string", "plugin_engine", "explorer-action")
        resValue("string", "plugin_variant", "default")
        resValue("string", "plugin_id", "audio-player")
        resValue("string", "plugin_version_date", utils.getDateString("MMM d, yyyy", "GMT+08:00"))
    }

    lint {
        abortOnError = false
    }

    signingConfigs {
        if (signs.isValid) {
            create(buildTypeRelease) {
                storeFile = signs.properties["storeFile"]?.let { file(it as String) }
                keyPassword = signs.properties["keyPassword"] as String
                keyAlias = signs.properties["keyAlias"] as String
                storePassword = signs.properties["storePassword"] as String
            }
        }
    }

    buildTypes {
        val proguardFiles = arrayOf<Any>(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro",
        )
        val niceSigningConfig = takeIf { signs.isValid }?.let {
            signingConfigs.getByName(buildTypeRelease)
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(*proguardFiles)
            niceSigningConfig?.let { signingConfig = it }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(*proguardFiles)
            niceSigningConfig?.let { signingConfig = it }
        }
    }

    buildFeatures {
        aidl = true
        buildConfig = true
        compose = true
        resValues = true
        viewBinding = true
    }

    sourceSets.named("main") {
        kotlin.directories += "src/main/java"
    }

    packaging {
        resources.pickFirsts.addAll(
            listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.*",
                "META-INF/NOTICE",
                "META-INF/NOTICE.*",
                "META-INF/*.kotlin_module",
            ),
        )
    }

    bundle {
        language.enableSplit = false
        density.enableSplit = false
        abi.enableSplit = false
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val architecture = output.filters.find {
                it.filterType == FilterConfiguration.FilterType.ABI
            }?.identifier
            val outputFileNameProperty = output.javaClass.methods.firstOrNull {
                it.name == "getOutputFileName" && it.parameterTypes.isEmpty()
            }?.invoke(output) as? Property<*>

            @Suppress("UNCHECKED_CAST")
            (outputFileNameProperty as? Property<String>)?.set(
                output.versionName.map { versionName ->
                    val version = versionName.replace("\\s".toRegex(), "-")
                    val abiSuffix = architecture?.let { "-$it" }.orEmpty()
                    "${rootProject.name}-v$version$abiSuffix.${utils.FILE_EXTENSION_APK}".lowercase()
                },
            )
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.21")
    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:2.2.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    implementation(files("$rootDir/libs/common-plugin-api.aar"))
    implementation(files("$rootDir/libs/explorer-action-api.aar"))

    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.material)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.recyclerview)

    // Jetpack Compose, only for the release history page (AUTOJS6_PLUGIN_RELEASE_HISTORY_AGENTS.md); the BOM is the
    // single version source and also manages activity-compose.
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.activity)

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
}

tasks {
    withType(JavaCompile::class.java) {
        options.encoding = "UTF-8"
    }


}

extra {
    versions.handleIfNeeded(project, "", listOf(buildTypeDebug, buildTypeRelease))
}

// Native code inventory: the plugin has no native code of its own, but Compose ui-graphics depends on
// androidx.graphics:graphics-path, whose libandroidx.graphics.path.so is packaged for four ABIs. The APK stays one
// universal package without ABI splits (supportedAbis stays empty); the shared gate keeps the 16 KB alignment checks,
// release-archive.gradle expects these ABIs in the universal APK, and verifyReleaseNativeLibraries below asserts that
// exactly these libraries are present and stored uncompressed.
val nativeLibraryAbis = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
val allowedNativeLibraries = nativeLibraryAbis.map { "lib/$it/libandroidx.graphics.path.so" }
nativeAlignment { expectNoNativeLibraries.set(false) }
extra["releaseUniversalNativeAbis"] = nativeLibraryAbis

apply(from = rootProject.file("gradle/release-archive.gradle"))

val verifyReleaseNativeLibraries = tasks.register("verifyReleaseNativeLibraries") {
    group = "verification"
    description = "Checks that the release APK carries exactly the allowed native libraries, stored uncompressed"
    dependsOn("assembleRelease")
    doLast {
        val directory = layout.buildDirectory.dir("outputs/apk/release").get().asFile
        val apks = directory.listFiles { file -> file.isFile && file.extension == "apk" }.orEmpty()
        check(apks.isNotEmpty()) { "No release APK in $directory" }
        apks.forEach { apk ->
            ZipFile(apk).use { zip ->
                val natives = zip.entries().asSequence().filter { it.name.startsWith("lib/") }.toList()
                check(natives.map { it.name }.sorted() == allowedNativeLibraries.sorted()) {
                    "Unexpected native library set in ${apk.name}: ${natives.map { it.name }.sorted()}"
                }
                check(natives.all { it.method == ZipEntry.STORED }) { "Native libraries must be stored uncompressed: ${apk.name}" }
            }
        }
    }
}
tasks.named("appendDigestToReleasedFiles") { dependsOn(verifyReleaseNativeLibraries) }
