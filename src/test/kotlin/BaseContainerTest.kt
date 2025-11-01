import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.Volume
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.testcontainers.images.PullPolicy
import org.testcontainers.ollama.OllamaContainer
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class BaseContainerTest {
    protected val container by lazy {
        OllamaContainer("ollama/ollama:latest").apply {
            withExposedPorts(EXPOSED_PORT)
            withCreateContainerCmdModifier { cmd ->
                cmd.hostConfig?.apply {
                    val isCi =
                        true.toString().equals(other = System.getenv("CI"), ignoreCase = true)
                    if (isCi) {
                        withMemory(4L * 1024 * 1024 * 1024) // 4GB RAM
                        withCpuCount(2L)
                    } else {
                        val path =
                            System.getProperty("project.root.dir", ".") +
                                File.separator + ".ollama"
                        println("Container volume: $path")
                        withBinds(Bind(path, Volume("/root/.ollama")))
                    }
                }
            }
            withImagePullPolicy(PullPolicy.alwaysPull())
            withReuse(true)
        }
    }

    @BeforeTest
    fun start() {
        container.start()
    }

    @AfterTest
    fun teardown() {
        container.stop()
    }

    protected fun waitForOllamaServer(baseUrl: String) {
        val httpClient =
            HttpClient {
                install(HttpTimeout) {
                    connectTimeoutMillis = 1000
                }
            }

        val maxAttempts = 100

        runBlocking {
            for (attempt in 1..maxAttempts) {
                @Suppress("TooGenericExceptionCaught")
                try {
                    val response = httpClient.get(baseUrl)
                    if (response.status.isSuccess()) {
                        httpClient.close()
                        return@runBlocking
                    }
                } catch (e: Exception) {
                    if (attempt == maxAttempts) {
                        httpClient.close()
                        throw IllegalStateException(
                            "Ollama server didn't respond after $maxAttempts attemps",
                            e,
                        )
                    }
                }
                delay(1000)
            }
        }
    }

    companion object {
        const val EXPOSED_PORT = 11434
    }
}
