package com.example.model

/**
 * Data structures representing network packet dissection records
 * and adversarial duel telemetry scenarios.
 */
data class ScapyParsedPacket(
  val number: Int,
  val timestamp: String,
  val protocol: String,
  val flags: String,
  val ethSrc: String,
  val ethDst: String,
  val ipSrc: String,
  val ipDst: String,
  val layers: String,
  val summary: String,
  val payloadHex: String = "",
  val payloadAscii: String = "",
  val isSuspicious: Boolean = false
)

data class DuelScenario(
  val id: String,
  val adversary: String,
  val rawTelemetry: String,
  val decodedPayload: String,
  val aiClaim: String,
  val isAiHallucinating: Boolean,
  val explanation: String,
  val mitreKillChainStage: String = "Delivery",
  val threatScore: Int = 85,
  val pcapPackets: List<ScapyParsedPacket> = emptyList()
)
