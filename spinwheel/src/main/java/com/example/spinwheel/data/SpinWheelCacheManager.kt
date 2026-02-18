package com.example.spinwheel.data

import android.content.Context
import java.io.File

class SpinWheelCacheManager(context: Context) {

    private val rootDir: File = File(context.filesDir, ROOT_DIR_NAME).apply { mkdirs() }
    private val assetsDir: File = File(rootDir, ASSETS_DIR_NAME).apply { mkdirs() }
    private val framesDir: File = File(rootDir, FRAMES_DIR_NAME).apply { mkdirs() }

    fun getConfigFile(): File = File(rootDir, CONFIG_FILE_NAME)

    fun getAssetFile(fileName: String): File = File(assetsDir, fileName)

    fun getFrameFile(index: Int): File = File(framesDir, "wheel_%03d.png".format(index))

    fun hasAsset(fileName: String): Boolean {
        val f = getAssetFile(fileName)
        return f.exists() && f.length() > 0
    }

    fun writeConfig(json: String) {
        rootDir.mkdirs()
        val f = getConfigFile()
        f.parentFile?.mkdirs()
        f.writeText(json)
    }

    fun readConfigOrNull(): String? {
        val f = getConfigFile()
        if (!f.exists() || f.length() == 0L) return null
        return f.readText()
    }

    fun writeAsset(fileName: String, bytes: ByteArray) {
        assetsDir.mkdirs()
        val f = getAssetFile(fileName)
        f.parentFile?.mkdirs()
        f.writeBytes(bytes)
    }

    fun clearFrames() {
        framesDir.listFiles()?.forEach { it.delete() }
    }

    fun framesCount(): Int = framesDir.listFiles()?.size ?: 0

    private companion object {
        const val ROOT_DIR_NAME = "spinwheel"
        const val ASSETS_DIR_NAME = "assets"
        const val FRAMES_DIR_NAME = "frames"
        const val CONFIG_FILE_NAME = "config.json"
    }
}