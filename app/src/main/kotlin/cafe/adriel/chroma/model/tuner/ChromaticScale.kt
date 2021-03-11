package cafe.adriel.chroma.model.tuner

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class ChromaticScale(
    val tone: String,
    val octave: Int,
    val frequency: Float,
    val semitone: Boolean = false
) : Parcelable {

    C0(Tone.C, 0, 16.35f),
    C0_SHARP(Tone.C, 0, 17.32f, true),
    D0(Tone.D, 0, 18.35f),
    D0_SHARP(Tone.D, 0, 19.45f, true),
    E0(Tone.E, 0, 20.60f),
    F0(Tone.F, 0, 21.83f),
    F0_SHARP(Tone.F, 0, 23.12f, true),
    G0(Tone.G, 0, 24.50f),
    G0_SHARP(Tone.G, 0, 25.96f, true),
    A0(Tone.A, 0, 27.50f),
    A0_SHARP(Tone.A, 0, 29.14f, true),
    B0(Tone.B, 0, 30.87f),

    C1(Tone.C, 1, 32.70f),
    C1_SHARP(Tone.C, 1, 34.65f, true),
    D1(Tone.D, 1, 36.71f),
    D1_SHARP(Tone.D, 1, 38.89f, true),
    E1(Tone.E, 1, 41.20f),
    F1(Tone.F, 1, 43.65f),
    F1_SHARP(Tone.F, 1, 46.25f, true),
    G1(Tone.G, 1, 49.00f),
    G1_SHARP(Tone.G, 1, 51.91f, true),
    A1(Tone.A, 1, 55.00f),
    A1_SHARP(Tone.A, 1, 58.27f, true),
    B1(Tone.B, 1, 61.74f),

    C2(Tone.C, 2, 65.41f),
    C2_SHARP(Tone.C, 2, 69.30f, true),
    D2(Tone.D, 2, 73.42f),
    D2_SHARP(Tone.D, 2, 77.78f, true),
    E2(Tone.E, 2, 82.41f),
    F2(Tone.F, 2, 87.31f),
    F2_SHARP(Tone.F, 2, 92.50f, true),
    G2(Tone.G, 2, 98.00f),
    G2_SHARP(Tone.G, 2, 103.83f, true),
    A2(Tone.A, 2, 110.00f),
    A2_SHARP(Tone.A, 2, 116.54f, true),
    B2(Tone.B, 2, 123.47f),

    C3(Tone.C, 3, 130.81f),
    C3_SHARP(Tone.C, 3, 138.59f, true),
    D3(Tone.D, 3, 146.83f),
    D3_SHARP(Tone.D, 3, 155.56f, true),
    E3(Tone.E, 3, 164.81f),
    F3(Tone.F, 3, 174.61f),
    F3_SHARP(Tone.F, 3, 185.00f, true),
    G3(Tone.G, 3, 196.00f),
    G3_SHARP(Tone.G, 3, 207.65f, true),
    A3(Tone.A, 3, 220.00f),
    A3_SHARP(Tone.A, 3, 233.08f, true),
    B3(Tone.B, 3, 246.94f),

    C4(Tone.C, 4, 261.63f),
    C4_SHARP(Tone.C, 4, 277.18f, true),
    D4(Tone.D, 4, 293.66f),
    D4_SHARP(Tone.D, 4, 311.13f, true),
    E4(Tone.E, 4, 329.63f),
    F4(Tone.F, 4, 349.23f),
    F4_SHARP(Tone.F, 4, 369.99f, true),
    G4(Tone.G, 4, 392.00f),
    G4_SHARP(Tone.G, 4, 415.30f, true),
    A4(Tone.A, 4, 440.00f),
    A4_SHARP(Tone.A, 4, 466.16f, true),
    B4(Tone.B, 4, 493.88f),

    C5(Tone.C, 5, 523.25f),
    C5_SHARP(Tone.C, 5, 554.37f, true),
    D5(Tone.D, 5, 587.33f),
    D5_SHARP(Tone.D, 5, 622.25f, true),
    E5(Tone.E, 5, 659.25f),
    F5(Tone.F, 5, 698.46f),
    F5_SHARP(Tone.F, 5, 739.99f, true),
    G5(Tone.G, 5, 783.99f),
    G5_SHARP(Tone.G, 5, 830.61f, true),
    A5(Tone.A, 5, 880.00f),
    A5_SHARP(Tone.A, 5, 932.33f, true),
    B5(Tone.B, 5, 987.77f),

    C6(Tone.C, 6, 1046.50f),
    C6_SHARP(Tone.C, 6, 1108.73f, true),
    D6(Tone.D, 6, 1174.66f),
    D6_SHARP(Tone.D, 6, 1244.51f, true),
    E6(Tone.E, 6, 1318.51f),
    F6(Tone.F, 6, 1396.91f),
    F6_SHARP(Tone.F, 6, 1479.98f, true),
    G6(Tone.G, 6, 1567.98f),
    G6_SHARP(Tone.G, 6, 1661.22f, true),
    A6(Tone.A, 6, 1760.00f),
    A6_SHARP(Tone.A, 6, 1864.66f, true),
    B6(Tone.B, 6, 1975.53f),

    C7(Tone.C, 7, 2093.00f),
    C7_SHARP(Tone.C, 7, 2217.46f, true),
    D7(Tone.D, 7, 2349.32f),
    D7_SHARP(Tone.D, 7, 2489.02f, true),
    E7(Tone.E, 7, 2637.02f),
    F7(Tone.F, 7, 2793.83f),
    F7_SHARP(Tone.F, 7, 2959.96f, true),
    G7(Tone.G, 7, 3135.96f),
    G7_SHARP(Tone.G, 7, 3322.44f, true),
    A7(Tone.A, 7, 3520.00f),
    A7_SHARP(Tone.A, 7, 3729.31f, true),
    B7(Tone.B, 7, 3951.07f),

    C8(Tone.C, 8, 4186.01f),
    C8_SHARP(Tone.C, 8, 4434.92f, true),
    D8(Tone.D, 8, 4698.63f),
    D8_SHARP(Tone.D, 8, 4978.03f, true),
    E8(Tone.E, 8, 5274.04f),
    F8(Tone.F, 8, 5587.65f),
    F8_SHARP(Tone.F, 8, 5919.91f, true),
    G8(Tone.G, 8, 6271.93f),
    G8_SHARP(Tone.G, 8, 6644.88f, true),
    A8(Tone.A, 8, 7040.00f),
    A8_SHARP(Tone.A, 8, 7458.62f, true),
    B8(Tone.B, 8, 7902.13f);

    val formattedFrequency by lazy { FREQUENCY_FORMAT.format(frequency) }

    companion object {
        const val FREQUENCY_FORMAT = "%.2f"

        val notes by lazy {
            values().sortedBy { it.frequency }
        }

        fun getFlatTone(tone: String) = when (tone) {
            Tone.C -> Tone.D
            Tone.D -> Tone.E
            Tone.F -> Tone.G
            Tone.G -> Tone.A
            Tone.A -> Tone.B
            else -> throw IllegalArgumentException("Can't convert $tone to flat")
        }

        fun getSolfegeTone(tone: String) = when (tone) {
            Tone.C -> "Do"
            Tone.D -> "Re"
            Tone.E -> "Mi"
            Tone.F -> "Fa"
            Tone.G -> "Sol"
            Tone.A -> "La"
            Tone.B -> "Si"
            else -> throw IllegalArgumentException("Can't convert $tone to Solfege notation")
        }
    }
}
