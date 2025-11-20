# Prerequisites

- **JDK** installed
- **Docker** installed
- **Ollama** installed

# Running a test in using testcontainer with Ollama
- run `./gradlew clean :ollama:test -DRUNNER_ENVIRONMENT=github-hosted -Dollama-model-id=llama3.2:latest --tests "KoogTextPromptTest" --stacktrace`
- run `./gradlew clean :ollama:test -DRUNNER_ENVIRONMENT=github-hosted -Dollama-model-id=granite3.2-vision --tests "KoogImagePromptTest" --stacktrace`
- run `./gradlew clean :ollama:test -DRUNNER_ENVIRONMENT=github-hosted -Dollama-model-id=granite3-guardian:latest --tests "KoogGuardPromptTest" --stacktrace`

# Running a test in using standalone Ollama
- run `./gradlew clean :ollama:test -DOLLAMA_BASE_URL=http://localhost:11434 -Dollama-model-id=llama3.2:latest --tests "KoogTextPromptTest" --stacktrace`
- run `./gradlew clean :ollama:test -DOLLAMA_BASE_URL=http://localhost:11434 -Dollama-model-id=granite3.2-vision --tests "KoogImagePromptTest" --stacktrace`
- run `./gradlew clean :ollama:test -DOLLAMA_BASE_URL=http://localhost:11434 -Dollama-model-id=granite3-guardian:latest --tests "KoogGuardPromptTest" --stacktrace`
