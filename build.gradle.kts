import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.MetricType

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
}

group = "dev.boringx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}

koverReport {
    verify {
        rule {
            isEnabled = true
            bound {
                bound {
                    minValue = 80
                    maxValue = 100
                    metric = MetricType.LINE
                    aggregation = AggregationType.COVERED_PERCENTAGE
                }
            }
        }
    }
}