import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar


val archivaUser: String? by project
val archivaPassword: String? by project
//val version: String by project
val archivaHostId: String? by project
val archivaPort: String? by project



plugins {
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
    id("maven-publish")
}




java.sourceCompatibility = JavaVersion.VERSION_17
//java.targetCompatibility = JavaVersion.VERSION_17

group = "edu.vanderbilt"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
    implementation("com.azure:azure-storage-blob:12.24.0")
    implementation("com.microsoft.azure:msal4j:1.13.10")
    implementation("com.microsoft.azure:msal4j-persistence-extension:1.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
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