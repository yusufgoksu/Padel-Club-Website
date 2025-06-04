plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    application
}

group = "com.example"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo1.maven.org/maven2") } // Alternatif repo eklemek
}


dependencies {
    // ✅ HTTP4K - HTTP Server
    implementation("org.http4k:http4k-core:4.41.1.0")
    implementation("org.http4k:http4k-server-jetty:4.41.1.0")
    implementation("org.http4k:http4k-client-okhttp:4.41.1.0")
    implementation("org.http4k:http4k-format-kotlinx-serialization:4.41.1.0")
    implementation("org.http4k:http4k-client-apache:4.41.0.0") // örnek son sürüm

    // ✅ Eksik Bağımlılık: Routing Desteği


    // OpenAPI ve Swagger UI
    implementation("org.http4k:http4k-contract:4.41.1.0")
    implementation("org.http4k:http4k-contract-ui-swagger:4.41.1.0")

    // JSON Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Logging
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.4.0")

    // ✅ JUnit ve Test Bağımlılıkları
    testImplementation(kotlin("test"))
    testImplementation("org.http4k:http4k-testing-hamkrest:4.41.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    // PostgreSQL Bağlantısı
    implementation("org.postgresql:postgresql:42.2.5")

    // ✅ Eksik Bağımlılık - Suite API
    testImplementation("org.junit.platform:junit-platform-suite-api:1.10.0") // Eklenmesi gerekiyor
    testImplementation("org.junit.platform:junit-platform-suite-engine:1.10.0") // Suite Engine
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0") // Testleri çalıştırmak için
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.example.MainKt")  // ✅ Ana dosyanın doğru isimde olduğuna emin ol
}

kotlin {
    jvmToolchain(17)  // Java 17 kullanımı
}
