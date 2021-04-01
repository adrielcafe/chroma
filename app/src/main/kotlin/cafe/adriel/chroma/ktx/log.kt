package cafe.adriel.chroma.ktx

import com.google.firebase.crashlytics.FirebaseCrashlytics

fun logError(error: Throwable) =
    FirebaseCrashlytics.getInstance().recordException(error)
