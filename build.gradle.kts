import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.31")
    }
}

plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm") version "1.3.31"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("io.ktor:ktor-server-netty:1.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}