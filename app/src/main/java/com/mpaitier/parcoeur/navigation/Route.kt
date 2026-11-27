package com.mpaitier.parcoeur.navigation

import kotlinx.serialization.Serializable

// Routes type-safe (Navigation Compose 2.8+).
// Ajoute ici les autres écrans, par exemple :
// @Serializable data class ChapterList(val courseId: String) : Route
// @Serializable data class DefinitionList(val chapterId: String) : Route
sealed interface Route {
    @Serializable
    data object CourseList : Route
}
