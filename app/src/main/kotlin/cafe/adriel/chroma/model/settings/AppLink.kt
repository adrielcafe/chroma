package cafe.adriel.chroma.model.settings

import cafe.adriel.chroma.BuildConfig

enum class AppLink(val url: String) {
    PLAY_STORE("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"),
    MARKET("market://details?id=${BuildConfig.APPLICATION_ID}")
}
