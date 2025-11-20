object Models {
    val all =
        listOf(
            "gemma3:4b", // https://ollama.com/library/gemma3
            "granite3-guardian:latest", // https://ollama.com/library/granite3-guardian
            "granite3.2-vision", // https://ollama.com/library/granite3.2-vision
            "llama-guard3:latest", // https://ollama.com/library/llama-guard3
            "llama3-groq-tool-use:8b", // https://ollama.com/library/llama3-groq-tool-use
            "llama3.2:3b", // https://ollama.com/library/llama3.2
            "llama3.2:latest", // https://ollama.com/library/llama3.2
            "qwen2.5-coder:3b", // https://ollama.com/library/qwen2.5-coder
            "qwen2.5:0.5b", // https://ollama.com/library/qwen2
            "qwen2.5vl:3b", // https://ollama.com/library/qwen2.5vl
            "qwen3-vl:4b", // https://ollama.com/library/qwen3-vl
            "qwen3:0.6b", // https://ollama.com/library/qwen3
        )
    val text =
        listOf(
            "granite3.2-vision", // https://ollama.com/library/granite3.2-vision
            "llama3-groq-tool-use:8b", // https://ollama.com/library/llama3-groq-tool-use
            "llama3.2:3b", // https://ollama.com/library/llama3.2
            "llama3.2:latest", // https://ollama.com/library/llama3.2
            "qwen2.5-coder:3b", // https://ollama.com/library/qwen2.5-coder
            "qwen2.5:0.5b", // https://ollama.com/library/qwen2
            "qwen3:0.6b", // https://ollama.com/library/qwen3
        )
    val image =
        listOf(
            "granite3.2-vision", // https://ollama.com/library/granite3.2-vision
            "qwen2.5vl:3b", // https://ollama.com/library/qwen2.5vl
            "qwen3-vl:4b", // https://ollama.com/library/qwen3-vl
            "gemma3:4b", // https://ollama.com/library/gemma3
        )
    val guard =
        listOf(
            "granite3-guardian:latest", // https://ollama.com/library/granite3-guardian
            "llama-guard3:latest", // https://ollama.com/library/llama-guard3
        )

    object SelfHosted {
        val text =
            listOf(
                "gpt-oss:20b", // https://ollama.com/library/gpt-oss
            )
        val image =
            listOf(
                "llama3.2-vision", // https://ollama.com/library/llama3.2-vision
                // "llama4" // https://ollama.com/library/llama4
                // "mistral-small3.1" // https://ollama.com/library/mistral-small3.1
                // "mistral-small3.2" // https://ollama.com/library/mistral-small3.2
            )
    }
}
