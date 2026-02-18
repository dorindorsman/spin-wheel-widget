package com.example.spinwheel.data

import com.example.spinwheel.model.RootConfig
import com.example.spinwheel.model.WidgetConfig
import com.example.spinwheel.prefs.SpinWheelPrefs
import kotlinx.serialization.json.Json

class SpinWheelRepository(
    private val api: SpinWheelApi,
    private val cache: SpinWheelCacheManager,
    private val prefs: SpinWheelPrefs,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {

    fun getOrFetchActiveConfig(configUrl: String): WidgetConfig {
        val cached = cache.readConfigOrNull()
        if (cached != null) {
            val parsed = parseConfig(cached)
            if (isCacheValid(parsed)) {
                return parsed.data.first().also { syncRotationToPrefs(it) }
            }
        }

        val raw = api.fetchText(configUrl)
        cache.writeConfig(raw)
        prefs.setLastConfigFetchTimeMillis(System.currentTimeMillis())

        val parsed = parseConfig(raw)
        return parsed.data.first().also { syncRotationToPrefs(it) }
    }

    private fun syncRotationToPrefs(active: WidgetConfig) {
        prefs.setSpinDurationMs(active.wheel.rotation.duration.toLong())
        prefs.setMinSpins(active.wheel.rotation.minimumSpins)
        prefs.setMaxSpins(active.wheel.rotation.maximumSpins)
    }

    fun ensureAssets(config: WidgetConfig) {
        val host = config.network.assets.host
        val assets = config.wheel.assets

        downloadAssetIfMissing("bg.jpeg",  joinUrl(host , assets.bg))
        downloadAssetIfMissing("wheel.png",  joinUrl(host , assets.wheel))
        downloadAssetIfMissing("wheel-frame.png",  joinUrl(host , assets.wheelFrame))
        downloadAssetIfMissing("wheel-spin.png",  joinUrl(host , assets.wheelSpin))
    }

    private fun downloadAssetIfMissing(localFileName: String, url: String) {
        if (cache.hasAsset(localFileName)) return
        val bytes = api.fetchBytes(url)
        cache.writeAsset(localFileName, bytes)
    }

    private fun joinUrl(host: String, path: String): String {
        val cleanHost = host.trim()
        val cleanPath = path.trimStart('/')

        return if (cleanHost.endsWith("=")) {
            // Google Drive style host (id=)
            cleanHost + cleanPath
        } else {
            // Regular host/path
            cleanHost.trimEnd('/') + "/" + cleanPath
        }
    }

    private fun parseConfig(raw: String): RootConfig =
        json.decodeFromString(RootConfig.serializer(), raw)

    private fun isCacheValid(root: RootConfig): Boolean {
        val cfg = root.data.firstOrNull() ?: return false
        val expirationSec = cfg.network.attributes.cacheExpiration
        if (expirationSec <= 0) return false

        val ageMs = System.currentTimeMillis() - prefs.getLastConfigFetchTimeMillis()
        return ageMs < expirationSec * 1000L
    }
}