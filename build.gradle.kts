import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	jacoco
	id("org.sonarqube") version "3.0"

	id("org.springframework.boot") version "2.3.4.RELEASE"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"

	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"

	id("io.gitlab.arturbosch.detekt").version("1.10.0")
}

group = "com.kapitonov"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	jcenter()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.security.oauth:spring-security-oauth2:2.5.0.RELEASE")

	runtimeOnly("com.h2database:h2")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
		exclude(module = "mockito-core")
	}
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.ninja-squad:springmockk:2.0.2")

	detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.10.0")
}

tasks.test {
	useJUnitPlatform()

	testLogging {
		events("passed", "skipped", "failed")
	}

	reports {
		html.isEnabled = true
	}
}

tasks.check {
	dependsOn(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	reports {
		xml.isEnabled = true
		xml.destination = File("$buildDir/reports/jacoco/test/jacoco.xml")
	}
}

tasks.clean {
	doFirst {
		delete("src/main/gen", "out")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

detekt {
	toolVersion = "1.9.1"
	config = files("${rootProject.projectDir}/config/detekt/detekt.yml")
	baseline = file("${rootProject.projectDir}/config/detekt/baseline.xml")
	buildUponDefaultConfig = true
}

sonarqube {
	properties {
		property("sonar.sourceEncoding", "UTF-8")
		property("sonar.projectName", "KotlinRestAPI")
		property("sonar.projectKey", "kotlin-rest-api")
		property("sonar.projectVersion", version)
		property("sonar.scm.exclusions.disabled", "true")
		property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/test/jacoco.xml")
		property("sonar.coverage.exclusions", "**/com/kapitonov/restapi/config/**")
		property("sonar.host.url", "http://172.17.110.3:9000")
	}
}
