import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    `java-library`
}

group = "com.ukma.nechyporchuk"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
//                implementation(compose.desktop.currentOs)

            }
        }
        val jvmTest by getting
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Client"
            packageVersion = "1.0.0"
        }
    }
}

tasks.register("clean run") {
    dependsOn("clean")
    dependsOn("build")
    dependsOn("run")
}

dependencies {
    api("io.jsonwebtoken:jjwt-api:0.11.5")
    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5") // or 'io.jsonwebtoken:jjwt-gson:0.11.5' for gson

    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20220320")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")


    // https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0-RC1")
    // https://mvnrepository.com/artifact/org.junit.platform/junit-platform-launcher
    testImplementation("org.junit.platform:junit-platform-launcher:1.9.0-RC1")
    // https://mvnrepository.com/artifact/org.junit.platform/junit-platform-runner
    testImplementation("org.junit.platform:junit-platform-runner:1.9.0-RC1")


    implementation("com.adeo:kviewmodel:0.7.1") // Core functions
    implementation("com.adeo:kviewmodel-compose:0.7.1") // Compose extensions
//    implementation("com.adeo:kviewmodel-odyssey:0.7.1") // Odyssey extensions


//    implementation("androidx.compose.runtime:runtime:1.2.0-rc03")
//    implementation("androidx.compose.foundation:foundation:1.2.0-rc03")

    implementation("com.arkivanov.decompose:decompose:0.6.0")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:0.6.0")

    // https://mvnrepository.com/artifact/org.jetbrains.compose.desktop/desktop
    implementation("org.jetbrains.compose.desktop:desktop:1.2.0-alpha01-dev620")
    runtimeOnly("org.jetbrains.skiko:skiko-jvm-runtime-windows-x64:0.6.7")

//    implementation("org.jetbrains.compose.runtime:runtime-desktop:1.1.1")
//    implementation("androidx.compose.ui:ui:1.1.0")

    implementation("br.com.devsrsouza.compose.icons.jetbrains:feather:1.0.0")
}