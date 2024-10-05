package com.misana.barcodeface.domain.model

enum class Emotion {
    ANGRY,
    DISGUST,
    FEAR,
    HAPPY,
    NEUTRAL,
    SAD,
    SURPRISE;

    override fun toString() = when (this) {
        ANGRY -> "Angry"
        DISGUST -> "Disgust"
        FEAR -> "Fear"
        HAPPY -> "Happy"
        NEUTRAL -> "Neutral"
        SAD -> "Sad"
        SURPRISE -> "Surprise"
    }
}
