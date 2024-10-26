val kotlin_version: String by project
val logback_version: String by project
val mongodb_version: String by project
val bson_version: String by project
val bcrypt_version: String by project

plugins {
    kotlin("jvm") version "2.0.21"

    id("io.ktor.plugin") version "3.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
}

group = "roomme"
version = "0.0.1"

application {
    mainClass.set("roomme.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("io.ktor:ktor-client-apache-jvm")
    implementation("io.ktor:ktor-server-sessions-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-websockets")
    implementation("io.ktor:ktor-server-config-yaml")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    implementation("at.favre.lib:bcrypt:$bcrypt_version") // Hashing for passwords

    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongodb_version") // MongoDB driver

    implementation("org.mongodb:bson-kotlinx:$bson_version") // BSON serializer for MongoDB driver

    implementation("ch.qos.logback:logback-classic:$logback_version") // Logging

    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
