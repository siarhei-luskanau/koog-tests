import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.prompt.message.AttachmentContent
import ai.koog.prompt.message.AttachmentSource
import ai.koog.prompt.message.MessagePart
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class KoogImagePromptTest : BaseContainerTest() {
    @Test
    fun imageTest() =
        runTest(timeout = 60.minutes) {
            val baseUrl = getBaseUrl()
            println("KoogTest: wait container at $baseUrl ...")
            waitForOllamaServer(baseUrl)

            val model = findModel(System.getProperty("ollama-model-id"))
            println("KoogTest: model: $model")

            println("KoogTest: creating LLMClient ...")
            val llmClient = OllamaClient(baseUrl = baseUrl)
            llmClient.getModelOrNull(model.id, pullIfMissing = true)

            val promptExecutor = MultiLLMPromptExecutor(llmClient)
            val prompt =
                prompt(id = Uuid.random().toString()) {
                    system(content = "You are a helpful assistant.")
                    user {
                        text(text = "What is in the attached image?")
                        this::class.java.getResource("/image.jpg")?.readBytes()?.let { attachmentData ->
                            image(
                                image =
                                    AttachmentSource.Image(
                                        content = AttachmentContent.Binary.Bytes(data = attachmentData),
                                        format = "jpg",
                                    ),
                            )
                        }
                    }
                }

            println("KoogTest: execute agent prompt: $prompt")
            val response = promptExecutor.execute(prompt = prompt, model = model)
            println("KoogTest: agent response: $response")

            assertTrue(
                actual = response.parts.isNotEmpty(),
                message = "Result should contain parts",
            )

            response.parts.forEach { part ->
                when (part) {
                    is MessagePart.Text -> {
                        assertContains(
                            part.text,
                            other = "circle",
                            ignoreCase = true,
                            message = "Result should contain: circle",
                        )
                    }

                    else -> {}
                }
            }
        }
}
