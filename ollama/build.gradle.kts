plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(
        libs.versions.jdkVersion
            .get()
            .toInt(),
    )
}

dependencies {
    implementation("sl.koog:models:0.0.1-SNAPSHOT")
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
    systemProperty("ollama-model-id", System.getProperty("ollama-model-id"))
    systemProperty("RUNNER_ENVIRONMENT", System.getProperty("RUNNER_ENVIRONMENT"))
    systemProperty("OLLAMA_BASE_URL", System.getProperty("OLLAMA_BASE_URL"))
}
