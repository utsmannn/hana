val ktor_version: String by project
val logback_version: String by project
val commonmark_version: String by project
val app_version: String by project

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
            version = app_version

            from(components["java"])
        }
    }
}

group = "me.hana"
version = app_version

dependencies {

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-html-builder-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-freemarker:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.atlassian.commonmark:commonmark:$commonmark_version")

    api("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
    api("io.ktor:ktor-server-html-builder-jvm:$ktor_version")
    api("org.jetbrains:kotlin-css-jvm:1.0.0-pre.129-kotlin-1.4.20")
}