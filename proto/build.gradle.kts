import com.google.protobuf.gradle.*

plugins {
    java
    id("com.google.protobuf") version "0.9.1"
}

group = "com.meeting"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.21.9")
    implementation("io.grpc:grpc-stub:1.50.2")
    implementation("io.grpc:grpc-protobuf:1.50.2")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("io.github.lognet:grpc-spring-boot-starter:4.9.1")
    implementation("me.dinowernli:java-grpc-prometheus:0.3.0")


    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.21.9"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.50.2"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") { }
            }
        }
    }
}