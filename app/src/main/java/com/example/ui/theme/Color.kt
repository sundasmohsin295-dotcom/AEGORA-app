package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// AEGORA // Multi-Platform Design System — Exact Specification Palette
// Canvas Background: #050B14 (Deep Void Blue-Black)
// Deep Surface Cards: #0B1528 (Primary Card Background)
// Elevated Surfaces: #12203A (Sub-cards, Inputs, Terminal Boxes)
// Hairline Borders: #1E3A5F (1.dp border)
// Primary Action Blue: #3B82F6 (Solid Vibrant Electric Blue)
// Cyan Highlights: #22D3EE (Status indicators, live pills, active bottom-bar items)
// Emerald Verification: #34D399 (Checkmarks, "AI FAILURE DETECTED ✓", Success state)
// Warning/Decay Amber: #F59E0B
// Failure/Crime Red: #EF4444
// Typography: Primary Headings: #F8FAFC, Secondary Metadata: #8CA3C7

val SpecCanvasBg = Color(0xFF050B14)
val SpecCardBg = Color(0xFF0B1528)
val SpecElevatedBg = Color(0xFF12203A)
val SpecBorder = Color(0xFF1E3A5F)
val SpecPrimaryBlue = Color(0xFF3B82F6)
val SpecCyanHighlight = Color(0xFF22D3EE)
val SpecEmeraldVerification = Color(0xFF34D399)
val SpecWarningAmber = Color(0xFFF59E0B)
val SpecFailureRed = Color(0xFFEF4444)
val SpecHeadingWhite = Color(0xFFF8FAFC)
val SpecSubtextSlate = Color(0xFF8CA3C7)

val AegoraBackground = SpecCanvasBg
val AegoraBackgroundGradientEnd = SpecCanvasBg
val AegoraSurface = SpecCardBg
val AegoraSurfaceElevated = SpecElevatedBg
val AegoraSurfaceHighlight = SpecElevatedBg
val AegoraBorder = SpecBorder
val AegoraBorderSubtle = SpecBorder
val AegoraBorderHighlight = SpecPrimaryBlue
val AegoraTextPrimary = SpecHeadingWhite
val AegoraTextSecondary = SpecSubtextSlate
val AegoraTextTertiary = Color(0xFF5A718A)

// AEGORA Core & Semantic Color Accents
val SemanticElectricBlue = SpecPrimaryBlue
val SemanticHome = SpecPrimaryBlue
val SemanticLearn = Color(0xFF8B5CF6)
val SemanticPractice = SpecEmeraldVerification
val SemanticInvestigate = SpecPrimaryBlue
val SemanticAI = SpecPrimaryBlue
val SemanticProof = SpecEmeraldVerification
val SemanticRoadmap = SpecCyanHighlight
val SemanticIntel = SpecCyanHighlight
val SemanticTools = SpecPrimaryBlue
val SemanticWarning = SpecWarningAmber
val SemanticSuccess = SpecEmeraldVerification

// Legacy & Semantic Compatibility mappings (Aliased to Strict Enterprise Obsidian)
val PureBlack = Color(0xFF090A0C)
val CyberBlack = Color(0xFF090A0C)
val CyberDarkSlate = Color(0xFF15171C)
val CyberCardBg = Color(0xFF15171C)
val CyberCardBorder = Color(0xFF2D313A)
val CyberBackground = Color(0xFF090A0C)
val CyberSurface = Color(0xFF15171C)
val CyberSurfaceVariant = Color(0xFF15171C)
val CyberSurfaceElevated = Color(0xFF15171C)
val CyberBorder = Color(0xFF2D313A)
val CyberBorderSubtle = Color(0xFF2D313A)

// Operational semantic accents mapped to Enterprise Obsidian palette (No cyan/neon glow)
val NeonCyan = Color(0xFF2962FF)                  // Mapped to Cobalt #2962FF
val NeonGreen = Color(0xFF00E676)
val NeonEmerald = Color(0xFF00E676)
val NeonCrimson = Color(0xFFFF1744)
val NeonViolet = Color(0xFF8B5CF6)
val CyberViolet = Color(0xFF8B5CF6)
val NeonPink = Color(0xFFEC4899)
val TerminalAmber = Color(0xFFFFB300)

val CyberCyan = Color(0xFF2962FF)                 // Cobalt replacement for legacy cyan
val CyberCyanDark = Color(0xFF1E40AF)
val CyberBlue = Color(0xFF2962FF)
val CyberIndigo = Color(0xFF3B82F6)
val CyberMagenta = Color(0xFF2962FF)
val CyberPurple = SemanticLearn
val CyberEmerald = SemanticPractice
val CyberGreen = SemanticPractice
val CyberAmber = SemanticRoadmap
val CyberCrimson = SemanticInvestigate
val CyberRed = SemanticInvestigate
val CyberGold = SemanticProof

val AegoraCyanVerified = SemanticHome
val AegoraRedRisk = SemanticInvestigate
val AegoraAmberDecay = SemanticProof

// Container Accents
val VibrantCyanContainer = Color(0xFF102035)
val VibrantCyanOnContainer = SemanticHome
val VibrantPinkContainer = Color(0xFF2E121E)
val VibrantPinkOnContainer = Color(0xFFFDA4AF)
val VibrantPurpleContainer = Color(0xFF1E1735)
val VibrantPurpleOnContainer = SemanticLearn
val VibrantBlueContainer = Color(0xFF0C243C)
val VibrantBlueOnContainer = SemanticHome
val VibrantMintContainer = Color(0xFF0C261E)
val VibrantMintOnContainer = SemanticPractice
val VibrantEmeraldContainer = Color(0xFF0C261E)
val VibrantEmeraldOnContainer = SemanticPractice
val VibrantAmberContainer = Color(0xFF2E220C)
val VibrantAmberOnContainer = SemanticRoadmap
val VibrantNavPill = Color(0xFF121820)

// High-Contrast Typography Tokens
val TextPrimaryDark = AegoraTextPrimary
val TextSecondaryDark = AegoraTextSecondary
val TextTertiaryDark = AegoraTextTertiary
val CodeBackground = Color(0xFF07090D)
val CodeGreen = SemanticPractice
val CodeAmber = SemanticRoadmap

// Light Theme Fallbacks forced to dark tokens
val CyberBackgroundLight = AegoraBackground
val CyberSurfaceLight = AegoraSurface
val CyberSurfaceVariantLight = AegoraSurfaceElevated
val CyberBorderLight = AegoraBorder
val TextPrimaryLight = AegoraTextPrimary
val TextSecondaryLight = AegoraTextSecondary
val TextTertiaryLight = AegoraTextTertiary


