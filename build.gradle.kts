plugins {
	base
	checkstyle
	idea
	java
}

version = "0.0.1-SNAPSHOT"


java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

allprojects {
	group = "com.meeting"
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
}

tasks.withType<Test> {
	useJUnitPlatform()
}
