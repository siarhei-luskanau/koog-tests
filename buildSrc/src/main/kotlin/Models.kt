object Models {
    object Actions {
        val text =
            setOf(
                OllamaModel.GRANITE3_2_VISION,
                OllamaModel.LLAMA3_2_3B,
                OllamaModel.LLAMA3_2_LATEST,
                OllamaModel.LLAMA3_GROQ_TOOL_USE_8B,
                OllamaModel.QWEN2_5_0_5B,
                OllamaModel.QWEN2_5_CODER_3B,
                OllamaModel.QWEN3_0_6B,
            )
        val image =
            setOf(
                OllamaModel.GEMMA3_4B,
                OllamaModel.GRANITE3_2_VISION,
                OllamaModel.QWEN2_5VL_3B,
                OllamaModel.QWEN3_VL_4B,
            )
        val guard =
            setOf(
                OllamaModel.GRANITE3_GUARDIAN_LATEST,
                OllamaModel.LLAMA_GUARD3_LATEST,
            )
    }

    object SelfHosted {
        val text =
            mutableSetOf(
                OllamaModel.GPT_OSS_20B,
            ).also { it.addAll(Actions.text) }
        val image =
            mutableSetOf(
                OllamaModel.MISTRAL_SMALL3_1,
                OllamaModel.MISTRAL_SMALL3_2,
            ).also { it.addAll(Actions.image) }
        val guard =
            mutableSetOf(
                OllamaModel.LLAMA_GUARD3_LATEST,
            ).also { it.addAll(Actions.guard) }
    }
}
