import com.google.gson.annotations.SerializedName

data class MatrixModel(
    @SerializedName("variants") val variants: List<Variant>,
)

data class Variant(
    @SerializedName("artifact-name") val artifactName: String,
    @SerializedName("runs-on") val runsOn: String,
    @SerializedName("enabled") val enabled: Boolean,
    @SerializedName("ollama-model-id") val ollamaModelId: String,
    @SerializedName("test-group") val testGroup: String,
)
