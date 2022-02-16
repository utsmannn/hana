val ktor_version: String by project
val logback_version: String by project
val commonmark_version: String by project

plugins {
    `maven-publish`
}

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.utsmannn"
            artifactId = "hana-doc"
            version = "0.0.5"

            from(components["java"])
        }
    }
}

group = "me.hana"
version = "0.0.5"

dependencies {

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-html-builder-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-freemarker:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson-jvm:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
    implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.129-kotlin-1.4.20")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.atlassian.commonmark:commonmark:$commonmark_version")
}