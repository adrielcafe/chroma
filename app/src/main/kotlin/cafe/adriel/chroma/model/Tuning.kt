package cafe.adriel.chroma.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tuning(val note: ChromaticScale? = null,
                  val frequency: Float = -1f,
                  val deviation: Int = 0) : Parcelable