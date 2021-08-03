package cafe.adriel.chroma.model.tuner

import cafe.adriel.chroma.model.settings.AccidentalOption
import cafe.adriel.chroma.model.settings.NotationOption
import cafe.adriel.chroma.model.settings.Settings

data class Tuning(
    val note: ChromaticScale? = null,
    val frequency: Float = -1f,
    val deviation: TuningDeviationResult = TuningDeviationResult.NotDetected
) {

    val formattedFrequency by lazy { ChromaticScale.FREQUENCY_FORMAT.format(frequency) }

    fun getTone(settings: Settings): String {
        requireNotNull(note)

        return when {
            settings.accidental == AccidentalOption.FLAT &&
                settings.notation == NotationOption.DO_RE_MI &&
                note.semitone -> ChromaticScale.getSolfegeTone(ChromaticScale.getFlatTone(note.tone))

            settings.accidental == AccidentalOption.FLAT &&
                note.semitone -> ChromaticScale.getFlatTone(note.tone)

            settings.notation == NotationOption.DO_RE_MI -> ChromaticScale.getSolfegeTone(note.tone)

            else -> note.tone
        }
    }

    fun getSemitoneSymbolRes(settings: Settings): Int? {
        requireNotNull(note)

        return if (note.semitone) {
            settings.accidental.symbolRes
        } else {
            null
        }
    }
}
