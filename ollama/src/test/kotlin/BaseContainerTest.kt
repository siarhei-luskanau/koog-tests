import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.llm.OllamaModels
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
import sl.koog.models.AdditionalKoogModels
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class BaseContainerTest {
    private val container: OllamaContainer? by lazy {
        if (listOf(
                System.getenv("RUNNER_ENVIRONMENT"),
                System.getProperty("RUNNER_ENVIRONMENT"),
            ).contains("github-hosted")
        ) {
            OllamaContainer("ollama/ollama:latest").apply {
                withCreateContainerCmdModifier { cmd ->
                    cmd.hostConfig?.apply {
                        // "gpt-oss:20b" model requires 13.1GB RAM
                        withMemory((13.3 * 1024 * 1024 * 1024).toLong()) // 13.3GB RAM
                        val isCi =
                            true.toString().equals(other = System.getenv("CI"), ignoreCase = true)
                        if (isCi) {
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
        } else {
            null
        }
    }

    @BeforeTest
    fun start() {
        container?.start()
    }

    @AfterTest
    fun teardown() {
        container?.stop()
    }

    protected fun getBaseUrl(): String =
        when {
            container != null -> "http://localhost:${container?.port}"
            System.getProperty("OLLAMA_BASE_URL").orEmpty().isNotEmpty() -> System.getProperty("OLLAMA_BASE_URL")
            else -> "http://host.docker.internal:11434"
        }

    protected fun findModel(id: String): LLModel =
        when (id) {
            OllamaModels.Groq.LLAMA_3_GROK_TOOL_USE_8B.id -> {
                OllamaModels.Groq.LLAMA_3_GROK_TOOL_USE_8B
            }

            OllamaModels.Meta.LLAMA_3_2_3B.id -> {
                OllamaModels.Meta.LLAMA_3_2_3B
            }

            OllamaModels.Meta.LLAMA_3_2.id -> {
                OllamaModels.Meta.LLAMA_3_2
            }

            OllamaModels.Meta.LLAMA_GUARD_3.id -> {
                OllamaModels.Meta.LLAMA_GUARD_3
            }

            OllamaModels.Alibaba.QWEN_2_5_05B.id -> {
                OllamaModels.Alibaba.QWEN_2_5_05B
            }

            OllamaModels.Alibaba.QWEN_3_06B.id -> {
                OllamaModels.Alibaba.QWEN_3_06B
            }

            OllamaModels.Alibaba.QWQ_32B.id -> {
                OllamaModels.Alibaba.QWQ_32B
            }

            OllamaModels.Alibaba.QWQ.id -> {
                OllamaModels.Alibaba.QWQ
            }

            OllamaModels.Granite.GRANITE_3_2_VISION.id -> {
                OllamaModels.Granite.GRANITE_3_2_VISION
            }

            AdditionalKoogModels.Ollama.GPT_OSS_20B.id -> {
                AdditionalKoogModels.Ollama.GPT_OSS_20B
            }

            AdditionalKoogModels.Ollama.DEEPSEEK_OCR_3B.id -> {
                AdditionalKoogModels.Ollama.DEEPSEEK_OCR_3B
            }

            "qwen2.5-coder:3b" -> {
                OllamaModels.Alibaba.QWEN_CODER_2_5_32B.copy(id = id, contextLength = 3 * 1024)
            }

            "qwen3-vl:4b", "qwen2.5vl:3b", "gemma3:4b" -> {
                LLModel(
                    provider = LLMProvider.Ollama,
                    id = id,
                    capabilities =
                        listOf(
                            LLMCapability.Temperature,
                            LLMCapability.Schema.JSON.Basic,
                            LLMCapability.Tools,
                            LLMCapability.Vision.Image,
                            LLMCapability.Document,
                        ),
                    contextLength = 32 * 1024,
                )
            }

            "granite3-guardian:latest" -> {
                LLModel(
                    provider = LLMProvider.Ollama,
                    id = id,
                    capabilities =
                        listOf(
                            LLMCapability.Moderation,
                        ),
                    contextLength = 8 * 1024,
                )
            }

            "llama3.2-vision" -> {
                OllamaModels.Meta.LLAMA_4
            }

            "mistral-small3.1", "mistral-small3.2" -> {
                LLModel(
                    provider = LLMProvider.Ollama,
                    id = id,
                    capabilities =
                        listOf(
                            LLMCapability.Temperature,
                            LLMCapability.Schema.JSON.Basic,
                            LLMCapability.Tools,
                            LLMCapability.Vision.Image,
                            LLMCapability.Document,
                        ),
                    contextLength = 128 * 1024,
                )
            }

            else -> {
                throw IllegalArgumentException("Model $id not found")
            }
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
}
