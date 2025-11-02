@file:Suppress("PropertyName")

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.tools.ant.taskdefs.condition.Os
import java.util.Properties

allprojects {
    apply(from = "$rootDir/ktlint.gradle")
}

val CI_GRADLE = "CI_GRADLE"

tasks.register("devRunMatrix") {
    group = CI_GRADLE
    val injected = project.objects.newInstance<Injected>()
    doLast {
        val variants =
            Gson()
                .fromJson(project.file("matrix.json").readText(), MatrixModel::class.java)
                .variants
                .filter { it.enabled }
                .sortedBy { it.testGroup + it.ollamaModelId }
                .distinctBy { it.testGroup + it.ollamaModelId }

        println("variants: $variants")
        variants.forEach { variant ->
            println("variant: $variant")
            injected.gradlew(":ollama:test", "-Dollama-model-id=${variant.ollamaModelId}", "--tests", variant.testGroup)
        }
    }
}

abstract class Injected {
    @get:Inject
    abstract val execOperations: ExecOperations

    @get:Inject
    abstract val projectLayout: ProjectLayout

    fun gradlew(
        vararg tasks: String,
        addToSystemProperties: Map<String, String>? = null,
    ) {
        execOperations
            .exec {
                commandLine =
                    mutableListOf<String>().also { mutableArgs ->
                        mutableArgs.add(
                            projectLayout.projectDirectory
                                .file(
                                    if (Os.isFamily(Os.FAMILY_WINDOWS)) "gradlew.bat" else "gradlew",
                                ).asFile.path,
                        )
                        mutableArgs.addAll(tasks)
                        addToSystemProperties?.toList()?.map { "-D${it.first}=${it.second}" }?.let {
                            mutableArgs.addAll(it)
                        }
                        mutableArgs.add("--stacktrace")
                    }
                val sdkDirPath =
                    Properties()
                        .apply {
                            val propertiesFile = projectLayout.projectDirectory.file("local.properties").asFile
                            if (propertiesFile.exists()) {
                                load(propertiesFile.inputStream())
                            }
                        }.getProperty("sdk.dir")
                if (sdkDirPath != null) {
                    val platformToolsDir = "$sdkDirPath${File.separator}platform-tools"
                    val pathEnvironment = System.getenv("PATH").orEmpty()
                    if (!pathEnvironment.contains(platformToolsDir)) {
                        environment =
                            environment.toMutableMap().apply {
                                put("PATH", "$platformToolsDir:$pathEnvironment")
                            }
                    }
                }
                if (System.getenv("JAVA_HOME") == null) {
                    System.getProperty("java.home")?.let { javaHome ->
                        environment =
                            environment.toMutableMap().apply {
                                put("JAVA_HOME", javaHome)
                            }
                    }
                }
                if (System.getenv("ANDROID_HOME") == null) {
                    environment =
                        environment.toMutableMap().apply {
                            put("ANDROID_HOME", sdkDirPath)
                        }
                }
                println("commandLine: ${this.commandLine}")
            }.apply { println("ExecResult: ${this.exitValue}") }
    }
}
