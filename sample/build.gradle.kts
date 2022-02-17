val ktor_version: String by project
val logback_version: String by project

plugins {
    application
}

group = "me.utsman"
version = "0.0.2"

application {
    mainClass.set("me.utsman.ApplicationKt")
}

dependencies {
    implementation(project(":docs"))

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-sessions-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "me.utsman.sample.ApplicationKt"
    }

    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}