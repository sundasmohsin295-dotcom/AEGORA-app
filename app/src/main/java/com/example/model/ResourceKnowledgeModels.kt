package com.example.model

enum class ResourceCategory(val label: String, val iconName: String) {
  ALL("All Resources", "AllInclusive"),
  BOOKS("Authoritative Books", "MenuBook"),
  PAPERS("Research Papers", "Article"),
  STANDARDS("Standards & RFCs", "FactCheck"),
  ADVISORIES("Threat Advisories", "Warning"),
  TOOLS("Essential Tools", "Construction"),
  CHEAT_SHEETS("Cheat Sheets", "Assignment"),
  CASE_STUDIES("Incident Case Studies", "FolderSpecial")
}

enum class ResourceTrustLevel(val label: String, val badgeColor: Long) {
  OFFICIAL("OFFICIAL // AUTHORITATIVE", 0xFF00F0FF),
  ACADEMIC("PEER-REVIEWED ACADEMIC", 0xFF8B5CF6),
  INDUSTRY("ESTABLISHED INDUSTRY", 0xFF00E676),
  COMMUNITY("COMMUNITY VERIFIED", 0xFFFFB800)
}

enum class ResourceAccessType(val label: String) {
  OPEN_ACCESS("Open Access / Free"),
  OFFICIAL_SPEC("Official Specification"),
  PUBLISHER_PREVIEW("Publisher Reference"),
  FREE_TOOL("Open Source Tool")
}

data class ResourceQualityScore(
  val overallScore: Int, // e.g. 96/100
  val authorityScore: Int,
  val practicalValueScore: Int,
  val careerRelevanceScore: Int,
  val recencyScore: Int
)

data class ResourceKnowledgeItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val category: ResourceCategory,
  val authorOrOrg: String,
  val publicationYear: String,
  val trustLevel: ResourceTrustLevel,
  val accessType: ResourceAccessType,
  val readingOrStudyTime: String,
  val quality: ResourceQualityScore,
  val targetedCareerPaths: List<String>,
  val targetedSkills: List<String>,
  val keyTakeaways: List<String>,
  val whyThisMatters: String,
  val relatedLabId: String? = null,
  val relatedLabTitle: String? = null,
  val officialUrlOrDoc: String,
  val isBookmarked: Boolean = false,
  val isCompleted: Boolean = false
)
