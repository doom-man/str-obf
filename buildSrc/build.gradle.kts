import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
}

repositories {
    google()
    jcenter()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "1.3"
}

dependencies {
    implementation("com.android.tools.build:gradle-api:8.1.0")
    implementation(kotlin("stdlib"))
    gradleApi()
}
dependencies {
    implementation("org.ow2.asm:asm-util:7.0")
    implementation("com.squareup:javawriter:2.5.1")
}