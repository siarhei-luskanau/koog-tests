plugins {
    alias(libs.plugins.kotlin.jvm)
}

apply(from = "$rootDir/ktlint.gradle")

kotlin.jvmToolchain(
    libs.versions.build.jvmTarget
        .get()
        .toInt(),
)

dependencies {
    implementation(libs.koog.agents.core)
    implementation(libs.koog.prompt.executor.llms.all)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.kotlinx.datetime)
    implementation(libs.ktor.client.apache5)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(project.dependencies.platform(libs.ktor.bom))
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.slf4j.simple)
    testImplementation(libs.testcontainers.ollama)
}

tasks.withType<Test> {
    systemProperty("project.root.dir", rootDir.absolutePath)
}
