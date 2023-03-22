plugins {
    kotlin("jvm") version "1.8.0"
    application
    kotlin("plugin.serialization") version "1.8.10"

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.testng:testng:7.1.0")
    implementation("junit:junit:4.13.1")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation("org.postgresql:postgresql:42.3.1")
    implementation ("com.zaxxer:HikariCP:5.0.0")



}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}