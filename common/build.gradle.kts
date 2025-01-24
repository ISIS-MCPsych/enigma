import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar


val archivaUser: String? by project
val archivaPassword: String? by project
// val version: String by project
val archivaHostId: String? by project
val archivaPort: String? by project

plugins {
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    id("maven-publish")
//     let's add the ktlink plugin
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
}

java.sourceCompatibility = JavaVersion.VERSION_17

// java.targetCompatibility = JavaVersion.VERSION_17

group = "edu.vanderbilt"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.4.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.1")
    implementation("com.azure:azure-storage-blob:12.29.0")
    implementation("com.microsoft.azure:msal4j:1.18.0")
    implementation("com.microsoft.azure:msal4j-persistence-extension:1.3.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    jvmArgs = mutableListOf("--enable-preview")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

repositories {
    mavenCentral()
    maven {
        isAllowInsecureProtocol = true
        url = uri("http://$archivaHostId:$archivaPort/repository/enigma-test-snapshot/")
    }
}

publishing {
    publications.create<MavenPublication>("common").from(components["java"])
    repositories {
        archivaHostId?.let {
            archivaPassword?.let {
                maven {
//                    name = "rootPublish"
                    val internalRepoUrl = "http://$archivaHostId:$archivaPort/repository/enigma-test-release"
                    val snapshotsRepoUrl = "http://$archivaHostId:$archivaPort/repository/enigma-test-snapshot"
                    url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else internalRepoUrl)

                    logger.info("URL = \"$url\"")
                    isAllowInsecureProtocol = true
                    authentication {
                        create<BasicAuthentication>("basic")
                    }
                    credentials {
                        username = archivaUser
                        password = archivaPassword
                    }
                }
            }
        }
    }
}
