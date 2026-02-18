package com.example.spinwheel.model

import kotlinx.serialization.Serializable

@Serializable
data class RootConfig(
    val data: List<WidgetConfig>,
    val meta: Meta
)

@Serializable
data class Meta(
    val version: Int,
    val copyright: String
)

@Serializable
data class WidgetConfig(
    val id: String,
    val name: String,
    val type: String,
    val network: Network,
    val wheel: Wheel
)

@Serializable
data class Network(
    val attributes: NetworkAttributes,
    val assets: AssetsHost
)

@Serializable
data class NetworkAttributes(
    val refreshInterval: Int,
    val networkTimeout: Int,
    val retryAttempts: Int,
    val cacheExpiration: Int,
    val debugMode: Boolean
)

@Serializable
data class AssetsHost(
    val host: String
)

@Serializable
data class Wheel(
    val rotation: Rotation,
    val assets: WheelAssets
)

@Serializable
data class Rotation(
    val duration: Int,
    val minimumSpins: Int,
    val maximumSpins: Int,
    val spinEasing: String
)

@Serializable
data class WheelAssets(
    val bg: String,
    val wheelFrame: String,
    val wheelSpin: String,
    val wheel: String
)