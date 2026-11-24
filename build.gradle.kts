plugins {
	java
	id("org.springframework.boot") version "4.2.0"
	id("io.spring.dependency-management") version "1.1.7"
	`java-test-fixtures`
	jacoco
	id("io.freefair.lombok") version "9.8.3"
}

group = "com.demo"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

sourceSets {
	create("intTest") {
		java {
			srcDir("src/intTest/java")
		}

		compileClasspath += sourceSets["main"].output + sourceSets["test"].output
		runtimeClasspath += output + compileClasspath
	}
}

configurations {
	named("intTestImplementation") {
		extendsFrom(configurations["testImplementation"])
	}

	named("intTestRuntimeOnly") {
		extendsFrom(configurations["testRuntimeOnly"])
	}
}

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	// MySQL
	runtimeOnly("com.mysql:mysql-connector-j")

	compileOnly("org.projectlombok:lombok")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test-classic")
	testRuntimeOnly("com.h2database:h2")

	// Embedded MongoDB
	testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring4x:4.33.0")

	// Embedded Redis
	add("intTestImplementation", "com.github.codemonstur:embedded-redis:1.4.4")

}

tasks.test {
	useJUnitPlatform()
}

tasks.register<Test>("intTest") {
	description = "Runs integration tests."
	group = "verification"

	testClassesDirs = sourceSets["intTest"].output.classesDirs
	classpath = sourceSets["intTest"].runtimeClasspath

	shouldRunAfter(tasks.test)

	useJUnitPlatform()
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude("**/config/*")
			}
		})
	)
}