plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    application
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }  // JitPack deposu eklendi ✅
}

dependencies {
    // HTTP4K - HTTP Server
    implementation("org.http4k:http4k-core:4.41.1.0")
    implementation("org.http4k:http4k-server-jetty:4.41.1.0")
    implementation("org.http4k:http4k-format-kotlinx-serialization:4.41.1.0") // ✅ JSON için eksik bağımlılık eklendi
    implementation("ch.qos.logback:logback-classic:1.4.0")
    // JSON Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // PostgreSQL JDBC Driver
    implementation("org.postgresql:postgresql:42.6.0")

    // Logging (Java 8 Uyumlu)
    implementation("org.slf4j:slf4j-simple:1.7.36")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.http4k:http4k-testing-hamkrest:4.41.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("courtsServerKt")  // ✅ Ana dosyanın ismini düzelttik
}

kotlin {
    jvmToolchain(8)  // ✅ Java 8 kullanacak şekilde ayarlandı
}
