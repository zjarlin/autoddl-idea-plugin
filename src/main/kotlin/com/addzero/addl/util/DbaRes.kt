package com.addzero.addl.util

import com.fasterxml.jackson.annotation.JsonProperty

data class Dba(
    @JsonProperty("choices")
    val choices: List<ChoicesDTO?>? = null,

    @JsonProperty("object")
    val `object`: String? = null,

    @JsonProperty("usage")
    val usage: UsageDTO? = null,

    @JsonProperty("created")
    val created: Int? = null,

    @JsonProperty("system_fingerprint")
    val systemFingerprint: Any? = null,

    @JsonProperty("model")
    val model: String? = null,

    @JsonProperty("id")
    val id: String? = null,
) {
    data class UsageDTO(
        @JsonProperty("prompt_tokens")
        val promptTokens: Int? = null,

        @JsonProperty("completion_tokens")
        val completionTokens: Int? = null,

        @JsonProperty("total_tokens")
        val totalTokens: Int? = null,
    )

    data class ChoicesDTO(
        @JsonProperty("message")
        val message: MessageDTO? = null,

        @JsonProperty("finish_reason")
        val finishReason: String? = null,

        @JsonProperty("index")
        val index: Int? = null,

        @JsonProperty("logprobs")
        val logprobs: Any? = null,
    ) {
        data class MessageDTO(
            @JsonProperty("role")
            val role: String? = null,

            @JsonProperty("content")
            val content: String? = null,
        )
    }
}