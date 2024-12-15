package com.example.quick_reply.presentation.model

data class SpannedText(
    val fullText: String? = null,
    val subTexts: List<String>? = null
) {

    constructor(
        fullText: String? = null,
        subText: String
    ) : this(fullText, listOf(subText))
}