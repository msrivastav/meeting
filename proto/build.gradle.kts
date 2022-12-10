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
    implementation("io.grpc:grpc-stub:1.51.0")
    implementation("io.grpc:grpc-protobuf:1.51.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
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