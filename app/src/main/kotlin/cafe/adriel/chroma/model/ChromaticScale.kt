package cafe.adriel.chroma.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class ChromaticScale(val tone: String,
                          val octave: Int,
                          val frequency: Float,
                          val semitone: Boolean = false) : Parcelable {

    C0("C", 0, 16.35f),
    C0_SHARP("C", 0,  17.32f, true),
    D0("D", 0, 18.35f),
    D0_SHARP("D", 0, 19.45f, true),
    E0("E", 0, 20.60f),
    F0("F", 0, 21.83f),
    F0_SHARP("F", 0, 23.12f, true),
    G0("G", 0, 24.50f),
    G0_SHARP("G", 0, 25.96f, true),
    A0("A", 0, 27.50f),
    A0_SHARP("A", 0, 29.14f, true),
    B0("B", 0, 30.87f),

    C1("C", 1, 32.70f),
    C1_SHARP("C", 1, 34.65f, true),
    D1("D", 1, 36.71f),
    D1_SHARP("D", 1, 38.89f, true),
    E1("E", 1, 41.20f),
    F1("F", 1, 43.65f),
    F1_SHARP("F", 1, 46.25f, true),
    G1("G", 1, 49.00f),
    G1_SHARP("G", 1, 51.91f, true),
    A1("A", 1, 55.00f),
    A1_SHARP("A", 1, 58.27f, true),
    B1("B", 1, 61.74f),

    C2("C", 2, 65.41f),
    C2_SHARP("C", 2, 69.30f, true),
    D2("D", 2, 73.42f),
    D2_SHARP("D", 2, 77.78f, true),
    E2("E", 2, 82.41f),
    F2("F", 2, 87.31f),
    F2_SHARP("F", 2, 92.50f, true),
    G2("G", 2, 98.00f),
    G2_SHARP("G", 2, 103.83f, true),
    A2("A", 2, 110.00f),
    A2_SHARP("A", 2, 116.54f, true),
    B2("B", 2, 123.47f),

    C3("C", 3, 130.81f),
    C3_SHARP("C", 3, 138.59f, true),
    D3("D", 3, 146.83f),
    D3_SHARP("D", 3, 155.56f, true),
    E3("E", 3, 164.81f),
    F3("F", 3, 174.61f),
    F3_SHARP("F", 3, 185.00f, true),
    G3("G", 3, 196.00f),
    G3_SHARP("G", 3, 207.65f, true),
    A3("A", 3, 220.00f),
    A3_SHARP("A", 3, 233.08f, true),
    B3("B", 3, 246.94f),

    C4("C", 4, 261.63f),
    C4_SHARP("C", 4, 277.18f, true),
    D4("D", 4, 293.66f),
    D4_SHARP("D", 4, 311.13f, true),
    E4("E", 4, 329.63f),
    F4("F", 4, 349.23f),
    F4_SHARP("F", 4, 369.99f, true),
    G4("G", 4, 392.00f),
    G4_SHARP("G", 4, 415.30f, true),
    A4("A", 4, 440.00f),
    A4_SHARP("A", 4, 466.16f, true),
    B4("B", 4, 493.88f),

    C5("C", 5, 523.25f),
    C5_SHARP("C", 5, 554.37f, true),
    D5("D", 5, 587.33f),
    D5_SHARP("D", 5, 622.25f, true),
    E5("E", 5, 659.25f),
    F5("F", 5, 698.46f),
    F5_SHARP("F", 5, 739.99f, true),
    G5("G", 5, 783.99f),
    G5_SHARP("G", 5, 830.61f, true),
    A5("A", 5, 880.00f),
    A5_SHARP("A", 5, 932.33f, true),
    B5("B", 5, 987.77f),

    C6("C", 6, 1046.50f),
    C6_SHARP("C", 6, 1108.73f, true),
    D6("D", 6, 1174.66f),
    D6_SHARP("D", 6, 1244.51f, true),
    E6("E", 6, 1318.51f),
    F6("F", 6, 1396.91f),
    F6_SHARP("F", 6, 1479.98f, true),
    G6("G", 6, 1567.98f),
    G6_SHARP("G", 6, 1661.22f, true),
    A6("A", 6, 1760.00f),
    A6_SHARP("A", 6, 1864.66f, true),
    B6("B", 6, 1975.53f),

    C7("C", 7, 2093.00f),
    C7_SHARP("C", 7, 2217.46f, true),
    D7("D", 7, 2349.32f),
    D7_SHARP("D", 7, 2489.02f, true),
    E7("E", 7, 2637.02f),
    F7("F", 7, 2793.83f),
    F7_SHARP("F", 7, 2959.96f, true),
    G7("G", 7, 3135.96f),
    G7_SHARP("G", 7, 3322.44f, true),
    A7("A", 7, 3520.00f),
    A7_SHARP("A", 7, 3729.31f, true),
    B7("B", 7, 3951.07f),

    C8("C", 8, 4186.01f),
    C8_SHARP("C", 8, 4434.92f, true),
    D8("D", 8, 4698.63f),
    D8_SHARP("D", 8, 4978.03f, true),
    E8("E", 8, 5274.04f),
    F8("F", 8, 5587.65f),
    F8_SHARP("F", 8, 5919.91f, true),
    G8("G", 8, 6271.93f),
    G8_SHARP("G", 8, 6644.88f, true),
    A8("A", 8, 7040.00f),
    A8_SHARP("A", 8, 7458.62f, true),
    B8("B", 8, 7902.13f);

    companion object {
        val notes by lazy {
            values().sortedBy { it.frequency }
        }

        fun getFlatTone(tone: String) = when (tone) {
            "C" -> "D"
            "D" -> "E"
            "F" -> "G"
            "G" -> "A"
            "A" -> "B"
            else -> throw UnsupportedOperationException("Can't convert $tone to flat")
        }

        fun getSolfegeTone(tone: String) = when(tone){
            "C" -> "Do"
            "D" -> "Re"
            "E" -> "Mi"
            "F" -> "Fa"
            "G" -> "Sol"
            "A" -> "La"
            "B" -> "Si"
            else -> throw UnsupportedOperationException("Can't convert $tone to Solfege notation")
        }
    }

}