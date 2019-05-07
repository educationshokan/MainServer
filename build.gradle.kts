import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.jvm.tasks.Jar

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.31")
    }
}

plugins {
    `java-library`
    kotlin("jvm") version "1.3.31"
    idea
    application
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("io.ktor:ktor-server-netty:1.1.4")
    compile("org.litote.kmongo:kmongo-coroutine:3.10.1")
    compile("io.ktor:ktor-jackson:1.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = project.name
    // manifest Main-Class attribute is optional.
    manifest {
        attributes["Main-Class"] = "com.educationShokan.MainKt" // fully qualified class name of default main class
    }
    from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) } )
    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}