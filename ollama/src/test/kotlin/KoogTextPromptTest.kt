import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.executor.ollama.client.OllamaClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class KoogTextPromptTest : BaseContainerTest() {
    @Test
    fun textTest() =
        runTest(timeout = 60.minutes) {
            val baseUrl = "http://localhost:${container.port}"
            println("KoogTest: wait container at $baseUrl ...")
            waitForOllamaServer(baseUrl)

            val model = findModel(System.getProperty("ollama-model-id"))
            println("KoogTest: model: $model")

            println("KoogTest: creating LLMClient ...")
            val llmClient = OllamaClient(baseUrl = baseUrl)
            llmClient.getModelOrNull(model.id, pullIfMissing = true)

            val promptExecutor = SingleLLMPromptExecutor(llmClient)
            val prompt =
                prompt(id = Uuid.random().toString()) {
                    system(content = "You are a helpful assistant.")
                    user {
                        text(text = "What is the capital of France?")
                    }
                }

            println("KoogTest: execute agent prompt: $prompt")
            val response = promptExecutor.execute(prompt = prompt, model = model).single()
            println("KoogTest: agent response: $response")

            assertContains(
                response.content,
                other = "Paris",
                ignoreCase = true,
                message = "Result should contain Paris",
            )
        }
}
