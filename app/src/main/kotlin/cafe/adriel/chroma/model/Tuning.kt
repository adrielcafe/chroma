package cafe.adriel.chroma.model

import android.os.Parcelable
import cafe.adriel.chroma.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tuning(
    val note: ChromaticScale? = null,
    val frequency: Float = -1f,
    val deviationResult: TuningDeviationResult = TuningDeviationResult.NotDetected
) : Parcelable {

    val formattedFrequency by lazy { ChromaticScale.FREQUENCY_FORMAT.format(frequency) }

    fun getTone(settings: Settings): String {
        requireNotNull(note)

        return when {
            settings.flatSymbol && settings.solfegeNotation && note.semitone ->
                ChromaticScale.getSolfegeTone(ChromaticScale.getFlatTone(note.tone))
            settings.flatSymbol && note.semitone ->
                ChromaticScale.getFlatTone(note.tone)
            settings.solfegeNotation ->
                ChromaticScale.getSolfegeTone(note.tone)
            else -> note.tone
        }
    }

    fun getSemitoneSymbolRes(settings: Settings): Int? {
        requireNotNull(note)

        return if (note.semitone) {
            if (settings.flatSymbol) R.string.flat_symbol
            else R.string.sharp_symbol
        } else {
            null
        }
    }
}

